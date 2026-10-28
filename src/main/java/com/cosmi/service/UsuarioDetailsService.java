package com.cosmi.service;

import com.cosmi.domain.Usuario;
import com.cosmi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Conecta Spring Security con la tabla Usuario. */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe: " + correo));
        return User.withUsername(u.getCorreo())
                .password(u.getContrasena())
                .roles(u.getRol().name())
                .disabled(!u.isActivo())   // HU19: usuario desactivado no puede entrar
                .build();
    }
}
