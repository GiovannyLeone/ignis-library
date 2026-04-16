package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BibliotecaMapper {

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
            u.getIdUsuario(),
            u.getNome(),
            u.getEmail(),
            u.getCpf(),
            u.getDataNascimento(),
            u.getSexo(),
            u.getStatusUsuario() != null ? u.getStatusUsuario().getDescricao() : null,
            u.getAcl() != null ? u.getAcl().getDescricao() : null
        );
    }

    public CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getIdCategoria(), c.getDescricao());
    }

    public LivroResponse toResponse(Livro l) {
        return new LivroResponse(
            l.getIdLivro(),
            l.getIsbn(),
            l.getTitulo(),
            l.getAutor(),
            toResponse(l.getCategoria()),
            l.getEditora(),
            l.getSinopse(),
            l.getDataCadastro(),
            l.getAnoPublicacao(),
            l.getQuantidadeTotal(),
            l.getQuantidadeDisponivel(),
            l.verificarDisponibilidade()
        );
    }

    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isLast()
        );
    }
}
