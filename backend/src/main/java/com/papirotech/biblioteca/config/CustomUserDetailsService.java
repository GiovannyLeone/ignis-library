package com.papirotech.biblioteca.config;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AdministradorRepository administradorRepository;
    private final ClienteRepository clienteRepository;
    private final EstoquistaRepository estoquistaRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var admin = administradorRepository.findByEmail(username);
        if (admin.isPresent()) return admin.get();
        var cliente = clienteRepository.findByEmail(username);
        if (cliente.isPresent()) return cliente.get();
        return estoquistaRepository.findByCodigoAcesso(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
