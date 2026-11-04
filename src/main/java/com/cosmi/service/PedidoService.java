package com.cosmi.service;

import com.cosmi.model.Carrito;
import com.cosmi.model.DetalleCarrito;
import com.cosmi.model.DetallePedido;
import com.cosmi.model.Pedido;
import com.cosmi.model.Producto;
import com.cosmi.repository.CarritoRepository;
import com.cosmi.repository.PedidoRepository;
import com.cosmi.repository.ProductoRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;

    // HU8: todo ocurre en una sola transaccion. Si algo falla (RuntimeException),
    // se revierte TODO: no se crea el pedido, no se descuenta stock y el carrito queda igual.
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
            // Bloquea la fila del producto para que otra compra no reste el mismo stock a la vez
            Producto producto = productoRepository.findByIdBloqueado(d.getProducto().getIdProducto()).orElse(null);
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
}
