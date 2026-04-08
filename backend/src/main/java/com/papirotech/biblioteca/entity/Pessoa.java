package br.com.ignis.biblioteca.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "tb_usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int id;
    // Atributos de pessoas //
    @Column(name = "des_nome")
    private String nome;

    @Column(name = "des_email")
    private String email;

    @Column(name = "dat_nascimento") // date no banco = localdate no java
    private LocalDate dataNascimento;

    @Column(name = "des_senha")
    private String senha;

    @Column(name = "id_status_usuario")
    private int status;

    @Column(name = "des_cpf")
    private String cpf;

    @Column(name = "des_sexo")
    private String sexo;

    @ManyToOne
    @JoinColumn(name = "id_acl")
    private ACL acl;
}
