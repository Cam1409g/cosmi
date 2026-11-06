package com.cosmi.controller;

import com.cosmi.model.Carrito;
import com.cosmi.model.DetalleCarrito;
import com.cosmi.service.CarritoService;
import com.cosmi.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final ProductoService productoService;
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

    // Pagina temporal para probar mientras Camila termina el catalogo
    // http://localhost:8080/carrito/prueba
    @GetMapping("/prueba")
    public String prueba(HttpSession session, Model model) {
        if (session.getAttribute("idUsuario") == null) {
            return "redirect:/login";
        }
        model.addAttribute("productos", productoService.getProductos(true));
        return "carrito/prueba";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Integer idProducto,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        ejecutar(idUsuario, "agregar", idProducto, cantidad, redirectAttributes, "carrito.agregado");
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
        ejecutar(idUsuario, "actualizar", idDetalle, cantidad, redirectAttributes, "carrito.actualizado");
        return "redirect:/carrito";
    }

    @PostMapping("/quitar")
    public String quitar(@RequestParam Integer idDetalle,
            HttpSession session, RedirectAttributes redirectAttributes) {
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login";
        }
        ejecutar(idUsuario, "quitar", idDetalle, 0, redirectAttributes, "carrito.eliminado");
        return "redirect:/carrito";
    }

    // Ejecuta la accion y convierte las excepciones en mensajes para el toast
    private void ejecutar(Integer idUsuario, String accion, Integer id, int cantidad,
            RedirectAttributes redirectAttributes, String mensajeOk) {
        String titulo = "todoOk";
        String mensaje = mensajeOk;
        try {
            if (accion.equals("agregar")) {
                carritoService.agregar(idUsuario, id, cantidad);
            } else if (accion.equals("actualizar")) {
                carritoService.actualizar(idUsuario, id, cantidad);
            } else {
                carritoService.quitar(idUsuario, id);
            }
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "carrito.error.argumento";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "carrito.error.stock";
        } catch (Exception e) {
            titulo = "error";
            mensaje = "carrito.error.actualizar";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
    }
}