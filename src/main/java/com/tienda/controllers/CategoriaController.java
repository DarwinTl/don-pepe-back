package com.tienda.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

import com.tienda.entities.Categoria;
import com.tienda.services.ICategoriaService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/mantenimiento/categorias")
public class CategoriaController {

	private ICategoriaService categoriaService;

	public CategoriaController(ICategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping()
	public List<Categoria> getCategorias() {
		return categoriaService.findAll();
	}

	@GetMapping("/pagina")
	public Page<Categoria> listar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "2") int num) {
		return categoriaService.findAll(PageRequest.of(page, num));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> find(@PathVariable int id) {
		Optional<Categoria> categoria;
		Map<String, Object> response = new HashMap<>();
		try {
			categoria = categoriaService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!categoria.isPresent()) {
			response.put("mensaje", String.format("La categoria con el el ID %s no existe", id));
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(categoria.get(), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody Categoria categoria, BindingResult result) {
		Categoria categoriaNueva = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			return validation(result);
		}

		try {
			categoriaNueva = categoriaService.save(categoria);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Categoría creada con exito");
		response.put("categoria", categoriaNueva);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody Categoria categoria, BindingResult result,
			@PathVariable int id) {

		Optional<Categoria> optionalActual = categoriaService.findById(id);
		Map<String, Object> response = new HashMap<>();
		Categoria categoriaNueva = null;
		Categoria categoriaActual = null;

		if (result.hasErrors()) {
			return validation(result);
		}

		if (!optionalActual.isPresent()) {
			response.put("mensaje", "La categoria con ID " + id + " no existe en la BD");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}
		try {
			categoriaActual = optionalActual.get();
			categoriaActual.setDetalle(categoria.getDetalle());
			categoriaNueva = categoriaService.save(categoriaActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Categoria actualizada con exito");
		response.put("categoria", categoriaNueva);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, Object>> delete(@PathVariable int id) {
		Optional<Categoria> categoria = categoriaService.findById(id);
		Map<String, Object> response = new HashMap<>();

		if (!categoria.isPresent()) {
			response.put("mensaje", "La categoria con ID " + id + " no existe en la BD");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		try {
			categoriaService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Categoria eliminada");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private ResponseEntity<Map<String, Object>> validation(BindingResult result) {
		Map<String, Object> errors = new HashMap<>();

		result.getFieldErrors().forEach(error -> errors.put(error.getField(),
				"El campo " + error.getField() + " " + error.getDefaultMessage()));
		return ResponseEntity.badRequest().body(errors);

	}

}
