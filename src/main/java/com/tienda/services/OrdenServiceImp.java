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
	public void aprobarPago(String numero) throws MessagingException {
		Orden o = ordenDao.findByNumero(numero);
		Map<String, Object> datos = new HashMap<>();

		Email mail = new Email();
		// variables del correo
		datos.put("nombre", o.getUsuario().getNombres());
		datos.put("numero", o.getNumeroBoleta());
		datos.put("estado", o.getEstado());

		mail.setTo(o.getUsuario().getCorreo());
		mail.setSubject("El pago de tu orden nº " + o.getNumeroBoleta() + " ha sido confirmado");
		mail.setBody("");
		mailSender.enviarCorreo(mail, datos);
		ordenDao.aprobarPago(numero);
	}

	@Override
	public Orden findById(int id) {
		return ordenDao.findById(id).get();
	}

	@Override
	public void listaEntregar(String numero) {
		ordenDao.listaEntregar(numero);

	}

}
