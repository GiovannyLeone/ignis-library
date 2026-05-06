package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.UsuarioService;
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
@Tag(name = "Administrador", description = "Endpoints exclusivos do Administrador")
public class AdminController {

    private final UsuarioService usuarioService;

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
}
