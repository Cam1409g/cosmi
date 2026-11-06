package com.cosmi.controller;

import com.cosmi.service.PedidoService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pedidos")
public class PedidoAdminController {

    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    // http://localhost:8080/admin/pedidos
    @GetMapping
    public String listado(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "pedido/listado";
    }

    @PostMapping("/estado")
    public String avanzar(@RequestParam Integer idPedido, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "pedido.actualizado";
        try {
            pedidoService.avanzarEstado(idPedido);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "pedido.error.noExiste";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "pedido.error.transicion";
        } catch (Exception e) {
            titulo = "error";
            mensaje = "pedido.error.general";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam Integer idPedido, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "pedido.cancelado";
        try {
            pedidoService.cancelar(idPedido);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "pedido.error.noExiste";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "pedido.error.transicion";
        } catch (Exception e) {
            titulo = "error";
            mensaje = "pedido.error.general";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/admin/pedidos";
    }
}