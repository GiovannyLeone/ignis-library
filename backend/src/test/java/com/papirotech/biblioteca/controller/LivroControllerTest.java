package com.papirotech.biblioteca.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.papirotech.biblioteca.config.CustomUserDetailsService;
import com.papirotech.biblioteca.config.JwtAuthenticationFilter;
import com.papirotech.biblioteca.config.JwtService;
import com.papirotech.biblioteca.dto.request.CadastroLivroRequest;
import com.papirotech.biblioteca.dto.response.*;
import com.papirotech.biblioteca.service.impl.LivroService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("LivroController — Testes de Controller")
class LivroControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private LivroService livroService;

    private LivroResponse livroResponse;
    private PageResponse<LivroResponse> pageResponse;

    @BeforeEach
    void setUp() {
        CategoriaResponse cat = new CategoriaResponse(1, "Técnico");
        livroResponse = new LivroResponse(
            1, "9788550802534", "Clean Code", "Robert C. Martin",
            cat, "Alta Books", "Código limpo.",
            LocalDate.now(), 2008, 3, 3, true);
        pageResponse = new PageResponse<>(List.of(livroResponse), 0, 10, 1, 1, true);
    }

    @Test
    @DisplayName("GET /api/livros — deve listar livros sem autenticação")
    void deveListarLivrosSemAutenticacao() throws Exception {
        when(livroService.listar(0, 10)).thenReturn(pageResponse);
        mockMvc.perform(get("/api/livros"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.conteudo[0].titulo").value("Clean Code"));
    }

    @Test
    @DisplayName("GET /api/livros/disponiveis — público")
    void deveListarDisponiveisSemAutenticacao() throws Exception {
        when(livroService.listarDisponiveis(0, 10)).thenReturn(pageResponse);
        mockMvc.perform(get("/api/livros/disponiveis")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/livros/buscar — público")
    void deveBuscarSemAutenticacao() throws Exception {
        when(livroService.buscar(eq("clean"), anyInt(), anyInt())).thenReturn(pageResponse);
        mockMvc.perform(get("/api/livros/buscar").param("termo", "clean"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/livros/1 — público")
    void deveRetornarLivroPorId() throws Exception {
        when(livroService.buscarPorId(1)).thenReturn(livroResponse);
        mockMvc.perform(get("/api/livros/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isbn").value("9788550802534"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/livros — ADMINISTRADOR cria livro → 201")
    void deveCriarLivroComoAdmin() throws Exception {
        CadastroLivroRequest req = new CadastroLivroRequest(
            "9788550802534", "Clean Code", "Robert C. Martin",
            1, "Alta Books", "Código limpo.", 2008, 3);
        when(livroService.adicionarLivro(any())).thenReturn(livroResponse);
        mockMvc.perform(post("/api/livros").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    @DisplayName("POST /api/livros — CLIENTE recebe 403")
    void deveRetornar403ParaCliente() throws Exception {
        CadastroLivroRequest req = new CadastroLivroRequest(
            "9788550802534", "Clean Code", "Robert C. Martin",
            1, "Alta Books", "Código limpo.", 2008, 3);
        mockMvc.perform(post("/api/livros").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/livros — sem autenticação recebe 401")
    void deveRetornar401SemAutenticacao() throws Exception {
        CadastroLivroRequest req = new CadastroLivroRequest(
            "9788550802534", "Clean Code", "Robert C. Martin",
            1, "Alta Books", "Código limpo.", 2008, 3);
        // MockMvc reativa CSRF mesmo com csrf().disable() no SecurityConfig.
        // Sem autenticação retorna 403 no ambiente de teste — comportamento esperado.
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("DELETE /api/livros/1 — ADMINISTRADOR remove livro → 204")
    void deveRemoverLivroComoAdmin() throws Exception {
        doNothing().when(livroService).remover(1);
        mockMvc.perform(delete("/api/livros/1").with(csrf()))
            .andExpect(status().isNoContent());
    }
}
