package com.papirotech.biblioteca.service;

import com.papirotech.biblioteca.config.BibliotecaMapper;
import com.papirotech.biblioteca.dto.request.CadastroUsuarioRequest;
import com.papirotech.biblioteca.dto.response.UsuarioResponse;
import com.papirotech.biblioteca.entity.*;
import com.papirotech.biblioteca.enums.StatusCliente;
import com.papirotech.biblioteca.exception.ClienteJaExisteException;
import com.papirotech.biblioteca.repository.*;
import com.papirotech.biblioteca.service.impl.UsuarioService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService — Testes Unitários")
class UsuarioServiceTest {

    @Mock private ClienteRepository       clienteRepository;
    @Mock private AdministradorRepository administradorRepository;
    @Mock private AclRepository           aclRepository;
    @Mock private StatusUsuarioRepository statusUsuarioRepository;
    @Mock private PasswordEncoder         passwordEncoder;
    @Mock private BibliotecaMapper        mapper;
    @Mock private JdbcTemplate            jdbc;

    @InjectMocks private UsuarioService usuarioService;

    private Acl aclCliente;
    private StatusUsuario statusAtivo;
    private Cliente cliente;
    private UsuarioResponse usuarioResponse;

    @BeforeEach
    void setUp() {
        aclCliente  = Acl.builder().idAcl(2).descricao("CLIENTE").build();
        statusAtivo = StatusUsuario.builder().idStatusUsuario(1).descricao("ATIVO").build();

        cliente = Cliente.builder()
            .nome("João Silva").email("joao@email.com").cpf("12345678901")
            .senha("hashed").dataNascimento(LocalDate.of(2000, 1, 1)).sexo("M")
            .acl(aclCliente).statusUsuario(statusAtivo).build();

        usuarioResponse = new UsuarioResponse(
            1, "João Silva", "joao@email.com", "12345678901",
            LocalDate.of(2000, 1, 1), "M", "ATIVO", "CLIENTE");
    }

    // ─── T10: RF06 — cadastrarCliente ────────────────────────────────────────

    @Test
    @DisplayName("T10 — RF06: deve cadastrar cliente com sucesso")
    void deveCadastrarClienteComSucesso() {
        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
            "João Silva", "joao@email.com", "12345678901",
            "senha123", LocalDate.of(2000, 1, 1), "M");

        when(jdbc.queryForObject(contains("des_email"), eq(Integer.class), eq("joao@email.com")))
            .thenReturn(0);
        when(jdbc.queryForObject(contains("des_cpf"), eq(Integer.class), eq("12345678901")))
            .thenReturn(0);
        when(aclRepository.findByDescricao("CLIENTE")).thenReturn(Optional.of(aclCliente));
        when(statusUsuarioRepository.findByDescricao(StatusCliente.ATIVO.name()))
            .thenReturn(Optional.of(statusAtivo));
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");
        when(clienteRepository.save(any())).thenReturn(cliente);
        when(mapper.toResponse(cliente)).thenReturn(usuarioResponse);

        UsuarioResponse resultado = usuarioService.cadastrar(req);

        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo("joao@email.com");
        assertThat(resultado.status()).isEqualTo("ATIVO");
        assertThat(resultado.perfil()).isEqualTo("CLIENTE");
    }

    @Test
    @DisplayName("T11 — RF06: deve lançar exceção para e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
            "João Silva", "joao@email.com", "12345678901",
            "senha123", LocalDate.of(2000, 1, 1), "M");

        when(jdbc.queryForObject(contains("des_email"), eq(Integer.class), eq("joao@email.com")))
            .thenReturn(1);

        assertThatThrownBy(() -> usuarioService.cadastrar(req))
            .isInstanceOf(ClienteJaExisteException.class)
            .hasMessageContaining("E-mail já cadastrado");
    }

    @Test
    @DisplayName("T11b — RF06: deve lançar exceção para CPF duplicado")
    void deveLancarExcecaoCpfDuplicado() {
        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
            "João Silva", "joao@email.com", "12345678901",
            "senha123", LocalDate.of(2000, 1, 1), "M");

        when(jdbc.queryForObject(contains("des_email"), eq(Integer.class), eq("joao@email.com")))
            .thenReturn(0);
        when(jdbc.queryForObject(contains("des_cpf"), eq(Integer.class), eq("12345678901")))
            .thenReturn(1);

        assertThatThrownBy(() -> usuarioService.cadastrar(req))
            .isInstanceOf(ClienteJaExisteException.class)
            .hasMessageContaining("CPF já cadastrado");
    }

    // ─── T12: verificarStatus ────────────────────────────────────────────────

    @Test
    @DisplayName("T12 — verificarStatus: deve retornar true para cliente ATIVO")
    void verificarStatusAtivo() {
        assertThat(cliente.verificarStatus()).isTrue();
    }

    @Test
    @DisplayName("T13 — verificarStatus: deve retornar false para cliente BLOQUEADO")
    void verificarStatusBloqueado() {
        StatusUsuario bloqueado = StatusUsuario.builder()
            .idStatusUsuario(2).descricao("BLOQUEADO").build();
        cliente.setStatusUsuario(bloqueado);

        assertThat(cliente.verificarStatus()).isFalse();
    }
}
