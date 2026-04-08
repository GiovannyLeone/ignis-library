package com.papirotech.biblioteca.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Público
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/livros/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()

                // Cliente — cadastro próprio é público
                .requestMatchers(HttpMethod.POST, "/api/clientes/cadastro").permitAll()

                // Admin only
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST,   "/api/livros/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT,    "/api/livros/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/livros/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST,   "/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT,    "/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/penalidades/**").hasRole("ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/emprestimos/todos").hasRole("ADMINISTRADOR")

                // Estoquista only
                .requestMatchers("/api/estoque/**").hasRole("ESTOQUISTA")

                // Cliente + Admin
                .requestMatchers("/api/clientes/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                .requestMatchers("/api/emprestimos/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "ESTOQUISTA")
                .requestMatchers("/api/favoritos/**").hasRole("CLIENTE")

                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
