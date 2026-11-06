package com.cosmi.repository;

import com.cosmi.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByDescripcionIgnoreCase(String nombre);
    List<Categoria> findByActivoTrue();
}
