package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BibliotecaMapper {

    public UsuarioResponse toResponse(Pessoa p) {
        return new UsuarioResponse(
            p.getId(), p.getNome(), p.getEmail(), p.getCpf(),
            p.getDataNascimento(), p.getSexo(),
            p.getStatusUsuario() != null ? p.getStatusUsuario().getDescricao() : null,
            p.getAcl() != null ? p.getAcl().getDescricao() : null
        );
    }

    public CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getIdCategoria(), c.getDescricao());
    }

    public LivroResponse toResponse(Livro l) {
        return new LivroResponse(
            l.getIdLivro(), l.getIsbn(), l.getTitulo(), l.getAutor(),
            toResponse(l.getCategoria()), l.getEditora(), l.getSinopse(),
            l.getDataCadastro(), l.getAnoPublicacao(),
            l.getQuantidadeTotal(), l.getQuantidadeDisponivel(),
            l.verificarDisponibilidade()
        );
    }

    public FavoritoResponse toResponse(Favorito f) {
        return new FavoritoResponse(f.getIdFavorito(), toResponse(f.getLivro()));
    }

    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
            page.getContent(), page.getNumber(), page.getSize(),
            page.getTotalElements(), page.getTotalPages(), page.isLast()
        );
    }
}
