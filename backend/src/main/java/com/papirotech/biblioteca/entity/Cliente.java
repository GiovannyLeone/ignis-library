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
 *
 * Atributos:
 *   - status : Enum(ATIVO, BLOQUEADO) → via statusUsuario herdado de Pessoa
 *   - historico : List<Emprestimo>   — será mapeado quando Emprestimo for adicionado
 *   - favoritos : List<Livro>        — será mapeado quando Favorito for adicionado
 *
 * Métodos:
 *   + getStatus() / setStatus()
 *   + verificarStatus() : boolean
 *   + consultarHistorico() : List<Emprestimo>
 *   + gerarCodigoEmprestimo(livro, cliente) : Emprestimo  — no EmprestimoService
 *   + gerarCodigoDevolucao(emprestimo) : String            — no EmprestimoService
 *   + favoritarLivro(livro) : void                        — no FavoritoService
 */
@Entity
@DiscriminatorValue("CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Cliente extends Pessoa {

    // historico e favoritos serão adicionados quando Emprestimo for implementado

    // ===== Métodos de negócio conforme diagrama =====

    public boolean verificarStatus() {
        return getStatusUsuario() != null
            && StatusCliente.ATIVO.name().equals(getStatusUsuario().getDescricao());
    }

    // ===== Spring Security =====

    @Override
    public boolean isAccountNonLocked() {
        return verificarStatus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
}
