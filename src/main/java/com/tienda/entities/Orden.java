package com.tienda.entities;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity()
@Table(name = "ordenes")
public class Orden {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Temporal(TemporalType.DATE)
	private Date fechaVenta;

	private String estado;
	@Temporal(TemporalType.TIME)
	private Date horaVenta;

	private String numeroBoleta;

	private double subTotal;

	private double igv;

	private double descuento;

	private double total;

	private int tipoVenta;

	private int tipoDocumento;

	@OneToMany(mappedBy = "orden")
	@JsonManagedReference
	private List<DetalleOrden> detalles;

	@ManyToOne
	private Usuario usuario;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFechaVenta() {
		return fechaVenta;
	}

	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}

	public Date getHoraVenta() {
		return horaVenta;
	}

	public void setHoraVenta(Date horaVenta) {
		this.horaVenta = horaVenta;
	}

	public String getNumeroBoleta() {
		return numeroBoleta;
	}

	public void setNumeroBoleta(String numeroBoleta) {
		this.numeroBoleta = numeroBoleta;
	}

	public double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}

	public double getIgv() {
		return igv;
	}

	public void setIgv(double igv) {
		this.igv = igv;
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getTipoVenta() {
		return tipoVenta;
	}

	public void setTipoVenta(int tipoVenta) {
		this.tipoVenta = tipoVenta;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
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

	public List<DetalleOrden> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleOrden> detalles) {
		this.detalles = detalles;
	}

	public Orden() {

	}

	@PrePersist
	public void onCreate() {
		this.fechaVenta = new Date();
		this.horaVenta = new Date();

		igv = 0.18 * total;
		subTotal = total - igv;
		descuento = 0;

	}

	public Orden(int id, Date fechaVenta, String estado, Date horaVenta, String numeroBoleta, double subTotal,
			double igv, double descuento, double total, int tipoVenta, int tipoDocumento, List<DetalleOrden> detalles,
			Usuario usuario) {
		this.id = id;
		this.fechaVenta = fechaVenta;
		this.estado = estado;
		this.horaVenta = horaVenta;
		this.numeroBoleta = numeroBoleta;
		this.subTotal = subTotal;
		this.igv = igv;
		this.descuento = descuento;
		this.total = total;
		this.tipoVenta = tipoVenta;
		this.tipoDocumento = tipoDocumento;
		this.detalles = detalles;
		this.usuario = usuario;
	}

}
