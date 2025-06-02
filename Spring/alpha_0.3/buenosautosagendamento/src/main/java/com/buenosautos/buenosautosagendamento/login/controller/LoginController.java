package com.buenosautos.buenosautosagendamento.login.controller;

import com.buenosautos.buenosautosagendamento.login.model.Usuario;
import com.buenosautos.buenosautosagendamento.login.repository.UsuarioRepository;
import com.buenosautos.buenosautosagendamento.login.model.PasswordResetToken; // Novo import
import com.buenosautos.buenosautosagendamento.login.repository.PasswordResetTokenRepository; // Novo import
import com.buenosautos.buenosautosagendamento.notificacao.service.NotificacaoService; // Novo import

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime; // Novo import
import java.util.Optional;
import java.util.UUID; // Novo import
import java.time.temporal.ChronoUnit; // Opcional, para refinar a expiração do token

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService; // Injeção de dependência

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository; // Injeção de dependência

    // --- Métodos de Login e Logout ---

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
        return "login"; // login.html está na raiz de templates
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            Usuario user = optionalUser.get();
            if (user.getSenha().equals(senha)) { // ATENÇÃO: Senha em texto puro. Considere criptografia.
                session.setAttribute("loggedInUser", user.getEmail());
                System.out.println("Login bem-sucedido para o usuário: " + email);
                return "redirect:/dashboard";
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

    // --- Métodos de Perfil do Usuário ---

    @GetMapping("/perfil")
    public String showPerfil(HttpSession session, Model model) {
        String loggedInUserEmail = (String) session.getAttribute("loggedInUser");
        if (loggedInUserEmail == null) {
            return "redirect:/login?error";
        }

        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(loggedInUserEmail);
        if (optionalUser.isPresent()) {
            Usuario usuario = optionalUser.get();
            model.addAttribute("usuario", usuario);
            model.addAttribute("username", usuario.getEmail());
        } else {
            return "redirect:/login?error";
        }
        return "login/perfil"; // perfil.html está em /templates/login/
    }

    @PostMapping("/perfil")
    public String updatePerfil(@RequestParam String email,
                               @RequestParam(required = false) String novaSenha,
                               HttpSession session,
                               Model model) {
        String loggedInUserEmail = (String) session.getAttribute("loggedInUser");
        if (loggedInUserEmail == null) {
            return "redirect:/login?error";
        }

        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(loggedInUserEmail);
        if (optionalUser.isPresent()) {
            Usuario usuario = optionalUser.get();
            usuario.setEmail(email);

            if (novaSenha != null && !novaSenha.isEmpty()) {
                usuario.setSenha(novaSenha); // ATENÇÃO: Senha em texto puro. Considere criptografia.
            }
            usuarioRepository.save(usuario);

            session.setAttribute("loggedInUser", usuario.getEmail());
            model.addAttribute("success", "Perfil atualizado com sucesso!");
        } else {
            model.addAttribute("error", "Erro ao atualizar perfil. Usuário não encontrado.");
        }
        return showPerfil(session, model); // Redireciona para o GET de /perfil para recarregar a página
    }

    // --- Métodos para a funcionalidade de "Esqueci a Senha" ---

    @GetMapping("/esqueci-senha") // URL em português
    public String showEsqueciSenhaPage() {
        return "login/esqueci-senha"; // esqueci-senha.html está em /templates/login/
    }

    @PostMapping("/esqueci-senha") // URL em português
    public String processEsqueciSenhaRequest(@RequestParam String email, Model model) {
        Optional<Usuario> optionalUser = usuarioRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            Usuario user = optionalUser.get();
            
            // Opcional: Remover tokens antigos para este usuário para evitar múltiplos tokens ativos.
            // Para um único usuário, isso é menos crítico, mas boa prática para sistemas multi-usuário.
            passwordResetTokenRepository.deleteByUserId(user.getId());

            String token = UUID.randomUUID().toString(); // Gera um token único e aleatório
            LocalDateTime expiryDate = LocalDateTime.now().plus(30, ChronoUnit.MINUTES); // Token válido por 30 minutos

            // Salva o token no banco de dados, associado ao ID do usuário
            PasswordResetToken resetToken = new PasswordResetToken(token, user.getId(), expiryDate);
            passwordResetTokenRepository.save(resetToken);

            // Monta o link completo para o usuário clicar no e-mail
            // ATENÇÃO: Substitua "http://localhost:8080" pela URL base da sua aplicação em produção (ex: "https://seusite.com")
            String resetLink = "http://localhost:8080/redefinir-senha?token=" + token;

            // Envia o e-mail de redefinição de senha
            notificacaoService.sendPasswordResetEmail(user.getEmail(), resetLink);

            model.addAttribute("message", "Um link de redefinição de senha foi enviado para o seu e-mail.");
            return "login/esqueci-senha"; // Exibe a mensagem na mesma página, template em /templates/login/
        } else {
            // Mensagem genérica para não revelar se o e-mail existe no sistema por segurança
            model.addAttribute("error", "Se o e-mail estiver cadastrado, um link de redefinição será enviado.");
            return "login/esqueci-senha"; // Exibe a mensagem na mesma página, template em /templates/login/
        }
    }

    @GetMapping("/redefinir-senha") // URL em português
    public String showRedefinirSenhaPage(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);

        // Valida se o token existe e se ainda não expirou
        if (optionalToken.isPresent() && optionalToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            model.addAttribute("token", token); // Passa o token para o formulário de redefinição
            return "login/redefinir-senha"; // redefinir-senha.html está em /templates/login/
        } else {
            model.addAttribute("error", "Link de redefinição inválido ou expirado. Por favor, solicite um novo.");
            return "login"; // Redireciona para a página de login (na raiz) com mensagem de erro
        }
    }

    @PostMapping("/redefinir-senha") // URL em português
    public String redefinirSenha(@RequestParam("token") String token,
                                @RequestParam("novaSenha") String novaSenha,
                                @RequestParam("confirmarNovaSenha") String confirmarNovaSenha,
                                Model model) {

        // Verifica se as senhas digitadas são iguais
        if (!novaSenha.equals(confirmarNovaSenha)) {
            model.addAttribute("error", "As senhas não coincidem. Por favor, digite novamente.");
            model.addAttribute("token", token); // Mantém o token no modelo para que o usuário possa tentar novamente
            return "login/redefinir-senha"; // Retorna ao formulário de redefinição, template em /templates/login/
        }

        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);

        // Valida o token novamente antes de proceder com a redefinição da senha
        if (optionalToken.isPresent() && optionalToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            PasswordResetToken resetToken = optionalToken.get();
            Optional<Usuario> optionalUser = usuarioRepository.findById(resetToken.getUserId());

            if (optionalUser.isPresent()) {
                Usuario user = optionalUser.get();
                // ATENÇÃO CRÍTICA: A senha está sendo armazenada em texto puro aqui.
                // Para um ambiente de produção, é ABSOLUTAMENTE ESSENCIAL usar um algoritmo de hashing
                // como BCryptPasswordEncoder para proteger as senhas.
                user.setSenha(novaSenha); 
                usuarioRepository.save(user); // Salva a nova senha no banco de dados

                // Invalida o token após o uso para evitar que seja reutilizado
                passwordResetTokenRepository.delete(resetToken);

                model.addAttribute("logout", true); // Usa a mensagem de sucesso de logout na página de login
                return "redirect:/login"; // Redireciona para a página de login (na raiz) após a redefinição bem-sucedida
            }
        }
        // Se o token for inválido ou expirado neste ponto (por exemplo, tentativa de uso duplo)
        model.addAttribute("error", "Não foi possível redefinir a senha. O link pode ser inválido ou expirado. Tente novamente.");
        return "redirect:/esqueci-senha"; // Redireciona para o formulário de "Esqueci a Senha"
    }
}


