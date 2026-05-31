import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminService, livroService } from '../../services/api'

export default function DashboardPage() {
  const [stats, setStats] = useState({ livros: 0, clientes: 0, emprestimos: 0, atrasados: 0 })
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    Promise.all([
      livroService.listar(0, 1),
      adminService.listarUsuarios(0, 1),
      adminService.listarEmprestimos(0, 1),
    ]).then(([l, u, e]) => {
      setStats({
        livros: l.data.totalElementos || 0,
        clientes: u.data.totalElementos || 0,
        emprestimos: e.data.totalElementos || 0,
        atrasados: e.data.conteudo?.filter(x => x.status === 'ATRASADO').length || 0,
      })
    }).catch(console.error).finally(() => setLoading(false))
  }, [])

  const cards = [
    { label: 'Total de Livros', value: stats.livros, icon: '📚', borderColor: '#2B3640', link: '/admin/livros' },
    { label: 'Com Atraso', value: stats.atrasados, icon: '⚠️', borderColor: '#C0392B', link: '/admin/emprestimos' },
    { label: 'Empréstimos Ativos', value: stats.emprestimos, icon: '📋', borderColor: '#A65A49', link: '/admin/emprestimos' },
    { label: 'Clientes', value: stats.clientes, icon: '👥', borderColor: '#D9B391', link: '/admin/clientes' },
    { label: 'Usuários Ativos', value: stats.clientes, icon: '✅', borderColor: '#2D7A4F', link: '/admin/clientes' },
  ]

  return (
    <div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))', gap: 16, marginBottom: 32 }}>
        {cards.map(card => (
          <div key={card.label} onClick={() => navigate(card.link)} style={{
            background: 'white', borderRadius: 10, padding: '20px 16px',
            border: `1px solid #D9B391`, borderLeft: `4px solid ${card.borderColor}`,
            cursor: 'pointer', boxShadow: '0px 2px 4px rgba(0,0,0,0.1)',
            transition: 'transform 0.2s, box-shadow 0.2s',
          }}
            onMouseEnter={e => { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = '0 6px 16px rgba(0,0,0,0.15)' }}
            onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = '0px 2px 4px rgba(0,0,0,0.1)' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)', marginBottom: 4 }}>{card.label}</div>
                <div style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 28, color: card.borderColor }}>
                  {loading ? '...' : card.value}
                </div>
              </div>
              <span style={{ fontSize: 28 }}>{card.icon}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
