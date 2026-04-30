package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.entity.Cliente;
import com.papirotech.biblioteca.entity.Acl;
import com.papirotech.biblioteca.entity.StatusUsuario;
import com.papirotech.biblioteca.repository.ClienteRepository;
import com.papirotech.biblioteca.repository.AclRepository;
import com.papirotech.biblioteca.repository.StatusUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl {

    private final ClienteRepository clienteRepository;
    private final AclRepository aclRepository;
    private final StatusUsuarioRepository statusUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // RF06
    public Cliente cadastrarCliente(Cliente cliente) {

        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (clienteRepository.existsByCpf(cliente.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        Acl aclCliente = aclRepository.findByDescricao("CLIENTE")
                .orElseThrow(() -> new RuntimeException("ACL CLIENTE não encontrada"));

        StatusUsuario statusAtivo = statusUsuarioRepository.findByDescricao("ATIVO")
                .orElseThrow(() -> new RuntimeException("Status ATIVO não encontrado"));

        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        cliente.setCpf(passwordEncoder.encode(cliente.getCpf()));
        cliente.setAcl(aclCliente);
        cliente.setStatusUsuario(statusAtivo);

        return clienteRepository.save(cliente);
    }

    // RF16
    public Cliente editarCliente(Long id, Cliente dados) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        if (dados.getNome() != null) cliente.setNome(dados.getNome());
        if (dados.getEmail() != null) cliente.setEmail(dados.getEmail());
        if (dados.getSexo() != null) cliente.setSexo(dados.getSexo());
        if (dados.getDataNascimento() != null) cliente.setDataNascimento(dados.getDataNascimento());

        if (dados.getSenha() != null) {
            cliente.setSenha(passwordEncoder.encode(dados.getSenha()));
        }

        if (dados.getCpf() != null) {
            cliente.setCpf(passwordEncoder.encode(dados.getCpf()));
        }

        return clienteRepository.save(cliente);
    }

    // RF16
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    // RF16
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }
}