package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.entity.Categoria;
import com.papirotech.biblioteca.entity.Estoquista;
import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.entity.Usuario;
import com.papirotech.biblioteca.enums.PerfilAcesso;
import com.papirotech.biblioteca.enums.StatusUsuario;
import com.papirotech.biblioteca.repository.CategoriaRepository;
import com.papirotech.biblioteca.repository.EstoquistaRepository;
import com.papirotech.biblioteca.repository.LivroRepository;
import com.papirotech.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EstoquistaRepository estoquistaRepository;
    private final CategoriaRepository categoriaRepository;
    private final LivroRepository livroRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedCategorias();
        seedAdmin();
        seedEstoquista();
        seedLivros();
    }

    private void seedAdmin() {
        if (usuarioRepository.existsByEmail("admin@biblioteca.com")) return;
        usuarioRepository.save(Usuario.builder()
                .nome("Administrador Padrão")
                .email("admin@biblioteca.com")
                .cpf("00000000000")
                .senha(passwordEncoder.encode("admin123"))
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .sexo("M")
                .status(StatusUsuario.ATIVO)
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .build());

        usuarioRepository.save(Usuario.builder()
                .nome("Cliente Teste")
                .email("cliente@biblioteca.com")
                .cpf("11111111111")
                .senha(passwordEncoder.encode("cliente123"))
                .dataNascimento(LocalDate.of(2000, 6, 15))
                .sexo("F")
                .status(StatusUsuario.ATIVO)
                .perfil(PerfilAcesso.CLIENTE)
                .build());

        log.info(">>> Usuários padrão criados:");
        log.info("    Admin:   admin@biblioteca.com  / admin123");
        log.info("    Cliente: cliente@biblioteca.com / cliente123");
    }

    private void seedEstoquista() {
        if (estoquistaRepository.existsByCodigoAcesso("EST001")) return;
        estoquistaRepository.save(Estoquista.builder()
                .nome("Estoquista Padrão")
                .codigoAcesso("EST001")
                .senha(passwordEncoder.encode("estoque123"))
                .build());
        log.info(">>> Estoquista padrão criado: EST001 / estoque123");
    }

    private void seedCategorias() {
        if (categoriaRepository.count() > 0) return;
        List<String> cats = List.of(
                "Ficção Científica", "Romance", "Fantasia", "Suspense",
                "Terror", "Biografia", "História", "Tecnologia",
                "Autoajuda", "Infantil", "HQ / Graphic Novel", "Técnico"
        );
        cats.forEach(c -> categoriaRepository.save(Categoria.builder().descricao(c).build()));
        log.info(">>> {} categorias criadas.", cats.size());
    }

    private void seedLivros() {
        if (livroRepository.count() > 0) return;
        Categoria ficção = categoriaRepository.findByDescricaoIgnoreCase("Ficção Científica")
                .orElseThrow();
        Categoria tecnico = categoriaRepository.findByDescricaoIgnoreCase("Técnico")
                .orElseThrow();

        livroRepository.saveAll(List.of(
                Livro.builder()
                        .isbn("9780062316097")
                        .titulo("The Martian")
                        .autor("Andy Weir")
                        .editora("Crown Publishing")
                        .sinopse("Um astronauta é deixado para trás em Marte e precisa sobreviver com engenhosidade.")
                        .anoPublicacao(2011)
                        .quantidadeTotal(5)
                        .quantidadeDisponivel(5)
                        .dataCadastro(LocalDate.now())
                        .categoria(ficção)
                        .build(),
                Livro.builder()
                        .isbn("9788550802534")
                        .titulo("Clean Code")
                        .autor("Robert C. Martin")
                        .editora("Alta Books")
                        .sinopse("Um guia de boas práticas para escrever código limpo e legível.")
                        .anoPublicacao(2008)
                        .quantidadeTotal(3)
                        .quantidadeDisponivel(3)
                        .dataCadastro(LocalDate.now())
                        .categoria(tecnico)
                        .build()
        ));
        log.info(">>> 2 livros de exemplo criados.");
    }
}
