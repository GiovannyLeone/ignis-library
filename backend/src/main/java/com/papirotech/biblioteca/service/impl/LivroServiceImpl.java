package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroServiceImpl {

    private final LivroRepository livroRepository;

    // RF04 - consultar livros por título, autor ou ISBN
    public List<Livro> consultarAcervo(String termo) {
        if (termo == null || termo.isBlank()) {
            return livroRepository.findAll();
        }

        return livroRepository
                .findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
                        termo, termo, termo
                );
    }

    // RF05 - listar livros para empréstimo
    public List<Livro> listarLivrosParaEmprestimo() {
        return livroRepository.findByQuantidadeDisponivelGreaterThan(0);
    }

    // RF11 - verificar disponibilidade de um livro
    public boolean verificarDisponibilidade(Integer idLivro) {
        return livroRepository.findById(idLivro)
                .map(Livro::verificarDisponibilidade)
                .orElse(false);
    }
}