package com.papirotech.biblioteca.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Classe Administrador — herda de Pessoa. Mapeada em tb_usuario.
 * Conforme diagrama de classes seção 3.5.
 */
@Entity
@DiscriminatorValue("ADMINISTRADOR")
@Getter @Setter @NoArgsConstructor @SuperBuilder
public class Administrador extends Pessoa {

    @Column(name = "des_cargo", length = 255)
    private String cargo;

    // ===== Métodos de negócio conforme diagrama =====
    // Lógica real delegada ao EmprestimoService

    public String aplicarPenalidade(Emprestimo emprestimo) { return "BLOQUEADO"; }

    public boolean removerPenalidade(Emprestimo emprestimo) { return true; }

    // ===== Spring Security =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
    }
}
