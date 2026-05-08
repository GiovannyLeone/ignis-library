package com.papirotech.biblioteca.repository;
import com.papirotech.biblioteca.entity.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Integer>{
// busca se já existe um fav para aquele usuário e aquele livro
    Optional<Favorito> findByPessoaIdAndLivroId(Integer pessoaId, Integer livroId);

}
