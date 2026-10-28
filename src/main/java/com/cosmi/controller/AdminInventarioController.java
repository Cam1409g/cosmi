package com.cosmi.controller;

import com.cosmi.service.InventarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminInventarioController {

    private final InventarioService inventarioService;

    public AdminInventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/admin/inventario")
    public String inventario(Model model) {
        model.addAttribute("productos", inventarioService.listar());
        model.addAttribute("stockBajo", InventarioService.STOCK_BAJO);
        return "admin/inventario";
    }

    @PostMapping("/admin/inventario/{id}/stock")
    public String ajustar(@PathVariable Long id,
                          @RequestParam int stock,
                          RedirectAttributes flash) {
        try {
            inventarioService.ajustarStock(id, stock);
            flash.addFlashAttribute("exito", "Stock actualizado");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/inventario";
    }
}
