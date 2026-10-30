package com.cosmi.controller;

import com.cosmi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UsuarioRepository usuarioRepository;

    @ModelAttribute
    public void datosDeSesion(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean logueado = auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        model.addAttribute("logueado", logueado);
        model.addAttribute("esAdmin", false);
        model.addAttribute("nombreUsuario", "");
        model.addAttribute("cantidadCarrito", 0);   // TODO (Britzel): cantidad real del carrito

        if (logueado) {
            boolean admin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("esAdmin", admin);
            usuarioRepository.findByCorreo(auth.getName())
                    .ifPresent(u -> model.addAttribute("nombreUsuario", u.getNombre()));
        }
    }
}
