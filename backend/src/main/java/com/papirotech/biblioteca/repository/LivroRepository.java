package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {

    // verifica se já existe um livro com o ISBN informado antes de salvar
    boolean existsByIsbn(String isbn);
}