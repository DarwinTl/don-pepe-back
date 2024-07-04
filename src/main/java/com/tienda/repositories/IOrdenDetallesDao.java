package com.tienda.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tienda.entities.DetalleOrden;

@Repository
public interface IOrdenDetallesDao extends JpaRepository<DetalleOrden, Integer> {

	@Query("select d from DetalleOrden d where d.orden.numeroBoleta=?1")
	public List<DetalleOrden> detalles(String numero);

}
