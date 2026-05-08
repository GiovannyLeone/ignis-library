package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "RF07, RF08, RF13 — Cliente")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;
    private final UsuarioService    usuarioService;

    // RF07 — gerarCodigoEmprestimo(livro, cliente) — seção 3.4
    @PostMapping("/livros/{livroId}/reservar")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "RF07 — Gerar código de empréstimo")
    public ResponseEntity<EmprestimoResponse> reservar(@PathVariable Integer livroId) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(emprestimoService.gerarCodigoEmprestimo(livroId, usuarioService.clienteLogado()));
    }

    // RF08 — gerarCodigoDevolucao(emprestimo) — seção 3.4
    @PostMapping("/{id}/gerar-codigo-devolucao")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "RF08 — Gerar código de devolução")
    public ResponseEntity<EmprestimoResponse> gerarCodigoDevolucao(@PathVariable Integer id) {
        return ResponseEntity.ok(
            emprestimoService.gerarCodigoDevolucao(id, usuarioService.clienteLogado()));
    }

    // RF13 — consultarHistorico() — seção 3.4
    @GetMapping("/meu-historico")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "RF13 — Consultar próprio histórico de empréstimos")
    public ResponseEntity<PageResponse<EmprestimoResponse>> meuHistorico(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {
        return ResponseEntity.ok(
            emprestimoService.historicoPorCliente(
                usuarioService.clienteLogado().getId(), pagina, tamanho));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMINISTRADOR') or hasRole('ESTOQUISTA')")
    @Operation(summary = "Buscar empréstimo por ID")
    public ResponseEntity<EmprestimoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(emprestimoService.buscarPorId(id));
    }
}
