import { formatarData } from '../../utils/dateUtils'
import { useState, useEffect } from 'react'
import { adminService } from '../../services/api'
import Toast from '../../components/common/Toast'

const STATUS_STYLE = {
  RESERVADO: { label: 'Reservado', color: '#1A5276', bg: '#EBF5FB' },
  ATIVO: { label: 'Ativo', color: '#1E8449', bg: '#EAFAF1' },
  ATRASADO: { label: 'Atrasado', color: '#922B21', bg: '#FDEDEC' },
  EM_PROCESSO_DE_DEVOLUCAO: { label: 'Em Devolução', color: '#9A7D0A', bg: '#FEF9E7' },
  EM_PROCESSO_DE_DEVOLUCAO_ATRASADO: { label: 'Dev. Atrasada', color: '#9A7D0A', bg: '#FEF9E7' },
  DEVOLVIDO: { label: 'Devolvido', color: '#566573', bg: '#F2F3F4' },
  DEVOLVIDO_COM_ATRASO: { label: 'Dev. c/ Atraso', color: '#7B241C', bg: '#FDEDEC' },
  CANCELADO: { label: 'Cancelado', color: '#7F8C8D', bg: '#F2F3F4' },
}

export default function GerenciarEmprestimosPage() {
  const [emprestimos, setEmprestimos] = useState([])
  const [loading, setLoading] = useState(true)
  const [pagina, setPagina] = useState(0)
  const [total, setTotal] = useState(1)
  const [toast, setToast] = useState(null)
  const [loadingAcao, setLoadingAcao] = useState(null)

  const carregar = async (p = 0) => {
    setLoading(true)
    try { const { data } = await adminService.listarEmprestimos(p, 10); setEmprestimos(data.conteudo || []); setTotal(data.totalPaginas || 1); setPagina(p) }
    catch { console.error() } finally { setLoading(false) }
  }

  useEffect(() => { carregar(0) }, [])

  const aplicar = async (emp) => {
    if (!confirm(`Penalizar ${emp.cliente?.nome}?`)) return
    setLoadingAcao(emp.id)
    try { await adminService.aplicarPenalidade(emp.id); setToast({ mensagem: 'Penalidade aplicada!', tipo: 'sucesso' }); carregar(pagina) }
    catch (err) { setToast({ mensagem: err.response?.data?.detail || 'Erro.', tipo: 'erro' }) }
    finally { setLoadingAcao(null) }
  }

  const remover = async (emp) => {
    if (!confirm(`Remover penalidade de ${emp.cliente?.nome}?`)) return
    setLoadingAcao(emp.id)
    try { await adminService.removerPenalidade(emp.id); setToast({ mensagem: 'Penalidade removida!', tipo: 'sucesso' }); carregar(pagina) }
    catch (err) { setToast({ mensagem: err.response?.data?.detail || 'Erro.', tipo: 'erro' }) }
    finally { setLoadingAcao(null) }
  }

  return (
    <div>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      <div style={{ background: '#D9B391', borderRadius: 15, padding: '20px 24px', marginBottom: 20 }}>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          {['Título do Livro', 'Nome do Cliente', 'Título do Livro', 'Selecionar Status', 'Data Início', 'Data Fim'].map((p, i) => (
            <div key={i}>
              <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>{p}</label>
              <input placeholder={p} style={{ height: 36, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4, padding: '0 12px', fontFamily: 'Raleway', fontSize: 13, outline: 'none', width: 140 }} />
            </div>
          ))}
          <button style={{ background: '#A65A49', color: 'white', padding: '8px 20px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 14, alignSelf: 'flex-end', height: 36 }}>
            Limpar Filtros
          </button>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Carregando...</div>
        ) : emprestimos.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Nenhum empréstimo encontrado</div>
        ) : emprestimos.map(emp => {
          const st = STATUS_STYLE[emp.status] || STATUS_STYLE.CANCELADO
          const podeAplicar = (emp.status === 'ATRASADO' || emp.status === 'DEVOLVIDO_COM_ATRASO') && !emp.penalidadeGerada
          const podeRemover = emp.penalidadeGerada
          return (
            <div key={emp.id} style={{ background: 'white', borderRadius: 10, padding: '16px 20px', border: '1px solid #D9B391', display: 'flex', alignItems: 'center', gap: 16, flexWrap: 'wrap' }}>
              <div style={{ flex: 1, minWidth: 200 }}>
                <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 15, color: '#2B3640', marginBottom: 2 }}>{emp.livro?.titulo}</div>
                <div style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)' }}>
                  {emp.cliente?.nome} · {emp.dataEmprestimo ? formatarData(emp.dataEmprestimo) : '—'}
                </div>
              </div>
              <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontFamily: 'Raleway', fontSize: 11, color: 'rgba(43,54,64,0.5)', marginBottom: 2 }}>Empréstimos</div>
                  <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 18, color: '#2B3640' }}>
                    {emp.livro?.quantidadeTotal || '—'}
                  </div>
                </div>
                <div style={{ textAlign: 'center' }}>
                  <div style={{ fontFamily: 'Raleway', fontSize: 11, color: 'rgba(43,54,64,0.5)', marginBottom: 2 }}>Data</div>
                  <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 18, color: '#2B3640' }}>
                    {emp.dataDevolucaoPrevista ? formatarData(emp.dataDevolucaoPrevista) : '—'}
                  </div>
                </div>
                <span style={{ background: st.bg, color: st.color, padding: '4px 14px', borderRadius: 20, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}>
                  {st.label}
                </span>
                {podeAplicar && (
                  <button onClick={() => aplicar(emp)} disabled={loadingAcao === emp.id}
                    style={{ background: '#C0392B', color: 'white', padding: '6px 16px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}>
                    {loadingAcao === emp.id ? '...' : 'Penalizar'}
                  </button>
                )}
                {podeRemover && (
                  <button onClick={() => remover(emp)} disabled={loadingAcao === emp.id}
                    style={{ background: '#2D7A4F', color: 'white', padding: '6px 16px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}>
                    {loadingAcao === emp.id ? '...' : 'Reativar'}
                  </button>
                )}
              </div>
            </div>
          )
        })}
      </div>

      {total > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 20 }}>
          <button onClick={() => carregar(pagina - 1)} disabled={pagina === 0} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina === 0 ? 0.5 : 1 }}>←</button>
          <span style={{ padding: '8px 16px', fontFamily: 'Raleway', color: '#2B3640' }}>{pagina + 1} / {total}</span>
          <button onClick={() => carregar(pagina + 1)} disabled={pagina >= total - 1} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina >= total - 1 ? 0.5 : 1 }}>→</button>
        </div>
      )}
    </div>
  )
}
