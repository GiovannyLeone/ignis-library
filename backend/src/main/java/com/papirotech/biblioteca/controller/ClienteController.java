package com.papirotech.biblioteca.controller;
import com.papirotech.biblioteca.dto.request.*;
import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.PessoaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/clientes") @RequiredArgsConstructor
@Tag(name="Clientes", description="RF06, RF16")
public class ClienteController {
    private final PessoaService pessoaService;
    @PostMapping("/cadastro") @Operation(summary="RF06 — Cadastrar cliente (público)")
    public ResponseEntity<PessoaResponse> cadastrar(@RequestBody @Valid CadastroClienteRequest req) { return ResponseEntity.status(HttpStatus.CREATED).body(pessoaService.cadastrarCliente(req)); }
    @GetMapping("/perfil") @Operation(summary="Ver próprio perfil")
    public ResponseEntity<PessoaResponse> perfil() { return ResponseEntity.ok(pessoaService.buscarClientePorId(pessoaService.clienteLogado().getId())); }
    @PutMapping("/perfil") @Operation(summary="RF16 — Atualizar próprio perfil")
    public ResponseEntity<PessoaResponse> atualizar(@RequestBody @Valid AtualizarPessoaRequest req) { return ResponseEntity.ok(pessoaService.atualizarProprioPerfil(req)); }
}
