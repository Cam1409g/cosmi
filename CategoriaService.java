package com.cosmi.service;

import com.cosmi.domain.Categoria;
import com.cosmi.repository.CategoriaRepository;
import com.cosmi.repository.ProductoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(boolean activo) {
        if (activo) {
            return categoriaRepository.findByActivoTrue();
        } else {
            return categoriaRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> getCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    @Transactional
    public void save(Categoria categoria) {
        if (!categoria.isActivo()
                && categoria.getIdCategoria() != null
                && categoriaRepository.existsById(categoria.getIdCategoria())
                && productoRepository.existsByCategoria_IdCategoriaAndActivoTrue(
                        categoria.getIdCategoria())) {

            throw new IllegalStateException(
                    "No se puede desactivar la categoría porque tiene productos activos.");
        }

        categoria = categoriaRepository.save(categoria);
    }

    @Transactional
    public void delete(Integer idCategoria) {
        if (!categoriaRepository.existsById(idCategoria)) {
            throw new IllegalArgumentException(
                    "La categoria no existe: " + idCategoria);
        }

        try {
            categoriaRepository.deleteById(idCategoria);
            categoriaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar la categoria, tiene datos asociados: "
                    + idCategoria);
        }
    }
}
