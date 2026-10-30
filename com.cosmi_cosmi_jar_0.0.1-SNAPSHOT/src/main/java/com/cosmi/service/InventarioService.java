package com.cosmi.service;

import com.cosmi.model.Producto;
import com.cosmi.repository.ProductoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventarioService {

    public static final int STOCK_BAJO = 5;

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findByActivoTrueOrderByExistenciasAsc();
    }

    @Transactional
    public void ajustarExistencias(Integer idProducto, int nuevasExistencias) {
        if (nuevasExistencias < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas");
        }
        Producto p = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe"));
        p.setExistencias(nuevasExistencias);
        productoRepository.save(p);
    }
}
