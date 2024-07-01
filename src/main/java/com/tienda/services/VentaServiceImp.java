package com.tienda.services;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tienda.entities.Carrito;
import com.tienda.entities.DetalleOrden;
import com.tienda.entities.ItemCarrito;
import com.tienda.entities.Orden;
import com.tienda.entities.Usuario;
import com.tienda.mail.Email;
import com.tienda.mail.JavaMailSenderService;
import com.tienda.repositories.ICarritoDao;
import com.tienda.repositories.IItemCarritoDao;
import com.tienda.repositories.IOrdenDao;
import com.tienda.repositories.IOrdenDetallesDao;
import com.tienda.repositories.IUsuarioDao;
import com.tienda.repositories.IVentaDao;

import jakarta.mail.MessagingException;

@Service
public class VentaServiceImp implements IVentaService {

	private IUsuarioDao usuarioDao;

	private IVentaDao ventaDao;

	private IOrdenDao ordenDao;

	private ICarritoDao carritoDao;

	private IItemCarritoDao iItemCarritoDao;

	private IOrdenDetallesDao ordenDetallesDao;

	private JavaMailSenderService mailSender;

	public VentaServiceImp(IVentaDao ventaDao, ICarritoDao carritoDao, IItemCarritoDao iItemCarritoDao,
			IOrdenDao ordenDao, IOrdenDetallesDao ordenDetallesDao, IUsuarioDao usuarioDao,
			JavaMailSenderService mailSender) {
		this.ventaDao = ventaDao;
		this.carritoDao = carritoDao;
		this.iItemCarritoDao = iItemCarritoDao;
		this.ordenDao = ordenDao;
		this.mailSender = mailSender;
		this.ordenDetallesDao = ordenDetallesDao;
		this.usuarioDao = usuarioDao;
	}

	@Override
	public String generarNumVenta() {
		long num = 0;
		String numBoleta = "";

		List<Orden> ordenes = findAllVentas();
		List<Long> numerosBoleta = new ArrayList<>();

		ordenes.stream().forEach(o -> numerosBoleta.add(Long.valueOf(o.getId())));

		if (ordenes.isEmpty()) {
			num = 1;
		} else {
			num = numerosBoleta.stream().max(Long::compare).get();
			num++;
		}

		if (num < 10) {
			numBoleta = "VEN-000000" + String.valueOf(num);
		} else if (num < 100) {
			numBoleta = "VEN-00000" + String.valueOf(num);
		} else if (num < 1000) {
			numBoleta = "VEN-0000" + String.valueOf(num);
		} else if (num < 10000) {
			numBoleta = "VEN-000" + String.valueOf(num);
		} else if (num < 100000) {
			numBoleta = "VEN-00" + String.valueOf(num);
		}

		return numBoleta;
	}

	@Override
	public List<Orden> findAllVentas() {
		return ventaDao.findAll();
	}

	@Override
	public Orden compra(int userId) throws MessagingException {
		Map<String, Object> datos = new HashMap<>();

		String num = generarNumVenta();
		Carrito c = carritoDao.findByUsuario(userId);
		Usuario u = usuarioDao.findById(userId).get();

		if (c.getItems().isEmpty()) {
			return null;
		}

		Orden orden = new Orden();
		List<DetalleOrden> detalles = new ArrayList<>();

		orden.setTotal(c.getTotal());
		orden.setNumeroBoleta(num);
		orden.setUsuario(u);
		orden.setEstado("Corroborando pago");
		// estado
		String est = orden.getEstado();
		DecimalFormat df = new DecimalFormat("#.00");
		orden.setIgv(Double.parseDouble(df.format(orden.getIgv())));
		orden = ordenDao.save(orden);
		for (ItemCarrito itemCarrito : c.getItems()) {
			DetalleOrden dt = new DetalleOrden();
			dt.setCantidad(itemCarrito.getCantidad());
			dt.setImporte(itemCarrito.getImporte());
			dt.setProducto(itemCarrito.getProducto());
			dt.setOrden(orden);
			detalles.add(dt);
		}
		Usuario usu = usuarioDao.findByCorreo(u.getCorreo()).get();
		// variables del correo
		datos.put("nombre", usu.getNombres());
		datos.put("numero", num);
		datos.put("estado", est);
		ordenDetallesDao.saveAll(detalles);
		// para enviar el correo
		Email mail = new Email();
		mail.setTo(u.getCorreo());
		mail.setSubject("Orden de Compra");
		mail.setBody("Tu orden nº " + num + " ha sido recibida");
		mailSender.enviarCorreo(mail, datos);

		c.setItems(new ArrayList<>());
		iItemCarritoDao.deleteAllByCarritoId((long) c.getId());
		c.setTotal(0);
		carritoDao.save(c);

		return orden;
	}

}
