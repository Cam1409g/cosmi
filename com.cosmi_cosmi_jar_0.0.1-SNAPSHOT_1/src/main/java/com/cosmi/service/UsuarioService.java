package com.cosmi.service;

import com.cosmi.model.Rol;
import com.cosmi.model.Usuario;
import com.cosmi.repository.UsuarioRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Set<String> TIPOS_PIEL = Set.of("seca", "grasa", "mixta", "normal", "sensible");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean correoExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    //Devuelve el usuario si el correo y la contraseña son correctos y la cuenta está activa; si no, devuelve null
    @Transactional(readOnly = true)
    public Usuario autenticar(String correo, String contrasena) {
        return usuarioRepository.findByCorreo(correo)
                .filter(Usuario::isActivo)
                .filter(u -> passwordEncoder.matches(contrasena, u.getContrasena()))
                .orElse(null);
    }

    @Transactional
    public Usuario registrar(Usuario u) {
        u.setIdUsuario(null);
        u.setRol(Rol.CLIENTE);
        u.setActivo(true);

        String tipo = u.getTipoPiel() == null ? "" : u.getTipoPiel().trim().toLowerCase();
        u.setTipoPiel(TIPOS_PIEL.contains(tipo) ? tipo : null);

        u.setContrasena(passwordEncoder.encode(u.getContrasena()));
        return usuarioRepository.save(u);
    }
}