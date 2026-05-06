package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_status_usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatusUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_usuario")
    private Integer idStatusUsuario;

    @Column(name = "des_status_usuario", nullable = false, length = 255)
    private String descricao;
}
