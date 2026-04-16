package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Classe ACL — conforme diagrama de classes seção 3.3.
 *
 * Atributos:
 *   - idAcl : int  (PK)
 *   - descricao : String  (ex.: ADMINISTRADOR, CLIENTE, ESTOQUISTA)
 *
 * Métodos:
 *   + getIdAcl() : int
 *   + getDescricao() : String
 *   + setDescricao(descricao: String) : void
 */
@Entity
@Table(name = "tb_acl")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Acl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acl")
    private Integer idAcl;

    // descricao é String conforme diagrama — ex.: ADMINISTRADOR, CLIENTE, ESTOQUISTA
    @Column(name = "des_acl", nullable = false, length = 255)
    private String descricao;
}
