package com.cosmi.controller;

import com.cosmi.service.PedidoService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/confirmar")
    public String confirmar(Principal principal, RedirectAttributes flash) {
        try {
            Long id = pedidoService.realizarPedido(principal.getName()).getId();
            return "redirect:/pedido/exito/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
    }

    @GetMapping("/exito/{idPedido}")
    public String exito(@PathVariable Long idPedido, Principal principal,
                        Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("pedido", pedidoService.getPedidoDeUsuario(idPedido, principal.getName()));
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/carrito";
        }
        return "pedido/confirmacion";
    }
}