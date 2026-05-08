package com.papirotech.biblioteca.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmprestimoResponse(
    Integer id,
    String codigoRetirada,
    String codigoDevolucao,
    LocalDateTime dataEmprestimo,
    LocalDate dataDevolucaoPrevista,
    LocalDate dataDevolucaoReal,
    String status,
    Boolean penalidadeGerada,
    LivroResponse livro,
    UsuarioResponse cliente
) {}
