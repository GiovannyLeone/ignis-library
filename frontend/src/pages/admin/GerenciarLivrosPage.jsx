import { useState, useEffect } from 'react'
import { livroService, categoriaService } from '../../services/api'
import Toast from '../../components/common/Toast'

const EMPTY = { isbn: '', titulo: '', autor: '', categoriaId: '1', editora: '', sinopse: '', anoPublicacao: '', quantidadeTotal: '' }
const inputStyle = { width: '100%', height: 40, background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4, padding: '0 12px', fontFamily: 'Raleway', fontWeight: 600, fontSize: 15, color: '#2B3640', outline: 'none' }

export default function GerenciarLivrosPage() {
  const [livros, setLivros] = useState([])
  const [loading, setLoading] = useState(true)
  const [pagina, setPagina] = useState(0)
  const [total, setTotal] = useState(1)
  const [busca, setBusca] = useState('')
  const [toast, setToast] = useState(null)
  const [modal, setModal] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)
  const [categorias, setCategorias] = useState([])

  const carregar = async (p = 0, termo = busca) => {
    setLoading(true)
    try {
      const res = termo ? await livroService.buscar(termo, p, 10) : await livroService.listar(p, 10)
      setLivros(res.data.conteudo || [])
      setTotal(res.data.totalPaginas || 1)
      setPagina(p)
    } catch { console.error() }
    finally { setLoading(false) }
  }

  useEffect(() => {
    carregar(0)
    categoriaService.listar().then(r => setCategorias(r.data || [])).catch(console.error)
  }, [])

  const handleSalvar = async (e) => {
    e.preventDefault(); setSaving(true)
    try {
      const p = { ...form, categoriaId: parseInt(form.categoriaId) || 1, anoPublicacao: parseInt(form.anoPublicacao), quantidadeTotal: parseInt(form.quantidadeTotal) }
      if (modal === 'novo') { await livroService.cadastrar(p); setToast({ mensagem: 'Livro cadastrado!', tipo: 'sucesso' }) }
      else { await livroService.atualizar(modal.idLivro, p); setToast({ mensagem: 'Livro atualizado!', tipo: 'sucesso' }) }
      setModal(null); carregar(pagina)
    } catch (err) { setToast({ mensagem: err.response?.data?.detail || 'Erro ao salvar.', tipo: 'erro' }) }
    finally { setSaving(false) }
  }

  const handleRemover = async (livro) => {
    if (!confirm(`Remover "${livro.titulo}"?`)) return
    try { await livroService.remover(livro.idLivro); setToast({ mensagem: 'Livro removido.', tipo: 'sucesso' }); carregar(pagina) }
    catch (err) { setToast({ mensagem: err.response?.data?.detail || 'Erro ao remover.', tipo: 'erro' }) }
  }

  return (
    <div>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      {/* Filtros + botão */}
      <div style={{ background: '#D9B391', borderRadius: 15, padding: '20px 24px', marginBottom: 20, display: 'flex', gap: 12, alignItems: 'flex-end', flexWrap: 'wrap' }}>
        <div style={{ flex: 1, minWidth: 200 }}>
          <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 14, color: '#2B3640', display: 'block', marginBottom: 4 }}>Pesquisa</label>
          <input value={busca} onChange={e => setBusca(e.target.value)} onKeyDown={e => e.key === 'Enter' && carregar(0, busca)}
            placeholder="Título, autor, ISBN..." style={inputStyle} />
        </div>
        <button onClick={() => carregar(0, busca)} style={{ background: '#2B3640', color: 'white', padding: '8px 20px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 15, height: 40 }}>
          Filtrar
        </button>
        {busca && <button onClick={() => { setBusca(''); carregar(0, '') }} style={{ background: '#A65A49', color: 'white', padding: '8px 16px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 15, height: 40 }}>
          Limpar Filtros
        </button>}
      </div>

      {/* Botão adicionar */}
      <div style={{ marginBottom: 16 }}>
        <button onClick={() => { setForm(EMPTY); setModal('novo') }} style={{
          background: '#A65A49', color: 'white', padding: '10px 24px', borderRadius: 4,
          fontFamily: 'Raleway', fontWeight: 700, fontSize: 16,
          display: 'flex', alignItems: 'center', gap: 8,
        }}>
          + Adicionar Livro
        </button>
      </div>

      {/* Tabela */}
      <div style={{ background: 'white', borderRadius: 10, overflow: 'hidden', border: '1px solid #D9B391' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
          <thead>
            <tr style={{ background: '#F2E2C4', borderBottom: '2px solid #D9B391' }}>
              {['Título', 'Autor', 'Editora', 'Ano', 'Disponível', 'Ações'].map(h => (
                <th key={h} style={{ padding: '12px 16px', textAlign: 'left', fontFamily: 'Raleway', fontWeight: 700, color: '#2B3640' }}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6} style={{ textAlign: 'center', padding: '40px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Carregando...</td></tr>
            ) : livros.length === 0 ? (
              <tr><td colSpan={6} style={{ textAlign: 'center', padding: '40px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.5)' }}>Nenhum livro encontrado</td></tr>
            ) : livros.map((livro, i) => (
              <tr key={livro.idLivro} style={{ borderBottom: '1px solid #F2E2C4', background: i % 2 === 0 ? 'white' : '#FAFAFA' }}>
                <td style={{ padding: '12px 16px', fontFamily: 'Raleway', fontWeight: 600, color: '#2B3640', maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{livro.titulo}</td>
                <td style={{ padding: '12px 16px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.7)' }}>{livro.autor}</td>
                <td style={{ padding: '12px 16px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.7)' }}>{livro.editora}</td>
                <td style={{ padding: '12px 16px', fontFamily: 'Raleway', color: 'rgba(43,54,64,0.7)' }}>{livro.anoPublicacao}</td>
                <td style={{ padding: '12px 16px' }}>
                  <span style={{ fontFamily: 'Raleway', fontWeight: 700, color: livro.disponivel ? '#2D7A4F' : '#C0392B' }}>
                    {livro.quantidadeDisponivel}/{livro.quantidadeTotal}
                  </span>
                </td>
                <td style={{ padding: '12px 16px' }}>
                  <div style={{ display: 'flex', gap: 8 }}>
                    <button onClick={() => { setForm({ isbn: livro.isbn, titulo: livro.titulo, autor: livro.autor, categoriaId: livro.categoria?.id || '1', editora: livro.editora, sinopse: livro.sinopse || '', anoPublicacao: livro.anoPublicacao, quantidadeTotal: livro.quantidadeTotal }); setModal(livro) }}
                      style={{ background: '#2B3640', color: 'white', padding: '5px 14px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}>
                      Editar
                    </button>
                    <button onClick={() => handleRemover(livro)}
                      style={{ background: '#C0392B', color: 'white', padding: '5px 14px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 13 }}>
                      Remover
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {total > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 16 }}>
          <button onClick={() => carregar(pagina - 1)} disabled={pagina === 0} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina === 0 ? 0.5 : 1 }}>←</button>
          <span style={{ padding: '8px 16px', fontFamily: 'Raleway', color: '#2B3640' }}>{pagina + 1} / {total}</span>
          <button onClick={() => carregar(pagina + 1)} disabled={pagina >= total - 1} style={{ padding: '8px 20px', background: '#A65A49', color: 'white', borderRadius: 10, fontFamily: 'Raleway', fontWeight: 700, opacity: pagina >= total - 1 ? 0.5 : 1 }}>→</button>
        </div>
      )}

      {/* Modal */}
      {modal && (
        <div style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', zIndex: 1000, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16 }}>
          <div style={{ background: '#F2E2C4', borderRadius: 15, padding: '32px', width: '100%', maxWidth: 540, maxHeight: '90vh', overflowY: 'auto' }}>
            <h3 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 22, color: '#A65A49', marginBottom: 20 }}>
              {modal === 'novo' ? 'Adicionar Livro' : `Editar: ${modal.titulo}`}
            </h3>
            <form onSubmit={handleSalvar}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                {/* Título */}
                <div style={{ gridColumn: '1/-1' }}>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Título</label>
                  <input value={form.titulo} onChange={e => setForm({ ...form, titulo: e.target.value })} style={inputStyle} required />
                </div>
                {/* Autor */}
                <div style={{ gridColumn: '1/-1' }}>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Autor</label>
                  <input value={form.autor} onChange={e => setForm({ ...form, autor: e.target.value })} style={inputStyle} required />
                </div>
                {/* ISBN */}
                <div>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>ISBN</label>
                  <input value={form.isbn} onChange={e => setForm({ ...form, isbn: e.target.value })} style={inputStyle} required />
                </div>
                {/* Categoria — SELECT */}
                <div>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Categoria</label>
                  <select
                    value={form.categoriaId}
                    onChange={e => setForm({ ...form, categoriaId: e.target.value })}
                    style={{ ...inputStyle, width: '100%' }}
                    required
                  >
                    <option value="">Selecione...</option>
                    {categorias.map(cat => (
                      <option key={cat.id} value={cat.id}>{cat.descricao}</option>
                    ))}
                  </select>
                </div>
                {/* Editora */}
                <div>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Editora</label>
                  <input value={form.editora} onChange={e => setForm({ ...form, editora: e.target.value })} style={inputStyle} required />
                </div>
                {/* Ano */}
                <div>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Ano</label>
                  <input type="number" value={form.anoPublicacao} onChange={e => setForm({ ...form, anoPublicacao: e.target.value })} style={inputStyle} required />
                </div>
                {/* Quantidade */}
                <div>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Quantidade</label>
                  <input type="number" value={form.quantidadeTotal} onChange={e => setForm({ ...form, quantidadeTotal: e.target.value })} style={inputStyle} required />
                </div>
                {/* Sinopse */}
                <div style={{ gridColumn: '1/-1' }}>
                  <label style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 13, color: '#2B3640', display: 'block', marginBottom: 4 }}>Sinopse</label>
                  <textarea value={form.sinopse} onChange={e => setForm({ ...form, sinopse: e.target.value })} style={{ ...inputStyle, height: 80, resize: 'vertical', padding: '8px 12px' }} required />
                </div>
              </div>
              <div style={{ display: 'flex', gap: 12, marginTop: 20, justifyContent: 'flex-end' }}>
                <button type="button" onClick={() => setModal(null)} style={{ padding: '10px 24px', border: '1px solid #A65A49', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, background: 'white', color: '#2B3640' }}>Cancelar</button>
                <button type="submit" disabled={saving} style={{ padding: '10px 28px', background: '#A65A49', color: 'white', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700 }}>
                  {saving ? 'Salvando...' : modal === 'novo' ? 'Cadastrar' : 'Salvar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}
