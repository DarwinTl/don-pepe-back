package com.tienda.entities;

public class Medida {

	private int id;

	private String descripcion;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Medida() {
	}

	public Medida(int id, String descripcion) {
		this.id = id;
		this.descripcion = descripcion;
	}

}
