package com.buenosautos.buenosautosagendamento;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index"; // carrega o arquivo index.html
    }
}