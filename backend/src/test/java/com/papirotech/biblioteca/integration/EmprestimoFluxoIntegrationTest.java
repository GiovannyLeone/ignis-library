package com.papirotech.biblioteca.integration;

import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.enums.StatusEmprestimo;
import com.papirotech.biblioteca.exception.AcessoNegadoException;
import com.papirotech.biblioteca.repository.*;
import com.papirotech.biblioteca.service.impl.EmprestimoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Integração — Fluxo Completo de Empréstimo (T12-T20)")
class EmprestimoFluxoIntegrationTest {

    @Autowired private EmprestimoService          emprestimoService;
    @Autowired private EmprestimoRepository       emprestimoRepository;
    @Autowired private LivroRepository            livroRepository;
    @Autowired private ClienteRepository          clienteRepository;
    @Autowired private AclRepository              aclRepository;
    @Autowired private StatusUsuarioRepository    statusUsuarioRepository;
    @Autowired private StatusEmprestimoRepository statusEmprestimoRepository;
    @Autowired private CategoriaRepository        categoriaRepository;
    @Autowired private PasswordEncoder            passwordEncoder;

    private static Integer clienteId;
    private static Integer livroId;

    @BeforeEach
    void setUp() {
        // Limpa entre testes
        emprestimoRepository.deleteAll();
        livroRepository.deleteAll();
        clienteRepository.deleteAll();
        statusEmprestimoRepository.deleteAll();
        statusUsuarioRepository.deleteAll();
        aclRepository.deleteAll();
        categoriaRepository.deleteAll();

        Acl acl = aclRepository.save(Acl.builder().descricao("CLIENTE").build());
        StatusUsuario ativo = statusUsuarioRepository.save(
            StatusUsuario.builder().descricao(StatusCliente.ATIVO.name()).build());
        statusUsuarioRepository.save(
            StatusUsuario.builder().descricao(StatusCliente.BLOQUEADO.name()).build());

        for (StatusEmprestimo s : StatusEmprestimo.values()) {
            statusEmprestimoRepository.save(
                StatusEmprestimoEntity.builder().descricao(s.name()).build());
        }

        Categoria cat = categoriaRepository.save(
            Categoria.builder().descricao("Técnico").build());

        Livro livro = livroRepository.save(Livro.builder()
            .isbn("9788550802534").titulo("Clean Code")
            .autor("Robert C. Martin").categoria(cat)
            .editora("Alta Books").sinopse("Código limpo.")
            .dataCadastro(LocalDate.now()).anoPublicacao(2008)
            .quantidadeTotal(2).quantidadeDisponivel(2).build());

        Cliente cliente = clienteRepository.save(Cliente.builder()
            .nome("João Silva").email("joao@test.com").cpf("11111111111")
            .senha(passwordEncoder.encode("senha123"))
            .dataNascimento(LocalDate.of(2000, 1, 1)).sexo("M")
            .acl(acl).statusUsuario(ativo).build());

        clienteId = cliente.getId();
        livroId = livro.getIdLivro();
    }

    private Cliente cliente() {
        return clienteRepository.findById(clienteId).orElseThrow();
    }

    private Livro livro() {
        return livroRepository.findById(livroId).orElseThrow();
    }

    @Test
    @DisplayName("T12 — Fluxo completo: reservar → retirar → devolver")
    @Transactional
    void fluxoCompletoEmprestimo() {
        var reserva = emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        assertThat(reserva.status()).isEqualTo("RESERVADO");
        assertThat(reserva.codigoRetirada()).isNotNull().hasSize(16);

        assertThat(livro().getQuantidadeDisponivel()).isEqualTo(1);

        var retirada = emprestimoService.realizarEmprestimo(reserva.codigoRetirada());
        assertThat(retirada.status()).isEqualTo("ATIVO");

        var devolucaoPrep = emprestimoService.gerarCodigoDevolucao(reserva.id(), cliente());
        assertThat(devolucaoPrep.status()).isEqualTo("EM_PROCESSO_DE_DEVOLUCAO");
        assertThat(devolucaoPrep.codigoDevolucao()).endsWith("D");

        var devolucao = emprestimoService.registrarDevolucao(devolucaoPrep.codigoDevolucao());
        assertThat(devolucao.status()).isEqualTo("DEVOLVIDO");

        assertThat(livro().getQuantidadeDisponivel()).isEqualTo(2);
    }

