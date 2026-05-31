import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
})

// Injeta token automaticamente
api.interceptors.request.use(config => {
  const t = localStorage.getItem('ignis_token')
  if (t) config.headers.Authorization = `Bearer ${t}`
  return config
})

// Redireciona para /login apenas se NÃO for a rota de auth
api.interceptors.response.use(res => res, err => {
  const isAuthRoute = err.config?.url?.includes('/api/auth/')
  if (err.response?.status === 401 && !isAuthRoute) {
    localStorage.clear()
    window.location.href = '/login'
  }
  return Promise.reject(err)
})

export const authService = {
  login: (login, senha) => api.post('/api/auth/login', { login, senha })
}

export const categoriaService = {
  listar: () => api.get('/api/livros/categorias'),
}

export const livroService = {
  listar: (p=0,t=12) => api.get(`/api/livros?pagina=${p}&tamanho=${t}`),
  listarDisponiveis: (p=0,t=12) => api.get(`/api/livros/disponiveis?pagina=${p}&tamanho=${t}`),
  buscar: (termo,p=0,t=12) => api.get(`/api/livros/buscar?termo=${termo}&pagina=${p}&tamanho=${t}`),
  buscarPorId: id => api.get(`/api/livros/${id}`),
  cadastrar: dados => api.post('/api/livros', dados),
  atualizar: (id, dados) => api.put(`/api/livros/${id}`, dados),
  remover: id => api.delete(`/api/livros/${id}`),
}

export const clienteService = {
  cadastrar: dados => api.post('/api/clientes/cadastro', dados),
  perfil: () => api.get('/api/clientes/perfil'),
  atualizar: dados => api.put('/api/clientes/perfil', dados),
}

export const favoritoService = {
  listar: () => api.get('/api/favoritos'),
  toggle: id => api.post(`/api/favoritos/${id}/toggle`),
}

export const emprestimoService = {
  reservar: id => api.post(`/api/emprestimos/livros/${id}/reservar`),
  gerarCodigoDevolucao: id => api.post(`/api/emprestimos/${id}/gerar-codigo-devolucao`),
  meuHistorico: (p=0,t=10) => api.get(`/api/emprestimos/meu-historico?pagina=${p}&tamanho=${t}`),
}

export const estoqueService = {
  retirada: codigo => api.post(`/api/estoque/retirada/${codigo}`),
  devolucao: codigo => api.post(`/api/estoque/devolucao/${codigo}`),
}

export const adminService = {
  listarUsuarios: (p=0,t=10) => api.get(`/api/admin/usuarios?pagina=${p}&tamanho=${t}`),
  listarEmprestimos: (p=0,t=10) => api.get(`/api/admin/emprestimos?pagina=${p}&tamanho=${t}`),
  emprestimosDoCliente: (clienteId,p=0,t=10) => api.get(`/api/admin/emprestimos/clientes/${clienteId}?pagina=${p}&tamanho=${t}`),
  aplicarPenalidade: id => api.post(`/api/admin/emprestimos/${id}/penalidade`),
  removerPenalidade: id => api.delete(`/api/admin/emprestimos/${id}/penalidade`),
}

export default api
