package com.tienda.entities;

public class ProductoDTO {

	private Long id;
	private String nombre;
	private int stock;
	private String categoria;
	private String marca;
	private double precio;
	private String descripcion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public ProductoDTO(Long id, String nombre, int stock, String categoria, String marca, double precio,
			String descripcion) {
		this.id = id;
		this.nombre = nombre;
		this.stock = stock;
		this.categoria = categoria;
		this.marca = marca;
		this.precio = precio;
		this.descripcion = descripcion;
	}

	public ProductoDTO() {
	}

}
