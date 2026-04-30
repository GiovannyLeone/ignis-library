package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.dto.request.LoginRequest;
import com.papirotech.biblioteca.dto.response.LoginResponse;
import com.papirotech.biblioteca.entity.Pessoa;
import com.papirotech.biblioteca.repository.PessoaRepository;
import com.papirotech.biblioteca.service.AuthService;
import com.papirotech.biblioteca.service.CriptografiaService;
import com.papirotech.biblioteca.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceimpl implements AuthService {

    private final PessoaRepository pessoaRepository;
    private final CriptografiaService criptografiaService;
    private final JwtService jwtService;

    @Override
    public LoginResponse autenticar(LoginRequest request) {

        String identificadorHash = criptografiaService.criptografar(request.getLogin());

        //  procura a pessoa no banco
        Pessoa pessoa = pessoaRepository.findByCpf(identificadorHash)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // valida a senha
        if (!criptografiaService.validar(request.getSenha(), pessoa.getSenha())) {
            throw new RuntimeException("Senha inválida");
        }

        // gera o Token
        String token = jwtService.gerarToken(pessoa);

        // retorna a resposta
        return new LoginResponse(
                token,
                pessoa.getNome(),
                pessoa.getAcl().getNome()
        );
    }
}