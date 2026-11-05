package com.cosmi.service;

import com.cosmi.domain.Producto;
import com.cosmi.repository.ProductoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    //Se enlaza la capa de datos por medio de un objeto Producto
    private final ProductoRepository productoRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Transactional(readOnly = true)
    public List<Producto> getProductos(boolean activo) {
        if (activo) { //Si solo quiero los productos activos
            return productoRepository.findByActivoTrue();
        } else { // quiero todos los registros de la tabla producto
            return productoRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idProducto) {
        return productoRepository.findById(idProducto);
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {
        producto = productoRepository.save(producto);
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile,
                        "producto",
                        producto.getIdProducto());
                producto.setImagen(rutaImagen);
                productoRepository.save(producto);
            } catch (IOException e) {
                log.warn("No fue posible almacenar la imagen del producto: " + producto.getIdProducto());
            }
        }
    }

    @Transactional
    public void delete(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new IllegalArgumentException("El producto no existe: " + idProducto);
        }
        try {
            productoRepository.deleteById(idProducto);
            productoRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el producto, tiene datos asociados: " + idProducto);
        }
    }

    // HU15: activar o desactivar un producto
    @Transactional
    public void cambiarEstado(Integer idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null) {
            throw new IllegalArgumentException("El producto no existe: " + idProducto);
        }
        producto.setActivo(!producto.isActivo());
    }

    //HU3: se llama a la consulta derivada que busca por nombre
    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrueOrderByNombreAsc(nombre.trim());
    }

    //HU5: se llaman las consultas derivadas según los filtros
    @Transactional(readOnly = true)
    public List<Producto> consultaFiltros(Integer idCategoria, String tipoPiel, double precioInf, double precioSup) {
        if (idCategoria != null && tipoPiel != null) { //categoría, tipo de piel y precio
            return productoRepository.findByCategoriaIdCategoriaAndTipoPielAndPrecioBetweenAndActivoTrueOrderByPrecioAsc(idCategoria, tipoPiel, precioInf, precioSup);
        } else if (idCategoria != null) { //categoría y precio
            return productoRepository.findByCategoriaIdCategoriaAndPrecioBetweenAndActivoTrueOrderByPrecioAsc(idCategoria, precioInf, precioSup);
        } else if (tipoPiel != null) { //tipo de piel y precio
            return productoRepository.findByTipoPielAndPrecioBetweenAndActivoTrueOrderByPrecioAsc(tipoPiel, precioInf, precioSup);
        } else { //solo precio
            return productoRepository.findByPrecioBetweenAndActivoTrueOrderByPrecioAsc(precioInf, precioSup);
        }
    }
}