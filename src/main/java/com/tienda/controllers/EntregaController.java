package com.tienda.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.entities.Orden;
import com.tienda.services.IOrdenService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/ordenes")
public class EntregaController {

	private IOrdenService ordenService;

	public EntregaController(IOrdenService ordenService) {
		this.ordenService = ordenService;
	}

	@GetMapping()
	public List<Orden> getAll() {
		return ordenService.listar();
	}

	@GetMapping("/estado")
	public List<Orden> getByEstado(@RequestParam String estado) {
		return ordenService.listByEstado(estado);
	}

	@PutMapping("/aprobar")
	public ResponseEntity<Object> aprobarPago(@RequestParam String numero) throws MessagingException {
		Orden o = ordenService.findByNumero(numero);
		if (o != null) {
			ordenService.aprobarPago(o.getNumeroBoleta());
			return new ResponseEntity<>("Orden aprobada para empaquetar", HttpStatus.OK);
		}
		return ResponseEntity.badRequest().body("El número de orden no existe");
	}

	@PutMapping("/preparar")
	public ResponseEntity<Object> listaEntregar(@RequestParam String numero) {
		Orden o = ordenService.findByNumero(numero);
		if (o != null && o.getEstado().equals("confirmado")) {
			ordenService.listaEntregar(o.getNumeroBoleta());
			return new ResponseEntity<>("Orden lista para entregar", HttpStatus.OK);
		}
		if (o != null && !o.getEstado().equals("confirmado")) {
			return ResponseEntity.badRequest().body("El pago aun no ha sido confirmado");
		}
		return ResponseEntity.badRequest().body("El número de orden no existe");
	}
}
