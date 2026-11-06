package com.cosmi.service;

import com.cosmi.domain.Carrito;
import com.cosmi.domain.DetalleCarrito;
import com.cosmi.domain.DetallePedido;
import com.cosmi.domain.EstadoPedido;
import com.cosmi.domain.Pedido;
import com.cosmi.domain.Producto;
import com.cosmi.repository.CarritoRepository;
import com.cosmi.repository.PedidoRepository;
import com.cosmi.repository.ProductoRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

   //HU8: se realiza el pedido, si algo falla no se guarda nada
    @Transactional
    public Pedido realizarPedido(Integer idUsuario) {
        Carrito carrito = carritoRepository.findByUsuarioIdUsuario(idUsuario).orElse(null);
        if (carrito == null || carrito.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Carrito vacio");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(carrito.getUsuario());
        BigDecimal total = BigDecimal.ZERO;

        for (DetalleCarrito d : carrito.getDetalles()) {
            Producto producto = productoRepository.findById(d.getProducto().getIdProducto()).orElse(null);
            if (producto == null) {
                throw new IllegalArgumentException("Producto no existe");
            }
            if (!producto.isActivo() || d.getCantidad() > producto.getExistencias()) {
                throw new IllegalStateException("Sin existencias: " + producto.getNombre());
            }
            producto.setExistencias(producto.getExistencias() - d.getCantidad());
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            pedido.getDetalles().add(detalle);

            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(d.getCantidad())));
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        // Vacia el carrito
        carrito.getDetalles().clear();
        carritoRepository.save(carrito);

        return pedido;
    }

    @Transactional(readOnly = true)
    public Pedido getPedido(Integer idPedido) {
        return pedidoRepository.findById(idPedido).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaDesc();
    }

    // HU18: avanza el pedido al siguiente estado del flujo
    @Transactional
    public void avanzarEstado(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no existe");
        }
        EstadoPedido actual = pedido.getEstado();
        if (actual == EstadoPedido.PENDIENTE) {
            pedido.setEstado(EstadoPedido.EN_PREPARACION);
        } else if (actual == EstadoPedido.EN_PREPARACION) {
            pedido.setEstado(EstadoPedido.EN_CAMINO);
        } else if (actual == EstadoPedido.EN_CAMINO) {
            pedido.setEstado(EstadoPedido.ENTREGADO);
        } else {
            throw new IllegalStateException("El pedido ya esta en un estado final");
        }
        pedidoRepository.save(pedido);
    }

    // HU18: cancela el pedido y devuelve las existencias a cada producto
    @Transactional
    public void cancelar(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no existe");
        }
        EstadoPedido actual = pedido.getEstado();
        if (actual != EstadoPedido.PENDIENTE && actual != EstadoPedido.EN_PREPARACION) {
            throw new IllegalStateException("El pedido ya no se puede cancelar");
        }
        for (DetallePedido d : pedido.getDetalles()) {
            Producto producto = productoRepository.findById(d.getProducto().getIdProducto()).orElse(null);
            if (producto != null) {
                producto.setExistencias(producto.getExistencias() + d.getCantidad());
                productoRepository.save(producto);
            }
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
    }
}