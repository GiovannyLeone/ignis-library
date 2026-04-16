package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.repository.EstoquistaRepository;
import com.papirotech.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository    usuarioRepository;
    private final EstoquistaRepository estoquistaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tenta como Usuario (Admin/Cliente) pelo e-mail
        var usuario = usuarioRepository.findByEmail(username);
        if (usuario.isPresent()) return usuario.get();

        // Tenta como Estoquista pelo codigoAcesso
        return estoquistaRepository.findByCodigoAcesso(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
