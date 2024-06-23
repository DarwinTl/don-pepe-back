package com.tienda.services;

import java.util.List;

import com.tienda.entities.Orden;

public interface IVentaService {

	public List<Orden> findAllVentas();

	public String generarNumVenta();

	public Orden compra(int userId);

}
