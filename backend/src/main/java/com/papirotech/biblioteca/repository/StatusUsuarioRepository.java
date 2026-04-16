package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.StatusUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusUsuarioRepository extends JpaRepository<StatusUsuario, Integer> {
    Optional<StatusUsuario> findByDescricao(String descricao);
}
