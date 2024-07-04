package com.tienda.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tienda.entities.Orden;

import jakarta.transaction.Transactional;

@Repository
public interface IOrdenDao extends JpaRepository<Orden, Integer> {

	@Query("select o from Orden o where o.estado=?1")
	public List<Orden> listByEstado(String estado);

	@Query("select o from Orden o where o.numeroBoleta=:numeroBoleta")
	public Orden findByNumero(String numeroBoleta);

	@Modifying
	@Transactional
	@Query("UPDATE Orden o set o.estado='confirmado' where o.numeroBoleta=?1")
	public void aprobarPago(String numero);

	@Modifying
	@Transactional
	@Query("UPDATE Orden o set o.estado='lista para entregar' where o.numeroBoleta=?1")
	public void listaEntregar(String numero);

	@Query("select o from Orden o where o.usuario.numeroDocumento=?1 and o.estado='lista para entregar'")
	public List<Orden> findByDni(String dni);

}
