package com.cosmi.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ManejadorSubida {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String demasiadoGrande(RedirectAttributes flash) {
        flash.addFlashAttribute("error", "La imagen no puede superar los 2 MB");
        return "redirect:/admin/productos";
    }
}