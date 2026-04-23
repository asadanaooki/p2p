package com.example.p2p.advice;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalBindingAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringStripEditor());
    }

    @InitBinder
    public void initSearchBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, "keyword" ,new NormalizationEditor());
    }

}
