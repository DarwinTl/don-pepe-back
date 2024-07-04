package com.tienda.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tienda.entities.DetalleOrden;
import com.tienda.repositories.IOrdenDetallesDao;

@Service
public class DetalleOrdenServiceImp implements IDetalleOrdenService {

	private IOrdenDetallesDao ordenDetallesDao;

	public DetalleOrdenServiceImp(IOrdenDetallesDao ordenDetallesDao) {
		this.ordenDetallesDao = ordenDetallesDao;
	}

	@Override
	public List<DetalleOrden> detalles(String numero) {
		return ordenDetallesDao.detalles(numero);
	}

}
