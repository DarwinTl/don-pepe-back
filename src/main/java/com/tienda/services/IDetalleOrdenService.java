package com.tienda.services;

import java.util.List;

import com.tienda.entities.DetalleOrden;

public interface IDetalleOrdenService {

	public List<DetalleOrden> detalles(String numero);
}
