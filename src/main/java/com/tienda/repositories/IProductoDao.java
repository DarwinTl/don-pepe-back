package com.tienda.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tienda.entities.Producto;

@Repository
public interface IProductoDao extends JpaRepository<Producto, Integer> {

	@Query("select  p from Producto p where p.categoria.id=?1")
	List<Producto> listByCategoria(int categoria); 

}
