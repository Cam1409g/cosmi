package com.cosmi.service;

import com.cosmi.model.Carrito;
import com.cosmi.model.DetalleCarrito;
import com.cosmi.model.Producto;
import com.cosmi.model.Usuario;
import com.cosmi.repository.CarritoRepository;
import com.cosmi.repository.ProductoRepository;
import com.cosmi.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    // Devuelve el carrito del usuario; si todavía no tiene uno, lo crea
    @Transactional
    public Carrito getCarrito(Integer idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario).orElseGet(() -> {
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new IllegalArgumentException("carrito.usuario.noEncontrado"));
            Carrito carrito = new Carrito();
            carrito.setUsuario(usuario);
            return carritoRepository.save(carrito);
        });
    }

    @Transactional
    public List<DetalleCarrito> getDetalles(Integer idUsuario) {
        return new ArrayList<>(getCarrito(idUsuario).getDetalles());
    }

    public BigDecimal total(List<DetalleCarrito> detalles) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCarrito d : detalles) {
            total = total.add(d.getProducto().getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())));
        }
        return total;
    }

    // HU6: agregar un producto (si ya está, se suma la cantidad)
    @Transactional
    public void agregar(Integer idUsuario, Integer idProducto, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("carrito.error.cantidad");
        }
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("producto.error01"));
        if (!producto.isActivo() || producto.getExistencias() <= 0) {
            throw new IllegalStateException("carrito.error.agotado");
        }
        Carrito carrito = getCarrito(idUsuario);
        DetalleCarrito detalle = carrito.getDetalles().stream()
                .filter(d -> d.getProducto().getIdProducto().equals(idProducto))
                .findFirst().orElse(null);

        int nuevaCantidad = (detalle == null ? 0 : detalle.getCantidad()) + cantidad;
        if (nuevaCantidad > producto.getExistencias()) {
            throw new IllegalStateException("carrito.error.stock");
        }
        if (detalle == null) {
            detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setProducto(producto);
            carrito.getDetalles().add(detalle);
        }
        detalle.setCantidad(nuevaCantidad);
        carritoRepository.save(carrito);
    }

    // HU7: cambiar la cantidad de un producto del carrito
    @Transactional
    public void modificar(Integer idUsuario, Integer idDetalle, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("carrito.error.cantidad");
        }
        DetalleCarrito detalle = buscarDetalle(getCarrito(idUsuario), idDetalle);
        if (cantidad > detalle.getProducto().getExistencias()) {
            throw new IllegalStateException("carrito.error.stock");
        }
        detalle.setCantidad(cantidad);
    }

    // HU7: quitar un producto del carrito
    @Transactional
    public void eliminar(Integer idUsuario, Integer idDetalle) {
        Carrito carrito = getCarrito(idUsuario);
        carrito.getDetalles().remove(buscarDetalle(carrito, idDetalle));
    }

    // Solo busca dentro del carrito del usuario, así nadie toca el de otra persona
    private DetalleCarrito buscarDetalle(Carrito carrito, Integer idDetalle) {
        return carrito.getDetalles().stream()
                .filter(d -> d.getIdDetalleCarrito().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("carrito.error.detalle"));
    }
}