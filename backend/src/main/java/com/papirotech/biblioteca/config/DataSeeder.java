package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.PerfilAcesso;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AclRepository           aclRepository;
    private final StatusUsuarioRepository statusUsuarioRepository;
    private final UsuarioRepository       usuarioRepository;
    private final PasswordEncoder         passwordEncoder;

    @Override
    public void run(String... args) {
        // tb_acl e tb_status_usuario já foram populados pelo SQL init
        // Aqui apenas cria o usuário padrão de teste
        seedUsuarioPadrao();
    }

    private void seedUsuarioPadrao() {
        if (usuarioRepository.existsByEmail("cliente@biblioteca.com")) return;

        Acl acl = aclRepository.findByDescricao(PerfilAcesso.CLIENTE.name())
            .orElseThrow(() -> new RuntimeException("ACL CLIENTE não encontrada. Verifique o SQL init."));
        StatusUsuario ativo = statusUsuarioRepository.findByDescricao(StatusCliente.ATIVO.name())
            .orElseThrow(() -> new RuntimeException("Status ATIVO não encontrado. Verifique o SQL init."));

        usuarioRepository.save(Usuario.builder()
            .nome("Cliente Teste")
            .email("cliente@biblioteca.com")
            .cpf("11111111111")
            .senha(passwordEncoder.encode("cliente123"))
            .dataNascimento(LocalDate.of(2000, 6, 15))
            .sexo("F")
            .acl(acl)
            .statusUsuario(ativo)
            .build());

        log.info(">>> Usuário padrão criado: cliente@biblioteca.com / cliente123");
    }
}
