package com.buenosautos.buenosautosagendamento.login.controller; 

import com.buenosautos.buenosautosagendamento.login.model.Usuario; // Pacote correto para sua Usuario
import com.buenosautos.buenosautosagendamento.login.repository.UsuarioRepository; // Repositório correto
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }

    @PostMapping("/login")
    // Mude os nomes dos parâmetros para 'email' e 'senha'
    public String processLogin(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        // Mude para findByEmail (você pode precisar adicionar esse método ao UsuarioRepository)
        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            Usuario user = optionalUser.get();
            // Compare com 'getSenha()'
            if (user.getSenha().equals(senha)) {
                session.setAttribute("loggedInUser", user.getEmail());
                System.out.println("Login bem-sucedido para o usuário: " + email);
                return "redirect:/dashboard"; // <--- Redirecionar para o dashboard
            }
        }
        System.out.println("Falha no login para o usuário: " + email);
        return "redirect:/login?error";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        System.out.println("Usuário desconectado.");
        return "redirect:/login?logout";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login?error";
        }
        model.addAttribute("username", session.getAttribute("loggedInUser"));
        return "admin-dashboard";
    }
    
}