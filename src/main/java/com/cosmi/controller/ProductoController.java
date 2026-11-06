package com.cosmi.controller;

import com.cosmi.domain.Producto;
import com.cosmi.service.CategoriaService;
import com.cosmi.service.ProductoService;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/productos")
public class ProductoController {

    //se enlaza con la capa de servicio por medio de un ProductoService
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    // http://localhost:8080/admin/productos
    @GetMapping
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);

        return "producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto,
            BindingResult bindingResult,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        if (bindingResult.hasErrors()) {
            titulo = "error";
            mensaje = "producto.error04";
        } else {
            try {
                productoService.save(producto, imagenFile);
            } catch (DataIntegrityViolationException e) {
                titulo = "error";
                mensaje = "producto.error05";
            }
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));

        return "redirect:/admin/productos";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idProducto,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.eliminado";
        try {
            productoService.delete(idProducto);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "producto.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "producto.error02";
        } catch (Exception e) {
            titulo = "error";
            mensaje = "producto.error03";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));

        return "redirect:/admin/productos";
    }

    // HU15: activar o desactivar un producto
    @PostMapping("/estado")
    public String estado(@RequestParam Integer idProducto,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        try {
            productoService.cambiarEstado(idProducto);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "producto.error01";
        }
        redirectAttributes.addFlashAttribute(titulo,
                messageSource.getMessage(mensaje, null, Locale.getDefault()));

        return "redirect:/admin/productos";
    }
}