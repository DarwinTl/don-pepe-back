package com.tienda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.entities.Marca;

@Repository
public interface IMarcaDao extends JpaRepository<Marca, Integer> {

}
