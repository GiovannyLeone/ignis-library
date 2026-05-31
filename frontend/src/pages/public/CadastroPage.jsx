import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { clienteService } from '../../services/api'
import { useAuth } from '../../context/AuthContext'
import { useEffect } from 'react'
import Toast from '../../components/common/Toast'

export default function CadastroPage() {
  const navigate = useNavigate()
  const { token, perfil } = useAuth()
  const [toast, setToast] = useState(null)

  useEffect(() => {
    if (token) {
      if (perfil === 'ADMINISTRADOR') navigate('/admin/dashboard')
      else if (perfil === 'ESTOQUISTA') navigate('/estoquista/retirada')
      else navigate('/cliente/historico')
    }
  }, [token])
  const [loading, setLoading] = useState(false)
  const [form, setForm] = useState({ nome: '', email: '', cpf: '', senha: '', confirmarSenha: '', dataNascimento: '', sexo: '' })
  const set = e => setForm({ ...form, [e.target.name]: e.target.value })

  const inputStyle = {
    width: '100%', height: 49, background: '#F2E2C4',
    border: '1px solid #A65A49', borderRadius: 4,
    padding: '0 16px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 18,
    color: '#2B3640', outline: 'none',
  }
  const label = text => (
    <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', display: 'block', marginBottom: 6 }}>{text}</label>
  )

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (form.senha !== form.confirmarSenha) { setToast({ mensagem: 'Senhas não coincidem.', tipo: 'erro' }); return }
    setLoading(true)
    try {
      await clienteService.cadastrar({ nome: form.nome, email: form.email, cpf: form.cpf.replace(/\D/g,''), senha: form.senha, dataNascimento: form.dataNascimento, sexo: form.sexo })
      setToast({ mensagem: 'Conta criada!', tipo: 'sucesso' })
      setTimeout(() => navigate('/login'), 1500)
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Erro ao criar conta.', tipo: 'erro' })
    } finally { setLoading(false) }
  }

  return (
    <div style={{ minHeight: '100vh', background: '#2B3640', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24 }}>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}
      <div style={{ background: '#D9B391', borderRadius: 15, padding: '48px', width: '100%', maxWidth: 580, boxShadow: '0 20px 60px rgba(0,0,0,0.3)' }}>
        <h1 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 28, color: '#A65A49', textAlign: 'center', marginBottom: 32 }}>
          Criar Conta — PapiroTech
        </h1>
        <form onSubmit={handleSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16, marginBottom: 16 }}>
            <div style={{ gridColumn: '1/-1' }}>
              {label('Nome Completo')}
              <input name="nome" value={form.nome} onChange={set} placeholder="Seu nome completo" style={inputStyle} required />
            </div>
            <div style={{ gridColumn: '1/-1' }}>
              {label('E-mail')}
              <input name="email" type="email" value={form.email} onChange={set} placeholder="seu@email.com" style={inputStyle} required />
            </div>
            <div>
              {label('CPF')}
              <input name="cpf" value={form.cpf} onChange={set} placeholder="00000000000" style={inputStyle} required maxLength={11} />
            </div>
            <div>
              {label('Data de Nascimento')}
              <input name="dataNascimento" type="date" value={form.dataNascimento} onChange={set} style={inputStyle} required />
            </div>
            <div style={{ gridColumn: '1/-1' }}>
              {label('Sexo')}
              <select name="sexo" value={form.sexo} onChange={set} style={inputStyle} required>
                <option value="">Selecione</option>
                <option value="M">Masculino</option>
                <option value="F">Feminino</option>
                <option value="O">Outro</option>
              </select>
            </div>
            <div>
              {label('Senha')}
              <input name="senha" type="password" value={form.senha} onChange={set} placeholder="Mínimo 6 caracteres" style={inputStyle} required minLength={6} />
            </div>
            <div>
              {label('Confirmar Senha')}
              <input name="confirmarSenha" type="password" value={form.confirmarSenha} onChange={set} placeholder="Repita a senha" style={inputStyle} required />
            </div>
          </div>
          <button type="submit" disabled={loading} style={{
            width: '100%', height: 49, background: '#A65A49', color: 'white',
            borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 18, marginBottom: 16,
          }}>
            {loading ? 'Criando conta...' : 'Criar Conta'}
          </button>
        </form>
        <p style={{ fontFamily: 'Raleway', fontSize: 15, color: '#2B3640', textAlign: 'center' }}>
          Já tem conta? <Link to="/login" style={{ color: '#A65A49', fontWeight: 700 }}>Entrar</Link>
        </p>
      </div>
    </div>
  )
}
