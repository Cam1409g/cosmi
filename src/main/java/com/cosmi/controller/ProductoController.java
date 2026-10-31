package com.cosmi.controller;

import com.cosmi.domain.Producto;
import com.cosmi.repository.CategoriaRepository;
import com.cosmi.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;

    public ProductoController(ProductoService productoService,
                              CategoriaRepository categoriaRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
    }

    // Listado + formulario. Con ?editar=ID el formulario sale con los datos del producto
    @GetMapping
    public String listado(@RequestParam(required = false) Long editar, Model model) {
        Producto producto = new Producto();
        if (editar != null) {
            producto = productoService.buscar(editar).orElse(new Producto());
        }
        model.addAttribute("producto", producto);
        cargarListas(model);
        return "admin/productos";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto,
                          BindingResult result,
                          @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                          Model model,
                          RedirectAttributes flash) {
        if (result.hasErrors()) {
            cargarListas(model);
            return "admin/productos";
        }
        try {
            productoService.guardar(producto, imagenFile);
            flash.addFlashAttribute("exito", "Producto guardado correctamente");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
            if (producto.getId() != null) {
                return "redirect:/admin/productos?editar=" + producto.getId();
            }
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        try {
            boolean activo = productoService.cambiarEstado(id);
            flash.addFlashAttribute("exito", activo ? "Producto activado" : "Producto desactivado");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    private void cargarListas(Model model) {
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("categorias", categoriaRepository.findAll());
    }
}