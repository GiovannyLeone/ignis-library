package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.JwtService;
import com.papirotech.biblioteca.dto.request.LoginRequest;
import com.papirotech.biblioteca.dto.response.TokenResponse;
import com.papirotech.biblioteca.repository.EstoquistaRepository;
import com.papirotech.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtService            jwtService;
    private final UsuarioRepository     usuarioRepository;
    private final EstoquistaRepository  estoquistaRepository;

    public TokenResponse login(LoginRequest req) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.login(), req.senha()));

        // Tenta como Usuario
        var usuarioOpt = usuarioRepository.findByEmail(req.login());
        if (usuarioOpt.isPresent()) {
            var u = usuarioOpt.get();
            return new TokenResponse(
                jwtService.gerarToken(u), "Bearer",
                u.getAcl().getDescricao(), u.getNome());
        }

        // Tenta como Estoquista
        var estoquista = estoquistaRepository.findByCodigoAcesso(req.login())
            .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));
        return new TokenResponse(
            jwtService.gerarToken(estoquista), "Bearer",
            "ESTOQUISTA", estoquista.getCodigoAcesso());
    }
}
