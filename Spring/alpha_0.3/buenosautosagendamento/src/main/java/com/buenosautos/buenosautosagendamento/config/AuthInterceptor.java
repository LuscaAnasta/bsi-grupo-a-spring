package com.buenosautos.buenosautosagendamento.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    // Defina as URLs que são públicas (não exigem login)
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/login",
        "/logout",
        "/solicitacoes/nova", // <--- Adicione a rota de solicitação aqui
        "/solicitacoes", 
        "/protocolos/consultar",             // Formulário de consulta
        "/protocolos/detalhes-protocolo-consulta",// Se houver uma rota base para solicitações
        "/"                   // A página inicial, se for pública
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath(); // Para lidar com root context

        // Normaliza a URI removendo o contextPath se presente
        if (requestUri.startsWith(contextPath + "/")) {
            requestUri = requestUri.substring(contextPath.length());
        }

        // 1. Permite acesso a recursos estáticos e URLs explicitamente públicas
        if (requestUri.startsWith("/css/") || requestUri.startsWith("/js/") || requestUri.startsWith("/images/")) {
            return true; // Deixa passar recursos estáticos
        }

        if (PUBLIC_URLS.contains(requestUri)) {
            return true; // Deixa passar URLs públicas definidas
        }

        // 2. Para todas as outras URLs, verifica se o usuário está logado
        HttpSession session = request.getSession(false); // Não cria nova sessão se não existir

        if (session != null && session.getAttribute("loggedInUser") != null) {
            // Usuário logado, permite continuar para o controlador
            return true;
        } else {
            // Usuário não logado, redireciona para a página de login
            response.sendRedirect(request.getContextPath() + "/login?error=auth");
            return false; // Impede que a requisição chegue ao controlador
        }
    }
}