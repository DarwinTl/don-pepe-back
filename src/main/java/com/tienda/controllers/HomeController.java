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
import com.tienda.mail.Email;
import com.tienda.mail.JavaMailSenderService;
import com.tienda.services.ICategoriaService;
import com.tienda.services.IComentarioService;
import com.tienda.services.IProductoService;
import com.tienda.services.IUsuarioService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@CrossOrigin(origins = "https://www.voiceflow.com/")
@RestController
@RequestMapping("/api/home")
public class HomeController {

	private IComentarioService comentarioService;

	private IUsuarioService usuarioService;

	private IProductoService productoService;

	private ICategoriaService categoriaService;

	private JavaMailSenderService mailService;

	public HomeController(IComentarioService comentarioService, IUsuarioService usuarioService,
			IProductoService productoService, ICategoriaService categoriaService, JavaMailSenderService mailService) {
		this.comentarioService = comentarioService;
		this.usuarioService = usuarioService;
		this.productoService = productoService;
		this.categoriaService = categoriaService;
		this.mailService = mailService;
	}

	@GetMapping()
	public List<Producto> catalogo() {
		return productoService.getProductos();
	}

	@GetMapping("/producto/detalles/{id}")
	public ResponseEntity<Object> find(@PathVariable int id) {
		Optional<Producto> producto;
		Map<String, Object> response = new HashMap<>();

		try {
			producto = productoService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar consulta en la BD");
			response.put("error", e.getMessage() + " " + e.getMostSpecificCause().getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!producto.isPresent()) {
			response.put("mensaje", "El producto con ID " + id + " no existe en la BD");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(producto.get(), HttpStatus.OK);
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
	public ResponseEntity<Map<String, Object>> cliente(@Valid @RequestBody Usuario usuario, BindingResult result) {
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
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Cuenta creada Correctamente");
		response.put("usuario", usuarioNuevo);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/users")
	public List<Usuario> getUsers() {
		return usuarioService.listar();
	}

	@GetMapping("/categorias/{categoria}")
	public ResponseEntity<Object> categorizar(@PathVariable int categoria) {
		List<Producto> productos = productoService.getProductos(categoria);
		if (productos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No hay productos disponibles para la categoría especificada.");
		} else {
			return ResponseEntity.ok(productos);
		}
	}

	@GetMapping("/marcas/{marca}")
	public ResponseEntity<Object> marcas(@PathVariable int marca) {
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

	private ResponseEntity<Map<String, Object>> validation(BindingResult result) {
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
	public ResponseEntity<Object> getProductNames() {

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
			String url = "https://picked-pleasantly-quetzal.ngrok-free.app/api/mantenimiento/productos/img/"
					+ producto.getRuta();
			productoDTO.setUrl(url);
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
			String url = "https://picked-pleasantly-quetzal.ngrok-free.app/api/mantenimiento/productos/img/"
					+ producto.getRuta();
			productoDTO.setUrl(url);
			pd.add(productoDTO);
		}

		StringBuilder sb = new StringBuilder();
		for (ProductoDTO producto : pd) {
			sb.append("id: ").append(producto.getId()).append(", ").append("nombre: ").append(producto.getNombre())
					.append(", ").append("stock: ").append(producto.getStock()).append(", ").append("categoria: ")
					.append(producto.getCategoria()).append(", ").append("marca: ").append(producto.getMarca())
					.append(", ").append("precio: ").append(producto.getPrecio()).append(", ").append("descripcion: ")
					.append(producto.getDescripcion()).append(", ").append("url: ").append(producto.getUrl())
					.append("    ");
		}
		response.put("productos", sb.toString());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/reco/{id}")
	public List<Producto> getMethodName(@PathVariable int id) {
		return productoService.masComprados(id);
	}

	@PostMapping("/send")
	public String send(@RequestBody Email mail) throws MessagingException {
		mailService.enviarCorreo1(mail);

		return "Correo enviado";
	}

}
