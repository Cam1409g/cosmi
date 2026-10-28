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

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario,
                            BindingResult result) {
        // Escenario 2: correo ya registrado
        if (!result.hasFieldErrors("correo") && usuarioService.correoExiste(usuario.getCorreo())) {
            result.rejectValue("correo", "duplicado", "El correo ya está registrado");
        }
        // Escenario 3: campos obligatorios vacíos
        if (result.hasErrors()) {
            return "registro";
        }
        usuarioService.registrar(usuario);   // Escenario 1: registro exitoso
        return "redirect:/login?registrado";
    }
}
