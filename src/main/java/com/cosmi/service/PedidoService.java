package com.cosmi.service;

import com.cosmi.domain.Carrito;
import com.cosmi.domain.DetalleCarrito;
import com.cosmi.domain.DetallePedido;
import com.cosmi.domain.EstadoPedido;
import com.cosmi.domain.Pedido;
import com.cosmi.domain.Producto;
import com.cosmi.repository.PedidoRepository;
import com.cosmi.repository.ProductoRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final CarritoService carritoService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProductoRepository productoRepository,
                         CarritoService carritoService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.carritoService = carritoService;
    }

    /** HU8: convierte el carrito en pedido. Si algo falla, se revierte TODO (stock, pedido y carrito). */
    @Transactional
    public Pedido realizarPedido(String correo) {
        Carrito carrito = carritoService.getCarrito(correo);
        if (carrito.getDetalles().isEmpty()) {
            throw new IllegalStateException("Tu carrito está vacío");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(carrito.getUsuario());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCarrito item : carrito.getDetalles()) {
            Producto producto = productoRepository.findByIdBloqueado(item.getProducto().getId())
                    .orElseThrow(() -> new IllegalStateException("Uno de los productos ya no existe"));
            if (!producto.isActivo() || producto.getStock() < item.getCantidad()) {
                throw new IllegalStateException("No hay existencias suficientes de "
                        + producto.getNombre() + ". No se realizó el pedido.");
            }
            // Rebajo de stock
            producto.setStock(producto.getStock() - item.getCantidad());

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecio()); // se guarda el precio del momento
            pedido.getDetalles().add(detalle);

            total = total.add(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
        }
        pedido.setTotal(total);
        pedidoRepository.save(pedido);

        carrito.getDetalles().clear(); // el carrito queda vacío
        return pedido;
    }

    /** Devuelve el pedido solo si pertenece al usuario. */
    @Transactional(readOnly = true)
    public Pedido getPedidoDeUsuario(Long idPedido, String correo) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .filter(p -> p.getUsuario().getCorreo().equals(correo))
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe"));
        pedido.getDetalles().size(); // carga los detalles dentro de la transacción
        return pedido;
    }
        /** HU18: todos los pedidos, los más recientes primero. */
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaDesc();
        pedidos.forEach(p -> p.getDetalles().size()); // carga los detalles
        return pedidos;
    }

    /** HU18: avanza el pedido al siguiente estado del flujo. */
    @Transactional
    public EstadoPedido avanzarEstado(Long idPedido) {
        Pedido pedido = buscarPedido(idPedido);
        EstadoPedido siguiente = switch (pedido.getEstado()) {
            case PENDIENTE -> EstadoPedido.EN_PREPARACION;
            case EN_PREPARACION -> EstadoPedido.EN_CAMINO;
            case EN_CAMINO -> EstadoPedido.ENTREGADO;
            case ENTREGADO, CANCELADO ->
                    throw new IllegalStateException("El pedido ya está en un estado final");
        };
        pedido.setEstado(siguiente);
        return siguiente;
    }

    /** HU18: cancela el pedido y devuelve el stock. Todo o nada (@Transactional). */
    @Transactional
    public void cancelar(Long idPedido) {
        Pedido pedido = buscarPedido(idPedido);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE
                && pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new IllegalStateException(
                    "Solo se pueden cancelar pedidos pendientes o en preparación");
        }
        for (DetallePedido d : pedido.getDetalles()) {
            Producto producto = productoRepository.findByIdBloqueado(d.getProducto().getId())
                    .orElseThrow(() -> new IllegalStateException("Un producto del pedido ya no existe"));
            producto.setStock(producto.getStock() + d.getCantidad()); // devolución de stock
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
    }

    private Pedido buscarPedido(Long idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe"));
    }
}