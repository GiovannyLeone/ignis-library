package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.JwtService;
import com.papirotech.biblioteca.dto.request.LoginRequest;
import com.papirotech.biblioteca.dto.response.TokenResponse;
import com.papirotech.biblioteca.repository.AdministradorRepository;
import com.papirotech.biblioteca.repository.ClienteRepository;
import com.papirotech.biblioteca.repository.EstoquistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager   authManager;
    private final JwtService              jwtService;
    private final AdministradorRepository administradorRepository;
    private final ClienteRepository       clienteRepository;
    private final EstoquistaRepository    estoquistaRepository;

    public TokenResponse login(LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.login(), req.senha()));

        // Tenta como Administrador
        var adminOpt = administradorRepository.findByEmail(req.login());
        if (adminOpt.isPresent()) {
            var a = adminOpt.get();
            return new TokenResponse(
                jwtService.gerarToken(a), "Bearer", "ADMINISTRADOR", a.getNome());
        }

        // Tenta como Cliente
        var clienteOpt = clienteRepository.findByEmail(req.login());
        if (clienteOpt.isPresent()) {
            var c = clienteOpt.get();
            return new TokenResponse(
                jwtService.gerarToken(c), "Bearer", "CLIENTE", c.getNome());
        }

        // Tenta como Estoquista
        var estoquista = estoquistaRepository.findByCodigoAcesso(req.login())
            .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));
        return new TokenResponse(
            jwtService.gerarToken(estoquista), "Bearer",
            "ESTOQUISTA", estoquista.getCodigoAcesso());
    }
}
