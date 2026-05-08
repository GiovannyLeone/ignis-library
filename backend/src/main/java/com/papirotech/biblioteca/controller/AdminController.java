package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@RequiredArgsConstructor
@Tag(name = "Administrador", description = "RF12, RF14 — Administrador")
public class AdminController {

    private final UsuarioService    usuarioService;
    private final EmprestimoService emprestimoService;

    // Gerenciamento de usuários
    @GetMapping("/usuarios")
    @Operation(summary = "Listar todos os usuários")
    public ResponseEntity<PageResponse<UsuarioResponse>> listar(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(usuarioService.listarTodos(pagina, tamanho));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // RF12 — histórico de todos os clientes
    @GetMapping("/emprestimos")
    @Operation(summary = "RF12 — Listar todos os empréstimos")
    public ResponseEntity<PageResponse<EmprestimoResponse>> listarEmprestimos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(emprestimoService.listarTodos(pagina, tamanho));
    }

    @GetMapping("/emprestimos/clientes/{clienteId}")
    @Operation(summary = "RF12 — Histórico de empréstimos de um cliente")
    public ResponseEntity<PageResponse<EmprestimoResponse>> historicoPorCliente(
            @PathVariable Integer clienteId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(emprestimoService.historicoPorCliente(clienteId, pagina, tamanho));
    }

    // RF14 — aplicarPenalidade() — seção 3.5
    @PostMapping("/emprestimos/{id}/penalidade")
    @Operation(summary = "RF14 — Aplicar penalidade ao cliente")
    public ResponseEntity<EmprestimoResponse> aplicarPenalidade(@PathVariable Integer id) {
        return ResponseEntity.ok(emprestimoService.aplicarPenalidade(id));
    }

    // RF14 — removerPenalidade() — seção 3.5
    @DeleteMapping("/emprestimos/{id}/penalidade")
    @Operation(summary = "RF14 — Remover penalidade do cliente")
    public ResponseEntity<EmprestimoResponse> removerPenalidade(@PathVariable Integer id) {
        return ResponseEntity.ok(emprestimoService.removerPenalidade(id));
    }
}
