package com.buenosautos.buenosautosagendamento.initdb;

import com.buenosautos.buenosautosagendamento.login.model.Usuario; // Pacote correto para sua Usuario
import com.buenosautos.buenosautosagendamento.login.repository.UsuarioRepository; // Repositório correto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AddUsuarioBase implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            System.out.println("Inserindo usuário padrão de login...");
            // Use 'email' e 'senha' aqui
            String user = "bsipr4sem.buenosautos@gmail.com";
            Usuario adminUser = new Usuario(user, "123"); // Exemplo de email
            usuarioRepository.save(adminUser);
            System.out.println("Usuário padrão"+user+" inserido com sucesso!");
        } else {
            System.out.println("Usuários já existentes, pulando inserção inicial de usuário.");
        }
    }
}