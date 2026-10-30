package com.cosmi.controller;

import com.cosmi.model.Usuario;
import com.cosmi.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    // http://localhost:8080/login
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "acceso";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.autenticar(correo, contrasena);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("error.login", null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }
        session.setAttribute("idUsuario", usuario.getIdUsuario());
        session.setAttribute("nombreUsuario", usuario.getNombre());
        session.setAttribute("rolUsuario", usuario.getRol().name());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String salir(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // http://localhost:8080/registro
    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "acceso";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (!result.hasFieldErrors("correo") && usuarioService.correoExiste(usuario.getCorreo())) {
            result.rejectValue("correo", "duplicado",
                    messageSource.getMessage("registro.error.correoDuplicado", null, LocaleContextHolder.getLocale()));
        }
        if (result.hasErrors()) {
            return "acceso";
        }
        usuarioService.registrar(usuario);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("registro.exito", null, LocaleContextHolder.getLocale()));
        return "redirect:/login";
    }
}