package com.tienda.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "carritos")
public class Carrito {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@OneToOne
	private Usuario usuario;

	@OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<ItemCarrito> items;

	private String estado;

	private double total;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<ItemCarrito> getItems() {
		return items;
	}

	public void setItems(List<ItemCarrito> items) {
		this.items = items;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public Carrito() {
	}

	public Carrito(int id, Usuario usuario, List<ItemCarrito> items, String estado, double total) {
		this.id = id;
		this.usuario = usuario;
		this.items = items;
		this.estado = estado;
		this.total = total;
	}

}
