package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Integer> {

    List<Livro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String titulo, String autor, String isbn
    );

    List<Livro> findByQuantidadeDisponivelGreaterThan(Integer quantidadeDisponivel);
}