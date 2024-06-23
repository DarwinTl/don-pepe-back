package com.tienda.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tienda.entities.Producto;

@Repository
public interface IProductoDao extends JpaRepository<Producto, Integer> {

	@Query("select  p from Producto p where p.categoria.id=?1")
	List<Producto> listByCategoria(int categoria);

	@Query("select  p from Producto p where p.marca.id=?1")
	List<Producto> listByMarca(int marca);

	@Query("select  p from Producto p where p.nombre like %:texto%")
	List<Producto> buscar(@Param("texto") String texto);

	@Query("SELECT  SUM(d.cantidad) AS cantidad_por_producto, d.producto.id, p.nombre " + "FROM DetalleOrden d "
			+ "JOIN d.orden o " + "JOIN d.producto p " + "WHERE o.usuario.id = :usuarioId " + "GROUP BY d.producto.id "
			+ "ORDER BY cantidad_por_producto DESC")
	List<Object[]> getQuantityByProduct(@Param("usuarioId") int id);
}
