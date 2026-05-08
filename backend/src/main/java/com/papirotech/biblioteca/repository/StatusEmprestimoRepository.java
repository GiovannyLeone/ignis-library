package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.StatusEmprestimoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusEmprestimoRepository extends JpaRepository<StatusEmprestimoEntity, Integer> {
    Optional<StatusEmprestimoEntity> findByDescricao(String descricao);
}
