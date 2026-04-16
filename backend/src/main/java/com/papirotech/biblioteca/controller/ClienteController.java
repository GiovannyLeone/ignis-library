package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.request.AtualizarUsuarioRequest;
import com.papirotech.biblioteca.dto.request.CadastroUsuarioRequest;
import com.papirotech.biblioteca.dto.response.UsuarioResponse;
import com.papirotech.biblioteca.service.impl.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "RF06, RF16")
public class ClienteController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    @Operation(summary = "RF06 — Cadastrar novo cliente (público)")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid CadastroUsuarioRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(req));
    }

    @GetMapping("/perfil")
    @Operation(summary = "Visualizar próprio perfil")
    public ResponseEntity<UsuarioResponse> perfil() {
        return ResponseEntity.ok(usuarioService.buscarPerfil());
    }

    @PutMapping("/perfil")
    @Operation(summary = "RF16 — Atualizar próprio perfil")
    public ResponseEntity<UsuarioResponse> atualizar(@RequestBody @Valid AtualizarUsuarioRequest req) {
        return ResponseEntity.ok(usuarioService.atualizar(req));
    }
}
