package com.tienda.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tienda.entities.Orden;

@Repository
public interface IOrdenDao extends JpaRepository<Orden, Integer> {

}
