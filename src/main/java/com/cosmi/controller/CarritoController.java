package com.cosmi.controller;

import com.cosmi.domain.Carrito;
import com.cosmi.repository.ProductoRepository;
import com.cosmi.service.CarritoService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;
    private final ProductoRepository productoRepository;

    public CarritoController(CarritoService carritoService, ProductoRepository productoRepository) {
        this.carritoService = carritoService;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String ver(Principal principal, Model model) {
        Carrito carrito = carritoService.getCarrito(principal.getName());
        model.addAttribute("detalles", carrito.getDetalles());
        model.addAttribute("total", carritoService.total(carrito));
        return "carrito/listado";
    }

    @PostMapping("/agregar")
    public String agregar(@RequestParam Long idProducto,
                          @RequestParam(defaultValue = "1") int cantidad,
                          Principal principal, RedirectAttributes flash) {
        try {
            carritoService.agregar(principal.getName(), idProducto, cantidad);
            flash.addFlashAttribute("exito", "Producto agregado al carrito");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/modificar")
    public String modificar(@RequestParam Long idDetalle, @RequestParam int cantidad,
                            Principal principal, RedirectAttributes flash) {
        try {
            carritoService.modificar(principal.getName(), idDetalle, cantidad);
            flash.addFlashAttribute("exito", "Cantidad actualizada");
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Long idDetalle, Principal principal, RedirectAttributes flash) {
        try {
            carritoService.eliminar(principal.getName(), idDetalle);
            flash.addFlashAttribute("exito", "Producto eliminado del carrito");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/carrito";
    }

    // TEMPORAL: para probar mientras no está el catálogo de Camila. Se borra al unir su trabajo.
    @GetMapping("/prueba")
    public String prueba(Model model) {
        model.addAttribute("productos", productoRepository.findByActivoTrueOrderByStockAsc());
        return "carrito/prueba";
    }
}