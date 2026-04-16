package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BibliotecaMapper {

    public CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getDescricao());
    }

    public LivroResponse toResponse(Livro l) {
        return new LivroResponse(
                l.getId(), l.getIsbn(), l.getTitulo(), l.getAutor(),
                l.getEditora(), l.getSinopse(), l.getDataCadastro(),
                l.getAnoPublicacao(), l.getQuantidadeTotal(),
                l.getQuantidadeDisponivel(), toResponse(l.getCategoria()),
                l.verificarDisponibilidade()
        );
    }

    public UsuarioResponse toResponse(Usuario u) {
        return new UsuarioResponse(
                u.getId(), u.getNome(), u.getEmail(), u.getCpf(),
                u.getDataNascimento(), u.getSexo(), u.getStatus(), u.getPerfil()
        );
    }

    public EmprestimoResponse toResponse(Emprestimo e) {
        return new EmprestimoResponse(
                e.getId(), e.getCodigoRetirada(), e.getCodigoDevolucao(),
                e.getDataEmprestimo(), e.getDataDevolucaoPrevista(),
                e.getDataDevolucaoReal(), e.getStatus(), e.getPenalidadeGerada(),
                toResponse(e.getLivro()), toResponse(e.getUsuario())
        );
    }

    public FavoritoResponse toResponse(Favorito f) {
        return new FavoritoResponse(f.getId(), toResponse(f.getLivro()), true);
    }

    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast()
        );
    }
}
