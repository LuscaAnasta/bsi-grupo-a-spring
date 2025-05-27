package com.buenosautos.buenosautosagendamento.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard") // Mapeamento base para o dashboard
public class DashboardController {

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        // O AuthInterceptor já garante que o usuário esteja logado para acessar /dashboard
        // Mas é bom ter uma verificação defensiva ou para obter o nome de usuário
        String loggedInUserEmail = (String) session.getAttribute("loggedInUser");
        
        if (loggedInUserEmail == null) {
            // Isso não deve acontecer se o interceptor estiver funcionando corretamente,
            // mas é uma verificação de segurança extra.
            return "redirect:/login?error=auth";
        }

        model.addAttribute("username", loggedInUserEmail);
        return "home-logado"; // Nome do template HTML
    }
}