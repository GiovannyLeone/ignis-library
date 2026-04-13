package com.papirotech.biblioteca.entity;

import com.papirotech.biblioteca.enums.PerfilAcesso;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade tb_acl — tabela real no banco conforme diagrama de banco.
 * Usa o enum PerfilAcesso (ADMINISTRADOR, CLIENTE, ESTOQUISTA)
 * conforme diagrama de classes.
 */
@Entity
@Table(name = "tb_acl")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Acl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acl")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "des_acl", nullable = false, columnDefinition = "VARCHAR(50)")
    private PerfilAcesso descricao;

    public Acl(PerfilAcesso descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoStr() {
        return descricao != null ? descricao.name() : null;
    }
}
