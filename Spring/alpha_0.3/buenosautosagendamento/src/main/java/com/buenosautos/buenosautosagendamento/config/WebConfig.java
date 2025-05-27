package com.buenosautos.buenosautosagendamento.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // O interceptor agora intercepta TUDO (/**), e a lógica de exclusão está dentro do próprio interceptor
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**"); // Intercepta todas as requisições
        // Não é necessário excluir paths aqui, o interceptor faz isso
    }
}