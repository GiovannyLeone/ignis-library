package br.com.ignis.biblioteca.model;
import jakarta.persistence.*;
import lombok.Data;

@Data // Get/Set automatico
@Entity // avisa que a tabela é um banco
@Table(name = "tb_acl")
public class ACL {

    @Id // idAcl é a chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // garante que não tenha id duplicado
    @Column(name = "id_acl")
    private int idAcl;

    @Column(name = "des_acl", nullable = false, length = 255)
    private String descricao;
}