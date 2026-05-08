package com.papirotech.biblioteca.entity;

import com.papirotech.biblioteca.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Classe Cliente — herda de Pessoa. Mapeada em tb_usuario.
 * Conforme diagrama de classes seção 3.4.
 */
@Entity
@DiscriminatorValue("CLIENTE")
@Getter @Setter @NoArgsConstructor @SuperBuilder
public class Cliente extends Pessoa {

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Emprestimo> historico;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Favorito> favoritos;

    // ===== Métodos de negócio conforme diagrama =====

    public List<Emprestimo> consultarHistorico() { return this.historico; }

    public boolean verificarStatus() {
        return getStatusUsuario() != null
            && StatusCliente.ATIVO.name().equals(getStatusUsuario().getDescricao());
    }

    public void favoritarLivro(Livro livro) {}

    public Emprestimo gerarCodigoEmprestimo(Livro livro, Cliente cliente) { return null; }

    public String gerarCodigoDevolucao(Emprestimo emprestimo) { return null; }

    // ===== Spring Security =====

    @Override
    public boolean isAccountNonLocked() { return verificarStatus(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
}
