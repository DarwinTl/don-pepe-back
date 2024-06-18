package com.tienda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tienda.entities.ItemCarrito;

import jakarta.transaction.Transactional;

@Repository
public interface IItemCarritoDao extends JpaRepository<ItemCarrito, Integer> {

	@Modifying
	@Transactional
	@Query("DELETE FROM ItemCarrito ic WHERE ic.carrito.id = :cartId")
	void deleteAllByCarritoId(Long cartId);

}