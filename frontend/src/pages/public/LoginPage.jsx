import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { authService } from '../../services/api'
import Toast from '../../components/common/Toast'

export default function LoginPage() {
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [loading, setLoading] = useState(false)
  const [toast, setToast] = useState(null)
  const { login: doLogin } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const { data } = await authService.login(login, senha)
      doLogin(data.token, data.perfil, data.nome)
      if (data.perfil === 'ADMINISTRADOR') navigate('/admin/dashboard')
      else if (data.perfil === 'ESTOQUISTA') navigate('/estoquista/retirada')
      else navigate('/cliente/historico')
    } catch (err) {
      const status = err.response?.status
      const detail = err.response?.data?.detail || ''
      if (status === 403 || detail.toLowerCase().includes('bloqueada')) {
        setToast({ mensagem: '⛔ Conta bloqueada. Entre em contato com a biblioteca.', tipo: 'aviso' })
      } else {
        setToast({ mensagem: 'Credenciais inválidas.', tipo: 'erro' })
      }
    } finally { setLoading(false) }
  }

  const inputStyle = {
    width: '100%', height: 49,
    background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4,
    padding: '0 16px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 20,
    color: '#2B3640', outline: 'none',
  }
  const labelStyle = { fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', display: 'block', marginBottom: 6 }

  return (
    <div style={{ minHeight: '100vh', background: '#2B3640', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24 }}>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}
      <div style={{ background: '#D9B391', borderRadius: 15, padding: '48px 48px', width: '100%', maxWidth: 520, boxShadow: '0 20px 60px rgba(0,0,0,0.3)' }}>
        <h1 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 32, color: '#A65A49', textAlign: 'center', marginBottom: 8 }}>
          PapiroTech
        </h1>
        <p style={{ fontFamily: 'Raleway', fontWeight: 400, fontSize: 16, color: '#2B3640', textAlign: 'center', marginBottom: 36 }}>
          Bem-vindo à Biblioteca IGNIS
        </p>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 20 }}>
            <label style={labelStyle}>E-mail ou código de acesso</label>
            <input value={login} onChange={e => setLogin(e.target.value)} placeholder="Digite seu e-mail" style={inputStyle} autoFocus required />
          </div>
          <div style={{ marginBottom: 28 }}>
            <label style={labelStyle}>Senha</label>
            <input type="password" value={senha} onChange={e => setSenha(e.target.value)} placeholder="Digite sua senha" style={inputStyle} required />
          </div>
          <button type="submit" disabled={loading} style={{
            width: '100%', height: 49, background: '#A65A49', color: 'white',
            borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 18,
            marginBottom: 20,
          }}>
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <p style={{ fontFamily: 'Raleway', fontWeight: 400, fontSize: 15, color: '#2B3640', textAlign: 'center' }}>
          Não tem conta?{' '}
          <Link to="/cadastro" style={{ color: '#A65A49', fontWeight: 700 }}>Cadastre-se</Link>
        </p>

      </div>
    </div>
  )
}
