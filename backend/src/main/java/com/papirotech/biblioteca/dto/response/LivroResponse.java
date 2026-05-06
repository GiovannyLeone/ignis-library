package com.papirotech.biblioteca.dto.response;
import java.time.LocalDate;
public record LivroResponse(
    Integer idLivro, String isbn, String titulo, String autor,
    CategoriaResponse categoria, String editora, String sinopse,
    LocalDate dataCadastro, Integer anoPublicacao,
    Integer quantidadeTotal, Integer quantidadeDisponivel, boolean disponivel
) {}
