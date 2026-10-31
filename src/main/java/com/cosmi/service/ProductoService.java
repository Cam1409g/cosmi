package com.cosmi.service;

import com.cosmi.domain.Categoria;
import com.cosmi.domain.Producto;
import com.cosmi.repository.CategoriaRepository;
import com.cosmi.repository.ProductoRepository;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private static final long MAX_BYTES = 2L * 1024 * 1024; // 2 MB
    private static final Path CARPETA = Paths.get("uploads", "productos");

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Producto> listar() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

    public Optional<Producto> buscar(Long id) {
        return productoRepository.findById(id);
    }

    /** Crea o edita un producto. La imagen es opcional: si no viene, se conserva la anterior. */
    @Transactional
    public void guardar(Producto datos, MultipartFile imagen) {
        String extension = validarImagen(imagen);

        Producto producto;
        if (datos.getId() != null) {
            producto = productoRepository.findById(datos.getId())
                    .orElseThrow(() -> new IllegalArgumentException("El producto no existe"));
        } else {
            producto = new Producto();
        }

        producto.setNombre(datos.getNombre().trim());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setTipoPiel(datos.getTipoPiel());
        producto.setCategoria(buscarCategoria(datos.getCategoria()));

        if (extension != null) {
            producto.setImagen(guardarArchivo(imagen, extension));
        }
        productoRepository.save(producto);
    }

    /** Activa o desactiva el producto. Devuelve el nuevo estado. */
    @Transactional
    public boolean cambiarEstado(Long id) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El producto no existe"));
        p.setActivo(!p.isActivo());
        return p.isActivo();
    }

    private Categoria buscarCategoria(Categoria c) {
        if (c == null || c.getId() == null) {
            return null;
        }
        return categoriaRepository.findById(c.getId()).orElse(null);
    }

    // Devuelve "jpg" o "png"; null si no se envió imagen
    private String validarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return null;
        }
        if (imagen.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("La imagen no puede superar los 2 MB");
        }
        String nombre = imagen.getOriginalFilename() == null
                ? "" : imagen.getOriginalFilename().toLowerCase();
        String tipo = imagen.getContentType() == null ? "" : imagen.getContentType();

        if ((nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) && tipo.equals("image/jpeg")) {
            return "jpg";
        }
        if (nombre.endsWith(".png") && tipo.equals("image/png")) {
            return "png";
        }
        throw new IllegalArgumentException("La imagen debe ser JPG o PNG");
    }

    private String guardarArchivo(MultipartFile imagen, String extension) {
        try {
            Files.createDirectories(CARPETA);
            String nombre = UUID.randomUUID() + "." + extension;
            try (InputStream in = imagen.getInputStream()) {
                Files.copy(in, CARPETA.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/productos/" + nombre;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la imagen. Intenta de nuevo.");
        }
    }
}