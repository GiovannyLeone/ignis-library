package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe Emprestimo — conforme diagrama de classes seção 3.7.
 *
 * Atributos:
 *   - id : int (PK)
 *   - livro : Livro (FK → tb_livro)
 *   - cliente : Cliente (FK → tb_usuario)
 *   - dataEmprestimo : Date
 *   - codigoRetiradaEmprestimo : String (único)
 *   - codigoDevolucaoEmprestimo : String
 *   - dataDevolucaoPrevista : Date
 *   - dataDevolucaoReal : Date
 *   - status : Enum (FK → tb_status_emprestimo)
 *   - penalidadeGerada : boolean
 */
@Entity
@Table(name = "tb_emprestimo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_emprestimo")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Cliente cliente;

    @Column(name = "dat_emprestimo", nullable = false)
    private LocalDateTime dataEmprestimo;

    @Column(name = "des_codigo_retirada", nullable = false, unique = true, length = 255)
    private String codigoRetiradaEmprestimo;

    @Column(name = "des_codigo_devolucao", length = 255)
    private String codigoDevolucaoEmprestimo;

    @Column(name = "dat_devolucao_prevista", nullable = false)
    private LocalDate dataDevolucaoPrevista;

    @Column(name = "dat_devolucao_real")
    private LocalDate dataDevolucaoReal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status_emprestimo", nullable = false)
    private StatusEmprestimoEntity status;

    @Column(name = "bit_penalidade_gerada", nullable = false)
    @Builder.Default
    private Boolean penalidadeGerada = false;

    // ===== Métodos de negócio conforme diagrama =====

    public boolean isPenalidadeGerada() {
        return Boolean.TRUE.equals(this.penalidadeGerada);
    }
}
