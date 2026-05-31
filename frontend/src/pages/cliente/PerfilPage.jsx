import { useState, useEffect } from 'react'
import { clienteService } from '../../services/api'
import Toast from '../../components/common/Toast'

const campo = (label, valor, onChange, type = 'text', placeholder = '') => (
  <div style={{ marginBottom: 16 }}>
    <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', display: 'block', marginBottom: 4 }}>{label}</label>
    <input
      type={type} value={valor} onChange={onChange} placeholder={placeholder}
      style={{
        width: '100%', height: 49, background: '#F2E2C4',
        border: '1px solid #A65A49', borderRadius: 4,
        padding: '0 16px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 20,
        color: '#2B3640', outline: 'none',
      }}
    />
  </div>
)

export default function PerfilPage() {
  const [perfil, setPerfil] = useState(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [toast, setToast] = useState(null)
  const [form, setForm] = useState({ nome: '', email: '', sexo: '', dataNascimento: '' })
  const [senhaForm, setSenhaForm] = useState({ novaSenha: '', confirmarSenha: '' })

  useEffect(() => {
    clienteService.perfil().then(({ data }) => {
      setPerfil(data)
      setForm({ nome: data.nome || '', email: data.email || '', sexo: data.sexo || '', dataNascimento: data.dataNascimento || '' })
    }).catch(console.error).finally(() => setLoading(false))
  }, [])

  const handleSalvar = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      const payload = { ...form }
      if (senhaForm.novaSenha) {
        if (senhaForm.novaSenha !== senhaForm.confirmarSenha) { setToast({ mensagem: 'Senhas não coincidem.', tipo: 'erro' }); setSaving(false); return }
        payload.senha = senhaForm.novaSenha
      }
      await clienteService.atualizar(payload)
      setToast({ mensagem: 'Perfil atualizado!', tipo: 'sucesso' })
      setSenhaForm({ novaSenha: '', confirmarSenha: '' })
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Erro ao salvar.', tipo: 'erro' })
    } finally { setSaving(false) }
  }

  if (loading) return <div style={{ padding: 40, fontFamily: 'Raleway', color: '#2B3640' }}>Carregando...</div>

  return (
    <div style={{ paddingTop: 32 }}>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      <h2 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 32, color: '#A65A49', marginBottom: 24 }}>
        Configurações de Perfil
      </h2>

      <form onSubmit={handleSalvar}>
        {/* Informações Pessoais */}
        <div style={{ background: '#D9B391', borderRadius: 15, padding: '28px 32px', marginBottom: 16 }}>
          <h3 style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 32, color: '#2B3640', marginBottom: 24 }}>
            Informações Pessoais
          </h3>
          {campo('Nome Completo', form.nome, e => setForm({ ...form, nome: e.target.value }), 'text', 'Nome completo')}
          {campo('E-mail', form.email, e => setForm({ ...form, email: e.target.value }), 'email', 'E-mail')}
          <div style={{ marginBottom: 16 }}>
            <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', display: 'block', marginBottom: 4 }}>Sexo</label>
            <select value={form.sexo} onChange={e => setForm({ ...form, sexo: e.target.value })} style={{
              width: '100%', height: 49, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4,
              padding: '0 16px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 20, color: '#2B3640', outline: 'none',
            }}>
              <option value="M">Masculino</option>
              <option value="F">Feminino</option>
              <option value="O">Outro</option>
            </select>
          </div>
          {campo('Data de Nascimento', form.dataNascimento, e => setForm({ ...form, dataNascimento: e.target.value }), 'date')}
        </div>

        {/* Alterar Senha */}
        <div style={{ background: '#D9B391', borderRadius: 15, padding: '28px 32px', marginBottom: 24 }}>
          <h3 style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 32, color: '#2B3640', marginBottom: 24 }}>
            Alterar Senha
          </h3>
          <div style={{ marginBottom: 16 }}>
            <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', display: 'block', marginBottom: 4 }}>Nova Senha</label>
            <input
              type="password" value={senhaForm.novaSenha}
              onChange={e => setSenhaForm({ ...senhaForm, novaSenha: e.target.value })}
              placeholder="Digite uma nova senha"
              style={{ width: '100%', height: 49, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4, padding: '0 16px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 20, color: 'rgba(43,54,64,0.5)', outline: 'none' }}
            />
          </div>
          <div>
            <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', display: 'block', marginBottom: 4 }}>Confirmar Senha</label>
            <input
              type="password" value={senhaForm.confirmarSenha}
              onChange={e => setSenhaForm({ ...senhaForm, confirmarSenha: e.target.value })}
              placeholder="Confirme a nova senha"
              style={{ width: '100%', height: 49, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4, padding: '0 16px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 20, color: 'rgba(43,54,64,0.5)', outline: 'none' }}
            />
          </div>
        </div>

        <button type="submit" disabled={saving} style={{
          background: '#A65A49', color: 'white', padding: '10px 32px',
          borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, fontSize: 18,
        }}>
          {saving ? 'Salvando...' : 'Salvar Alterações'}
        </button>
      </form>
    </div>
  )
}
