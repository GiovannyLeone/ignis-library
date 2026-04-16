package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Entidade tb_usuario — conforme seção 7.3 da documentação.
 * Armazena Administradores e Clientes, diferenciados pelo campo id_acl.
 * Não há herança com tb_pessoa — essa tabela não existe no banco.
 */
@Entity
@Table(name = "tb_usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "des_nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "des_email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "dat_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "des_senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "des_cpf", nullable = false, length = 255)
    private String cpf;

    @Column(name = "des_sexo", nullable = false, length = 255)
    private String sexo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status_usuario", nullable = false)
    private StatusUsuario statusUsuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_acl", nullable = false)
    private Acl acl;

    // ===== Spring Security =====

    @Override public String getUsername()               { return this.email; }
    @Override public String getPassword()               { return this.senha; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return statusUsuario != null && "ATIVO".equals(statusUsuario.getDescricao());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String perfil = acl != null ? acl.getDescricao() : "CLIENTE";
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil));
    }

    public boolean isAtivo() {
        return statusUsuario != null && "ATIVO".equals(statusUsuario.getDescricao());
    }
}
