package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.response.PageResponse;
import com.papirotech.biblioteca.dto.response.UsuarioResponse;
import com.papirotech.biblioteca.service.impl.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController — endpoints do Administrador conforme documentação.
 *
 * Conforme seção 3.5, o Administrador tem os métodos:
 *   + getCargo() / setCargo()
 *   + aplicarPenalidade(emprestimo) — será adicionado com Emprestimo
 *   + removerPenalidade(emprestimo) — será adicionado com Emprestimo
 *
 * Conforme requisitos funcionais:
 *   - Gerenciar clientes (visualizar)
 *   - Visualizar relatórios de disponibilidade e histórico de todos os clientes
 *   - Gerenciar e aplicar penalidades por atraso
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@RequiredArgsConstructor
@Tag(name = "Administrador", description = "Endpoints exclusivos do Administrador")
public class AdminController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos os usuários (Admin)")
    public ResponseEntity<PageResponse<UsuarioResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(usuarioService.listarTodos(pagina, tamanho));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Buscar usuário por ID (Admin)")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }
}
