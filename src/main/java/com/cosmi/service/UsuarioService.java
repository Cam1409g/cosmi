package com.cosmi.service;

import com.cosmi.domain.Rol;
import com.cosmi.domain.Usuario;
import com.cosmi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean correoExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    /** HU1: registra un cliente nuevo. El rol siempre es CLIENTE, sin importar lo que llegue del formulario. */
    public Usuario registrar(Usuario u) {
        u.setId(null);
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);
        u.setContrasena(passwordEncoder.encode(u.getContrasena()));
        return usuarioRepository.save(u);
    }
}
