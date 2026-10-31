package com.cosmi.repository;

import com.cosmi.model.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByActivoTrueOrderByExistenciasAsc();
    List<Producto> findByActivoTrue();
}
