package com.cosmi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PLACEHOLDER: página de inicio temporal para poder probar el login.
 * Camila la reemplaza con el catálogo; cuando lo haga, se borra este archivo.
 */
@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }
}
