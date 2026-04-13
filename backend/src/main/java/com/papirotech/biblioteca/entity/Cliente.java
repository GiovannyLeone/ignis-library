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
 * Classe Cliente — herda de Pessoa. Mapeada em tb_usuario (JOINED com tb_pessoa).
 *
 * Atributos conforme diagrama (seção 3.4):
 *   - historico : List<Emprestimo>
 *   - favoritos : List<Livro>  (tb_favorito)
 *   - status : Enum(ATIVO, BLOQUEADO)  → FK → tb_status_usuario
 *
 * Métodos conforme diagrama:
 *   + getHistorico() : List<Emprestimo>
 *   + getFavoritos() : List<Livro>
 *   + getStatus() : Enum
 *   + setStatus(status: Enum) : void
 *   + consultarHistorico() : List<Emprestimo>
 *   + verificarStatus() : boolean
 *   + gerarCodigoEmprestimo(livro, cliente) : Emprestimo
 *   + gerarCodigoDevolucao(emprestimo) : String
 *   + favoritarLivro(livro) : void
 */
@Entity
@Table(name = "tb_usuario")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Pessoa {

    // status pertence a Cliente, não a Pessoa — conforme diagrama seção 3.4
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_status_usuario", nullable = false)
    private StatusUsuario status;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Emprestimo> historico;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Favorito> favoritos;


    // ===== Métodos de negócio conforme diagrama =====


    public boolean verificarStatus() {
        return status != null && StatusCliente.ATIVO.equals(status.getDescricao());
    }


}
