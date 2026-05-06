package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BibliotecaMapper {
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

}
