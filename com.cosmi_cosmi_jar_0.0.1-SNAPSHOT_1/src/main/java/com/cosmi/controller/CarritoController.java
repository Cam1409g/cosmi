package com.cosmi.controller;

import com.cosmi.service.CarritoService;
import com.cosmi.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoService productoService;
    private final MessageSource messageSource;

    // http://localhost:8080/carrito
    @GetMapping
    public String ver(HttpSession session, Model model) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        var detalles = carritoService.getDetalles(idUsuario);
        model.addAttribute("detalles", detalles);
        model.addAttribute("totalCarrito", carritoService.total(detalles));
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
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            mensaje = e.getMessage();
        } catch (Exception e) {
            log.error("Error al agregar al carrito", e);
            titulo = "error";
            mensaje = "carrito.error.actualizar";
        }
        redirectAttributes.addFlashAttribute(titulo, texto(mensaje));
        return "redirect:/carrito";
    }

    @PostMapping("/modificar")
    public String modificar(@RequestParam Integer idDetalle,
            @RequestParam int cantidad,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        String titulo = "todoOk";
        String mensaje = "carrito.actualizado";
        try {
            carritoService.modificar(idUsuario, idDetalle, cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            titulo = "error";
            mensaje = e.getMessage();
        } catch (Exception e) {
            log.error("Error al modificar el carrito", e);
            titulo = "error";
            mensaje = "carrito.error.actualizar";
        }
        redirectAttributes.addFlashAttribute(titulo, texto(mensaje));
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idDetalle,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        String titulo = "todoOk";
        String mensaje = "carrito.eliminado";
        try {
            carritoService.eliminar(idUsuario, idDetalle);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = e.getMessage();
        } catch (Exception e) {
            log.error("Error al eliminar del carrito", e);
            titulo = "error";
            mensaje = "carrito.error.actualizar";
        }
        redirectAttributes.addFlashAttribute(titulo, texto(mensaje));
        return "redirect:/carrito";
    }

    // TEMPORAL: página para probar el carrito mientras Camila termina el catálogo.
    // BORRAR este método (y templates/carrito/prueba.html) cuando el catálogo esté en main.
    @GetMapping("/prueba")
    public String prueba(Model model) {
        model.addAttribute("productos", productoService.getProductos(true));
        return "carrito/prueba";
    }

    private String texto(String codigo) {
        return messageSource.getMessage(codigo, null, codigo, LocaleContextHolder.getLocale());
    }
}