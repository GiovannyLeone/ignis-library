package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.response.EmprestimoResponse;
import com.papirotech.biblioteca.service.impl.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * EstoqueController — RF09 e RF10.
 * Referencia emprestarLivro() e devolverLivro() da classe Estoquista (seção 3.6).
 */
@RestController
@RequestMapping("/api/estoque")
@PreAuthorize("hasRole('ESTOQUISTA')")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "RF09, RF10 — Estoquista")
public class EstoqueController {

    private final EmprestimoService emprestimoService;

    // RF09 — emprestarLivro(emprestimo) — seção 3.6
    @PostMapping("/retirada/{codigoRetirada}")
    @Operation(summary = "RF09 — Registrar retirada do livro via código")
    public ResponseEntity<EmprestimoResponse> registrarRetirada(
            @PathVariable String codigoRetirada) {
        return ResponseEntity.ok(emprestimoService.realizarEmprestimo(codigoRetirada));
    }

    // RF10 — devolverLivro(emprestimo) — seção 3.6
    @PostMapping("/devolucao/{codigoDevolucao}")
    @Operation(summary = "RF10 — Registrar devolução do livro via código")
    public ResponseEntity<EmprestimoResponse> registrarDevolucao(
            @PathVariable String codigoDevolucao) {
        return ResponseEntity.ok(emprestimoService.registrarDevolucao(codigoDevolucao));
    }
}
