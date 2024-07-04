package com.tienda.services;

import java.util.List;

import com.tienda.entities.Orden;

import jakarta.mail.MessagingException;

public interface IOrdenService {

	public List<Orden> listar();

	public Orden findById(int id);

	public Orden findByNumero(String numero);

	public List<Orden> listByEstado(String estado);

	public Orden listaEntregar(String numero) throws MessagingException;

	public Orden aprobarPago(String numero) throws MessagingException;

	public Orden entregar(String numero) throws MessagingException;

	public List<Orden> findByDni(String dni);

}
