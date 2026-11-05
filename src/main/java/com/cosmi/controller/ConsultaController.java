package com.cosmi.controller;

import com.cosmi.service.CategoriaService;
import com.cosmi.service.ProductoService;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/consultas")
public class ConsultaController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    //HU3: página del catálogo con todos los productos activos
    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos(true);
        model.addAttribute("productos", productos);
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "consultas/listado";
    }

    //HU3: búsqueda de productos por nombre
    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false) String nombre, Model model) {
        if (nombre == null || nombre.isBlank()) { //no escribió nada
            model.addAttribute("errorConsulta", messageSource.getMessage("busqueda.vacia", null, Locale.getDefault()));
            model.addAttribute("productos", productoService.getProductos(true));
        } else {
            model.addAttribute("productos", productoService.buscarPorNombre(nombre));
            model.addAttribute("nombre", nombre);
        }
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "consultas/listado";
    }

    //HU3 y HU5: filtros por categoría, tipo de piel y rango de precio
    @GetMapping("/filtrar")
    public String filtrar(@RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String tipoPiel,
            @RequestParam(required = false) Double precioInf,
            @RequestParam(required = false) Double precioSup,
            Model model) {
        //Si no se indicó un precio se usa el rango completo
        double inferior = 0;
        double superior = 999999999;
        if (precioInf != null) {
            inferior = precioInf;
        }
        if (precioSup != null) {
            superior = precioSup;
        }
        String piel = tipoPiel;
        if (piel != null && piel.isBlank()) {
            piel = null;
        }

        if (inferior > superior) {
            model.addAttribute("errorConsulta", messageSource.getMessage("consulta.error", null, Locale.getDefault()));
            model.addAttribute("productos", java.util.Collections.EMPTY_LIST);
        } else {
            var productos = productoService.consultaFiltros(idCategoria, piel, inferior, superior);
            model.addAttribute("productos", productos);
        }
        //Se devuelven los valores para que el formulario los siga mostrando
        model.addAttribute("idCategoriaFiltro", idCategoria);
        model.addAttribute("tipoPiel", piel);
        model.addAttribute("precioInf", precioInf);
        model.addAttribute("precioSup", precioSup);
        var categorias = categoriaService.getCategorias(true);
        model.addAttribute("categorias", categorias);
        return "consultas/listado";
    }

    //HU4: información detallada de un producto
    @GetMapping("/detalle/{idProducto}")
    public String detalle(@PathVariable("idProducto") Integer idProducto, Model model) {
        var productoOptional = productoService.getProducto(idProducto);
        if (productoOptional.isEmpty() || !productoOptional.get().isActivo()) {
            return "redirect:/consultas/listado";
        }
        model.addAttribute("producto", productoOptional.get());
        return "consultas/detalle";
    }
}