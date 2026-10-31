package com.cosmi.controller;

import com.cosmi.model.Producto;
import com.cosmi.service.CategoriaService;
import com.cosmi.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    // http://localhost:8080/admin/productos
    @GetMapping
    public String listado(Model model) {
        var productos = productoService.getProductos(false);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("categorias", categoriaService.getCategorias(true));
        return "producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Producto producto,
            BindingResult bindingResult,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "producto.guardado";
        if (bindingResult.hasErrors()) {
            titulo = "error";
            mensaje = "producto.error04";
        } else {
            try {
                productoService.save(producto, imagenFile);
            } catch (IllegalArgumentException | IllegalStateException e) {
                log.error("No se pudo guardar el producto: {}", e.getMessage(), e);
                titulo = "error";
                mensaje = e.getMessage();
            } catch (Exception e) {
                log.error("Error al guardar el producto", e);
                titulo = "error";
                mensaje = "producto.error.inesperado";
            }
        }
        redirectAttributes.addFlashAttribute(titulo, texto(mensaje));
        return "redirect:/admin/productos";
    }

    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Integer idProducto,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje;
        try {
            mensaje = productoService.cambiarEstado(idProducto) ? "producto.activado" : "producto.desactivado";
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "producto.error01";
        } catch (Exception e) {
            log.error("Error al guardar el producto", e);
            titulo = "error";
            mensaje = "producto.error.inesperado";
        }
        redirectAttributes.addFlashAttribute(titulo, texto(mensaje));
        return "redirect:/admin/productos";
    }

    private String texto(String codigo) {
        return messageSource.getMessage(codigo, null, codigo, LocaleContextHolder.getLocale());
    }
}