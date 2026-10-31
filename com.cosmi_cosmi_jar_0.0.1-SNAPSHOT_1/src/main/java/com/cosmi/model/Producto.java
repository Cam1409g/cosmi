package com.cosmi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;
    @NotBlank
    private String nombre;
    private String descripcion;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal precio;
    @Min(0)
    private int existencias;
    private String imagen;
    private String tipoPiel;
    private boolean activo = true;
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
}