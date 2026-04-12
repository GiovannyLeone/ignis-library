package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe Livro — conforme diagrama de classes.
 *
 * Atributos:
 *   - idLivro, isbn, titulo, autor, categoria (FK), editora, sinopse,
 *     dataCadastro, anoPublicacao, quantidadeTotal, quantidadeDisponivel
 *
 * Métodos de negócio:
 *   - adicionarLivro()
 *   - removerLivro(): boolean
 *   - verificarDisponibilidade(): boolean
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

    @Column(name = "des_editora", nullable = false, length = 255)
    private String editora;

    @Column(name = "des_sinopse", nullable = false, columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "dat_cadastro", nullable = false)
    private LocalDate dataCadastro;

    @Column(name = "dat_ano_publicacao", nullable = false)
    private Integer anoPublicacao;

    @Column(name = "num_quantidade_total", nullable = false)
    private Integer quantidadeTotal;

    @Column(name = "num_quantidade_disponivel", nullable = false)
    private Integer quantidadeDisponivel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "livro", fetch = FetchType.LAZY)
    private List<Emprestimo> emprestimos;

    // ===== Métodos de negócio conforme diagrama =====

    public void adicionarLivro() {
        // lógica delegada ao LivroService
    }

    public boolean removerLivro() {
        if (this.emprestimos != null && this.emprestimos.stream()
                .anyMatch(e -> {
                    String s = e.getStatusEmprestimo().getDescricao();
                    return !s.equals("DEVOLVIDO") && !s.equals("DEVOLVIDO_COM_ATRASO") && !s.equals("CANCELADO");
                })) {
            return false;
        }
        return true;
    }

    public boolean verificarDisponibilidade() {
        return this.quantidadeDisponivel > 0;
    }

    public void decrementarDisponivel() {
        if (this.quantidadeDisponivel <= 0)
            throw new IllegalStateException("Livro sem exemplares disponíveis.");
        this.quantidadeDisponivel--;
    }

    public void incrementarDisponivel() {
        this.quantidadeDisponivel++;
    }
}
