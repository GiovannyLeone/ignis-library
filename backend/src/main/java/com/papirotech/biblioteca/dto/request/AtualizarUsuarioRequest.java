package com.papirotech.biblioteca.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AtualizarUsuarioRequest(
    String nome,
    @Email String email,
    @Size(min = 6) String senha,
    LocalDate dataNascimento,
    String sexo
) {}
