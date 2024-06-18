package com.tienda.controllers;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.tienda.entities.Categoria;
import com.tienda.entities.Comentario;
import com.tienda.entities.Medida;
import com.tienda.entities.Producto;
import com.tienda.entities.ProductoDTO;
import com.tienda.entities.Usuario;
import com.tienda.entities.listas.ListaMedidas;
import com.tienda.services.ICategoriaService;
import com.tienda.services.IComentarioService;
import com.tienda.services.IProductoService;
import com.tienda.services.IUsuarioService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "https://www.voiceflow.com/")
@RestController
@RequestMapping("/api/home")
public class HomeController {

	private IComentarioService comentarioService;

	private IUsuarioService usuarioService;

	private IProductoService productoService;

	private ICategoriaService categoriaService;

	public HomeController(IComentarioService comentarioService, IUsuarioService usuarioService,
			IProductoService productoService, ICategoriaService categoriaService) {
		this.comentarioService = comentarioService;
		this.usuarioService = usuarioService;
		this.productoService = productoService;
		this.categoriaService = categoriaService;
	}

	@GetMapping()
	public List<Producto> catalogo() {
		return productoService.getProductos();
	}

	@GetMapping("/producto/detalles/{id}")
	public ResponseEntity<?> find(@PathVariable int id) {
		Optional<Producto> producto = null;
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

	@GetMapping("/medidas")
	public List<Medida> getLista() {
		return new ListaMedidas().getListaMedidas();
	}

	@GetMapping("/medidas/{id}")
	public Medida getLista(@PathVariable int id) {
		return new ListaMedidas().find(id);
	}

	@GetMapping("/categorias")
	public List<Categoria> getCategorias() {
		return categoriaService.findAll();
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> cliente(@Valid @RequestBody Usuario usuario, BindingResult result) {
		Usuario usuarioNuevo = null;
		Map<String, Object> response = new HashMap<>();

		if (result.hasErrors()) {
			return validation(result);
		}

		try {

			usuarioNuevo = usuarioService.save(usuario);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Cuenta creada Correctamente");
		response.put("usuario", usuarioNuevo);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}

	@GetMapping("/users")
	public List<Usuario> getUsers() {
		return usuarioService.listar();
	}

	@GetMapping("/categorias/{categoria}")
	public ResponseEntity<?> categorizar(@PathVariable int categoria) {
		List<Producto> productos = productoService.getProductos(categoria);
		if (productos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No hay productos disponibles para la categoría especificada.");
		} else {
			return ResponseEntity.ok(productos);
		}
	}

	@GetMapping("/marcas/{marca}")
	public ResponseEntity<?> marcas(@PathVariable int marca) {
		List<Producto> productos = productoService.getProductosMarca(marca);
		if (productos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No hay productos disponibles para la marca especificada.");
		} else {
			return ResponseEntity.ok(productos);
		}
	}

	@GetMapping("/pagina")
	public Page<Producto> listar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "2") int num) {
		return productoService.findAll(PageRequest.of(page, num));
	}

	private ResponseEntity<?> validation(BindingResult result) {
		Map<String, Object> errors = new HashMap<>();

		result.getFieldErrors().forEach(error -> {
			errors.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errors);

	}

	@GetMapping("/buscar")
	public List<Producto> buscar(@RequestParam String texto) {

		return productoService.buscar(texto);
	}

	@GetMapping("/comentarios/{id}")
	public List<Comentario> getComentarios(@PathVariable int id) {
		return comentarioService.listarPorProducto(id);
	}

	@GetMapping("/names")
	public ResponseEntity<?> getProductNames() {

		return ResponseEntity.ok(productoService.getProductos());
	}

	@GetMapping("/dsd")
	public List<ProductoDTO> getP() {
		List<Producto> p = productoService.getProductos();
		List<ProductoDTO> pd = new ArrayList<>();

		for (Producto producto : p) {
			ProductoDTO productoDTO = new ProductoDTO();
			productoDTO.setId((long) producto.getId());
			productoDTO.setNombre(producto.getNombre());
			productoDTO.setStock(producto.getStock());
			productoDTO.setCategoria(producto.getCategoria().getNombre());
			productoDTO.setMarca(producto.getMarca().getNombre());
			productoDTO.setPrecio(producto.getPrecioVenta());
			pd.add(productoDTO);
		}

		return pd;
	}

	@GetMapping("/lista")
	public ResponseEntity<Map<String, Object>> obtenerProductos() {
		Map<String, Object> response = new HashMap<>();
		List<Producto> p = productoService.getProductos();
		List<ProductoDTO> pd = new ArrayList<>();
		for (Producto producto : p) {
			ProductoDTO productoDTO = new ProductoDTO();
			productoDTO.setId((long) producto.getId());
			productoDTO.setNombre(producto.getNombre());
			productoDTO.setStock(producto.getStock());
			productoDTO.setCategoria(producto.getCategoria().getNombre());
			productoDTO.setMarca(producto.getMarca().getNombre());
			productoDTO.setPrecio(producto.getPrecioVenta());
			productoDTO.setDescripcion(producto.getDescripcion());
			pd.add(productoDTO);
		}

		StringBuilder sb = new StringBuilder();
		for (ProductoDTO producto : pd) {
			sb.append("id: ").append(producto.getId()).append(", ").append("nombre: ").append(producto.getNombre())
					.append(", ").append("stock: ").append(producto.getStock()).append(", ").append("categoria: ")
					.append(producto.getCategoria()).append(", ").append("marca: ").append(producto.getMarca())
					.append(", ").append("precio: ").append(producto.getPrecio()).append(", ").append("descripcion: ")
					.append(producto.getDescripcion()).append("    ");
		}
		response.put("productos", sb.toString());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
