package com.cosmi.service;

import com.cosmi.domain.Categoria;
import com.cosmi.repository.CategoriaRepository;
import com.cosmi.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) { //solo las categorías activas
            return categoriaRepository.findByActivoTrue();
        } else { //todas las categorías
            return categoriaRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    //Si es nueva queda activa; si se edita, conserva su estado actual
    @Transactional
    public void save(Categoria categoria) {
        if (categoria.getIdCategoria() == null) {
            categoria.setActivo(true);
        } else {
            var actual = categoriaRepository.findById(categoria.getIdCategoria());
            if (actual.isPresent()) {
                categoria.setActivo(actual.get().isActivo());
            }
        }
        categoriaRepository.save(categoria);
    }

    //HU16: activa o desactiva; no se puede desactivar si tiene productos activos
    @Transactional
    public void cambiarEstado(Integer idCategoria) {
        var categoriaOptional = categoriaRepository.findById(idCategoria);
        if (categoriaOptional.isEmpty()) {
            throw new IllegalArgumentException("La categoria no existe: " + idCategoria);
        }
        Categoria categoria = categoriaOptional.get();
        if (categoria.isActivo() && productoRepository.existsByCategoriaIdCategoriaAndActivoTrue(idCategoria)) {
            throw new IllegalStateException("La categoria tiene productos activos: " + idCategoria);
        }
        categoria.setActivo(!categoria.isActivo());
        categoriaRepository.save(categoria);
    }
}
