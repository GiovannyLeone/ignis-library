package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Classe Estoquista — seção 3.6.
 * Tabela independente tb_estoquista. Acessa via codigoAcesso + senha.
 */
@Entity
@Table(name = "tb_estoquista")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Estoquista implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoquista")
    private Integer idEstoquista;

    @Column(name = "des_codigo_acesso", nullable = false, unique = true, length = 255)
    private String codigoAcesso;

    @Column(name = "des_senha", nullable = false, length = 255)
    private String senha;

    // ===== Métodos de negócio conforme diagrama =====
    // Lógica real delegada ao EmprestimoService

    public boolean emprestarLivro(Emprestimo emprestimo) { return true; }

    public boolean devolverLivro(Emprestimo emprestimo) { return true; }

    // ===== Spring Security =====

    @Override public String getUsername()               { return this.codigoAcesso; }
    @Override public String getPassword()               { return this.senha; }
    @Override public boolean isAccountNonExpired()      { return true; }
    @Override public boolean isCredentialsNonExpired()  { return true; }
    @Override public boolean isEnabled()                { return true; }
    @Override public boolean isAccountNonLocked()       { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ESTOQUISTA"));
    }
}
