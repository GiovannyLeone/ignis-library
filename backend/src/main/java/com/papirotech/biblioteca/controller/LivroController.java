package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.request.AtualizarLivroRequest;
import com.papirotech.biblioteca.dto.request.CadastroLivroRequest;
import com.papirotech.biblioteca.dto.response.LivroResponse;
import com.papirotech.biblioteca.dto.response.PageResponse;
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
@Tag(name = "Livros", description = "RF01-RF05, RF11 — Gerenciamento do acervo")
public class LivroController {

    private final LivroService livroService;

    // RF05 — listar todos os livros (público)
    @GetMapping
    @Operation(summary = "RF05 — Listar todos os livros")
    public ResponseEntity<PageResponse<LivroResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(livroService.listar(pagina, tamanho));
    }

    // RF05 — listar disponíveis (público)
    @GetMapping("/disponiveis")
    @Operation(summary = "RF05 — Listar livros disponíveis para empréstimo")
    public ResponseEntity<PageResponse<LivroResponse>> listarDisponiveis(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(livroService.listarDisponiveis(pagina, tamanho));
    }

    // RF04 — buscar por título, autor ou ISBN (público)
    @GetMapping("/buscar")
    @Operation(summary = "RF04 — Buscar livros por título, autor ou ISBN")
    public ResponseEntity<PageResponse<LivroResponse>> buscar(
            @RequestParam String termo,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(livroService.buscar(termo, pagina, tamanho));
    }

    // RF11 — verificar disponibilidade (público)
    @GetMapping("/{id}")
    @Operation(summary = "RF11 — Buscar livro por ID e verificar disponibilidade")
    public ResponseEntity<LivroResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    // RF01 — cadastrar livro (Admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "RF01 — Cadastrar novo livro (Admin)")
    public ResponseEntity<LivroResponse> cadastrar(@RequestBody @Valid CadastroLivroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(livroService.adicionarLivro(req));
    }

    // RF02 — editar livro (Admin)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "RF02 — Editar livro (Admin)")
    public ResponseEntity<LivroResponse> atualizar(
            @PathVariable Integer id,
            @RequestBody @Valid AtualizarLivroRequest req) {
        return ResponseEntity.ok(livroService.atualizar(id, req));
    }

    // RF03 — remover livro (Admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "RF03 — Remover livro do acervo (Admin)")
    public ResponseEntity<Void> remover(@PathVariable Integer id) {
        livroService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
