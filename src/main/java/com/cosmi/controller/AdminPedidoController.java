package com.cosmi.controller;

import com.cosmi.domain.EstadoPedido;
import com.cosmi.service.PedidoService;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pedidos")
public class AdminPedidoController {

    private static final Map<EstadoPedido, String> ETIQUETAS = Map.of(
            EstadoPedido.PENDIENTE, "Pendiente",
            EstadoPedido.EN_PREPARACION, "En preparación",
            EstadoPedido.EN_CAMINO, "En camino",
            EstadoPedido.ENTREGADO, "Entregado",
            EstadoPedido.CANCELADO, "Cancelado");

    private final PedidoService pedidoService;

    public AdminPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        model.addAttribute("etiquetas", ETIQUETAS);
        return "admin/pedidos";
    }

    @PostMapping("/{id}/avanzar")
    public String avanzar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            EstadoPedido nuevo = pedidoService.avanzarEstado(id);
            flash.addFlashAttribute("exito", "Pedido #" + id + " ahora está: " + ETIQUETAS.get(nuevo));
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            pedidoService.cancelar(id);
            flash.addFlashAttribute("exito", "Pedido #" + id + " cancelado. El stock fue devuelto.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/pedidos";
    }
}