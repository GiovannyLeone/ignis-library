import { Outlet, NavLink } from 'react-router-dom'
import Footer from '../../components/common/Footer'
import Navbar from '../../components/common/Navbar'

export default function ClienteLayout() {
  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', background: '#F2E2C4' }}>
      <Navbar />

      {/* Sub-nav */}
      <div style={{ background: '#F2E2C4', padding: '24px 116px 0', display: 'flex', gap: 8 }}>
        {[
          { to: '/cliente/historico', label: 'Meus Alugueis' },
          { to: '/cliente/favoritos', label: 'Favoritos' },
          { to: '/cliente/perfil', label: 'Configurações de Perfil' },
        ].map(item => (
          <NavLink key={item.to} to={item.to} style={({ isActive }) => ({
            padding: '10px 24px',
            background: isActive ? '#A65A49' : 'transparent',
            color: isActive ? 'white' : '#A65A49',
            borderRadius: '8px 8px 0 0',
            fontFamily: 'Raleway', fontWeight: 700, fontSize: 16,
            border: '1px solid #A65A49',
            borderBottom: isActive ? 'none' : '1px solid #A65A49',
          })}>
            {item.label}
          </NavLink>
        ))}
      </div>

      <main style={{ flex: 1, padding: '0 116px 48px' }}>
        <Outlet />
      </main>
      <Footer />
    </div>
  )
}
