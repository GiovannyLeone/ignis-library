package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Estoquista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoquistaRepository extends JpaRepository<Estoquista, Integer> {
    Optional<Estoquista> findByCodigoAcesso(String codigoAcesso);
    boolean existsByCodigoAcesso(String codigoAcesso);
}
