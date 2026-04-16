package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PessoaRepository extends JpaRepository<Pessoa, Integer> {

    // Verifica e-mail em TODA tb_usuario, independente do discriminador
    @Query("SELECT COUNT(p) > 0 FROM Pessoa p WHERE p.email = :email")
    boolean existsByEmailEmTodos(@Param("email") String email);

    // Verifica CPF em TODA tb_usuario, independente do discriminador
    @Query("SELECT COUNT(p) > 0 FROM Pessoa p WHERE p.cpf = :cpf")
    boolean existsByCpfEmTodos(@Param("cpf") String cpf);

    // Verifica discriminador específico para o DataSeeder
    @Query("SELECT COUNT(p) > 0 FROM Pessoa p WHERE p.email = :email AND TYPE(p) = :tipo")
    boolean existsByEmailETipo(@Param("email") String email, @Param("tipo") Class<?> tipo);
}
