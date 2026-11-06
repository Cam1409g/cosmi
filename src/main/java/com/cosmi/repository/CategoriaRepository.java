package com.cosmi.repository;

import com.cosmi.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    //Consulta derivada para recuperar solo las categorías activas
    public List<Categoria> findByActivoTrue();
}
