package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_acl")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Acl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acl")
    private Integer idAcl;

    @Column(name = "des_acl", nullable = false, length = 255)
    private String descricao;
}
