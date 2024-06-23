package com.tienda.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tienda.entities.Carrito;
import com.tienda.entities.ItemCarrito;
import com.tienda.entities.Producto;
import com.tienda.repositories.ICarritoDao;
import com.tienda.repositories.IItemCarritoDao;
import com.tienda.repositories.IProductoDao;
import com.tienda.repositories.IUsuarioDao;

@Service
public class CarritoServiceImp implements ICarritoService {

	private ICarritoDao carritoDao;

	private IProductoDao productoDao;

	private IUsuarioDao usuarioDao;

	private IItemCarritoDao itemCarritoDao;

	public CarritoServiceImp(ICarritoDao carritoDao, IProductoDao productoDao, IUsuarioDao usuarioDao,
			IItemCarritoDao itemCarritoDao) {
		this.carritoDao = carritoDao;
		this.productoDao = productoDao;
		this.usuarioDao = usuarioDao;
		this.itemCarritoDao = itemCarritoDao;
	}

	@Override
	public Carrito getCarritoByUsuario(int idUsuario) {
		Carrito c = carritoDao.findByUsuario(idUsuario);
		if (c == null) {
			c = new Carrito();
			c.setUsuario(usuarioDao.findById(idUsuario).orElseThrow());
			carritoDao.save(c);
		}
		return c;
	}

	@Override
	public Carrito agregarItem(int idUsuario, int idProducto, int cantidad) {

		Carrito c = carritoDao.findByUsuario(idUsuario);
		if (c == null) {
			c = new Carrito();
			c.setItems(new ArrayList<>());
			c.setUsuario(usuarioDao.findById(idUsuario).orElseThrow());
			carritoDao.save(c);
		}
		Optional<ItemCarrito> itemRepetido = c.getItems().stream()
				.filter(item -> item.getProducto().getId() == idProducto).findFirst();

		Producto p = productoDao.findById(idProducto).orElseThrow();

		if (p.getStock() < cantidad) {
			throw new IllegalArgumentException("El producto no tiene suficiente stock");
		}

		if (itemRepetido.isPresent()) {
			ItemCarrito item = itemRepetido.get();
			item.setCantidad(item.getCantidad() + cantidad);
			item.setImporte(item.getCantidad() * item.getProducto().getPrecioVenta());
			itemCarritoDao.save(item);
		} else {

			ItemCarrito linea = new ItemCarrito();
			linea.setCantidad(cantidad);
			linea.setProducto(p);
			linea.setImporte(cantidad * p.getPrecioVenta());
			linea.setCarrito(c);
			itemCarritoDao.save(linea);
			c.getItems().add(linea);
		}
		p.setStock(p.getStock() - cantidad);
		productoDao.save(p);
		c.setTotal(c.getItems().stream().mapToDouble(ln -> ln.getImporte()).sum());
		carritoDao.save(c);
		return c;
	}

	@Override
	public Carrito quitarItem(int idUsuario, int idProducto) {
		Carrito c = carritoDao.findByUsuario(idUsuario);
		Optional<ItemCarrito> itemToRemove = c.getItems().stream()
				.filter(item -> item.getProducto().getId() == idProducto).findFirst();

		if (itemToRemove.isPresent()) {
			ItemCarrito itemToBeRemoved = itemToRemove.get();
			Producto producto = itemToBeRemoved.getProducto();
			c.getItems().remove(itemToRemove.get());
			producto.setStock(producto.getStock() + itemToBeRemoved.getCantidad());
			productoDao.save(producto);
			itemCarritoDao.deleteById(itemToRemove.get().getId());
			c.setTotal(c.getItems().stream().mapToDouble(ItemCarrito::getImporte).sum());
			carritoDao.save(c);
		}
		return c;
	}

	@Override
	public Carrito vaciarCarrito(int idUsuario) {
		Carrito c = carritoDao.findByUsuario(idUsuario);
		c.setItems(new ArrayList<>());
		itemCarritoDao.deleteAllByCarritoId((long) c.getId());
		c.setTotal(0);
		carritoDao.save(c);
		return c;
	}

	@Override
	public List<ItemCarrito> getItems(int idUsuario) {
		// TODO Auto-generated method stub
		return new ArrayList<>();
	}

	@Override
	public int gtCantidadItems(int idUsuadio) {
		Carrito c = carritoDao.findByUsuario(idUsuadio);
		List<ItemCarrito> items = c.getItems();
		return items.size();
	}

}
