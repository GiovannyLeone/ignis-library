package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.response.EmprestimoResponse;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.enums.StatusEmprestimo;
import com.papirotech.biblioteca.exception.AcessoNegadoException;
import com.papirotech.biblioteca.exception.LivroNaoEncontradoException;
import com.papirotech.biblioteca.repository.*;
import com.papirotech.biblioteca.service.impl.EmprestimoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmprestimoService — Testes Unitários")
class EmprestimoServiceTest {

    @Mock private EmprestimoRepository       emprestimoRepository;
    @Mock private LivroRepository            livroRepository;
    @Mock private ClienteRepository          clienteRepository;
    @Mock private StatusUsuarioRepository    statusUsuarioRepository;
    @Mock private StatusEmprestimoRepository statusEmprestimoRepository;
    @Mock private BibliotecaMapper           mapper;

    @InjectMocks private EmprestimoService emprestimoService;

    private Cliente clienteAtivo;
    private Cliente clienteBloqueado;
    private Livro livro;
    private StatusEmprestimoEntity statusReservado;
    private StatusEmprestimoEntity statusAtivo;
    private StatusEmprestimoEntity statusAtrasado;
    private StatusEmprestimoEntity statusEmProcesso;
    private StatusEmprestimoEntity statusEmProcessoAtrasado;
    private StatusEmprestimoEntity statusDevolvido;
    private StatusEmprestimoEntity statusDevolvidoAtraso;
    private StatusUsuario statusUsuarioAtivo;
    private StatusUsuario statusUsuarioBloqueado;
    private Emprestimo emprestimo;
    private EmprestimoResponse response;

    @BeforeEach
    void setUp() {
        statusUsuarioAtivo    = StatusUsuario.builder().idStatusUsuario(1).descricao("ATIVO").build();
        statusUsuarioBloqueado = StatusUsuario.builder().idStatusUsuario(2).descricao("BLOQUEADO").build();

        Acl acl = Acl.builder().idAcl(2).descricao("CLIENTE").build();

        clienteAtivo = Cliente.builder()
            .nome("João").email("joao@email.com").cpf("11111111111")
            .senha("hash").dataNascimento(LocalDate.of(2000, 1, 1)).sexo("M")
            .acl(acl).statusUsuario(statusUsuarioAtivo).build();
        clienteAtivo.setId(1);

        clienteBloqueado = Cliente.builder()
            .nome("Maria").email("maria@email.com").cpf("22222222222")
            .senha("hash").dataNascimento(LocalDate.of(2000, 1, 1)).sexo("F")
            .acl(acl).statusUsuario(statusUsuarioBloqueado).build();
        clienteBloqueado.setId(2);

        Categoria cat = Categoria.builder().idCategoria(1).descricao("Técnico").build();
        livro = Livro.builder()
            .idLivro(1).isbn("9788550802534").titulo("Clean Code")
            .autor("Robert C. Martin").categoria(cat)
            .editora("Alta Books").sinopse("Código limpo.")
            .dataCadastro(LocalDate.now()).anoPublicacao(2008)
            .quantidadeTotal(3).quantidadeDisponivel(3).build();

        statusReservado         = StatusEmprestimoEntity.builder().idStatusEmprestimo(1).descricao("RESERVADO").build();
        statusAtivo             = StatusEmprestimoEntity.builder().idStatusEmprestimo(2).descricao("ATIVO").build();
        statusAtrasado          = StatusEmprestimoEntity.builder().idStatusEmprestimo(3).descricao("ATRASADO").build();
        statusEmProcesso        = StatusEmprestimoEntity.builder().idStatusEmprestimo(4).descricao("EM_PROCESSO_DE_DEVOLUCAO").build();
        statusEmProcessoAtrasado = StatusEmprestimoEntity.builder().idStatusEmprestimo(5).descricao("EM_PROCESSO_DE_DEVOLUCAO_ATRASADO").build();
        statusDevolvido         = StatusEmprestimoEntity.builder().idStatusEmprestimo(6).descricao("DEVOLVIDO").build();
        statusDevolvidoAtraso   = StatusEmprestimoEntity.builder().idStatusEmprestimo(7).descricao("DEVOLVIDO_COM_ATRASO").build();

        emprestimo = Emprestimo.builder()
            .id(1).livro(livro).cliente(clienteAtivo)
            .dataEmprestimo(LocalDateTime.now())
            .dataDevolucaoPrevista(LocalDate.now().plusDays(7))
            .status(statusReservado)
            .codigoRetiradaEmprestimo("ABC123DEF456GHIJ")
            .penalidadeGerada(false).build();

        response = new EmprestimoResponse(
            1, "ABC123DEF456GHIJ", null,
            LocalDateTime.now(), LocalDate.now().plusDays(7), null,
            "RESERVADO", false, null, null);
    }

