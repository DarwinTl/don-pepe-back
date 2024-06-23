package com.tienda.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tienda.entities.Categoria;
import com.tienda.repositories.ICategoriaDao;

@Service
public class CategoriaServiceImp implements ICategoriaService {
	@Autowired
	private ICategoriaDao categoriaDao;

	public ICategoriaDao getCategoriaDao() {
		return categoriaDao;
	}

	public void setCategoriaDao(ICategoriaDao categoriaDao) {
		this.categoriaDao = categoriaDao;
	}

	@Override
	public List<Categoria> findAll() {
		return (List<Categoria>) categoriaDao.findAll();
	}

	@Override
	public Categoria save(Categoria categoria) {
		return categoriaDao.save(categoria);
	}

	@Override
	public Optional<Categoria> findById(int id) {
		return categoriaDao.findById(id);
	}

	@Override
	public void deleteById(int id) {
		categoriaDao.deleteById(id);
	}

	@Override
	public Page<Categoria> findAll(Pageable pageable) {
		return categoriaDao.findAll(pageable);
	}

}
