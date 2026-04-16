package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.request.AtualizarUsuarioRequest;
import com.papirotech.biblioteca.dto.request.CadastroUsuarioRequest;
import com.papirotech.biblioteca.dto.response.UsuarioResponse;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.exception.*;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository     usuarioRepository;
    private final AclRepository         aclRepository;
    private final StatusUsuarioRepository statusUsuarioRepository;
    private final PasswordEncoder       passwordEncoder;
    private final BibliotecaMapper      mapper;

    // ─── RF06: cadastrar Cliente ──────────────────────────────────────────────
    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest req) {
        if (usuarioRepository.existsByEmail(req.email()))
            throw new ClienteJaExisteException("E-mail já cadastrado: " + req.email());
        if (usuarioRepository.existsByCpf(req.cpf()))
            throw new ClienteJaExisteException("CPF já cadastrado.");

        Usuario u = Usuario.builder()
            .nome(req.nome())
            .email(req.email())
            .cpf(req.cpf())
            .senha(passwordEncoder.encode(req.senha()))
            .dataNascimento(req.dataNascimento())
            .sexo(req.sexo())
            .acl(buscarAcl("CLIENTE"))
            .statusUsuario(buscarStatus("ATIVO"))
            .build();

        return mapper.toResponse(usuarioRepository.save(u));
    }

    // ─── RF16: atualizar próprio perfil ──────────────────────────────────────
    @Transactional
    public UsuarioResponse atualizar(AtualizarUsuarioRequest req) {
        Usuario u = usuarioLogado();
        if (req.nome()           != null) u.setNome(req.nome());
        if (req.email()          != null) u.setEmail(req.email());
        if (req.dataNascimento() != null) u.setDataNascimento(req.dataNascimento());
        if (req.sexo()           != null) u.setSexo(req.sexo());
        if (req.senha()          != null) u.setSenha(passwordEncoder.encode(req.senha()));
        return mapper.toResponse(usuarioRepository.save(u));
    }

    public UsuarioResponse buscarPerfil() {
        return mapper.toResponse(usuarioLogado());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    public Usuario usuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ClienteNaoEncontradoException("Usuário não encontrado."));
    }

    private Acl buscarAcl(String descricao) {
        return aclRepository.findByDescricao(descricao)
            .orElseThrow(() -> new RuntimeException("ACL não encontrada: " + descricao));
    }

    private StatusUsuario buscarStatus(String descricao) {
        return statusUsuarioRepository.findByDescricao(descricao)
            .orElseThrow(() -> new RuntimeException("Status não encontrado: " + descricao));
    }
}
