package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "tb_usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Pessoa implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private int id;

    @Column(name = "des_nome")
    private String nome;

    @Column(name = "des_email")
    private String email;

    @Column(name = "dat_nascimento")
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
    private Acl acl;

    public void cadastrarCliente() {

    }


    // permitem que o Spring Security leia os dados da Pessoa

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority(this.acl.getNome()));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.cpf; // Define que o campo de login (username) é o atributo 'cpf'
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Define que a conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Define que a conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Define que a senha não expirou
    }

    @Override
    public boolean isEnabled() {
        // O usuário só está habilitado se o status for 1 (Ativo)
        return this.status == 1;
    }
}
