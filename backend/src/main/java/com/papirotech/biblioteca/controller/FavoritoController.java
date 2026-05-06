package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
@PreAuthorize("hasRole('CLIENTE')")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "RF15 — Favoritar e desfavoritar livros (Cliente)")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final UsuarioService  usuarioService;

    @PostMapping("/{livroId}/toggle")
    @Operation(summary = "RF15 — Favoritar / desfavoritar livro (toggle)")
    public ResponseEntity<MensagemResponse> toggle(@PathVariable Integer livroId) {
        return ResponseEntity.ok(
            favoritoService.favoritarLivro(livroId, usuarioService.clienteLogado()));
    }

    @GetMapping
    @Operation(summary = "Listar livros favoritos do cliente")
    public ResponseEntity<List<FavoritoResponse>> listar() {
        return ResponseEntity.ok(
            favoritoService.listarFavoritos(usuarioService.clienteLogado().getId()));
    }
}
