package com.cosmi.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "categoria")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String nombre;

    @Column(length = 255)
    @Size(max = 255)
    private String descripcion;

    private boolean activo;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;
}
