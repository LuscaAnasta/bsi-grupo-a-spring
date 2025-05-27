package com.buenosautos.buenosautosagendamento.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.buenosautos.buenosautosagendamento.login.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	  Optional<Usuario> findByEmail(String email);
}
