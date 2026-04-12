package com.papirotech.biblioteca.config;
import com.papirotech.biblioteca.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final EstoquistaRepository estoquistaRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tenta como Administrador
        var admin = administradorRepository.findByEmail(username);
        if (admin.isPresent()) return admin.get();
        // Tenta como Cliente
        var cliente = clienteRepository.findByEmail(username);
        if (cliente.isPresent()) return cliente.get();
        // Tenta como Estoquista (codigoAcesso)
        return estoquistaRepository.findByCodigoAcesso(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }
}
