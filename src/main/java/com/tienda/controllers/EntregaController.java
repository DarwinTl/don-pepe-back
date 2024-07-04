package com.tienda.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.entities.DetalleOrden;
import com.tienda.entities.Orden;
import com.tienda.services.IDetalleOrdenService;
import com.tienda.services.IOrdenService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/ordenes")
public class EntregaController {

	private IOrdenService ordenService;
	private IDetalleOrdenService detalleOrdenService;

	public EntregaController(IOrdenService ordenService, IDetalleOrdenService detalleOrdenService) {
		this.ordenService = ordenService;
		this.detalleOrdenService = detalleOrdenService;
	}

	@GetMapping()
	public List<Orden> getAll() {
		return ordenService.listar();
	}

	@GetMapping("/{num}")
	public ResponseEntity<Object> getAlldsad(@PathVariable String num) {
		Orden o = ordenService.findByNumero(num);
		if (o != null) {
			return ResponseEntity.ok(o);
		}
		return ResponseEntity.badRequest().body("No hay");
	}

	@GetMapping("/estado")
	public List<Orden> getByEstado(@RequestParam String estado) {
		return ordenService.listByEstado(estado);
	}

	@PutMapping("/preparar")
	public ResponseEntity<Object> listaEntregar(@RequestParam String numero) throws MessagingException {
		Orden o = ordenService.findByNumero(numero);
		if (o != null && o.getEstado().equals("confirmado")) {
			ordenService.listaEntregar(o.getNumeroBoleta());
			return ResponseEntity.ok(o);
		}
		if (o != null && !o.getEstado().equals("confirmado")) {
			return ResponseEntity.badRequest().body("El pago aun no ha sido confirmado");
		}
		return ResponseEntity.badRequest().body("El número de orden no existe");
	}

	@PutMapping("/aprobar")
	public ResponseEntity<Object> aprobar(@RequestParam String numero) throws MessagingException {
		Orden o = ordenService.findByNumero(numero);
		if (o != null) {
			ordenService.aprobarPago(o.getNumeroBoleta());
			return new ResponseEntity<>(o, HttpStatus.OK);
		}
		return ResponseEntity.badRequest().body("El número de orden no existe");
	}

	@PutMapping("/recoger")
	public ResponseEntity<Object> recoger(@RequestParam String numero) throws MessagingException {
		Orden o = ordenService.findByNumero(numero);
		if (o != null) {
			ordenService.entregar(o.getNumeroBoleta());
			return new ResponseEntity<>(o, HttpStatus.OK);
		}
		return ResponseEntity.badRequest().body("El número de orden no existe");
	}

	@GetMapping("/detalles/{numero}")
	public List<DetalleOrden> detalles(@PathVariable String numero) {

		return detalleOrdenService.detalles(numero);
	}

	@GetMapping("/buscar/{dni}")
	public ResponseEntity<Object> find(@PathVariable String dni) {
		return ResponseEntity.ok(ordenService.findByDni(dni));
	}

}
