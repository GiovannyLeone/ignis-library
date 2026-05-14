package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EmprestimoScheduler — jobs agendados para manutenção dos empréstimos.
 *
 * Job 1 — marcarAtrasados():
 *   Roda todo dia à meia-noite.
 *   Busca empréstimos com status ATIVO cuja dataDevolucaoPrevista já passou
 *   e atualiza para ATRASADO.
 *
 * Job 2 — cancelarReservasExpiradas():
 *   Roda a cada hora.
 *   Busca empréstimos com status RESERVADO há mais de 24h sem retirada
 *   e cancela automaticamente, devolvendo o exemplar ao acervo.
 */
@Component
@Profile("!test")
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class EmprestimoScheduler {

    private final EmprestimoRepository       emprestimoRepository;
    private final StatusEmprestimoRepository statusEmprestimoRepository;
    private final LivroRepository            livroRepository;

    // ─── Roda todo dia à meia-noite ──────────────────────────────────────────
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void marcarAtrasados() {
        StatusEmprestimoEntity atrasado = buscarStatus("ATRASADO");

        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosAtrasados();

        if (emprestimos.isEmpty()) {
            log.info(">>> Scheduler: nenhum empréstimo em atraso.");
            return;
        }

        for (Emprestimo e : emprestimos) {
            e.setStatus(atrasado);
            emprestimoRepository.save(e);
            log.warn(">>> Empréstimo #{} marcado como ATRASADO — cliente: {} — livro: {}",
                e.getId(), e.getCliente().getNome(), e.getLivro().getTitulo());
        }

        log.info(">>> Scheduler: {} empréstimo(s) marcado(s) como ATRASADO.", emprestimos.size());
    }

    // ─── Roda a cada hora ────────────────────────────────────────────────────
    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void cancelarReservasExpiradas() {
        StatusEmprestimoEntity cancelado = buscarStatus("CANCELADO");

        List<Emprestimo> reservasExpiradas = emprestimoRepository
            .findReservasExpiradasAntesDe(java.time.LocalDateTime.now().minusHours(24));

        if (reservasExpiradas.isEmpty()) return;

        for (Emprestimo e : reservasExpiradas) {
            e.setStatus(cancelado);

            // Devolve o exemplar ao acervo
            Livro livro = e.getLivro();
            livro.incrementarDisponivel();
            livroRepository.save(livro);

            emprestimoRepository.save(e);
            log.info(">>> Reserva #{} cancelada por expiração — livro '{}' devolvido ao acervo.",
                e.getId(), livro.getTitulo());
        }

        log.info(">>> Scheduler: {} reserva(s) expirada(s) cancelada(s).", reservasExpiradas.size());
    }

    // ─── Helper ──────────────────────────────────────────────────────────────
    private StatusEmprestimoEntity buscarStatus(String descricao) {
        return statusEmprestimoRepository.findByDescricao(descricao)
            .orElseThrow(() -> new RuntimeException("Status não encontrado: " + descricao));
    }
}
