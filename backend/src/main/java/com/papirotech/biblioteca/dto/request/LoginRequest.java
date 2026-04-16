package com.papirotech.biblioteca.dto.request;
import jakarta.validation.constraints.NotBlank;
public record LoginRequest(@NotBlank String login, @NotBlank String senha) {}
