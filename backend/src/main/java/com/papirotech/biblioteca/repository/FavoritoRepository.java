package com.papirotech.biblioteca.repository;
import com.papirotech.biblioteca.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface FavoritoRepository extends JpaRepository<Favorito, Integer> {
    Optional<Favorito> findByClienteIdAndLivroIdLivro(Integer clienteId, Integer livroId);
    List<Favorito> findByClienteId(Integer clienteId);
}
