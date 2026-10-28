package com.cosmi.service;

import com.cosmi.domain.Producto;
import com.cosmi.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventarioService {

    public static final int STOCK_BAJO = 5;

    private final ProductoRepository productoRepository;

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** HU17 escenario 1: lista de productos activos, los de menor stock primero. */
    public List<Producto> listar() {
        return productoRepository.findByActivoTrueOrderByStockAsc();
    }

    /** HU17 escenario 4: rechaza cantidades negativas. */
    @Transactional
    public void ajustarStock(Long productoId, int nuevoStock) {
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe"));
        p.setStock(nuevoStock);
        productoRepository.save(p);
    }
}
