package com.tienda.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.entities.UnidadMedida;
import com.tienda.services.IUnidadMedidaService;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/mantenimiento/unidades")
public class UnidadMedidaController {

	@Autowired
	private IUnidadMedidaService unidadMedidaService;

	@GetMapping()
	public List<UnidadMedida> getUnidades() {
		return unidadMedidaService.getUnidadesMedida();
	}
	
	@GetMapping("/pagina")
	public Page<UnidadMedida> listar(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "2") int num) {
		return unidadMedidaService.findAll(PageRequest.of(page, num));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> find(@PathVariable int id) {
		Optional<UnidadMedida> medida = null;
		Map<String, Object> response = new HashMap<>();

		try {
			medida = unidadMedidaService.findUnidadMedida(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!medida.isPresent()) {
			response.put("mensaje", "La Unidad de medidad con ID " + id + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<UnidadMedida>(medida.get(), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<Map<String, Object>> save(@RequestBody UnidadMedida medida) {
		UnidadMedida medidaNueva = null;
		Map<String, Object> response = new HashMap<>();

		try {
			medidaNueva = unidadMedidaService.save(medida);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Unidadad de medida creada con exito");
		response.put("unidad", medidaNueva);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestBody UnidadMedida medida, @PathVariable int id) {

		UnidadMedida medidaActual = unidadMedidaService.findUnidadMedida(id).get();
		Map<String, Object> response = new HashMap<>();
		UnidadMedida medidaActualizada = null;

		if (medidaActual == null) {
			response.put("mensaje", "La Unidad de medidad con ID " + id + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			medidaActual.setDescripcion(medida.getDescripcion());
			medidaActualizada = unidadMedidaService.save(medidaActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Unidadad de medida creada con exito");
		response.put("unidad", medidaActualizada);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable int id) {
		UnidadMedida medida = unidadMedidaService.findUnidadMedida(id).get();
		Map<String, Object> response = new HashMap<>();

		if (medida == null) {
			response.put("mensaje", "La categoria con ID " + id + " no existe en la BD");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			unidadMedidaService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Unidad de medida eliminada");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
