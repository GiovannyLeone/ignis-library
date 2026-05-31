import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import LoginPage from './pages/public/LoginPage'
import CadastroPage from './pages/public/CadastroPage'
import CatalogoPage from './pages/public/CatalogoPage'
import LivroDetalhePage from './pages/public/LivroDetalhePage'
import ClienteLayout from './pages/cliente/ClienteLayout'
import HistoricoPage from './pages/cliente/HistoricoPage'
import FavoritosPage from './pages/cliente/FavoritosPage'
import PerfilPage from './pages/cliente/PerfilPage'
import EstoquistaLayout from './pages/estoquista/EstoquistaLayout'
import RetiradaPage from './pages/estoquista/RetiradaPage'
import DevolucaoPage from './pages/estoquista/DevolucaoPage'
import AdminLayout from './pages/admin/AdminLayout'
import DashboardPage from './pages/admin/DashboardPage'
import GerenciarLivrosPage from './pages/admin/GerenciarLivrosPage'
import GerenciarClientesPage from './pages/admin/GerenciarClientesPage'
import GerenciarEmprestimosPage from './pages/admin/GerenciarEmprestimosPage'

// Rota que exige login — redireciona para /login se não autenticado
function RotaAutenticada({ children }) {
  const { token } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  return children
}

// Rota que exige perfil específico — redireciona para a área correta se perfil errado
function RotaProtegida({ children, perfisPermitidos }) {
  const { token, perfil } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (!perfisPermitidos.includes(perfil)) {
    // Redireciona cada perfil para sua área
    if (perfil === 'ADMINISTRADOR') return <Navigate to="/admin/dashboard" replace />
    if (perfil === 'ESTOQUISTA') return <Navigate to="/estoquista/retirada" replace />
    if (perfil === 'CLIENTE') return <Navigate to="/cliente/historico" replace />
    return <Navigate to="/login" replace />
  }
  return children
}

// Redireciona para a área correta após login
function RedirectPorPerfil() {
  const { token, perfil } = useAuth()
  if (!token) return <Navigate to="/login" replace />
  if (perfil === 'ADMINISTRADOR') return <Navigate to="/admin/dashboard" replace />
  if (perfil === 'ESTOQUISTA') return <Navigate to="/estoquista/retirada" replace />
  return <Navigate to="/cliente/historico" replace />
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Raiz — redireciona para login */}
          <Route path="/" element={<Navigate to="/login" replace />} />

          {/* Login — única rota pública */}
          <Route path="/login" element={<LoginPage />} />

          {/* Cadastro — público, redireciona se já logado */}
          <Route path="/cadastro" element={<CadastroPage />} />

          {/* Após login — redireciona para área correta */}
          <Route path="/inicio" element={<RedirectPorPerfil />} />

          {/* Catálogo e detalhe — apenas CLIENTE */}
          <Route path="/catalogo" element={
            <RotaProtegida perfisPermitidos={['CLIENTE']}><CatalogoPage /></RotaProtegida>
          } />
          <Route path="/livros/:id" element={
            <RotaProtegida perfisPermitidos={['CLIENTE']}><LivroDetalhePage /></RotaProtegida>
          } />

          {/* Área do Cliente */}
          <Route path="/cliente" element={
            <RotaProtegida perfisPermitidos={['CLIENTE']}>
              <ClienteLayout />
            </RotaProtegida>
          }>
            <Route index element={<Navigate to="/cliente/historico" replace />} />
            <Route path="historico" element={<HistoricoPage />} />
            <Route path="favoritos" element={<FavoritosPage />} />
            <Route path="perfil" element={<PerfilPage />} />
          </Route>

          {/* Área do Estoquista */}
          <Route path="/estoquista" element={
            <RotaProtegida perfisPermitidos={['ESTOQUISTA']}>
              <EstoquistaLayout />
            </RotaProtegida>
          }>
            <Route index element={<Navigate to="/estoquista/retirada" replace />} />
            <Route path="retirada" element={<RetiradaPage />} />
            <Route path="devolucao" element={<DevolucaoPage />} />
          </Route>

          {/* Área do Admin */}
          <Route path="/admin" element={
            <RotaProtegida perfisPermitidos={['ADMINISTRADOR']}>
              <AdminLayout />
            </RotaProtegida>
          }>
            <Route index element={<Navigate to="/admin/dashboard" replace />} />
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="livros" element={<GerenciarLivrosPage />} />
            <Route path="clientes" element={<GerenciarClientesPage />} />
            <Route path="emprestimos" element={<GerenciarEmprestimosPage />} />
          </Route>

          {/* Qualquer rota desconhecida → login */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
