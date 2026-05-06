package com.papirotech.biblioteca.repository;
import com.papirotech.biblioteca.entity.Livro;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface LivroRepository extends JpaRepository<Livro, Integer> {
    Optional<Livro> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    @Query("SELECT l FROM Livro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%',:t,'%')) OR LOWER(l.autor) LIKE LOWER(CONCAT('%',:t,'%')) OR l.isbn LIKE CONCAT('%',:t,'%')")
    Page<Livro> buscarPorTermo(@Param("t") String termo, Pageable pageable);
    @Query("SELECT l FROM Livro l WHERE l.quantidadeDisponivel > 0")
    Page<Livro> findDisponiveis(Pageable pageable);
}
