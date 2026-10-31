package com.cosmi.service;

import com.cosmi.domain.Carrito;
import com.cosmi.domain.DetalleCarrito;
import com.cosmi.domain.Producto;
import com.cosmi.domain.Usuario;
import com.cosmi.repository.CarritoRepository;
import com.cosmi.repository.ProductoRepository;
import com.cosmi.repository.UsuarioRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    /** Devuelve el carrito del usuario (por su correo); si no tiene, lo crea. */
    @Transactional
    public Carrito getCarrito(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return carritoRepository.findByUsuarioId(usuario.getId()).orElseGet(() -> {
            Carrito nuevo = new Carrito();
            nuevo.setUsuario(usuario);
            return carritoRepository.save(nuevo);
        });
    }

    public BigDecimal total(Carrito carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCarrito d : carrito.getDetalles()) {
            total = total.add(d.getProducto().getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())));
        }
        return total;
    }

    /** HU6: agrega un producto; si ya estaba, suma la cantidad. Valida activo y stock. */
    @Transactional
    public void agregar(String correo, Long idProducto, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        Carrito carrito = getCarrito(correo);
        Producto producto = productoRepository.findById(idProducto)
                .filter(Producto::isActivo)
                .orElseThrow(() -> new IllegalArgumentException("El producto no está disponible"));
        if (producto.getStock() == 0) {
            throw new IllegalStateException("El producto está agotado");
        }

        DetalleCarrito existente = carrito.getDetalles().stream()
                .filter(d -> d.getProducto().getId().equals(idProducto))
                .findFirst().orElse(null);
        int nueva = (existente != null ? existente.getCantidad() : 0) + cantidad;
        if (nueva > producto.getStock()) {
            throw new IllegalStateException("Solo hay " + producto.getStock() + " unidades disponibles");
        }

        if (existente != null) {
            existente.setCantidad(nueva);
        } else {
            DetalleCarrito detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setProducto(producto);
            detalle.setCantidad(nueva);
            carrito.getDetalles().add(detalle);
        }
        carritoRepository.save(carrito);
    }

    /** HU7: cambia la cantidad de una línea (solo del carrito del propio usuario). */
    @Transactional
    public void modificar(String correo, Long idDetalle, int cantidad) {
        if (cantidad < 1) {
            throw new IllegalArgumentException("La cantidad debe ser al menos 1");
        }
        DetalleCarrito detalle = buscarDetalle(getCarrito(correo), idDetalle);
        if (cantidad > detalle.getProducto().getStock()) {
            throw new IllegalStateException("Solo hay " + detalle.getProducto().getStock() + " unidades disponibles");
        }
        detalle.setCantidad(cantidad);
    }

    /** HU7: quita una línea del carrito. */
    @Transactional
    public void eliminar(String correo, Long idDetalle) {
        Carrito carrito = getCarrito(correo);
        DetalleCarrito detalle = buscarDetalle(carrito, idDetalle);
        carrito.getDetalles().remove(detalle); // orphanRemoval lo borra de la base
    }

    /** Cantidad total de unidades (para el número del navbar). No crea carrito. */
    @Transactional(readOnly = true)
    public int cantidadItems(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .flatMap(u -> carritoRepository.findByUsuarioId(u.getId()))
                .map(c -> c.getDetalles().stream().mapToInt(DetalleCarrito::getCantidad).sum())
                .orElse(0);
    }

    private DetalleCarrito buscarDetalle(Carrito carrito, Long idDetalle) {
        return carrito.getDetalles().stream()
                .filter(d -> d.getId().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El producto no está en tu carrito"));
    }
}