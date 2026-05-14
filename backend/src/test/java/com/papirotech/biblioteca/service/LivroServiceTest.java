package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.request.AtualizarLivroRequest;
import com.papirotech.biblioteca.dto.request.CadastroLivroRequest;
import com.papirotech.biblioteca.dto.response.LivroResponse;
import com.papirotech.biblioteca.entity.Categoria;
import com.papirotech.biblioteca.entity.Livro;
import com.papirotech.biblioteca.exception.LivroJaExisteException;
import com.papirotech.biblioteca.exception.LivroNaoEncontradoException;
import com.papirotech.biblioteca.repository.CategoriaRepository;
import com.papirotech.biblioteca.repository.EmprestimoRepository;
import com.papirotech.biblioteca.repository.LivroRepository;
import com.papirotech.biblioteca.service.impl.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LivroService — Testes Unitários")
class LivroServiceTest {

    @Mock private LivroRepository     livroRepository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private EmprestimoRepository emprestimoRepository;
    @Mock private BibliotecaMapper    mapper;

    @InjectMocks private LivroService livroService;

    private Categoria categoria;
    private Livro livro;
    private LivroResponse livroResponse;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
            .idCategoria(1).descricao("Técnico").build();

        livro = Livro.builder()
            .idLivro(1).isbn("9788550802534").titulo("Clean Code")
            .autor("Robert C. Martin").categoria(categoria)
            .editora("Alta Books").sinopse("Código limpo.")
            .dataCadastro(LocalDate.now()).anoPublicacao(2008)
            .quantidadeTotal(3).quantidadeDisponivel(3).build();

        livroResponse = new LivroResponse(
            1, "9788550802534", "Clean Code", "Robert C. Martin",
            null, "Alta Books", "Código limpo.",
            LocalDate.now(), 2008, 3, 3, true
        );
    }

    // ─── T01: RF01 — adicionarLivro ──────────────────────────────────────────

    @Test
    @DisplayName("T01 — RF01: deve cadastrar livro com sucesso")
    void deveAdicionarLivroComSucesso() {
        CadastroLivroRequest req = new CadastroLivroRequest(
            "9788550802534", "Clean Code", "Robert C. Martin",
            1, "Alta Books", "Código limpo.", 2008, 3);

        when(livroRepository.existsByIsbn(req.isbn())).thenReturn(false);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(livroRepository.save(any())).thenReturn(livro);
        when(mapper.toResponse(livro)).thenReturn(livroResponse);

        LivroResponse resultado = livroService.adicionarLivro(req);

        assertThat(resultado).isNotNull();
        assertThat(resultado.titulo()).isEqualTo("Clean Code");
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    @DisplayName("T02 — RF01: deve lançar exceção ao cadastrar livro com ISBN duplicado")
    void deveLancarExcecaoIsbnDuplicado() {
        CadastroLivroRequest req = new CadastroLivroRequest(
            "9788550802534", "Clean Code", "Robert C. Martin",
            1, "Alta Books", "Código limpo.", 2008, 3);

        when(livroRepository.existsByIsbn(req.isbn())).thenReturn(true);

        assertThatThrownBy(() -> livroService.adicionarLivro(req))
            .isInstanceOf(LivroJaExisteException.class)
            .hasMessageContaining("9788550802534");
    }

    // ─── T03: RF02 — atualizar livro ─────────────────────────────────────────

    @Test
    @DisplayName("T03 — RF02: deve atualizar título do livro")
    void deveAtualizarTituloLivro() {
        AtualizarLivroRequest req = new AtualizarLivroRequest(
            "Clean Code — Edição Revisada", null, null, null, null, null, null);

        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(livroRepository.save(any())).thenReturn(livro);
        when(mapper.toResponse(livro)).thenReturn(livroResponse);

        livroService.atualizar(1, req);

        assertThat(livro.getTitulo()).isEqualTo("Clean Code — Edição Revisada");
        verify(livroRepository).save(livro);
    }

    @Test
    @DisplayName("T04 — RF02: deve lançar exceção ao atualizar livro inexistente")
    void deveLancarExcecaoLivroInexistenteAoAtualizar() {
        when(livroRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.atualizar(99,
            new AtualizarLivroRequest(null, null, null, null, null, null, null)))
            .isInstanceOf(LivroNaoEncontradoException.class);
    }

    // ─── T05: RF03 — remover livro ───────────────────────────────────────────

    @Test
    @DisplayName("T05 — RF03: deve remover livro sem empréstimos ativos")
    void deveRemoverLivroSemEmprestimosAtivos() {
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsEmprestimoAtivoDoLivro(1)).thenReturn(false);

        livroService.remover(1);

        verify(livroRepository).deleteById(1);
    }

    @Test
    @DisplayName("T06 — RF03: deve bloquear remoção de livro com empréstimos ativos")
    void deveBlocarRemocaoLivroComEmprestimosAtivos() {
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.existsEmprestimoAtivoDoLivro(1)).thenReturn(true);

        assertThatThrownBy(() -> livroService.remover(1))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("empréstimos ativos");

        verify(livroRepository, never()).deleteById(any());
    }

    // ─── T07: RF11 — verificarDisponibilidade ────────────────────────────────

    @Test
    @DisplayName("T07 — RF11: verificarDisponibilidade deve retornar true quando há exemplares")
    void verificarDisponibilidadeComExemplares() {
        assertThat(livro.verificarDisponibilidade()).isTrue();
    }

    @Test
    @DisplayName("T08 — RF11: verificarDisponibilidade deve retornar false quando sem exemplares")
    void verificarDisponibilidadeSemExemplares() {
        livro.setQuantidadeDisponivel(0);
        assertThat(livro.verificarDisponibilidade()).isFalse();
    }

    @Test
    @DisplayName("T09 — RF11: buscarPorId deve lançar exceção para id inexistente")
    void buscarPorIdInexistente() {
        when(livroRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livroService.buscarPorId(99))
            .isInstanceOf(LivroNaoEncontradoException.class);
    }
}
