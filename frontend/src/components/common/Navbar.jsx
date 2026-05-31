import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { User, Bell } from 'lucide-react'

export default function Navbar() {
  const { user, token, perfil, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const handleUserIcon = () => {
    if (!token) { navigate('/login'); return }
    if (perfil === 'CLIENTE') navigate('/cliente/perfil')
    else if (perfil === 'ADMINISTRADOR') navigate('/admin/dashboard')
    else if (perfil === 'ESTOQUISTA') navigate('/estoquista/retirada')
  }

  const homeLink = () => {
    if (!token) return '/login'
    if (perfil === 'ADMINISTRADOR') return '/admin/dashboard'
    if (perfil === 'ESTOQUISTA') return '/estoquista/retirada'
    return '/cliente/historico'
  }

  return (
    <header style={{
      background: '#D9B391',
      height: 73,
      boxShadow: '0px 4px 4px rgba(0,0,0,0.25)',
      position: 'sticky', top: 0, zIndex: 100,
      display: 'flex', alignItems: 'center',
      padding: '0 116px',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', width: '100%', maxWidth: 1168 }}>
        {/* Logo */}
        <Link to={homeLink()}>
          <span style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#A65A49' }}>
            PapiroTech
          </span>
        </Link>

        {/* Nav links */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 40 }}>
          <Link to={homeLink()} style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#2B3640' }}>
            Início
          </Link>

          {/* Catálogo só aparece para clientes */}
          {token && perfil === 'CLIENTE' && (
            <Link to="/catalogo" style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#2B3640' }}>
              Catálogo
            </Link>
          )}

          {/* Botão Sair — só quando logado */}
          {token && (
            <button
              onClick={handleLogout}
              style={{
                background: '#A65A49', color: 'white',
                padding: '6px 20px', borderRadius: 4,
                fontFamily: 'Raleway', fontWeight: 700, fontSize: 16,
              }}
            >
              Sair
            </button>
          )}

          {/* Ícones */}
          <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <Bell size={22} color="#2B3640" style={{ cursor: 'pointer' }} />
            <User
              size={22}
              color="#2B3640"
              style={{ cursor: 'pointer' }}
              onClick={handleUserIcon}
            />
          </div>
        </div>
      </div>
    </header>
  )
}
