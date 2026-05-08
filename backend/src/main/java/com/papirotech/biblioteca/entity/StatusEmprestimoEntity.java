package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_status_emprestimo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatusEmprestimoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_emprestimo")
    private Integer idStatusEmprestimo;

    @Column(name = "des_status_emprestimo", nullable = false, length = 255)
    private String descricao;
}
