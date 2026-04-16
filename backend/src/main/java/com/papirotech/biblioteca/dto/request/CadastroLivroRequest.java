package com.papirotech.biblioteca.dto.request;

import jakarta.validation.constraints.*;

public record CadastroLivroRequest(
    @NotBlank String isbn,
    @NotBlank String titulo,
    @NotBlank String autor,
    @NotNull  Integer categoriaId,
    @NotBlank String editora,
    @NotBlank String sinopse,
    @NotNull  Integer anoPublicacao,
    @NotNull @Min(1) Integer quantidadeTotal
) {}
