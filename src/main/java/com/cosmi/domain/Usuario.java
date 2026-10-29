package com.cosmi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Este campo es obligatorio")
    private String nombre;

    private String apellidos;

    @NotBlank(message = "Este campo es obligatorio")
    @Email(message = "Ingresa un correo electrónico válido.")
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "Este campo es obligatorio")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    private String telefono;

    // seca, grasa, mixta, normal o sensible (opcional)
    private String tipoPiel;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.CLIENTE;

    private boolean activo = true;
}
