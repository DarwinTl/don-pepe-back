package com.tienda.controllers;

import java.util.Optional;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tienda.entities.Carrito;
import com.tienda.entities.Usuario;
import com.tienda.repositories.IUsuarioDao;
import com.tienda.services.ICarritoService;
import com.tienda.services.IVentaService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/home")
public class CompraController {

	private IVentaService ventaService;

	private ICarritoService carritoService;

	private IUsuarioDao usuarioDao;

	public CompraController(IVentaService ventaService, ICarritoService carritoService, IUsuarioDao usuarioDao) {
		this.ventaService = ventaService;
		this.carritoService = carritoService;
		this.usuarioDao = usuarioDao;
	}

	@GetMapping("/getNum")
	public ResponseEntity<String> getNum() {
		return ResponseEntity.ok(ventaService.generarNumVenta());
	}

	@GetMapping("/carrito/{id}")
	public ResponseEntity<Carrito> getCarrito(@PathVariable int id) {
		Carrito c = carritoService.getCarritoByUsuario(id);
		return ResponseEntity.ok(c);
	}

	@PostMapping("/add/producto/{idProducto}")
	public ResponseEntity<Object> agregarItem(@RequestParam int userId, @PathVariable int idProducto,
			@RequestParam int cantidad) {
		Optional<Usuario> u = usuarioDao.findById(userId);
		if (u.isEmpty()) {
			return ResponseEntity.badRequest().body("Inicia sesion");
		}

		return ResponseEntity.ok(carritoService.agregarItem(u.get().getId(), idProducto, cantidad));
	}

	@GetMapping("/cantidad")
	public ResponseEntity<Integer> getCantidadItems(@RequestParam int id) {

		return ResponseEntity.ok(carritoService.gtCantidadItems(id));
	}

	@DeleteMapping("/remove/{codProducto}")
	public ResponseEntity<Carrito> eliminarLinea(@RequestParam int idUsuario, @PathVariable int codProducto) {
		return ResponseEntity.ok(carritoService.quitarItem(idUsuario, codProducto));
	}

	@GetMapping("/vaciar")
	public ResponseEntity<Carrito> vaciar(@RequestParam int idUsuario) {

		return ResponseEntity.ok(carritoService.vaciarCarrito(usuarioDao.findById(idUsuario).get().getId()));
	}
}
