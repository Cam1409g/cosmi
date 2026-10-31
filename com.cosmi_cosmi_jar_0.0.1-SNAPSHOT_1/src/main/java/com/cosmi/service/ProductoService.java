package com.cosmi.service;

import com.cosmi.model.Producto;
import com.cosmi.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final long MAXIMO_BYTES = 2L * 1024 * 1024; // HU15: 2 MB

    private final ProductoRepository productoRepository;
    // Firebase se pide solo cuando hay que subir una imagen
    private final ObjectProvider<FirebaseStorageService> firebaseProvider;

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean soloActivos) {
        return soloActivos ? productoRepository.findByActivoTrue() : productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto getProducto(Producto producto) {
        return productoRepository.findById(producto.getIdProducto()).orElse(null);
    }

    @Transactional
    public void save(Producto producto) {
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(Producto producto) {
        productoRepository.delete(producto);
    }

    // Crea o actualiza un producto y, si viene imagen, la sube a Firebase
    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        validarImagen(imagenFile);

        Producto destino = producto;
        if (producto.getIdProducto() != null) {
            // Al editar se conserva la imagen y el estado que ya tenía
            destino = productoRepository.findById(producto.getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("producto.error01"));
            destino.setNombre(producto.getNombre());
            destino.setDescripcion(producto.getDescripcion());
            destino.setPrecio(producto.getPrecio());
            destino.setExistencias(producto.getExistencias());
            destino.setTipoPiel(producto.getTipoPiel());
            destino.setCategoria(producto.getCategoria());
        }
        productoRepository.save(destino); // se guarda primero para tener el id

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String url = firebaseProvider.getObject()
                        .uploadImage(imagenFile, "producto", destino.getIdProducto());
                destino.setImagen(url);
                productoRepository.save(destino);
            } catch (IOException e) {
                // Al ser RuntimeException, la transacción se revierte: no queda un producto a medias
                throw new IllegalStateException("producto.error.imagenSubida", e);
            }
        }
    }

    // Activa o desactiva un producto; devuelve el estado en que quedó
    @Transactional
    public boolean cambiarEstado(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("producto.error01"));
        producto.setActivo(!producto.isActivo());
        return producto.isActivo();
    }

    private void validarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return;
        }
        String tipo = imagen.getContentType();
        if (!"image/jpeg".equals(tipo) && !"image/png".equals(tipo)) {
            throw new IllegalArgumentException("producto.error.imagenTipo");
        }
        if (imagen.getSize() > MAXIMO_BYTES) {
            throw new IllegalArgumentException("producto.error.imagenTamano");
        }
    }
}