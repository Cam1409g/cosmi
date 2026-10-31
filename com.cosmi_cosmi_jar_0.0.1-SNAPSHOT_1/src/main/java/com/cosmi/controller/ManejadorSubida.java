package com.cosmi.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

@ControllerAdvice
@RequiredArgsConstructor
public class ManejadorSubida {

    private final MessageSource messageSource;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String archivoMuyGrande(HttpServletRequest request) {
        FlashMap flash = RequestContextUtils.getOutputFlashMap(request);
        flash.put("error", messageSource.getMessage("producto.error.imagenTamano", null,
                LocaleContextHolder.getLocale()));
        return "redirect:/admin/productos";
    }
}