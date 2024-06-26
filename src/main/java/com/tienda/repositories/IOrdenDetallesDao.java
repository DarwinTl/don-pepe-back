package com.tienda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.entities.DetalleOrden;

@Repository
public interface IOrdenDetallesDao extends JpaRepository<DetalleOrden, Integer> {

}
