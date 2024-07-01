package com.tienda.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tienda.entities.Producto;
import com.tienda.repositories.IProductoDao;

@Service
public class ProductoServiceImp implements IProductoService {

	private IProductoDao productoDao;

	public ProductoServiceImp(IProductoDao productoDao) {
		this.productoDao = productoDao;
	}

	@Override
	public List<Producto> getProductos() {
		return productoDao.findAll();
	}

	@Override
	public Producto save(Producto producto) {
		return productoDao.save(producto);
	}

	@Override
	public Optional<Producto> findById(int id) {
		return productoDao.findById(id);
	}

	@Override
	public void deleteById(int id) {
		productoDao.deleteById(id);

	}

	@Override
	public List<Producto> getProductos(int categoria) {
		return productoDao.listByCategoria(categoria);
	}

	@Override
	public Page<Producto> findAll(Pageable pageable) {
		return productoDao.findAll(pageable);
	}

	@Override
	public List<Producto> getProductosMarca(int marca) {
		return productoDao.listByMarca(marca);
	}

	@Override
	public List<Producto> buscar(String texto) {
		return productoDao.buscar(texto);
	}

	@Override
	public List<Producto> masComprados(int usuarioId) {
		List<Integer> ids = productoDao.getQuantityByProduct(usuarioId);
		return productoDao.findProductsByIds(ids);
	}

}
