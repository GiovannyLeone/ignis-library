package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;

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

    public Livro editarLivro(Integer idLivro, Livro livroComNovosDados) {

        Optional<Livro> caixaDoLivro = livroRepository.findById(idLivro);

        if (caixaDoLivro.isEmpty()) {
            throw new RuntimeException("LivroNaoEncontradoException: Livro não encontrado no sistema");
        }

        Livro livroExistente = caixaDoLivro.get();

        livroExistente.setTitulo(livroComNovosDados.getTitulo());
        livroExistente.setAutor(livroComNovosDados.getAutor());
        livroExistente.setIsbn(livroComNovosDados.getIsbn());
        livroExistente.setEditora(livroComNovosDados.getEditora());
        livroExistente.setSinopse(livroComNovosDados.getSinopse());
        livroExistente.setAnoPublicacao(livroComNovosDados.getAnoPublicacao());

        return livroRepository.save(livroExistente);

    }

    public boolean removerLivro(Integer idLivro) {

        Optional<Livro> caixaDoLivro = livroRepository.findById(idLivro);

        if (caixaDoLivro.isEmpty()) {
            throw new RuntimeException("LivroNaoEncontradoException: Livro não encontrado no sistema");
        }

        Livro livroExistente = caixaDoLivro.get();

        if (livroExistente.getEmprestimos() != null && livroExistente.getEmprestimos().stream()
                .anyMatch(e -> {
                    var status = e.getStatus();

                    return status != StatusEmprestimo.DEVOLVIDO
                            && status != StatusEmprestimo.DEVOLVIDO_COM_ATRASO
                            && status != StatusEmprestimo.CANCELADO;
                })) {
            return false;
        }
        livroRepository.delete(livroExistente);

        return true;

    }
}