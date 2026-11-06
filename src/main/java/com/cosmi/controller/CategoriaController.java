package com.cosmi.controller;

import com.cosmi.domain.Categoria;
import com.cosmi.service.CategoriaService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        return "categoria/listado";
    }

    //HU16: crear o editar una categoría
    @PostMapping("/guardar")
    public String guardar(@Valid Categoria categoria,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        if (bindingResult.hasErrors()) {
            titulo = "error";
            mensaje = "categoria.error04";
        } else {
            try {
                categoriaService.save(categoria);
            } catch (DataIntegrityViolationException e) {
                titulo = "error";
                mensaje = "categoria.error05";
            }
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/admin/categorias/listado";
    }

    //HU16: activar o desactivar una categoría
    @PostMapping("/estado")
    public String estado(@RequestParam Integer idCategoria,
            RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String mensaje = "mensaje.actualizado";
        try {
            categoriaService.cambiarEstado(idCategoria);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            mensaje = "categoria.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            mensaje = "categoria.error06";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/admin/categorias/listado";
    }
}