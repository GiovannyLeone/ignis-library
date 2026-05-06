package com.papirotech.biblioteca.dto.response;
import java.time.LocalDate;
public record PessoaResponse(Integer id, String nome, String email, String cpf, LocalDate dataNascimento, String sexo, String status, String perfil) {}
