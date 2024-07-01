package com.tienda.services;

import java.util.List;

import com.tienda.entities.Orden;

import jakarta.mail.MessagingException;

public interface IOrdenService {

	public List<Orden> listar();

	public Orden findById(int id);

	public Orden findByNumero(String numero);

	public List<Orden> listByEstado(String estado);

	public void aprobarPago(String numero) throws MessagingException;
	
	public void listaEntregar(String numero);
}
