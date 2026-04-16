package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade tb_status_usuario.
 * descricao armazena os valores do enum StatusCliente como String:
 * ATIVO ou BLOQUEADO — conforme diagrama seção 3.4.
 */
@Entity
@Table(name = "tb_status_usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatusUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_usuario")
    private Integer idStatusUsuario;

    // Armazena o name() do enum StatusCliente: ATIVO ou BLOQUEADO
    @Column(name = "des_status_usuario", nullable = false, length = 255)
    private String descricao;
}
