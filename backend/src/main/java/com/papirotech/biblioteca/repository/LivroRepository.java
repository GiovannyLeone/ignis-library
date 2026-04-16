package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Integer> {

    Optional<Livro> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    // RF04 — buscar por título, autor ou ISBN
    @Query("SELECT l FROM Livro l WHERE " +
           "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(l.autor)  LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "l.isbn LIKE CONCAT('%', :termo, '%')")
    Page<Livro> buscarPorTermo(@Param("termo") String termo, Pageable pageable);

    // RF05 — listar disponíveis
    @Query("SELECT l FROM Livro l WHERE l.quantidadeDisponivel > 0")
    Page<Livro> findDisponiveis(Pageable pageable);
}
