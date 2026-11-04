package com.cosmi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@RequiredArgsConstructor
public class ManejadorSubida {

    private final MessageSource messageSource;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String demasiadoGrande(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("producto.error.imagenTamano", null, LocaleContextHolder.getLocale()));
        return "redirect:/admin/productos";
    }
}