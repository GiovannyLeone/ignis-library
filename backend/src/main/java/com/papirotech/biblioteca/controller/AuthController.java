package com.papirotech.biblioteca.controller;

import com.papirotech.biblioteca.dto.request.LoginRequest;
import com.papirotech.biblioteca.dto.response.TokenResponse;
import com.papirotech.biblioteca.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login — Cliente, Administrador ou Estoquista")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
