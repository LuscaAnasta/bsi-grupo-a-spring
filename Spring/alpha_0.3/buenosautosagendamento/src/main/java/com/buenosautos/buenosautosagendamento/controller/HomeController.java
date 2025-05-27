package com.buenosautos.buenosautosagendamento.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") // Mapeia a URL raiz da aplicação
    public String index() {
        return "index"; // Retorna o nome do template HTML
    }
}