package com.papirotech.biblioteca.dto.request;

import jakarta.validation.constraints.Min;

public record AtualizarLivroRequest(
    String titulo,
    String autor,
    Integer categoriaId,
    String editora,
    String sinopse,
    Integer anoPublicacao,
    @Min(1) Integer quantidadeTotal
) {}
