package com.papirotech.biblioteca.service.impl;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.response.EmprestimoResponse;
import com.papirotech.biblioteca.dto.response.PageResponse;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.enums.StatusEmprestimo;
import com.papirotech.biblioteca.exception.*;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private static final int PRAZO_DIAS = 7;

    private final EmprestimoRepository       emprestimoRepository;
    private final LivroRepository            livroRepository;
    private final ClienteRepository          clienteRepository;
    private final StatusUsuarioRepository    statusUsuarioRepository;
    private final StatusEmprestimoRepository statusEmprestimoRepository;
    private final BibliotecaMapper           mapper;

    // ─── RF07: gerarCodigoEmprestimo(livro, cliente) — seção 3.4 ─────────────
    @Transactional
    public EmprestimoResponse gerarCodigoEmprestimo(Integer livroId, Cliente cliente) {
        // verificarStatus() — seção 3.4
        if (!cliente.verificarStatus())
            throw new AcessoNegadoException("Conta bloqueada. Não é possível realizar novos empréstimos.");

        Livro livro = livroRepository.findById(livroId)
            .orElseThrow(() -> new LivroNaoEncontradoException("Livro não encontrado: id=" + livroId));

        // Verifica se o cliente já tem empréstimo ativo ou reservado deste livro
        if (emprestimoRepository.existsEmprestimoAtivoDoClienteELivro(cliente.getId(), livroId))
            throw new AcessoNegadoException("Você já possui um empréstimo ativo ou reserva deste livro.");

        // verificarDisponibilidade() — seção 3.1
        if (!livro.verificarDisponibilidade())
            throw new AcessoNegadoException("Livro '" + livro.getTitulo() + "' sem exemplares disponíveis.");

        livro.decrementarDisponivel();
        livroRepository.save(livro);

        // Salva primeiro para obter o ID gerado
        Emprestimo emprestimo = Emprestimo.builder()
            .livro(livro)
            .cliente(cliente)
            .dataEmprestimo(LocalDateTime.now())
            .dataDevolucaoPrevista(LocalDate.now().plusDays(PRAZO_DIAS))
            .status(buscarStatus(StatusEmprestimo.RESERVADO.name()))
            .codigoRetiradaEmprestimo("TEMP")
            .penalidadeGerada(false)
            .build();

        emprestimo = emprestimoRepository.save(emprestimo);

        // Gera código com base nas informações reais do empréstimo
        emprestimo.setCodigoRetiradaEmprestimo(
            gerarCodigo(emprestimo.getId(), cliente.getId(), livro.getIdLivro()));

        return mapper.toResponse(emprestimoRepository.save(emprestimo));
    }

    // ─── RF08: gerarCodigoDevolucao(emprestimo) — seção 3.4 ──────────────────
    @Transactional
    public EmprestimoResponse gerarCodigoDevolucao(Integer emprestimoId, Cliente cliente) {
        Emprestimo emprestimo = buscarEntidade(emprestimoId);

        if (!emprestimo.getCliente().getId().equals(cliente.getId()))
            throw new AcessoNegadoException("Este empréstimo não pertence ao seu usuário.");

        String statusAtual = emprestimo.getStatus().getDescricao();
        if (!StatusEmprestimo.ATIVO.name().equals(statusAtual)
                && !StatusEmprestimo.ATRASADO.name().equals(statusAtual))
            throw new AcessoNegadoException("Empréstimo não pode ser devolvido. Status: " + statusAtual);

        boolean atrasado = LocalDate.now().isAfter(emprestimo.getDataDevolucaoPrevista());
        String novoStatus = atrasado
            ? StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO_ATRASADO.name()
            : StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO.name();

        emprestimo.setStatus(buscarStatus(novoStatus));
        // Código de devolução usa o mesmo ID do empréstimo + sufixo DEV para diferenciar
        emprestimo.setCodigoDevolucaoEmprestimo(
            gerarCodigo(emprestimo.getId(), emprestimo.getCliente().getId(), emprestimo.getLivro().getIdLivro())
            + "D");

        return mapper.toResponse(emprestimoRepository.save(emprestimo));
    }

    // ─── RF09: emprestarLivro(emprestimo) — seção 3.6 (Estoquista) ───────────
    @Transactional
    public EmprestimoResponse realizarEmprestimo(String codigoRetirada) {
        Emprestimo emprestimo = emprestimoRepository.findByCodigoRetiradaEmprestimo(codigoRetirada)
            .orElseThrow(() -> new AcessoNegadoException("Código de retirada inválido: " + codigoRetirada));

        if (!StatusEmprestimo.RESERVADO.name().equals(emprestimo.getStatus().getDescricao()))
            throw new AcessoNegadoException("Código não está mais válido. Status: "
                + emprestimo.getStatus().getDescricao());

        emprestimo.setStatus(buscarStatus(StatusEmprestimo.ATIVO.name()));
        return mapper.toResponse(emprestimoRepository.save(emprestimo));
    }

    // ─── RF10: devolverLivro(emprestimo) — seção 3.6 (Estoquista) ────────────
    @Transactional
    public EmprestimoResponse registrarDevolucao(String codigoDevolucao) {
        Emprestimo emprestimo = emprestimoRepository.findByCodigoDevolucaoEmprestimo(codigoDevolucao)
            .orElseThrow(() -> new AcessoNegadoException("Código de devolução inválido: " + codigoDevolucao));

        String statusAtual = emprestimo.getStatus().getDescricao();
        if (!StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO.name().equals(statusAtual)
                && !StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO_ATRASADO.name().equals(statusAtual))
            throw new AcessoNegadoException("Código inválido para este estado. Status: " + statusAtual);

        boolean atrasado = StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO_ATRASADO.name().equals(statusAtual);
        emprestimo.setStatus(buscarStatus(atrasado
            ? StatusEmprestimo.DEVOLVIDO_COM_ATRASO.name()
            : StatusEmprestimo.DEVOLVIDO.name()));
        emprestimo.setDataDevolucaoReal(LocalDate.now());

        // incrementarDisponivel() — seção 3.1
        Livro livro = emprestimo.getLivro();
        livro.incrementarDisponivel();
        livroRepository.save(livro);

        return mapper.toResponse(emprestimoRepository.save(emprestimo));
    }

    // ─── RF14: aplicarPenalidade(emprestimo) — seção 3.5 (Admin) ─────────────
    @Transactional
    public EmprestimoResponse aplicarPenalidade(Integer emprestimoId) {
        Emprestimo emprestimo = buscarEntidade(emprestimoId);
        String statusAtual = emprestimo.getStatus().getDescricao();

        if (!StatusEmprestimo.DEVOLVIDO_COM_ATRASO.name().equals(statusAtual)
                && !StatusEmprestimo.ATRASADO.name().equals(statusAtual))
            throw new AcessoNegadoException("Penalidade só aplicável em atrasos. Status: " + statusAtual);

        if (Boolean.TRUE.equals(emprestimo.getPenalidadeGerada()))
            throw new AcessoNegadoException("Penalidade já foi aplicada neste empréstimo.");

        // Bloqueia o cliente — status BLOQUEADO
        StatusUsuario bloqueado = statusUsuarioRepository.findByDescricao(StatusCliente.BLOQUEADO.name())
            .orElseThrow(() -> new RuntimeException("Status BLOQUEADO não encontrado"));

        Cliente cliente = emprestimo.getCliente();
        cliente.setStatusUsuario(bloqueado);
        clienteRepository.save(cliente);

        emprestimo.setPenalidadeGerada(true);
        return mapper.toResponse(emprestimoRepository.save(emprestimo));
    }

    // ─── RF14: removerPenalidade(emprestimo) — seção 3.5 (Admin) ─────────────
    @Transactional
    public EmprestimoResponse removerPenalidade(Integer emprestimoId) {
        Emprestimo emprestimo = buscarEntidade(emprestimoId);

        if (!Boolean.TRUE.equals(emprestimo.getPenalidadeGerada()))
            throw new AcessoNegadoException("Não há penalidade ativa neste empréstimo.");

        if (!StatusCliente.BLOQUEADO.name().equals(
                emprestimo.getCliente().getStatusUsuario().getDescricao()))
            throw new AcessoNegadoException("Cliente não está bloqueado.");

        // Restaura para ATIVO
        StatusUsuario ativo = statusUsuarioRepository.findByDescricao(StatusCliente.ATIVO.name())
            .orElseThrow(() -> new RuntimeException("Status ATIVO não encontrado"));

        Cliente cliente = emprestimo.getCliente();
        cliente.setStatusUsuario(ativo);
        clienteRepository.save(cliente);

        emprestimo.setPenalidadeGerada(false);
        return mapper.toResponse(emprestimoRepository.save(emprestimo));
    }

    // ─── RF12: histórico de todos os clientes (Admin) ────────────────────────
    public PageResponse<EmprestimoResponse> listarTodos(int pagina, int tamanho) {
        Page<EmprestimoResponse> page = emprestimoRepository
            .findAll(PageRequest.of(pagina, tamanho, Sort.by("dataEmprestimo").descending()))
            .map(mapper::toResponse);
        return mapper.toPageResponse(page);
    }

    // ─── RF13: consultarHistorico() — seção 3.4 (Cliente) ────────────────────
    public PageResponse<EmprestimoResponse> historicoPorCliente(Integer clienteId, int pagina, int tamanho) {
        Page<EmprestimoResponse> page = emprestimoRepository
            .findByClienteId(clienteId, PageRequest.of(pagina, tamanho, Sort.by("dataEmprestimo").descending()))
            .map(mapper::toResponse);
        return mapper.toPageResponse(page);
    }

    public EmprestimoResponse buscarPorId(Integer id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    /**
     * Gera código único baseado nas informações do empréstimo.
     * Combina: idEmprestimo + idCliente + idLivro + timestamp
     * Garante que cada código é único e rastreável.
     */
    private String gerarCodigo(Integer idEmprestimo, Integer idCliente, Integer idLivro) {
        try {
            String base = idEmprestimo + "-" + idCliente + "-" + idLivro + "-" + System.currentTimeMillis();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02X", hash[i]));
            }
            return hex.toString(); // 16 caracteres hex maiúsculos
        } catch (NoSuchAlgorithmException e) {
            // fallback seguro
            return String.format("%08X", Math.abs((idEmprestimo + idCliente + idLivro + System.currentTimeMillis()) % 0xFFFFFFFFL));
        }
    }

    private StatusEmprestimoEntity buscarStatus(String descricao) {
        return statusEmprestimoRepository.findByDescricao(descricao)
            .orElseThrow(() -> new RuntimeException("Status de empréstimo não encontrado: " + descricao));
    }

    public Emprestimo buscarEntidade(Integer id) {
        return emprestimoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado: id=" + id));
    }
}
