package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.request.*;
import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.exception.*;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository     livroRepository;
    private final CategoriaRepository categoriaRepository;
    private final BibliotecaMapper    mapper;

    // ─── RF01: adicionarLivro() ───────────────────────────────────────────────
    @Transactional
    public LivroResponse adicionarLivro(CadastroLivroRequest req) {
        if (livroRepository.existsByIsbn(req.isbn()))
            throw new LivroJaExisteException("Já existe um livro com o ISBN: " + req.isbn());

        Livro livro = Livro.builder()
            .isbn(req.isbn()).titulo(req.titulo()).autor(req.autor())
            .categoria(buscarCategoria(req.categoriaId()))
            .editora(req.editora()).sinopse(req.sinopse())
            .dataCadastro(LocalDate.now()).anoPublicacao(req.anoPublicacao())
            .quantidadeTotal(req.quantidadeTotal()).quantidadeDisponivel(req.quantidadeTotal())
            .build();

        return mapper.toResponse(livroRepository.save(livro));
    }

    // ─── RF02: editar livro ───────────────────────────────────────────────────
    @Transactional
    public LivroResponse atualizar(Integer idLivro, AtualizarLivroRequest req) {
        Livro livro = buscarEntidade(idLivro);
        if (req.titulo()          != null) livro.setTitulo(req.titulo());
        if (req.autor()           != null) livro.setAutor(req.autor());
        if (req.editora()         != null) livro.setEditora(req.editora());
        if (req.sinopse()         != null) livro.setSinopse(req.sinopse());
        if (req.anoPublicacao()   != null) livro.setAnoPublicacao(req.anoPublicacao());
        if (req.categoriaId()     != null) livro.setCategoria(buscarCategoria(req.categoriaId()));
        if (req.quantidadeTotal() != null) {
            int diff = req.quantidadeTotal() - livro.getQuantidadeTotal();
            livro.setQuantidadeTotal(req.quantidadeTotal());
            livro.setQuantidadeDisponivel(Math.max(0, livro.getQuantidadeDisponivel() + diff));
        }
        return mapper.toResponse(livroRepository.save(livro));
    }

    // ─── RF03: removerLivro() ─────────────────────────────────────────────────
    @Transactional
    public void remover(Integer idLivro) {
        Livro livro = buscarEntidade(idLivro);
        if (!livro.removerLivro())
            throw new RuntimeException("Livro possui empréstimos ativos e não pode ser removido.");
        livroRepository.deleteById(idLivro);
    }

    // ─── RF04: buscar por título, autor ou ISBN ───────────────────────────────
    public PageResponse<LivroResponse> buscar(String termo, int pagina, int tamanho) {
        Page<LivroResponse> page = livroRepository
            .buscarPorTermo(termo, PageRequest.of(pagina, tamanho, Sort.by("titulo")))
            .map(mapper::toResponse);
        return mapper.toPageResponse(page);
    }

    // ─── RF05: listar livros ──────────────────────────────────────────────────
    public PageResponse<LivroResponse> listar(int pagina, int tamanho) {
        Page<LivroResponse> page = livroRepository
            .findAll(PageRequest.of(pagina, tamanho, Sort.by("titulo")))
            .map(mapper::toResponse);
        return mapper.toPageResponse(page);
    }

    public PageResponse<LivroResponse> listarDisponiveis(int pagina, int tamanho) {
        Page<LivroResponse> page = livroRepository
            .findDisponiveis(PageRequest.of(pagina, tamanho, Sort.by("titulo")))
            .map(mapper::toResponse);
        return mapper.toPageResponse(page);
    }

    // ─── RF11: verificarDisponibilidade() ────────────────────────────────────
    public LivroResponse buscarPorId(Integer idLivro) {
        return mapper.toResponse(buscarEntidade(idLivro));
    }

    public Livro buscarEntidade(Integer idLivro) {
        return livroRepository.findById(idLivro)
            .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado: id=" + idLivro));
    }

    private Categoria buscarCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada: id=" + idCategoria));
    }
}
