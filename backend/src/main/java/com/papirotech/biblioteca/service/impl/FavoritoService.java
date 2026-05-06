package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.exception.LivroNaoEncontradoException;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final LivroRepository    livroRepository;
    private final BibliotecaMapper   mapper;

    // favoritarLivro(livro) — seção 3.4 / RF15
    @Transactional
    public MensagemResponse favoritarLivro(Integer livroId, Cliente cliente) {
        Livro livro = livroRepository.findById(livroId)
            .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado: id=" + livroId));

        Optional<Favorito> existente = favoritoRepository
            .findByClienteIdAndLivroIdLivro(cliente.getId(), livroId);

        if (existente.isPresent()) {
            favoritoRepository.delete(existente.get());
            return new MensagemResponse("Livro '" + livro.getTitulo() + "' removido dos favoritos.");
        }

        favoritoRepository.save(Favorito.builder().cliente(cliente).livro(livro).build());
        return new MensagemResponse("Livro '" + livro.getTitulo() + "' adicionado aos favoritos.");
    }

    // getFavoritos() — seção 3.4
    public List<FavoritoResponse> listarFavoritos(Integer clienteId) {
        return favoritoRepository.findByClienteId(clienteId)
            .stream()
            .map(mapper::toResponse)
            .toList();
    }
}
