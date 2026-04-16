package com.papirotech.biblioteca.service.impl;



public class PessoaService {

//    private final ClienteRepository       clienteRepository;
//    //private final AclRepository           aclRepository;
//    //private final StatusUsuarioRepository statusUsuarioRepository;
//    //private final PasswordEncoder         passwordEncoder;
//    //private final BibliotecaMapper        mapper;
//
//    // ─── RF06: cadastrarCliente() ─────────────────────────────────────────────
//    @Transactional
//    public PessoaResponse cadastrarCliente(CadastroClienteRequest req) {
//        if (clienteRepository.existsByEmail(req.email()))
//            throw new ClienteJaExisteException("E-mail já cadastrado: " + req.email());
//        if (clienteRepository.existsByCpf(req.cpf()))
//            throw new ClienteJaExisteException("CPF já cadastrado.");
//
//        Cliente c = Cliente.builder()
//                .nome(req.nome()).email(req.email()).cpf(req.cpf())
//                .senha(passwordEncoder.encode(req.senha()))
//                .dataNascimento(req.dataNascimento()).sexo(req.sexo())
//                .acl(buscarAcl(PerfilAcesso.CLIENTE))
//                // status pertence a Cliente — inicia como ATIVO
//                .status(buscarStatus(StatusCliente.ATIVO))
//                .build();
//        return mapper.toResponse(clienteRepository.save(c));
//    }
//
//    // ─── RF16: atualizarCliente() — próprio perfil ───────────────────────────
////    @Transactional
////    public PessoaResponse atualizarProprioPerfil(AtualizarPessoaRequest req) {
////        Cliente c = clienteLogado();
////        aplicarAtualizacao(c, req);
////        return mapper.toResponse(clienteRepository.save(c));
////    }
//
//    // ─── Listar / Buscar ─────────────────────────────────────────────────────
//    public PageResponse<PessoaResponse> listarClientes(int pagina, int tamanho) {
//        Page<PessoaResponse> p = clienteRepository
//                .findAll(PageRequest.of(pagina, tamanho, Sort.by("nome")))
//                .map(mapper::toResponse);
//        return mapper.toPageResponse(p);
//    }
//
//    public PessoaResponse buscarClientePorId(Integer id) {
//        return mapper.toResponse(clienteRepository.findById(id)
//                .orElseThrow(() -> new PessoaNaoEncontradaException("Cliente não encontrado: " + id)));
//    }
//
//    // ─── Helpers ─────────────────────────────────────────────────────────────
//    public Cliente clienteLogado() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        return clienteRepository.findByEmail(email)
//                .orElseThrow(() -> new PessoaNaoEncontradaException("Cliente não encontrado."));
//    }
//
//    private Acl buscarAcl(PerfilAcesso perfil) {
//        return aclRepository.findByDescricao(perfil)
//                .orElseThrow(() -> new RuntimeException("ACL não encontrada: " + perfil));
//    }
//
//    private StatusUsuario buscarStatus(StatusCliente status) {
//        return statusUsuarioRepository.findByDescricao(status)
//                .orElseThrow(() -> new RuntimeException("StatusUsuario não encontrado: " + status));
//    }

}