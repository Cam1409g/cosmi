package com.cosmi.controller;

import com.cosmi.domain.Carrito;
import com.cosmi.domain.DetalleCarrito;
import com.cosmi.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
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
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final MessageSource messageSource;

    // http://localhost:8080/carrito
    @GetMapping
    public String ver(HttpSession session, Model model) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        Carrito carrito = carritoService.getCarrito(idUsuario);
        List<DetalleCarrito> detalles = new ArrayList<>();
        if (carrito != null) {
            detalles = carrito.getDetalles();
        }
        model.addAttribute("detalles", detalles);
        model.addAttribute("total", carritoService.calcularTotal(carrito));
        return "carrito/listado";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Integer idProducto,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        String titulo = "todoOk";
        String mensaje = "carrito.agregado";
        try {
            carritoService.agregar(idUsuario, idProducto, cantidad);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "carrito.error.argumento";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "carrito.error.stock";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizar(@RequestParam Integer idDetalle,
            @RequestParam int cantidad,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        String titulo = "todoOk";
        String mensaje = "carrito.actualizado";
        try {
            carritoService.actualizar(idUsuario, idDetalle, cantidad);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "carrito.error.argumento";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "carrito.error.stock";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/carrito";
    }

    @PostMapping("/quitar")
    public String quitar(@RequestParam Integer idDetalle,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        String titulo = "todoOk";
        String mensaje = "carrito.eliminado";
        try {
            carritoService.quitar(idUsuario, idDetalle);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "carrito.error.argumento";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/carrito";
    }
}
