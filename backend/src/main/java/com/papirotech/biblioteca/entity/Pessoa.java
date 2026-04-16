package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;

/**
 * Classe abstrata Pessoa — conforme diagrama de classes seção 3.2.
 * Mapeada para tb_usuario com herança SINGLE_TABLE.
 * Cliente e Administrador herdam desta classe e compartilham tb_usuario,
 * diferenciados pelo campo des_acl (discriminador via id_acl).
 *
 * Atributos: id, nome, cpf, dataNascimento, sexo, email, senha, acl
 * Métodos: getId, getNome/setNome, getCpf/setCpf, getDataNascimento/setDataNascimento,
 *          getSexo/setSexo, getEmail/setEmail, getSenha/setSenha, getAcl/setAcl,
 *          cadastrarCliente()
 */
@Entity
@Table(name = "tb_usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "des_discriminador", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Pessoa implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "des_nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "des_cpf", nullable = false, length = 255)
    private String cpf;

    @Column(name = "dat_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "des_sexo", nullable = false, length = 255)
    private String sexo;

    @Column(name = "des_email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "des_senha", nullable = false, length = 255)
    private String senha;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_acl", nullable = false)
    private Acl acl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status_usuario", nullable = false)
    private StatusUsuario statusUsuario;

    // ===== Método de negócio conforme diagrama =====
    public void cadastrarCliente() {
        // lógica delegada ao UsuarioService
    }

    // ===== Spring Security =====
    @Override public String getUsername()               { return this.email; }
    @Override public String getPassword()               { return this.senha; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return statusUsuario != null
            && "ATIVO".equals(statusUsuario.getDescricao());
    }
}
