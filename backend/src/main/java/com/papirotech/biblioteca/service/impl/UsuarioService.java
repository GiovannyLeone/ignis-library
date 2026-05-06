package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.request.*;
import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.exception.*;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final ClienteRepository       clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final AclRepository           aclRepository;
    private final StatusUsuarioRepository statusUsuarioRepository;
    private final PasswordEncoder         passwordEncoder;
    private final BibliotecaMapper        mapper;
    private final JdbcTemplate            jdbc;

    // ─── RF06: cadastrarCliente() ─────────────────────────────────────────────
    @Transactional
    public UsuarioResponse cadastrar(CadastroUsuarioRequest req) {
        verificarEmailDuplicado(req.email());
        verificarCpfDuplicado(req.cpf());

        Cliente c = Cliente.builder()
            .nome(req.nome()).email(req.email()).cpf(req.cpf())
            .senha(passwordEncoder.encode(req.senha()))
            .dataNascimento(req.dataNascimento()).sexo(req.sexo())
            .acl(buscarAcl("CLIENTE"))
            .statusUsuario(buscarStatus(StatusCliente.ATIVO.name()))
            .build();

        return mapper.toResponse(clienteRepository.save(c));
    }

    // ─── RF16: atualizar próprio perfil ──────────────────────────────────────
    @Transactional
    public UsuarioResponse atualizar(AtualizarUsuarioRequest req) {
        Cliente c = clienteLogado();
        if (req.nome()           != null) c.setNome(req.nome());
        if (req.email()          != null) c.setEmail(req.email());
        if (req.dataNascimento() != null) c.setDataNascimento(req.dataNascimento());
        if (req.sexo()           != null) c.setSexo(req.sexo());
        if (req.senha()          != null) c.setSenha(passwordEncoder.encode(req.senha()));
        return mapper.toResponse(clienteRepository.save(c));
    }

    public UsuarioResponse buscarPerfil() {
        return mapper.toResponse(clienteLogado());
    }

    public PageResponse<UsuarioResponse> listarTodos(int pagina, int tamanho) {
        Page<UsuarioResponse> page = clienteRepository
            .findAll(PageRequest.of(pagina, tamanho, Sort.by("nome")))
            .map(mapper::toResponse);
        return mapper.toPageResponse(page);
    }

    public UsuarioResponse buscarPorId(Integer id) {
        return mapper.toResponse(clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNaoEncontradoException("Usuário não encontrado: id=" + id)));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    public Cliente clienteLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clienteRepository.findByEmail(email)
            .orElseThrow(() -> new ClienteNaoEncontradoException("Usuário não encontrado."));
    }

    private void verificarEmailDuplicado(String email) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM tb_usuario WHERE des_email = ?", Integer.class, email);
        if (count != null && count > 0)
            throw new ClienteJaExisteException("E-mail já cadastrado: " + email);
    }

    private void verificarCpfDuplicado(String cpf) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM tb_usuario WHERE des_cpf = ?", Integer.class, cpf);
        if (count != null && count > 0)
            throw new ClienteJaExisteException("CPF já cadastrado.");
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
