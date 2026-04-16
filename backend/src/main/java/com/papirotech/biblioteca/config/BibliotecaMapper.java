package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.dto.response.UsuarioResponse;
import com.papirotech.biblioteca.entity.Usuario;
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
}
