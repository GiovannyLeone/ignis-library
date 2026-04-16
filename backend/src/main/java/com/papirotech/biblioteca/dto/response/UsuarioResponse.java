package com.papirotech.biblioteca.dto.response;

import java.time.LocalDate;

public record UsuarioResponse(
    Integer id,
    String nome,
    String email,
    String cpf,
    LocalDate dataNascimento,
    String sexo,
    String status,
    String perfil
) {}
