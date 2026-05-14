package com.papirotech.biblioteca.repository;

import com.papirotech.biblioteca.entity.Emprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Integer> {

    Optional<Emprestimo> findByCodigoRetiradaEmprestimo(String codigo);

    Optional<Emprestimo> findByCodigoDevolucaoEmprestimo(String codigo);

    Page<Emprestimo> findByClienteId(Integer clienteId, Pageable pageable);

    // Verifica se livro tem empréstimos ativos (para RF03 — removerLivro)
    @Query("SELECT COUNT(e) > 0 FROM Emprestimo e WHERE e.livro.idLivro = :livroId " +
           "AND e.status.descricao NOT IN ('DEVOLVIDO', 'DEVOLVIDO_COM_ATRASO', 'CANCELADO')")
    boolean existsEmprestimoAtivoDoLivro(@Param("livroId") Integer livroId);

    // Verifica se cliente já tem empréstimo ativo ou reservado deste livro
    @Query("SELECT COUNT(e) > 0 FROM Emprestimo e WHERE e.cliente.id = :clienteId " +
           "AND e.livro.idLivro = :livroId " +
           "AND e.status.descricao IN ('RESERVADO', 'ATIVO', 'ATRASADO', " +
           "'EM_PROCESSO_DE_DEVOLUCAO', 'EM_PROCESSO_DE_DEVOLUCAO_ATRASADO')")
    boolean existsEmprestimoAtivoDoClienteELivro(@Param("clienteId") Integer clienteId,
                                                  @Param("livroId") Integer livroId);

    // Empréstimos ATIVO com prazo vencido — para job de marcar ATRASADO
    @Query("SELECT e FROM Emprestimo e WHERE e.status.descricao = 'ATIVO' " +
           "AND e.dataDevolucaoPrevista < CURRENT_DATE")
    List<Emprestimo> findEmprestimosAtrasados();

    // Reservas com mais de 24h sem retirada — para job de cancelar
    @Query("SELECT e FROM Emprestimo e WHERE e.status.descricao = 'RESERVADO' " +
           "AND e.dataEmprestimo < :limite")
    List<Emprestimo> findReservasExpiradasAntesDe(@Param("limite") java.time.LocalDateTime limite);
}
