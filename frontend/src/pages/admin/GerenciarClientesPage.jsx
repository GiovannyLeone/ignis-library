import { useState, useEffect } from 'react'
import { adminService } from '../../services/api'
import Toast from '../../components/common/Toast'
import { formatarData } from '../../utils/dateUtils'

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

export default function GerenciarClientesPage() {
  const [clientes, setClientes] = useState([])
  const [loading, setLoading] = useState(true)
  const [pagina, setPagina] = useState(0)
  const [total, setTotal] = useState(1)
  const [toast, setToast] = useState(null)

  // Modal de empréstimos do cliente
  const [clienteSelecionado, setClienteSelecionado] = useState(null)
  const [emprestimos, setEmprestimos] = useState([])
  const [loadingEmp, setLoadingEmp] = useState(false)
  const [loadingAcao, setLoadingAcao] = useState(null)

  const carregar = async (p = 0) => {
    setLoading(true)
    try {
      const { data } = await adminService.listarUsuarios(p, 10)
      setClientes(data.conteudo || [])
      setTotal(data.totalPaginas || 1)
      setPagina(p)
    } catch { console.error() }
    finally { setLoading(false) }
  }

  useEffect(() => { carregar(0) }, [])

  const abrirEmprestimos = async (cli) => {
    setClienteSelecionado(cli)
    setLoadingEmp(true)
    try {
      const { data } = await adminService.emprestimosDoCliente(cli.id, 0, 20)
      setEmprestimos(data.conteudo || [])
    } catch { setEmprestimos([]) }
    finally { setLoadingEmp(false) }
  }

  const aplicarPenalidade = async (emp) => {
    if (!confirm(`Penalizar ${clienteSelecionado?.nome}?`)) return
    setLoadingAcao(emp.id)
    try {
      await adminService.aplicarPenalidade(emp.id)
      setToast({ mensagem: 'Penalidade aplicada! Cliente bloqueado.', tipo: 'sucesso' })
      await abrirEmprestimos(clienteSelecionado)
      carregar(pagina)
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Erro ao aplicar penalidade.', tipo: 'erro' })
    } finally { setLoadingAcao(null) }
  }

  const removerPenalidade = async (emp) => {
    if (!confirm(`Reativar ${clienteSelecionado?.nome}?`)) return
    setLoadingAcao(emp.id)
    try {
      await adminService.removerPenalidade(emp.id)
      setToast({ mensagem: 'Penalidade removida! Cliente reativado.', tipo: 'sucesso' })
      await abrirEmprestimos(clienteSelecionado)
      carregar(pagina)
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Erro ao remover penalidade.', tipo: 'erro' })
    } finally { setLoadingAcao(null) }
  }

  return (
    <div>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      {/* Filtros decorativos */}
      <div style={{ background: '#D9B391', borderRadius: 15, padding: '20px 24px', marginBottom: 20 }}>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div>
            <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Nome</label>
            <input placeholder="Pesquisar nome..." style={{ height: 36, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4, padding: '0 12px', fontFamily: 'Raleway', fontSize: 14, outline: 'none', width: 180 }} />
          </div>
          <div>
            <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Status</label>
            <select style={{ height: 36, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4, padding: '0 12px', fontFamily: 'Raleway', fontSize: 14, outline: 'none', width: 140 }}>
              <option>Todos</option>
              <option>ATIVO</option>
              <option>BLOQUEADO</option>
            </select>
          </div>
          <button style={{ background: '#A65A49', color: 'white', padding: '8px 20px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 14, height: 36 }}>
            Limpar Filtros
          </button>
        </div>
      </div>

      {/* Lista de clientes */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {loading ? (
          <div style={{ textAlign: 'center', padding: '40px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Carregando...</div>
        ) : clientes.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '40px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Nenhum usuário encontrado</div>
        ) : clientes.map(cli => (
          <div key={cli.id} style={{
            background: 'white', borderRadius: 10, padding: '16px 20px',
            border: `1px solid ${cli.status === 'BLOQUEADO' ? '#FDEDEC' : '#D9B391'}`,
            borderLeft: `4px solid ${cli.status === 'BLOQUEADO' ? '#C0392B' : '#2D7A4F'}`,
            display: 'flex', alignItems: 'center', gap: 16, flexWrap: 'wrap',
          }}>
            {/* Avatar */}
            <div style={{
              width: 40, height: 40, borderRadius: '50%',
              background: cli.status === 'BLOQUEADO' ? '#C0392B' : '#2B3640',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: 'white', fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, flexShrink: 0,
            }}>
              {cli.nome?.[0]?.toUpperCase()}
            </div>

            {/* Info */}
            <div style={{ flex: 1, minWidth: 180 }}>
              <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640' }}>{cli.nome}</div>
              <div style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)' }}>{cli.email}</div>
            </div>

            {/* Status + perfil + ações */}
            <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
              <span style={{
                background: cli.status === 'ATIVO' ? '#EAFAF1' : '#FDEDEC',
                color: cli.status === 'ATIVO' ? '#1E8449' : '#922B21',
                padding: '4px 12px', borderRadius: 20,
                fontFamily: 'Raleway', fontWeight: 700, fontSize: 13,
              }}>
                ● {cli.status || 'ATIVO'}
              </span>
              <span style={{ background: '#F2E2C4', color: '#2B3640', padding: '4px 12px', borderRadius: 20, fontFamily: 'Raleway', fontWeight: 600, fontSize: 13 }}>
                {cli.perfil}
              </span>
              {/* Botão Ver empréstimos — acesso à penalidade */}
              <button
                onClick={() => abrirEmprestimos(cli)}
                style={{ background: '#2B3640', color: 'white', padding: '6px 14px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}
              >
                📋 Ver Empréstimos
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Paginação */}
      {total > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 20 }}>
          <button onClick={() => carregar(pagina - 1)} disabled={pagina === 0} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina === 0 ? 0.5 : 1 }}>←</button>
          <span style={{ padding: '8px 16px', fontFamily: 'Raleway', color: '#2B3640' }}>{pagina + 1} / {total}</span>
          <button onClick={() => carregar(pagina + 1)} disabled={pagina >= total - 1} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina >= total - 1 ? 0.5 : 1 }}>→</button>
        </div>
      )}

      {/* Modal de empréstimos do cliente */}
      {clienteSelecionado && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1000, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16 }}>
          <div style={{ background: '#F2E2C4', borderRadius: 15, padding: '32px', width: '100%', maxWidth: 700, maxHeight: '85vh', overflowY: 'auto' }}>

            {/* Header do modal */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
              <div>
                <h3 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 22, color: '#A65A49', marginBottom: 4 }}>
                  Empréstimos — {clienteSelecionado.nome}
                </h3>
                <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                  <span style={{
                    background: clienteSelecionado.status === 'ATIVO' ? '#EAFAF1' : '#FDEDEC',
                    color: clienteSelecionado.status === 'ATIVO' ? '#1E8449' : '#922B21',
                    padding: '3px 10px', borderRadius: 20,
                    fontFamily: 'Raleway', fontWeight: 700, fontSize: 13,
                  }}>
                    ● {clienteSelecionado.status || 'ATIVO'}
                  </span>
                  <span style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)' }}>
                    {clienteSelecionado.email}
                  </span>
                </div>
                {clienteSelecionado.status === 'BLOQUEADO' && (
                  <div style={{ marginTop: 8, background: '#FDEDEC', border: '1px solid #C0392B', borderRadius: 6, padding: '8px 12px', fontFamily: 'Raleway', fontSize: 13, color: '#922B21' }}>
                    ⚠️ Este cliente está bloqueado. Remova a penalidade em um empréstimo para reativá-lo.
                  </div>
                )}
              </div>
              <button onClick={() => setClienteSelecionado(null)} style={{ background: 'none', fontSize: 24, color: '#A65A49', fontWeight: 700, padding: '0 8px' }}>×</button>
            </div>

            {/* Empréstimos */}
            {loadingEmp ? (
              <div style={{ textAlign: 'center', padding: '32px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Carregando empréstimos...</div>
            ) : emprestimos.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '32px', background: '#D9B391', borderRadius: 10, fontFamily: 'Raleway', color: '#2B3640' }}>
                📚 Nenhum empréstimo encontrado para este cliente.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {emprestimos.map(emp => {
                  const st = STATUS_STYLE[emp.status] || STATUS_STYLE.CANCELADO
                  const podeAplicar = (emp.status === 'ATRASADO' || emp.status === 'DEVOLVIDO_COM_ATRASO') && !emp.penalidadeGerada
                  const podeRemover = emp.penalidadeGerada
                  return (
                    <div key={emp.id} style={{ background: 'white', borderRadius: 10, padding: '14px 18px', border: '1px solid #D9B391' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 8 }}>
                        <div>
                          <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 15, color: '#2B3640', marginBottom: 2 }}>
                            {emp.livro?.titulo}
                          </div>
                          <div style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(43,54,64,0.6)', display: 'flex', gap: 16 }}>
                            {emp.dataEmprestimo && <span>📅 Retirada: {formatarData(emp.dataEmprestimo)}</span>}
                            {emp.dataDevolucaoPrevista && <span>📅 Previsto: {formatarData(emp.dataDevolucaoPrevista)}</span>}
                            {emp.dataDevolucaoReal && <span>✓ Devolvido: {formatarData(emp.dataDevolucaoReal)}</span>}
                          </div>
                          {emp.penalidadeGerada && (
                            <div style={{ marginTop: 4, fontFamily: 'Raleway', fontSize: 12, color: '#922B21', fontWeight: 700 }}>
                              ⚠️ Penalidade aplicada neste empréstimo
                            </div>
                          )}
                        </div>
                        <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
                          <span style={{ background: st.bg, color: st.color, padding: '3px 12px', borderRadius: 20, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}>
                            {st.label}
                          </span>
                          {podeAplicar && (
                            <button
                              onClick={() => aplicarPenalidade(emp)}
                              disabled={loadingAcao === emp.id}
                              style={{ background: '#C0392B', color: 'white', padding: '6px 14px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}
                            >
                              {loadingAcao === emp.id ? '...' : '🚫 Penalizar'}
                            </button>
                          )}
                          {podeRemover && (
                            <button
                              onClick={() => removerPenalidade(emp)}
                              disabled={loadingAcao === emp.id}
                              style={{ background: '#2D7A4F', color: 'white', padding: '6px 14px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}
                            >
                              {loadingAcao === emp.id ? '...' : '✅ Reativar'}
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  )
                })}
              </div>
            )}

            <div style={{ marginTop: 20, textAlign: 'right' }}>
              <button onClick={() => setClienteSelecionado(null)} style={{ background: '#2B3640', color: 'white', padding: '10px 28px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 15 }}>
                Fechar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
