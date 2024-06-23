package com.tienda.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tienda.entities.Comentario;
import com.tienda.entities.Producto;
import com.tienda.entities.Usuario;
import com.tienda.repositories.IComentarioDao;
import com.tienda.repositories.IProductoDao;
import com.tienda.repositories.IUsuarioDao;

@Service
public class ComentarioServiceImp implements IComentarioService {

	private IComentarioDao comentarioDao;

	private IProductoDao productoDao;

	private IUsuarioDao usuarioDao;

	public ComentarioServiceImp(IComentarioDao comentarioDao, IProductoDao productoDao, IUsuarioDao usuarioDao) {
		this.comentarioDao = comentarioDao;
		this.productoDao = productoDao;
		this.usuarioDao = usuarioDao;
	}

	@Override
	public List<Comentario> getComentarios() {
		return comentarioDao.findAll();
	}

	@Override
	public Comentario comentar(int idProducto, String correo, Comentario comentario) {
		Producto p = productoDao.findById(idProducto).orElseThrow();
		Usuario u = usuarioDao.findByCorreo(correo).get();
		comentario.setProducto(p);
		comentario.setUsuario(u);
		return comentarioDao.save(comentario);
	}

	@Override
	public List<Comentario> listarPorProducto(int id) {
		return comentarioDao.listarPorProducto(id);
	}

}