    // ─── RF07: gerarCodigoEmprestimo ─────────────────────────────────────────

    @Test
    @DisplayName("T14 — RF07: deve gerar código de empréstimo para cliente ativo")
    void deveGerarCodigoEmprestimoComSucesso() {
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsEmprestimoAtivoDoClienteELivro(1, 1)).thenReturn(false);
        when(statusEmprestimoRepository.findByDescricao(StatusEmprestimo.RESERVADO.name()))
            .thenReturn(Optional.of(statusReservado));
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        EmprestimoResponse resultado = emprestimoService.gerarCodigoEmprestimo(1, clienteAtivo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.status()).isEqualTo("RESERVADO");
        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(2);
    }

    @Test
    @DisplayName("T15 — RF07: deve bloquear empréstimo para cliente BLOQUEADO")
    void deveBloquearEmprestimoClienteBloqueado() {
        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(1, clienteBloqueado))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("bloqueada");
    }

    @Test
    @DisplayName("T16 — RF07: deve bloquear empréstimo de livro sem exemplares")
    void deveBloquearEmprestimoLivroSemExemplares() {
        livro.setQuantidadeDisponivel(0);
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsEmprestimoAtivoDoClienteELivro(1, 1)).thenReturn(false);

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(1, clienteAtivo))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("disponíveis");
    }

    @Test
    @DisplayName("T17 — RF07: deve bloquear empréstimo duplicado do mesmo livro")
    void deveBloquearEmprestimoDuplicado() {
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsEmprestimoAtivoDoClienteELivro(1, 1)).thenReturn(true);

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(1, clienteAtivo))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("já possui");
    }

    @Test
    @DisplayName("T18 — RF07: deve lançar exceção para livro inexistente")
    void deveLancarExcecaoLivroInexistente() {
        when(livroRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(99, clienteAtivo))
            .isInstanceOf(LivroNaoEncontradoException.class);
    }

    // ─── RF09: realizarEmprestimo ─────────────────────────────────────────────

    @Test
    @DisplayName("T19 — RF09: deve registrar retirada com código válido")
    void deveRegistrarRetiradaComCodigoValido() {
        when(emprestimoRepository.findByCodigoRetiradaEmprestimo("ABC123DEF456GHIJ"))
            .thenReturn(Optional.of(emprestimo));
        when(statusEmprestimoRepository.findByDescricao(StatusEmprestimo.ATIVO.name()))
            .thenReturn(Optional.of(statusAtivo));
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        EmprestimoResponse resultado = emprestimoService.realizarEmprestimo("ABC123DEF456GHIJ");

        assertThat(resultado).isNotNull();
        verify(emprestimoRepository).save(emprestimo);
    }

    @Test
    @DisplayName("T20 — RF09: deve rejeitar código de retirada inválido")
    void deveRejeitarCodigoRetiradaInvalido() {
        when(emprestimoRepository.findByCodigoRetiradaEmprestimo("INVALIDO"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> emprestimoService.realizarEmprestimo("INVALIDO"))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("inválido");
    }

    @Test
    @DisplayName("T20b — RF09: deve rejeitar retirada de empréstimo já retirado")
    void deveRejeitarRetiradaJaRealizada() {
        emprestimo.setStatus(statusAtivo); // já está ATIVO
        when(emprestimoRepository.findByCodigoRetiradaEmprestimo("ABC123DEF456GHIJ"))
            .thenReturn(Optional.of(emprestimo));

        assertThatThrownBy(() -> emprestimoService.realizarEmprestimo("ABC123DEF456GHIJ"))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("válido");
    }

    // ─── RF08: gerarCodigoDevolucao ───────────────────────────────────────────

    @Test
    @DisplayName("T21 — RF08: deve gerar código de devolução para empréstimo ATIVO")
    void deveGerarCodigoDevolucaoParaEmprestimoAtivo() {
        emprestimo.setStatus(statusAtivo);
        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));
        when(statusEmprestimoRepository.findByDescricao(StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO.name()))
            .thenReturn(Optional.of(statusEmProcesso));
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        EmprestimoResponse resultado = emprestimoService.gerarCodigoDevolucao(1, clienteAtivo);

        assertThat(resultado).isNotNull();
        verify(emprestimoRepository).save(emprestimo);
    }

    @Test
    @DisplayName("T22 — RF08: deve gerar código de devolução com atraso para ATRASADO")
    void deveGerarCodigoDevolucaoAtrasado() {
        emprestimo.setStatus(statusAtrasado);
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(3));
        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));
        when(statusEmprestimoRepository.findByDescricao(StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO_ATRASADO.name()))
            .thenReturn(Optional.of(statusEmProcessoAtrasado));
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        emprestimoService.gerarCodigoDevolucao(1, clienteAtivo);

        assertThat(emprestimo.getStatus().getDescricao())
            .isEqualTo("EM_PROCESSO_DE_DEVOLUCAO_ATRASADO");
    }

    @Test
    @DisplayName("T23 — RF08: deve bloquear geração de código por outro cliente")
    void deveBloquearCodigoDevolucaoPorOutroCliente() {
        emprestimo.setStatus(statusAtivo);
        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoDevolucao(1, clienteBloqueado))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("não pertence");
    }

    // ─── RF10: registrarDevolucao ─────────────────────────────────────────────

    @Test
    @DisplayName("T24 — RF10: deve registrar devolução e incrementar disponível")
    void deveRegistrarDevolucaoEIncrementarDisponivel() {
        livro.setQuantidadeDisponivel(2);
        emprestimo.setStatus(statusEmProcesso);
        emprestimo.setCodigoDevolucaoEmprestimo("ABC123DEF456GHIJD");

        when(emprestimoRepository.findByCodigoDevolucaoEmprestimo("ABC123DEF456GHIJD"))
            .thenReturn(Optional.of(emprestimo));
        when(statusEmprestimoRepository.findByDescricao(StatusEmprestimo.DEVOLVIDO.name()))
            .thenReturn(Optional.of(statusDevolvido));
        when(livroRepository.save(any())).thenReturn(livro);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        emprestimoService.registrarDevolucao("ABC123DEF456GHIJD");

        assertThat(livro.getQuantidadeDisponivel()).isEqualTo(3);
        assertThat(emprestimo.getStatus().getDescricao()).isEqualTo("DEVOLVIDO");
    }

    @Test
    @DisplayName("T25 — RF10: deve registrar devolução com atraso")
    void deveRegistrarDevolucaoComAtraso() {
        emprestimo.setStatus(statusEmProcessoAtrasado);
        emprestimo.setCodigoDevolucaoEmprestimo("ABC123DEF456GHIJD");

        when(emprestimoRepository.findByCodigoDevolucaoEmprestimo("ABC123DEF456GHIJD"))
            .thenReturn(Optional.of(emprestimo));
        when(statusEmprestimoRepository.findByDescricao(StatusEmprestimo.DEVOLVIDO_COM_ATRASO.name()))
            .thenReturn(Optional.of(statusDevolvidoAtraso));
        when(livroRepository.save(any())).thenReturn(livro);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        emprestimoService.registrarDevolucao("ABC123DEF456GHIJD");

        assertThat(emprestimo.getStatus().getDescricao()).isEqualTo("DEVOLVIDO_COM_ATRASO");
    }

    // ─── RF14: aplicarPenalidade / removerPenalidade ─────────────────────────

    @Test
    @DisplayName("T26 — RF14: deve aplicar penalidade e bloquear cliente")
    void deveAplicarPenalidadeEBloquearCliente() {
        emprestimo.setStatus(statusDevolvidoAtraso);
        emprestimo.setPenalidadeGerada(false);

        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));
        when(statusUsuarioRepository.findByDescricao(StatusCliente.BLOQUEADO.name()))
            .thenReturn(Optional.of(statusUsuarioBloqueado));
        when(clienteRepository.save(any())).thenReturn(clienteAtivo);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        emprestimoService.aplicarPenalidade(1);

        assertThat(emprestimo.getPenalidadeGerada()).isTrue();
        assertThat(clienteAtivo.getStatusUsuario().getDescricao()).isEqualTo("BLOQUEADO");
    }

    @Test
    @DisplayName("T27 — RF14: deve bloquear penalidade duplicada")
    void deveBloquearPenalidadeDuplicada() {
        emprestimo.setStatus(statusDevolvidoAtraso);
        emprestimo.setPenalidadeGerada(true);

        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));

        assertThatThrownBy(() -> emprestimoService.aplicarPenalidade(1))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("já foi aplicada");
    }

    @Test
    @DisplayName("T28 — RF14: deve remover penalidade e reativar cliente")
    void deveRemoverPenalidadeEReativarCliente() {
        emprestimo.setPenalidadeGerada(true);
        clienteAtivo.setStatusUsuario(statusUsuarioBloqueado);

        when(emprestimoRepository.findById(1)).thenReturn(Optional.of(emprestimo));
        when(statusUsuarioRepository.findByDescricao(StatusCliente.ATIVO.name()))
            .thenReturn(Optional.of(statusUsuarioAtivo));
        when(clienteRepository.save(any())).thenReturn(clienteAtivo);
        when(emprestimoRepository.save(any())).thenReturn(emprestimo);
        when(mapper.toResponse(any(Emprestimo.class))).thenReturn(response);

        emprestimoService.removerPenalidade(1);

        assertThat(emprestimo.getPenalidadeGerada()).isFalse();
        assertThat(clienteAtivo.getStatusUsuario().getDescricao()).isEqualTo("ATIVO");
    }
}
