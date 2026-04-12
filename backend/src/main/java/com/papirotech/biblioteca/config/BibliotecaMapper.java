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
                l.getIdLivro(), l.getIsbn(), l.getTitulo(), l.getAutor(),
                l.getEditora(), l.getSinopse(), l.getDataCadastro(),
                l.getAnoPublicacao(), l.getQuantidadeTotal(),
                l.getQuantidadeDisponivel(), toResponse(l.getCategoria()),
                l.verificarDisponibilidade()
        );
    }

    public PessoaResponse toResponse(Pessoa p) {
        // status só existe em Cliente — verifica o tipo
        String status = null;
        if (p instanceof Cliente cliente && cliente.getStatus() != null) {
            status = cliente.getStatus().getDescricao().name();
        }
        return new PessoaResponse(
                p.getId(), p.getNome(), p.getEmail(), p.getCpf(),
                p.getDataNascimento(), p.getSexo(),
                status,
                p.getAcl() != null ? p.getAcl().getDescricao().name() : null
        );
    }

    public EmprestimoResponse toResponse(Emprestimo e) {
        return new EmprestimoResponse(
                e.getId(),
                e.getCodigoRetiradaEmprestimo(),
                e.getCodigoDevolucaoEmprestimo(),
                e.getDataEmprestimo(),
                e.getDataDevolucaoPrevista(),
                e.getDataDevolucaoReal(),
                e.getStatusEmprestimo().getDescricao().name(),
                e.getPenalidadeGerada(),
                toResponse(e.getLivro()),
                toResponse(e.getCliente())
        );
    }

    public FavoritoResponse toResponse(Favorito f) {
        return new FavoritoResponse(f.getId(), toResponse(f.getLivro()));
    }

    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast()
        );
    }
}
