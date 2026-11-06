package com.cosmi.service;

import com.cosmi.domain.Carrito;
import com.cosmi.domain.DetalleCarrito;
import com.cosmi.domain.Producto;
import com.cosmi.domain.Usuario;
import com.cosmi.repository.CarritoRepository;
import com.cosmi.repository.ProductoRepository;
import com.cosmi.repository.UsuarioRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    // Devuelve el carrito del usuario... si no tiene, lo crea vacio
    @Transactional
    public Carrito obtenerOCrear(Integer idUsuario) {
        Carrito carrito = carritoRepository.findByUsuarioIdUsuario(idUsuario).orElse(null);
        if (carrito == null) {
            Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuario no existe");
            }
            carrito = new Carrito();
            carrito.setUsuario(usuario);
            carrito = carritoRepository.save(carrito);
        }
        return carrito;
    }

    @Transactional(readOnly = true)
    public Carrito getCarrito(Integer idUsuario) {
        return carritoRepository.findByUsuarioIdUsuario(idUsuario).orElse(null);
    }

    // HU6: agregar producto. Si ya esta en el carrito, suma la cantidad.
    @Transactional
    public void agregar(Integer idUsuario, Integer idProducto, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto == null || !producto.isActivo()) {
            throw new IllegalArgumentException("Producto no disponible");
        }
        Carrito carrito = obtenerOCrear(idUsuario);

        DetalleCarrito detalle = null;
        for (DetalleCarrito d : carrito.getDetalles()) {
            if (d.getProducto().getIdProducto().equals(idProducto)) {
                detalle = d;
            }
        }
        int nueva = cantidad;
        if (detalle != null) {
            nueva = detalle.getCantidad() + cantidad;
        }
        if (nueva > producto.getExistencias()) {
            throw new IllegalStateException("Sin existencias suficientes");
        }
        if (detalle == null) {
            detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setProducto(producto);
            carrito.getDetalles().add(detalle);
        }
        detalle.setCantidad(nueva);
        carritoRepository.save(carrito);
    }

    // HU7: cambiar la cantidad de una linea
    @Transactional
    public void actualizar(Integer idUsuario, Integer idDetalle, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("Cantidad invalida");
        }
        Carrito carrito = obtenerOCrear(idUsuario);
        for (DetalleCarrito d : carrito.getDetalles()) {
            if (d.getIdDetalleCarrito().equals(idDetalle)) {
                if (cantidad > d.getProducto().getExistencias()) {
                    throw new IllegalStateException("Sin existencias suficientes");
                }
                d.setCantidad(cantidad);
                carritoRepository.save(carrito);
                return;
            }
        }
        throw new IllegalArgumentException("Linea no existe");
    }

    //HU7: quitar un producto del carrito
    @Transactional
    public void quitar(Integer idUsuario, Integer idDetalle) {
        Carrito carrito = obtenerOCrear(idUsuario);
        DetalleCarrito encontrado = null;
        for (DetalleCarrito d : carrito.getDetalles()) {
            if (d.getIdDetalleCarrito().equals(idDetalle)) {
                encontrado = d;
            }
        }
        if (encontrado == null) {
            throw new IllegalArgumentException("Linea no existe");
        }
        carrito.getDetalles().remove(encontrado);
        carritoRepository.save(carrito);
    }

    public BigDecimal calcularTotal(Carrito carrito) {
        BigDecimal total = BigDecimal.ZERO;
        if (carrito != null) {
            for (DetalleCarrito d : carrito.getDetalles()) {
                BigDecimal linea = d.getProducto().getPrecio().multiply(BigDecimal.valueOf(d.getCantidad()));
                total = total.add(linea);
            }
        }
        return total;
    }
}