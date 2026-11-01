package com.cosmi.repository;

import com.cosmi.domain.Producto;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrueOrderByStockAsc();

    List<Producto> findAllByOrderByNombreAsc();

    // Bloquea la fila mientras dura la transacción: dos pedidos a la vez no pueden vender el mismo stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> findByIdBloqueado(@Param("id") Long id);
}