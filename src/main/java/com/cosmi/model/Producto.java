package com.cosmi.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;

    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int existencias;
    private String imagen;
    private String tipoPiel;
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}
