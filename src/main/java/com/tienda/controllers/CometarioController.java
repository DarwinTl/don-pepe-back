package com.tienda.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.entities.Comentario;
import com.tienda.services.IComentarioService;

@CrossOrigin(origins = "http://localhost:4200")

@RestController
@RequestMapping("/api/usuario")
public class CometarioController {

	private IComentarioService comentarioService;

	public CometarioController(IComentarioService comentarioService) {
		this.comentarioService = comentarioService;
	}

	@PostMapping("/{idProducto}/comentario/{correo}")
	public ResponseEntity<Comentario> comentar(@PathVariable int idProducto, @PathVariable String correo,
			@RequestBody Comentario comentario) {

		return ResponseEntity.ok(comentarioService.comentar(idProducto, correo, comentario));
	}

}
