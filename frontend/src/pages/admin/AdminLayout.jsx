import { Outlet, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function AdminLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  return (
    <div style={{ minHeight: '100vh', background: '#F2E2C4' }}>
      {/* Header */}
      <header style={{ background: '#D9B391', height: 60, boxShadow: '0px 4px 4px rgba(0,0,0,0.25)', display: 'flex', alignItems: 'center', padding: '0 32px', justifyContent: 'space-between', position: 'sticky', top: 0, zIndex: 100 }}>
        <span style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 22, color: '#A65A49' }}>
          PapiroTech <span style={{ fontWeight: 400, fontSize: 16, color: '#2B3640' }}>| Admin</span>
        </span>
        <button onClick={() => { logout(); navigate('/login') }} style={{ background: '#A65A49', color: 'white', padding: '6px 20px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 15 }}>
          Sair
        </button>
      </header>

      {/* Sub-nav tabs */}
      <div style={{ background: '#F2E2C4', padding: '16px 32px 0', borderBottom: '2px solid #D9B391', display: 'flex', gap: 4 }}>
        {[
          { to: '/admin/dashboard', label: '📊 Dashboard' },
          { to: '/admin/livros', label: '📚 Livros' },
          { to: '/admin/emprestimos', label: '📋 Empréstimos' },
          { to: '/admin/clientes', label: '👥 Clientes' },
        ].map((item, i) => (
          <NavLink key={i} to={item.to} style={({ isActive }) => ({
            padding: '8px 20px', background: isActive ? '#A65A49' : 'white',
            color: isActive ? 'white' : '#2B3640',
            border: '1px solid #D9B391', borderBottom: isActive ? 'none' : '1px solid #D9B391',
            borderRadius: '6px 6px 0 0',
            fontFamily: 'Raleway', fontWeight: 700, fontSize: 14,
            transition: 'all 0.15s',
          })}>
            {item.label}
          </NavLink>
        ))}
      </div>

      {/* Título */}
      <div style={{ padding: '20px 32px 0' }}>
        <h1 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#2B3640' }}>
          Painel Administrativo
        </h1>
      </div>

      <main style={{ padding: '20px 32px 48px' }}>
        <Outlet />
      </main>

      <footer style={{ background: '#D9B391', padding: '16px 32px', borderTop: '1px solid #C49A72' }}>
        <span style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 16, color: '#A65A49' }}>PapiroTech</span>
        <span style={{ fontFamily: 'Raleway', fontWeight: 300, fontSize: 14, color: '#A65A49', marginLeft: 12 }}>
          © 2026 PapiroTech. Todos os direitos reservados.
        </span>
      </footer>
    </div>
  )
}
