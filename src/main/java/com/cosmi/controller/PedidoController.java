package com.cosmi.controller;

import com.cosmi.model.Pedido;
import com.cosmi.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pedido")
public class PedidoController {

    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    @PostMapping("/confirmar")
    public String confirmar(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        String mensaje;
        try {
            Pedido pedido = pedidoService.realizarPedido(idUsuario);
            return "redirect:/pedido/confirmacion/" + pedido.getIdPedido();
        } catch (IllegalArgumentException e) {
            mensaje = "pedido.error.vacio";
        } catch (IllegalStateException e) {
            mensaje = "pedido.error.stock";
        } catch (Exception e) {
            mensaje = "pedido.error.general";
        }
        redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/carrito";
    }

    @GetMapping("/confirmacion/{idPedido}")
    public String confirmacion(@PathVariable Integer idPedido, HttpSession session, Model model) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        Pedido pedido = pedidoService.getPedido(idPedido);
        // Solo el dueno del pedido puede verlo
        if (pedido == null || !pedido.getUsuario().getIdUsuario().equals(idUsuario)) {
            return "redirect:/carrito";
        }
        model.addAttribute("pedido", pedido);
        return "pedido/confirmacion";
    }
}