    @Test
    @DisplayName("T13 — Deve impedir empréstimo duplicado do mesmo livro")
    @Transactional
    void deveImpedirEmprestimoDuplicado() {
        emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(livroId, cliente()))
            .hasMessageContaining("já possui");
    }

    @Test
    @DisplayName("T14 — Deve respeitar quantidade disponível")
    @Transactional
    void deveRespeitarQuantidadeDisponivel() {
        Acl acl = aclRepository.findByDescricao("CLIENTE").orElseThrow();
        StatusUsuario ativo = statusUsuarioRepository.findByDescricao("ATIVO").orElseThrow();

        Cliente cliente2 = clienteRepository.save(Cliente.builder()
            .nome("Maria").email("maria@test.com").cpf("22222222222")
            .senha(passwordEncoder.encode("s")).dataNascimento(LocalDate.of(2000,1,1))
            .sexo("F").acl(acl).statusUsuario(ativo).build());

        Cliente cliente3 = clienteRepository.save(Cliente.builder()
            .nome("Pedro").email("pedro@test.com").cpf("33333333333")
            .senha(passwordEncoder.encode("s")).dataNascimento(LocalDate.of(2000,1,1))
            .sexo("M").acl(acl).statusUsuario(ativo).build());

        emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        emprestimoService.gerarCodigoEmprestimo(livroId, cliente2);

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(livroId, cliente3))
            .hasMessageContaining("disponíveis");
    }

    @Test
    @DisplayName("T15 — Deve bloquear empréstimo de cliente BLOQUEADO")
    @Transactional
    void deveBloquearEmprestimoClienteBloqueado() {
        StatusUsuario bloqueado = statusUsuarioRepository.findByDescricao("BLOQUEADO").orElseThrow();
        Cliente c = cliente();
        c.setStatusUsuario(bloqueado);
        clienteRepository.save(c);

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(livroId, c))
            .hasMessageContaining("bloqueada");
    }

    @Test
    @DisplayName("T16 — Código de retirada deve ter 16 caracteres")
    @Transactional
    void codigoRetiradaDeveTer16Caracteres() {
        var reserva = emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        assertThat(reserva.codigoRetirada()).hasSize(16);
    }

    @Test
    @DisplayName("T17 — Código de devolução deve terminar com D")
    @Transactional
    void codigoDevolucaoDeveTerminarComD() {
        var reserva = emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        emprestimoService.realizarEmprestimo(reserva.codigoRetirada());
        var dev = emprestimoService.gerarCodigoDevolucao(reserva.id(), cliente());
        assertThat(dev.codigoDevolucao()).endsWith("D");
    }

    @Test
    @DisplayName("T18 — Deve rejeitar código de retirada inválido")
    @Transactional
    void deveRejeitarCodigoInvalido() {
        assertThatThrownBy(() ->
            emprestimoService.realizarEmprestimo("CODIGOINVALIDO12"))
            .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("T19 — Deve registrar data de devolução real")
    @Transactional
    void deveRegistrarDataDevolucaoReal() {
        var reserva = emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        emprestimoService.realizarEmprestimo(reserva.codigoRetirada());
        var devolucaoPrep = emprestimoService.gerarCodigoDevolucao(reserva.id(), cliente());
        var devolucao = emprestimoService.registrarDevolucao(devolucaoPrep.codigoDevolucao());
        assertThat(devolucao.dataDevolucaoReal()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("T20 — Histórico deve conter empréstimo após fluxo completo")
    @Transactional
    void historicoDeveConterEmprestimo() {
        var reserva = emprestimoService.gerarCodigoEmprestimo(livroId, cliente());
        emprestimoService.realizarEmprestimo(reserva.codigoRetirada());
        var devolucaoPrep = emprestimoService.gerarCodigoDevolucao(reserva.id(), cliente());
        emprestimoService.registrarDevolucao(devolucaoPrep.codigoDevolucao());

        var historico = emprestimoService.historicoPorCliente(clienteId, 0, 10);
        assertThat(historico.conteudo()).hasSize(1);
        assertThat(historico.conteudo().get(0).status()).isEqualTo("DEVOLVIDO");
    }
}
