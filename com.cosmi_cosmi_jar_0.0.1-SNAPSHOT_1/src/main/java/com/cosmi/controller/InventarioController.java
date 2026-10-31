package com.cosmi.controller;

import com.cosmi.model.Producto;
import com.cosmi.service.InventarioService;
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
@RequestMapping("/admin/inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final MessageSource messageSource;

    // http://localhost:8080/admin/inventario
    @GetMapping
    public String listado(Model model) {
        List<Producto> productos = inventarioService.getProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("existenciasBajas", InventarioService.EXISTENCIAS_BAJAS);
        model.addAttribute("totalActivos", productos.size());
        model.addAttribute("totalBajas", inventarioService.contarBajas(productos));
        model.addAttribute("totalAgotados", inventarioService.contarAgotados(productos));
        return "inventario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idProducto,
            @RequestParam(required = false) Integer existencias,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        try {
            inventarioService.ajustarExistencias(idProducto, existencias);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "inventario.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "inventario.error02";
        } catch (Exception e) {
            titulo = "error";
            mensaje = "inventario.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/admin/inventario";
    }
}
