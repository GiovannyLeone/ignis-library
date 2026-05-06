package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_favorito",
    uniqueConstraints = @UniqueConstraint(name = "uk_favorito", columnNames = {"id_usuario", "id_livro"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorito")
    private Integer idFavorito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_livro", nullable = false)
    private Livro livro;
}
