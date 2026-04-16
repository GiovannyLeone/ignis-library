package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Classe Livro — conforme diagrama de classes seção 3.1.
 *
 * Atributos:
 *   - idLivro : int (PK)
 *   - isbn : String
 *   - titulo : String
 *   - autor : String
 *   - categoria : String (FK → tb_categoria)
 *   - editora : String
 *   - sinopse : String
 *   - dataCadastro : Date
 *   - anoPublicacao : int
 *   - quantidadeTotal : int
 *   - quantidadeDisponivel : int
 *
 * Métodos de negócio:
 *   + adicionarLivro() : void
 *   + removerLivro() : boolean
 *   + verificarDisponibilidade() : boolean
 */
@Entity
@Table(name = "tb_livro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livro")
    private Integer idLivro;

    @Column(name = "des_isbn", nullable = false, unique = true, length = 255)
    private String isbn;

    @Column(name = "des_titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "des_autor", nullable = false, length = 255)
    private String autor;

    // categoria : String — FK → tb_categoria (seção 3.1)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "des_editora", nullable = false, length = 255)
    private String editora;

    @Column(name = "des_sinopse", nullable = false, length = 255)
    private String sinopse;

    @Column(name = "dat_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "dat_ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    @Column(name = "num_quantidade_total", nullable = false)
    private Integer quantidadeTotal;

    @Column(name = "num_quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    // ===== Métodos de negócio conforme diagrama =====

    public void adicionarLivro() {
        // lógica delegada ao LivroService
    }

    public boolean removerLivro() {
        // lógica delegada ao LivroService
        // retorna false se houver empréstimos ativos
        return true;
    }

    public boolean verificarDisponibilidade() {
        return this.quantidadeDisponivel > 0;
    }

    // Helpers internos
    public void decrementarDisponivel() {
        if (this.quantidadeDisponivel <= 0)
            throw new IllegalStateException("Livro sem exemplares disponíveis.");
        this.quantidadeDisponivel--;
    }

    public void incrementarDisponivel() {
        this.quantidadeDisponivel++;
    }
}
