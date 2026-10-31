package com.cosmi.repository;

import com.cosmi.model.Carrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByUsuarioIdUsuario(Integer idUsuario);
}