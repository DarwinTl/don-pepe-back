package com.tienda.services;

import java.util.List;

import com.tienda.entities.Orden;

import jakarta.mail.MessagingException;

public interface IVentaService {

	public List<Orden> findAllVentas();

	public String generarNumVenta();

	public Orden compra(int userId) throws MessagingException;

}
