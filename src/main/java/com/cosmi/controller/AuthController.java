package com.cosmi.controller;

import com.cosmi.domain.Usuario;
import com.cosmi.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Según el prototipo, "Iniciar sesión" y "Crear cuenta" van en la misma pantalla (acceso.html).
 */
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "acceso";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "acceso";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario,
                            BindingResult result) {
        // Escenario 2: correo ya registrado
        if (!result.hasFieldErrors("correo") && usuarioService.correoExiste(usuario.getCorreo())) {
            result.rejectValue("correo", "duplicado", "Este correo ya está registrado");
        }
        // Escenario 3: campos obligatorios vacíos
        if (result.hasErrors()) {
            return "acceso";
        }
        usuarioService.registrar(usuario);   // Escenario 1: registro exitoso
        return "redirect:/login?registrado";
    }
}
