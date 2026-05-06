package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    public Livro adicionarLivro(Livro livro) {
        // validação de ISBN
        if (livroRepository.existsByIsbn(livro.getIsbn())) {
            throw new RuntimeException("LivroJaExisteException: Este ISBN já está cadastrado!");
        }

        // data de cadastro automática
        livro.setDataCadastro(LocalDate.now());

        // estoque inicial
        livro.setQuantidadeDisponivel(livro.getQuantidadeTotal());

        // salva no banco
        return livroRepository.save(livro);
    }
}