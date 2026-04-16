package com.papirotech.biblioteca.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CadastroUsuarioRequest(
    @NotBlank String nome,
    @NotBlank @Email String email,
    @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve ter 11 dígitos") String cpf,
    @NotBlank @Size(min = 6) String senha,
    @NotNull LocalDate dataNascimento,
    @NotBlank String sexo
) {}
