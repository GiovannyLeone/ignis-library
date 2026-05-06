package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("ADMINISTRADOR")
@Getter @Setter @NoArgsConstructor @SuperBuilder
public class Administrador extends Pessoa {

    @Column(name = "des_cargo", length = 255)
    private String cargo;

    public String aplicarPenalidade(Object emprestimo) { return "BLOQUEADO"; }
    public boolean removerPenalidade(Object emprestimo) { return true; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }
}
