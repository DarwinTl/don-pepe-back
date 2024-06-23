package com.tienda.controllers;

import java.io.IOException;
import java.net.MalformedURLException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.multipart.MultipartFile;

import com.tienda.entities.Producto;
import com.tienda.services.IProductoService;
import com.tienda.services.UploadFileService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/mantenimiento/productos")
public class ProductoController {

	private IProductoService productoService;

	private UploadFileService uploadFileService;

	public ProductoController(IProductoService productoService, UploadFileService uploadFileService) {
		this.productoService = productoService;
		this.uploadFileService = uploadFileService;
	}

	@GetMapping()
	public List<Producto> findAll() {
		return productoService.getProductos();
	}

	@GetMapping("/categorias/{categoria}")
	public List<Producto> findAll(@PathVariable int categoria) {
		return productoService.getProductos(categoria);
	}

	@GetMapping("/pagina")
	public Page<Producto> listar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "2") int num) {
		return productoService.findAll(PageRequest.of(page, num));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> find(@PathVariable int id) {
		Optional<Producto> producto;
		Map<String, Object> response = new HashMap<>();

		try {
			producto = productoService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!producto.isPresent()) {
			response.put("mensaje", "El producto con ID " + id + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Producto>(producto.get(), HttpStatus.OK);
	}

	// <!-- Este codigo puede servir
	@PostMapping("/productos-foto")
	public ResponseEntity<?> cargar(@RequestParam("foto") MultipartFile foto, @RequestParam("id") int id)
			throws IOException {

		String archivo = null;
		Map<String, Object> response = new HashMap<>();

		Producto producto = productoService.findById(id).get();

		if (foto.isEmpty()) {
			archivo = uploadFileService.guardarImagen(foto);
			producto.setRuta(archivo);
			productoService.save(producto);
			response.put("producto", producto);
			response.put("mensaje", "Imagen por defecto subida");
		}

		if (!foto.isEmpty()) {

			if (!producto.getRuta().equals("default.jpg")) {
				archivo = uploadFileService.guardarImagen(foto);
				producto.setRuta(archivo);
				productoService.save(producto);
				response.put("producto", producto);
				response.put("mensaje", "Imagen cambiada sin borrar default");
			} else {
				uploadFileService.elimarImagen(producto.getNombre());
				archivo = uploadFileService.guardarImagen(foto);
				producto.setRuta(archivo);
				productoService.save(producto);
				response.put("producto", producto);
				response.put("mensaje", "Imagen subida");
			}
		}

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	// -->

	@PostMapping()
	public ResponseEntity<?> save(Producto producto, @RequestParam(name = "foto") MultipartFile foto)
			throws IOException {
		Producto productoNuevo = null;
		Map<String, Object> response = new HashMap<>();

		try {
			String nombreImagen = uploadFileService.guardarImagen(foto);
			producto.setRuta(nombreImagen);
			producto.setInventoryStatus("En Stock");
			productoNuevo = productoService.save(producto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto registrado");

		response.put("producto", productoNuevo);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// Para enviar un producto sin foto
	// <--inicio
	@PostMapping("/productos")
	public ResponseEntity<?> save(@RequestBody Producto producto) {
		Producto productoNuevo = null;
		Map<String, Object> response = new HashMap<>();

		try {
			producto.setRuta("default.jpg");
			productoNuevo = productoService.save(producto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto registrado");
		response.put("producto", productoNuevo);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	// fin-->

	@PutMapping("/{id}")
	public ResponseEntity<?> update(Producto producto, @RequestParam(name = "foto") MultipartFile foto)
			throws IOException {
		Optional<Producto> optionalActual = productoService.findById(producto.getId());
		Map<String, Object> response = new HashMap<>();
		Producto productoActualizado = null;
		Producto productoActual = null;

		if (!optionalActual.isPresent()) {
			response.put("mensaje", "El producto con ID " + producto.getId() + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			productoActual = optionalActual.get();

			if (!foto.isEmpty()) {
				String archivo = uploadFileService.guardarImagen(foto);
				producto.setRuta(archivo);
				productoService.save(producto);
				response.put("producto", producto);
				response.put("mensaje", "Imagen subida");
			}
			productoActual = optionalActual.get();
			productoActual.setCategoria(producto.getCategoria());
			productoActual.setDescripcion(producto.getDescripcion());
			productoActual.setEstado(producto.getEstado());
			productoActual.setMarca(producto.getMarca());
			productoActual.setMedida(producto.getMedida());
			productoActual.setNombre(producto.getNombre());
			productoActual.setRuta(producto.getRuta());

			productoActualizado = productoService.save(productoActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto Modificado");
		response.put("producto", productoActualizado);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	// para editar solo el producto sin foto
	@PutMapping("/productos/{id}")
	public ResponseEntity<?> update2(@RequestBody Producto producto, @PathVariable int id) {
		Optional<Producto> optionalActual = productoService.findById(id);
		Map<String, Object> response = new HashMap<>();
		Producto productoActualizado = null;
		Producto productoActual = null;

		if (!optionalActual.isPresent()) {
			response.put("mensaje", "El producto con ID " + id + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			productoActual = optionalActual.get();

			productoActual = optionalActual.get();
			productoActual.setCategoria(producto.getCategoria());
			productoActual.setDescripcion(producto.getDescripcion());
			productoActual.setEstado(producto.getEstado());
			productoActual.setMarca(producto.getMarca());
			productoActual.setMedida(producto.getMedida());
			productoActual.setNombre(producto.getNombre());
			productoActual.setRuta(producto.getRuta());

			productoActualizado = productoService.save(productoActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "Producto Modificado");
		response.put("producto", productoActualizado);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable int id) {
		Producto producto = productoService.findById(id).get();
		Map<String, Object> response = new HashMap<>();

		if (producto == null) {
			response.put("mensaje", "El producto con ID " + id + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			productoService.deleteById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Producto borrado");
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping("/img/{foto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String foto) {
		Path ruta = Paths.get("images").resolve(foto).toAbsolutePath();
		Resource recurso = null;

		try {
			recurso = new UrlResource(ruta.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment ; filename=\"" + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}

	@PutMapping("/add-stock/{id}")
	public ResponseEntity<Map<String, Object>> actualizarStock(@PathVariable int id, @RequestParam int cantidad) {
		Optional<Producto> optionalProducto = productoService.findById(id);
		Map<String, Object> response = new HashMap<>();
		Producto p = null;

		if (!optionalProducto.isPresent()) {
			response.put("mensaje", "El producto con ID " + id + " no existe en la BD");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {

			p = optionalProducto.get();
			p.setStock(p.getStock() + cantidad);
			productoService.save(p);
		} catch (DataAccessException e) {
		}
		response.put("mensaje", "Stock actualizado");
		response.put("producto", p);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}

}
