package com.papirotech.biblioteca.config;

import com.papirotech.biblioteca.repository.EstoquistaRepository;
import com.papirotech.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final EstoquistaRepository estoquistaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tenta carregar como usuário (admin/cliente) pelo e-mail
        var usuarioOpt = usuarioRepository.findByEmail(username);
        if (usuarioOpt.isPresent()) {
            return usuarioOpt.get();
        }
        // Tenta carregar como estoquista pelo código de acesso
        return estoquistaRepository.findByCodigoAcesso(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
