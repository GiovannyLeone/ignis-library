package com.papirotech.biblioteca.service;
import com.papirotech.biblioteca.entity.Favorito;
import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.entity.Pessoa;
import com.papirotech.biblioteca.repository.FavoritoRepository;
import com.papirotech.biblioteca.repository.LivroRepository;
import com.papirotech.biblioteca.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LivroRepository livroRepository;

    public void favoritarLivro(Integer pessoaId, Integer livroId) {
        Pessoa pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Optional<Favorito> favoritoExistente = favoritoRepository.findByPessoaIdAndLivroId(pessoaId, livroId);

        if (favoritoExistente.isPresent()) {
            favoritoRepository.delete(favoritoExistente.get());
        } else {
            Favorito novoFavorito = Favorito.builder()
                    .pessoa(pessoa)
                    .livro(livro)
                    .build();
            favoritoRepository.save(novoFavorito);
        }
    }

}
