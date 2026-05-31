import { formatarData } from '../../utils/dateUtils'
import { useState, useEffect } from 'react'
import { emprestimoService } from '../../services/api'
import Toast from '../../components/common/Toast'

const STATUS_LABEL = {
  RESERVADO: { label: 'Reservado', color: '#1A5276', bg: '#EBF5FB' },
  ATIVO: { label: 'Ativo', color: '#1E8449', bg: '#EAFAF1' },
  ATRASADO: { label: 'Atrasado', color: '#922B21', bg: '#FDEDEC' },
  EM_PROCESSO_DE_DEVOLUCAO: { label: 'Em Devolução', color: '#9A7D0A', bg: '#FEF9E7' },
  EM_PROCESSO_DE_DEVOLUCAO_ATRASADO: { label: 'Dev. Atrasada', color: '#9A7D0A', bg: '#FEF9E7' },
  DEVOLVIDO: { label: 'Devolvido', color: '#566573', bg: '#F2F3F4' },
  DEVOLVIDO_COM_ATRASO: { label: 'Dev. c/ Atraso', color: '#7B241C', bg: '#FDEDEC' },
  CANCELADO: { label: 'Cancelado', color: '#7F8C8D', bg: '#F2F3F4' },
}

export default function HistoricoPage() {
  const [emprestimos, setEmprestimos] = useState([])
  const [loading, setLoading] = useState(true)
  const [pagina, setPagina] = useState(0)
  const [total, setTotal] = useState(1)
  const [toast, setToast] = useState(null)
  const [gerandoDev, setGerandoDev] = useState(null)
  const [codigos, setCodigos] = useState({})

  const carregar = async (p = 0) => {
    setLoading(true)
    try {
      const { data } = await emprestimoService.meuHistorico(p)
      setEmprestimos(data.conteudo || [])
      setTotal(data.totalPaginas || 1)
      setPagina(p)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  useEffect(() => { carregar(0) }, [])

  const gerarDevolucao = async (emp) => {
    setGerandoDev(emp.id)
    try {
      const { data } = await emprestimoService.gerarCodigoDevolucao(emp.id)
      setCodigos(prev => ({ ...prev, [emp.id]: data.codigoDevolucao }))
      setToast({ mensagem: 'Código de devolução gerado!', tipo: 'sucesso' })
      carregar(pagina)
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Erro ao gerar código.', tipo: 'erro' })
    } finally { setGerandoDev(null) }
  }

  if (loading) return <div style={{ padding: 40, fontFamily: 'Raleway', color: '#2B3640' }}>Carregando...</div>

  return (
    <div style={{ paddingTop: 32 }}>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      <h2 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 32, color: '#A65A49', marginBottom: 24 }}>
        Meus Aluguéis
      </h2>

      {emprestimos.length === 0 ? (
        <div style={{ background: '#D9B391', borderRadius: 15, padding: '60px 32px', textAlign: 'center' }}>
          <div style={{ fontSize: 48, marginBottom: 16 }}>📚</div>
          <p style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 20, color: '#2B3640' }}>Nenhum aluguel encontrado</p>
          <p style={{ fontFamily: 'Raleway', fontSize: 16, color: 'rgba(43,54,64,0.7)', marginTop: 8 }}>Explore o catálogo e alugue seu primeiro livro!</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {emprestimos.map(emp => {
            const st = STATUS_LABEL[emp.status] || STATUS_LABEL.CANCELADO
            const codDev = codigos[emp.id] || emp.codigoDevolucao
            const podeGerarDev = emp.status === 'ATIVO' || emp.status === 'ATRASADO'
            return (
              <div key={emp.id} style={{ background: '#D9B391', borderRadius: 15, padding: '24px 28px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
                  <div>
                    <p style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 20, color: '#2B3640', marginBottom: 4 }}>
                      {emp.livro?.titulo}
                    </p>
                    <p style={{ fontFamily: 'Raleway', fontWeight: 400, fontSize: 15, color: 'rgba(43,54,64,0.7)', marginBottom: 12 }}>
                      {emp.livro?.autor}
                    </p>
                    <div style={{ display: 'flex', gap: 24, fontSize: 14, color: 'rgba(43,54,64,0.8)', fontFamily: 'Raleway', flexWrap: 'wrap' }}>
                      {emp.dataEmprestimo && <span>📅 Retirada: {formatarData(emp.dataEmprestimo)}</span>}
                      {emp.dataDevolucaoPrevista && <span>📅 Devolução: {formatarData(emp.dataDevolucaoPrevista)}</span>}
                      {emp.dataDevolucaoReal && <span>✓ Devolvido: {formatarData(emp.dataDevolucaoReal)}</span>}
                    </div>
                  </div>
                  <span style={{ background: st.bg, color: st.color, padding: '4px 14px', borderRadius: 20, fontFamily: 'Raleway', fontWeight: 700, fontSize: 14 }}>
                    {st.label}
                  </span>
                </div>

                {/* Códigos */}
                <div style={{ marginTop: 16, paddingTop: 16, borderTop: '1px solid rgba(166,90,73,0.3)', display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'center' }}>
                  {emp.codigoRetirada && (
                    <div style={{ background: '#F2E2C4', borderRadius: 4, padding: '8px 16px' }}>
                      <span style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)' }}>Código Retirada: </span>
                      <span style={{ fontFamily: 'monospace', fontWeight: 700, fontSize: 15, color: '#2B3640', letterSpacing: 2 }}>{emp.codigoRetirada}</span>
                    </div>
                  )}
                  {codDev && (
                    <div style={{ background: '#F2E2C4', borderRadius: 4, padding: '8px 16px' }}>
                      <span style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)' }}>Código Devolução: </span>
                      <span style={{ fontFamily: 'monospace', fontWeight: 700, fontSize: 15, color: '#A65A49', letterSpacing: 2 }}>{codDev}</span>
                    </div>
                  )}
                  {podeGerarDev && !codDev && (
                    <button
                      onClick={() => gerarDevolucao(emp)}
                      disabled={gerandoDev === emp.id}
                      style={{ background: '#A65A49', color: 'white', padding: '8px 20px', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, fontSize: 15 }}
                    >
                      {gerandoDev === emp.id ? 'Gerando...' : '🔑 Gerar Código de Devolução'}
                    </button>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}

      {total > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 24 }}>
          <button onClick={() => carregar(pagina - 1)} disabled={pagina === 0} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina === 0 ? 0.5 : 1 }}>←</button>
          <span style={{ padding: '8px 16px', fontFamily: 'Raleway', color: '#2B3640' }}>{pagina + 1} / {total}</span>
          <button onClick={() => carregar(pagina + 1)} disabled={pagina >= total - 1} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina >= total - 1 ? 0.5 : 1 }}>→</button>
        </div>
      )}
    </div>
  )
}
