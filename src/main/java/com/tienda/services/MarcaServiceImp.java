package com.tienda.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tienda.entities.Marca;
import com.tienda.repositories.IMarcaDao;

@Service
public class MarcaServiceImp implements IMarcaService {

	private IMarcaDao marcaDao;

	public MarcaServiceImp(IMarcaDao marcaDao) {
		this.marcaDao = marcaDao;
	}

	@Override
	public List<Marca> getMarcas() {
		return (List<Marca>) marcaDao.findAll();
	}

	@Override
	public Marca save(Marca marca) {
		return marcaDao.save(marca);
	}

	@Override
	public Optional<Marca> findMarca(int id) {
		return marcaDao.findById(id);
	}

	@Override
	public void deleteById(int id) {
		marcaDao.deleteById(id);
	}

	@Override
	public Page<Marca> findAll(Pageable pageable) {
		return marcaDao.findAll(pageable);
	}

}
