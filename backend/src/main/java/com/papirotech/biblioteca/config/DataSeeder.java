package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final AdministradorRepository administradorRepository;
    private final ClienteRepository       clienteRepository;
    private final PasswordEncoder         passwordEncoder;
    private final JdbcTemplate            jdbc;

    @Override
    public void run(String... args) {
        seedAdminPadrao();
        seedClientePadrao();
    }

    private void seedAdminPadrao() {
        // Verifica diretamente no banco se já existe um admin com esse e-mail
        // independente do discriminador atual
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM tb_usuario WHERE des_email = ?",
            Integer.class, "admin@biblioteca.com");

        if (count != null && count > 0) {
            // Garante que o discriminador está correto caso tenha sido criado errado antes
            jdbc.update(
                "UPDATE tb_usuario SET des_discriminador = 'ADMINISTRADOR', des_cargo = 'Diretor' " +
                "WHERE des_email = ? AND des_discriminador != 'ADMINISTRADOR'",
                "admin@biblioteca.com");
            log.info(">>> Admin padrão já existe — discriminador verificado.");
            return;
        }

        Acl acl = buscarAcl("ADMINISTRADOR");
        StatusUsuario ativo = buscarStatus(StatusCliente.ATIVO.name());

        administradorRepository.save(Administrador.builder()
            .nome("Administrador Padrão")
            .email("admin@biblioteca.com")
            .cpf("00000000000")
            .senha(passwordEncoder.encode("admin123"))
            .dataNascimento(LocalDate.of(1990, 1, 1))
            .sexo("M")
            .acl(acl)
            .statusUsuario(ativo)
            .cargo("Diretor")
            .build());

        log.info(">>> Admin padrão criado: admin@biblioteca.com / admin123");
    }

    private void seedClientePadrao() {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM tb_usuario WHERE des_email = ?",
            Integer.class, "cliente@biblioteca.com");

        if (count != null && count > 0) {
            log.info(">>> Cliente padrão já existe.");
            return;
        }

        Acl acl = buscarAcl("CLIENTE");
        StatusUsuario ativo = buscarStatus(StatusCliente.ATIVO.name());

        clienteRepository.save(Cliente.builder()
            .nome("Cliente Teste")
            .email("cliente@biblioteca.com")
            .cpf("11111111111")
            .senha(passwordEncoder.encode("cliente123"))
            .dataNascimento(LocalDate.of(2000, 6, 15))
            .sexo("F")
            .acl(acl)
            .statusUsuario(ativo)
            .build());

        log.info(">>> Cliente padrão criado: cliente@biblioteca.com / cliente123");
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
