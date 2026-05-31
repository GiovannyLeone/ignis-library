import { formatarData } from '../../utils/dateUtils'
import { useState } from 'react'
import { estoqueService } from '../../services/api'
import Toast from '../../components/common/Toast'

export default function RetiradaPage() {
  const [codigo, setCodigo] = useState('')
  const [loading, setLoading] = useState(false)
  const [toast, setToast] = useState(null)
  const [resultado, setResultado] = useState(null)

  const handleSubmit = async (e) => {
    e.preventDefault()
    const cod = codigo.trim().toUpperCase()
    if (!cod) return
    setLoading(true); setResultado(null)
    try {
      const { data } = await estoqueService.retirada(cod)
      setResultado(data)
      setToast({ mensagem: 'Retirada registrada!', tipo: 'sucesso' })
      setCodigo('')
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Código inválido.', tipo: 'erro' })
    } finally { setLoading(false) }
  }

  return (
    <div>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}
      <h2 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 28, color: '#2B3640', marginBottom: 8 }}>Registrar Retirada</h2>
      <p style={{ fontFamily: 'Raleway', fontSize: 16, color: 'rgba(43,54,64,0.7)', marginBottom: 24 }}>
        Insira o código de retirada gerado pelo cliente.
      </p>
      <form onSubmit={handleSubmit}>
        <div style={{ background: '#D9B391', borderRadius: 15, padding: '28px 32px', marginBottom: 20 }}>
          <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 18, color: '#2B3640', display: 'block', marginBottom: 12 }}>
            Código de Retirada
          </label>
          <div style={{ display: 'flex', gap: 12 }}>
            <input
              value={codigo} onChange={e => setCodigo(e.target.value.toUpperCase())}
              placeholder="Ex: A1B2C3D4E5F6G7H8"
              maxLength={17} autoFocus
              style={{
                flex: 1, height: 52, background: '#F2E2C4', border: '2px solid #A65A49', borderRadius: 4,
                padding: '0 16px', fontFamily: 'monospace', fontSize: 18, letterSpacing: 2,
                color: '#2B3640', textTransform: 'uppercase', outline: 'none',
              }}
            />
            <button type="submit" disabled={loading || !codigo.trim()} style={{
              padding: '0 28px', background: '#A65A49', color: 'white', borderRadius: 4,
              fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, height: 52,
              opacity: loading || !codigo.trim() ? 0.6 : 1,
            }}>
              {loading ? 'Processando...' : '✓ Confirmar Retirada'}
            </button>
          </div>
          <p style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)', marginTop: 8 }}>
            O código possui 16 caracteres.
          </p>
        </div>
      </form>

      {resultado && (
        <div style={{ background: '#D9B391', borderRadius: 15, padding: '24px 32px', border: '2px solid #2D7A4F' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 16 }}>
            <span style={{ fontSize: 24 }}>✅</span>
            <h3 style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 20, color: '#2D7A4F' }}>Retirada Confirmada</h3>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
            {[
              ['Livro', resultado.livro?.titulo],
              ['Autor', resultado.livro?.autor],
              ['Cliente', resultado.cliente?.nome],
              ['Devolução Prevista', resultado.dataDevolucaoPrevista ? formatarData(resultado.dataDevolucaoPrevista) : '—'],
            ].map(([l, v]) => (
              <div key={l} style={{ background: '#F2E2C4', borderRadius: 4, padding: '10px 14px' }}>
                <div style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)', marginBottom: 2 }}>{l}</div>
                <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 15, color: '#2B3640' }}>{v || '—'}</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
