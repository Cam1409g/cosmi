package com.cosmi.controller;

import com.cosmi.service.CategoriaService;
import com.cosmi.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class InicioController {

    //Se enlaza con la capa de servicio
    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/")
    public String inicio(Model model) {
        var productos = productoService.getProductos(true);
        model.addAttribute("productos", productos);
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "index";
    }
}
