package com.tienda.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tienda.entities.Producto;

public interface IProductoService {

	public List<Producto> getProductos();

	public Producto save(Producto producto);

	public Optional<Producto> findById(int id);

	public void deleteById(int id);

	public List<Producto> getProductos(int categoria);

	public List<Producto> getProductosMarca(int marca);

	public Page<Producto> findAll(Pageable pageable);

	public List<Producto> buscar(String texto);

	List<Producto> masComprados(int usuarioId);
}
