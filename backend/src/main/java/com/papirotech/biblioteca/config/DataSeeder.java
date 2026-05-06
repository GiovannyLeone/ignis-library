package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.PerfilAcesso;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.enums.StatusEmprestimo;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * DataSeeder — cria apenas dados de exemplo (usuários e livros padrão).
 *
 * As tabelas e dados base (tb_acl, tb_status_usuario, tb_status_emprestimo,
 * tb_categoria) são criados e populados pelo Flyway via migrations SQL
 * em src/main/resources/db/migration/.
 *
 * O Flyway controla o que já foi executado na tabela flyway_schema_history —
 * nenhuma migration é executada mais de uma vez, independente de quantas
 * vezes o sistema for reiniciado.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    //private final AdministradorRepository    administradorRepository;
    private final ClienteRepository          clienteRepository;
    //private final EstoquistaRepository       estoquistaRepository;
    //private final LivroRepository            livroRepository;
    //private final CategoriaRepository        categoriaRepository;
    //private final AclRepository              aclRepository;
    //private final StatusUsuarioRepository    statusUsuarioRepository;
    private final PasswordEncoder            passwordEncoder;

    @Override
    public void run(String... args) {
        // Flyway já garantiu que as tabelas e dados base existem.
        // Aqui criamos apenas os dados de exemplo, verificando antes se já existem.
        seedAdmin();
        seedCliente();
        seedEstoquista();
        seedLivros();
    }

    private void seedAdmin() {
        if (administradorRepository.existsByEmail("admin@biblioteca.com")) return;

        administradorRepository.save(Administrador.builder()
                .nome("Administrador Padrão")
                .email("admin@biblioteca.com")
                .cpf("00000000000")
                .senha(passwordEncoder.encode("admin123"))
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .sexo("M")
                .acl(buscarAcl(PerfilAcesso.ADMINISTRADOR))
                .cargo("Diretor")
                .build());

        log.info(">>> Admin criado: admin@biblioteca.com / admin123");
    }

    private void seedCliente() {
        if (clienteRepository.existsByEmail("cliente@biblioteca.com")) return;

        clienteRepository.save(Cliente.builder()
                .nome("Cliente Teste")
                .email("cliente@biblioteca.com")
                .cpf("11111111111")
                .senha(passwordEncoder.encode("cliente123"))
                .dataNascimento(LocalDate.of(2000, 6, 15))
                .sexo("F")
                .acl(buscarAcl(PerfilAcesso.CLIENTE))
                .status(buscarStatus(StatusCliente.ATIVO))
                .build());

        log.info(">>> Cliente criado: cliente@biblioteca.com / cliente123");
    }

    private void seedEstoquista() {
        if (estoquistaRepository.existsByCodigoAcesso("EST001")) return;

        estoquistaRepository.save(Estoquista.builder()
                .nome("Estoquista Padrão")
                .email("estoquista@biblioteca.com")
                .cpf("22222222222")
                .senha(passwordEncoder.encode("estoque123"))
                .dataNascimento(LocalDate.of(1995, 3, 20))
                .sexo("M")
                .acl(buscarAcl(PerfilAcesso.ESTOQUISTA))
                .codigoAcesso("EST001")
                .build());

        log.info(">>> Estoquista criado: EST001 / estoque123");
    }

    private void seedLivros() {
        if (livroRepository.count() > 0) return;

        Categoria ficcao  = categoriaRepository.findByDescricaoIgnoreCase("Ficção Científica").orElseThrow();
        Categoria tecnico = categoriaRepository.findByDescricaoIgnoreCase("Técnico").orElseThrow();

        livroRepository.saveAll(List.of(
                Livro.builder()
                        .isbn("9780062316097").titulo("The Martian").autor("Andy Weir")
                        .editora("Crown Publishing").sinopse("Um astronauta sobrevive em Marte.")
                        .anoPublicacao(2011).quantidadeTotal(5).quantidadeDisponivel(5)
                        .dataCadastro(LocalDate.now()).categoria(ficcao).build(),
                Livro.builder()
                        .isbn("9788550802534").titulo("Clean Code").autor("Robert C. Martin")
                        .editora("Alta Books").sinopse("Boas práticas para código limpo.")
                        .anoPublicacao(2008).quantidadeTotal(3).quantidadeDisponivel(3)
                        .dataCadastro(LocalDate.now()).categoria(tecnico).build()
        ));

        log.info(">>> 2 livros de exemplo criados");
    }

//    private Acl buscarAcl(PerfilAcesso perfil) {
//        return aclRepository.findByDescricao(perfil)
//                .orElseThrow(() -> new RuntimeException("ACL não encontrada: " + perfil
//                        + ". Verifique se a migration V2 foi executada."));
//    }

//    private StatusUsuario buscarStatus(StatusCliente status) {
//        return statusUsuarioRepository.findByDescricao(status)
//                .orElseThrow(() -> new RuntimeException("Status não encontrado: " + status
//                        + ". Verifique se a migration V2 foi executada."));
//    }
}
