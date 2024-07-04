package com.tienda.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tienda.entities.Orden;
import com.tienda.mail.Email;
import com.tienda.mail.JavaMailSenderService;
import com.tienda.repositories.IOrdenDao;

import jakarta.mail.MessagingException;

@Service
public class OrdenServiceImp implements IOrdenService {

	private IOrdenDao ordenDao;
	private JavaMailSenderService mailSender;

	public OrdenServiceImp(IOrdenDao ordenDao, JavaMailSenderService mailSender) {
		this.ordenDao = ordenDao;
		this.mailSender = mailSender;
	}

	@Override
	public List<Orden> listar() {
		return ordenDao.findAll();
	}

	@Override
	public Orden findByNumero(String numero) {
		return ordenDao.findByNumero(numero);
	}

	@Override
	public List<Orden> listByEstado(String estado) {
		return ordenDao.listByEstado(estado);
	}

	@Override
	public Orden findById(int id) {
		return ordenDao.findById(id).get();
	}

	@Override
	public Orden listaEntregar(String numero) throws MessagingException {

		Orden o = ordenDao.findByNumero(numero);
		o.setEstado("lista para entregar");
		ordenDao.save(o);
		Map<String, Object> datos = new HashMap<>();

		Email mail = new Email();
		// variables del correo
		datos.put("nombre", o.getUsuario().getNombres());
		datos.put("numero", o.getNumeroBoleta());
		datos.put("estado", o.getEstado());
		datos.put("lineas", o.getDetalles());
		datos.put("sub", String.format("%.2f", o.getSubTotal()));
		datos.put("igv", String.format("%.2f", o.getIgv()));
		datos.put("tot", String.format("%.2f", o.getTotal()));
		mail.setTo(o.getUsuario().getCorreo());
		mail.setSubject("Tu pedido " + o.getNumeroBoleta() + " se encuentra listo para recoger ");
		mail.setBody("");
		mailSender.enviarCorreo(mail, datos);
		return o;

	}

	@Override
	public Orden aprobarPago(String numero) throws MessagingException {
		Orden o = ordenDao.findByNumero(numero);
		o.setEstado("confirmado");
		ordenDao.save(o);
		Map<String, Object> datos = new HashMap<>();

		Email mail = new Email();
		// variables del correo
		datos.put("nombre", o.getUsuario().getNombres());
		datos.put("numero", o.getNumeroBoleta());
		datos.put("estado", o.getEstado());
		datos.put("lineas", o.getDetalles());
		datos.put("sub", String.format("%.2f", o.getSubTotal()));
		datos.put("igv", String.format("%.2f", o.getIgv()));
		datos.put("tot", String.format("%.2f", o.getTotal()));
		mail.setTo(o.getUsuario().getCorreo());
		mail.setSubject("El pago de tu orden nº " + o.getNumeroBoleta() + " ha sido confirmado");
		mail.setBody("");
		mailSender.enviarCorreo(mail, datos);
		return o;
	}

	@Override
	public Orden entregar(String numero) throws MessagingException {
		Orden o = ordenDao.findByNumero(numero);
		o.setEstado("entregada");
		ordenDao.save(o);
		Map<String, Object> datos = new HashMap<>();

		Email mail = new Email();
		// variables del correo
		datos.put("nombre", o.getUsuario().getNombres());
		datos.put("numero", o.getNumeroBoleta());
		datos.put("estado", o.getEstado());
		datos.put("lineas", o.getDetalles());
		datos.put("sub", String.format("%.2f", o.getSubTotal()));
		datos.put("igv", String.format("%.2f", o.getIgv()));
		datos.put("tot", String.format("%.2f", o.getTotal()));
		mail.setTo(o.getUsuario().getCorreo());
		mail.setSubject("Tu orden nº " + o.getNumeroBoleta() + " ha sido entregada");
		mail.setBody("");
		mailSender.enviarCorreo(mail, datos);
		return o;
	}

	@Override
	public List<Orden> findByDni(String dni) {
		return ordenDao.findByDni(dni);
	}

}
