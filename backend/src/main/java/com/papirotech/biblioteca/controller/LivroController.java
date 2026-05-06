package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.request.*;
import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "RF01-RF05, RF11")
public class LivroController {

    private final LivroService livroService;

    @GetMapping
    @Operation(summary = "RF05 — Listar todos os livros (público)")
    public ResponseEntity<PageResponse<LivroResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(livroService.listar(pagina, tamanho));
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "RF05 — Listar livros disponíveis (público)")
    public ResponseEntity<PageResponse<LivroResponse>> listarDisponiveis(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(livroService.listarDisponiveis(pagina, tamanho));
    }

    @GetMapping("/buscar")
    @Operation(summary = "RF04 — Buscar por título, autor ou ISBN (público)")
    public ResponseEntity<PageResponse<LivroResponse>> buscar(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(livroService.buscar(termo, pagina, tamanho));
    }

    @GetMapping("/{id}")
    @Operation(summary = "RF11 — Buscar livro por ID (público)")
    public ResponseEntity<LivroResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "RF01 — Cadastrar livro (Admin)")
    public ResponseEntity<LivroResponse> cadastrar(@RequestBody @Valid CadastroLivroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.adicionarLivro(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "RF02 — Editar livro (Admin)")
    public ResponseEntity<LivroResponse> atualizar(
            @PathVariable Integer id, @RequestBody @Valid AtualizarLivroRequest req) {
        return ResponseEntity.ok(livroService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "RF03 — Remover livro (Admin)")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        livroService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
