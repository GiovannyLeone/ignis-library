package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.response.FavoritoResponse;
import com.papirotech.biblioteca.dto.response.MensagemResponse;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.exception.LivroNaoEncontradoException;
import com.papirotech.biblioteca.repository.FavoritoRepository;
import com.papirotech.biblioteca.repository.LivroRepository;
import com.papirotech.biblioteca.service.impl.FavoritoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FavoritoService — Testes Unitários (T21-T27)")
class FavoritoServiceTest {

    @Mock private FavoritoRepository favoritoRepository;
    @Mock private LivroRepository    livroRepository;
    @Mock private BibliotecaMapper   mapper;

    @InjectMocks private FavoritoService favoritoService;

    private Cliente cliente;
    private Livro livro;
    private Favorito favorito;

    @BeforeEach
    void setUp() {
        Acl acl = Acl.builder().idAcl(2).descricao("CLIENTE").build();
        StatusUsuario ativo = StatusUsuario.builder().idStatusUsuario(1).descricao("ATIVO").build();

        cliente = Cliente.builder()
            .nome("João").email("joao@email.com").cpf("11111111111")
            .senha("hash").dataNascimento(LocalDate.of(2000, 1, 1)).sexo("M")
            .acl(acl).statusUsuario(ativo).build();
        cliente.setId(1);

        Categoria cat = Categoria.builder().idCategoria(1).descricao("Técnico").build();
        livro = Livro.builder()
            .idLivro(1).isbn("9788550802534").titulo("Clean Code")
            .autor("Robert C. Martin").categoria(cat)
            .editora("Alta Books").sinopse("Código limpo.")
            .dataCadastro(LocalDate.now()).anoPublicacao(2008)
            .quantidadeTotal(3).quantidadeDisponivel(3).build();

        favorito = Favorito.builder()
            .idFavorito(1).cliente(cliente).livro(livro).build();
    }

    // ─── T21: favoritarLivro — adicionar ─────────────────────────────────────

    @Test
    @DisplayName("T21 — RF15: deve adicionar livro aos favoritos")
    void deveAdicionarFavorito() {
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(favoritoRepository.findByClienteIdAndLivroIdLivro(1, 1))
            .thenReturn(Optional.empty());
        when(favoritoRepository.save(any())).thenReturn(favorito);

        MensagemResponse resultado = favoritoService.favoritarLivro(1, cliente);

        assertThat(resultado.mensagem()).contains("adicionado");
        verify(favoritoRepository).save(any(Favorito.class));
    }

    // ─── T22: favoritarLivro — remover (toggle) ───────────────────────────────

    @Test
    @DisplayName("T22 — RF15: deve remover livro dos favoritos (toggle)")
    void deveRemoverFavorito() {
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(favoritoRepository.findByClienteIdAndLivroIdLivro(1, 1))
            .thenReturn(Optional.of(favorito));

        MensagemResponse resultado = favoritoService.favoritarLivro(1, cliente);

        assertThat(resultado.mensagem()).contains("removido");
        verify(favoritoRepository).delete(favorito);
        verify(favoritoRepository, never()).save(any());
    }

    // ─── T23: livro inexistente ───────────────────────────────────────────────

    @Test
    @DisplayName("T23 — RF15: deve lançar exceção para livro inexistente")
    void deveLancarExcecaoLivroInexistente() {
        when(livroRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoritoService.favoritarLivro(99, cliente))
            .isInstanceOf(LivroNaoEncontradoException.class);
    }

    // ─── T24: listarFavoritos ─────────────────────────────────────────────────

    @Test
    @DisplayName("T24 — RF15: deve listar favoritos do cliente")
    void deveListarFavoritos() {
        when(favoritoRepository.findByClienteId(1)).thenReturn(List.of(favorito));
        when(mapper.toResponse(favorito)).thenReturn(
            new FavoritoResponse(1, null));

        List<FavoritoResponse> resultado = favoritoService.listarFavoritos(1);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).idFavorito()).isEqualTo(1);
    }

    @Test
    @DisplayName("T25 — RF15: deve retornar lista vazia quando sem favoritos")
    void deveRetornarListaVaziaQuandoSemFavoritos() {
        when(favoritoRepository.findByClienteId(1)).thenReturn(List.of());

        List<FavoritoResponse> resultado = favoritoService.listarFavoritos(1);

        assertThat(resultado).isEmpty();
    }

    // ─── T26: toggle múltiplo ────────────────────────────────────────────────

    @Test
    @DisplayName("T26 — RF15: deve adicionar novamente após remover (toggle duplo)")
    void deveAdicionarNovamenteAposRemover() {
        // 1ª chamada: já existe — remove
        when(livroRepository.findById(1)).thenReturn(Optional.of(livro));
        when(favoritoRepository.findByClienteIdAndLivroIdLivro(1, 1))
            .thenReturn(Optional.of(favorito));

        MensagemResponse primeiraResposta = favoritoService.favoritarLivro(1, cliente);
        assertThat(primeiraResposta.mensagem()).contains("removido");

        // 2ª chamada: não existe — adiciona
        when(favoritoRepository.findByClienteIdAndLivroIdLivro(1, 1))
            .thenReturn(Optional.empty());
        when(favoritoRepository.save(any())).thenReturn(favorito);

        MensagemResponse segundaResposta = favoritoService.favoritarLivro(1, cliente);
        assertThat(segundaResposta.mensagem()).contains("adicionado");
    }

    // ─── T27: favoritar múltiplos livros ────────────────────────────────────

    @Test
    @DisplayName("T27 — RF15: deve listar múltiplos favoritos")
    void deveListarMultiplosFavoritos() {
        Livro livro2 = Livro.builder().idLivro(2).titulo("The Martian").build();
        Favorito favorito2 = Favorito.builder().idFavorito(2).cliente(cliente).livro(livro2).build();

        when(favoritoRepository.findByClienteId(1))
            .thenReturn(List.of(favorito, favorito2));
        when(mapper.toResponse(favorito)).thenReturn(new FavoritoResponse(1, null));
        when(mapper.toResponse(favorito2)).thenReturn(new FavoritoResponse(2, null));

        List<FavoritoResponse> resultado = favoritoService.listarFavoritos(1);

        assertThat(resultado).hasSize(2);
    }
}
