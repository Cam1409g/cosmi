package com.cosmi.service;

import com.cosmi.domain.Producto;
import com.cosmi.repository.ProductoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventarioService {

    
    public static final int EXISTENCIAS_BAJAS = 5;

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> getProductos() {
        return productoRepository.findByActivoTrueOrderByExistenciasAsc();
    }

    public long contarBajas(List<Producto> productos) {
        long cantidad = 0;
        for (Producto p : productos) {
            if (p.getExistencias() > 0 && p.getExistencias() <= EXISTENCIAS_BAJAS) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public long contarAgotados(List<Producto> productos) {
        long cantidad = 0;
        for (Producto p : productos) {
            if (p.getExistencias() == 0) {
                cantidad++;
            }
        }
        return cantidad;
    }

    @Transactional
    public void ajustarExistencias(Integer idProducto, Integer nuevasExistencias) {
        if (nuevasExistencias == null || nuevasExistencias < 0) {
            throw new IllegalStateException("Las existencias deben ser un número mayor o igual a cero");
        }
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) { 
            throw new IllegalArgumentException("El producto no existe: " + idProducto);
        }
        
        producto.setExistencias(nuevasExistencias);
        productoRepository.save(producto);
        log.info("Existencias del producto {} ajustadas a {}", idProducto, nuevasExistencias);
    }
}
