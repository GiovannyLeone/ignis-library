package com.papirotech.biblioteca.integration;

import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.enums.StatusEmprestimo;
import com.papirotech.biblioteca.exception.AcessoNegadoException;
import com.papirotech.biblioteca.repository.*;
import com.papirotech.biblioteca.service.impl.EmprestimoService;
import com.papirotech.biblioteca.service.impl.FavoritoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Integração — Penalidade e Favoritos (T21-T27)")
class PenalidadeFavoritoIntegrationTest {

    @Autowired private EmprestimoService          emprestimoService;
    @Autowired private FavoritoService            favoritoService;
    @Autowired private EmprestimoRepository       emprestimoRepository;
    @Autowired private LivroRepository            livroRepository;
    @Autowired private ClienteRepository          clienteRepository;
    @Autowired private AclRepository              aclRepository;
    @Autowired private StatusUsuarioRepository    statusUsuarioRepository;
    @Autowired private StatusEmprestimoRepository statusEmprestimoRepository;
    @Autowired private CategoriaRepository        categoriaRepository;
    @Autowired private FavoritoRepository         favoritoRepository;
    @Autowired private PasswordEncoder            passwordEncoder;

    private Integer clienteId;
    private Integer livroId;
    private Integer emprestimoId;

    @BeforeEach
    void setUp() {
        favoritoRepository.deleteAll();
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

        StatusEmprestimoEntity atrasado = statusEmprestimoRepository
            .findByDescricao(StatusEmprestimo.ATRASADO.name()).orElseThrow();

        Emprestimo emp = emprestimoRepository.save(Emprestimo.builder()
            .livro(livro).cliente(cliente)
            .dataEmprestimo(LocalDateTime.now().minusDays(10))
            .dataDevolucaoPrevista(LocalDate.now().minusDays(3))
            .status(atrasado)
            .codigoRetiradaEmprestimo("TESTCODE12345678")
            .penalidadeGerada(false).build());

        emprestimoId = emp.getId();
    }

    private Cliente cliente() {
        return clienteRepository.findById(clienteId).orElseThrow();
    }

    @Test
    @DisplayName("T21 — RF14: deve aplicar penalidade e bloquear cliente")
    @Transactional
    void deveAplicarPenalidadeEBloquearCliente() {
        emprestimoService.aplicarPenalidade(emprestimoId);

        Emprestimo atualizado = emprestimoRepository.findById(emprestimoId).orElseThrow();
        Cliente clienteAtualizado = clienteRepository.findById(clienteId).orElseThrow();

        assertThat(atualizado.getPenalidadeGerada()).isTrue();
        assertThat(clienteAtualizado.getStatusUsuario().getDescricao()).isEqualTo("BLOQUEADO");
    }

    @Test
    @DisplayName("T22 — RF14: cliente bloqueado não pode fazer novo empréstimo")
    @Transactional
    void clienteBloqueadoNaoPodeFazerEmprestimo() {
        emprestimoService.aplicarPenalidade(emprestimoId);
        Cliente clienteAtualizado = clienteRepository.findById(clienteId).orElseThrow();

        assertThatThrownBy(() ->
            emprestimoService.gerarCodigoEmprestimo(livroId, clienteAtualizado))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("bloqueada");
    }

    @Test
    @DisplayName("T23 — RF14: deve bloquear penalidade duplicada")
    @Transactional
    void deveBloquearPenalidadeDuplicada() {
        emprestimoService.aplicarPenalidade(emprestimoId);
        assertThatThrownBy(() -> emprestimoService.aplicarPenalidade(emprestimoId))
            .isInstanceOf(AcessoNegadoException.class)
            .hasMessageContaining("já foi aplicada");
    }

    @Test
    @DisplayName("T24 — RF14: deve remover penalidade e reativar cliente")
    @Transactional
    void deveRemoverPenalidadeEReativarCliente() {
        emprestimoService.aplicarPenalidade(emprestimoId);
        emprestimoService.removerPenalidade(emprestimoId);

        Emprestimo atualizado = emprestimoRepository.findById(emprestimoId).orElseThrow();
        Cliente clienteAtualizado = clienteRepository.findById(clienteId).orElseThrow();

        assertThat(atualizado.getPenalidadeGerada()).isFalse();
        assertThat(clienteAtualizado.getStatusUsuario().getDescricao()).isEqualTo("ATIVO");
    }

    @Test
    @DisplayName("T25 — RF14: cliente reativado pode fazer novo empréstimo")
    @Transactional
    void clienteReativadoPodeFazerEmprestimo() {
        emprestimoService.aplicarPenalidade(emprestimoId);
        emprestimoService.removerPenalidade(emprestimoId);

        // Cancela o empréstimo existente para liberar o livro
        StatusEmprestimoEntity cancelado = statusEmprestimoRepository
            .findByDescricao(StatusEmprestimo.CANCELADO.name()).orElseThrow();
        Emprestimo emp = emprestimoRepository.findById(emprestimoId).orElseThrow();
        emp.setStatus(cancelado);
        emprestimoRepository.save(emp);

        Cliente clienteAtualizado = clienteRepository.findById(clienteId).orElseThrow();

        assertThatCode(() ->
            emprestimoService.gerarCodigoEmprestimo(livroId, clienteAtualizado))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("T26 — RF15: deve favoritar e desfavoritar livro (toggle)")
    @Transactional
    void deveFavoritarEDesfavoritarLivro() {
        var resp1 = favoritoService.favoritarLivro(livroId, cliente());
        assertThat(resp1.mensagem()).contains("adicionado");
        assertThat(favoritoRepository.findByClienteId(clienteId)).hasSize(1);

        var resp2 = favoritoService.favoritarLivro(livroId, cliente());
        assertThat(resp2.mensagem()).contains("removido");
        assertThat(favoritoRepository.findByClienteId(clienteId)).isEmpty();
    }

    @Test
    @DisplayName("T27 — RF15: deve listar favoritos corretamente")
    @Transactional
    void deveListarFavoritosCorretamente() {
        Categoria cat2 = categoriaRepository.save(
            Categoria.builder().descricao("Romance").build());
        Livro livro2 = livroRepository.save(Livro.builder()
            .isbn("9780062316097").titulo("The Martian")
            .autor("Andy Weir").categoria(cat2)
            .editora("Crown").sinopse("Marte.")
            .dataCadastro(LocalDate.now()).anoPublicacao(2011)
            .quantidadeTotal(1).quantidadeDisponivel(1).build());

        favoritoService.favoritarLivro(livroId, cliente());
        favoritoService.favoritarLivro(livro2.getIdLivro(), cliente());

        var favoritos = favoritoService.listarFavoritos(clienteId);
        assertThat(favoritos).hasSize(2);
        assertThat(favoritos.stream().map(f -> f.livro().titulo()))
            .containsExactlyInAnyOrder("Clean Code", "The Martian");
    }
}
