import { formatarData } from '../../utils/dateUtils'
import { useState, useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import Navbar from '../../components/common/Navbar'
import Footer from '../../components/common/Footer'
import Toast from '../../components/common/Toast'
import { livroService, emprestimoService, favoritoService } from '../../services/api'
import { useAuth } from '../../context/AuthContext'
import { Heart, ArrowLeft } from 'lucide-react'
import CapaLivro from '../../components/common/CapaLivro'

export default function LivroDetalhePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { token, perfil } = useAuth()
  const [livro, setLivro] = useState(null)
  const [loading, setLoading] = useState(true)
  const [loadingAlugar, setLoadingAlugar] = useState(false)
  const [toast, setToast] = useState(null)
  const [codigoGerado, setCodigoGerado] = useState(null)

  useEffect(() => {
    livroService.buscarPorId(id).then(r => setLivro(r.data)).catch(() => navigate('/catalogo')).finally(() => setLoading(false))
  }, [id])

  const handleAlugar = async () => {
    if (!token) { navigate('/login'); return }
    if (perfil !== 'CLIENTE') { setToast({ mensagem: 'Apenas clientes podem alugar livros.', tipo: 'aviso' }); return }
    setLoadingAlugar(true)
    try {
      const { data } = await emprestimoService.reservar(id)
      setCodigoGerado(data.codigoRetirada)
      setToast({ mensagem: 'Livro reservado com sucesso!', tipo: 'sucesso' })
    } catch (err) {
      setToast({ mensagem: err.response?.data?.detail || 'Não foi possível reservar.', tipo: 'erro' })
    } finally { setLoadingAlugar(false) }
  }

  const handleFavorito = async () => {
    if (!token) { navigate('/login'); return }
    try { const { data } = await favoritoService.toggle(id); setToast({ mensagem: data.mensagem || 'Favorito atualizado!', tipo: 'sucesso' }) }
    catch { setToast({ mensagem: 'Erro ao alterar favorito.', tipo: 'erro' }) }
  }

  if (loading) return (
    <div style={{ minHeight: '100vh', background: '#F2E2C4', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: 'Raleway', fontSize: 18, color: '#2B3640' }}>
        Carregando...
      </div>
    </div>
  )

  const devDataObj = new Date()
  devDataObj.setDate(devDataObj.getDate() + 7)
  const devDataStr = `${String(devDataObj.getDate()).padStart(2,'0')}/${String(devDataObj.getMonth()+1).padStart(2,'0')}/${devDataObj.getFullYear()}`

  return (
    <div style={{ minHeight: '100vh', background: '#F2E2C4', display: 'flex', flexDirection: 'column' }}>
      <Navbar />
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      {/* Banner */}
      <div style={{ background: '#F2E2C4', padding: '24px 116px 0' }}>
        <Link to="/catalogo" style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#A65A49', display: 'inline-flex', alignItems: 'center', gap: 6 }}>
          <ArrowLeft size={18} /> Voltar ao Catálogo
        </Link>
      </div>

      <main style={{ flex: 1, padding: '24px 116px 48px' }}>
        {/* Container principal */}
        <div style={{ background: '#D9B391', borderRadius: 15, padding: '32px', display: 'flex', gap: 32, flexWrap: 'wrap' }}>
          {/* Capa do livro */}
          <div style={{ flexShrink: 0 }}>
            <CapaLivro isbn={livro.isbn} titulo={livro.titulo} autor={livro.autor} idLivro={livro.idLivro} width={335} height={400} borderRadius="10px" />
            <button
              onClick={handleFavorito}
              style={{ marginTop: 12, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6, fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#A65A49', width: '100%' }}
            >
              <Heart size={18} fill="#A65A49" color="#A65A49" /> Adicionar aos favoritos
            </button>
          </div>

          {/* Detalhes */}
          <div style={{ flex: 1, minWidth: 300 }}>
            <p style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#A65A49', marginBottom: 8 }}>
              Por {livro.autor}
            </p>
            <h1 style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 36, color: '#2B3640', marginBottom: 24, lineHeight: 1.2 }}>
              {livro.titulo}
            </h1>

            {/* Informações */}
            <div style={{ background: '#F2E2C4', borderRadius: 4, padding: '16px 20px', marginBottom: 16 }}>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                {[
                  ['Categoria', livro.categoria?.descricao],
                  ['Ano de Lançamento', livro.anoPublicacao > 0 ? livro.anoPublicacao : '—'],
                  ['Editora', livro.editora],
                  ['Cópias Disponíveis', livro.quantidadeDisponivel],
                ].map(([label, val]) => (
                  <div key={label}>
                    <div style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#A65A49' }}>{label}</div>
                    <div style={{ fontFamily: 'Raleway', fontWeight: val === livro.quantidadeDisponivel ? 700 : 600, fontSize: val === livro.quantidadeDisponivel ? 32 : 16, color: '#2B3640' }}>
                      {val || '—'}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Aluguel */}
            <div style={{ background: '#F2E2C4', borderRadius: 4, padding: '16px 20px', marginBottom: 16 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
                <span style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 20, color: '#2B3640' }}>Período de Aluguel</span>
                <div style={{ border: '2px solid #A65A49', borderRadius: 4, padding: '4px 16px', fontFamily: 'Raleway', fontWeight: 700, fontSize: 20, color: '#2B3640' }}>
                  7
                </div>
                <span style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640' }}>dias</span>
              </div>
              <div style={{ background: 'white', borderRadius: 4, padding: '12px 16px', marginBottom: 12 }}>
                <span style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640' }}>
                  Data de devolução: {devDataStr}
                </span>
              </div>
              <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 16, color: '#2B3640', textAlign: 'center', marginBottom: 12 }}>
                Aluguel Gratuito
              </div>
            </div>

            {/* Código ou botão */}
            {codigoGerado ? (
              <div style={{ background: '#2B3640', borderRadius: 4, padding: '20px', textAlign: 'center', marginBottom: 16 }}>
                <div style={{ fontFamily: 'Raleway', fontSize: 14, color: '#D9B391', marginBottom: 8 }}>Código de Retirada</div>
                <div style={{ fontFamily: 'monospace', fontSize: 24, fontWeight: 700, color: '#A65A49', letterSpacing: 4 }}>
                  {codigoGerado}
                </div>
                <div style={{ fontFamily: 'Raleway', fontSize: 13, color: '#D9B391', marginTop: 8 }}>
                  Apresente ao estoquista para retirar o livro
                </div>
              </div>
            ) : (
              <button
                onClick={handleAlugar}
                disabled={!livro.disponivel || loadingAlugar}
                style={{
                  width: '100%', height: 42,
                  background: livro.disponivel ? '#A65A49' : '#ccc',
                  color: 'white', borderRadius: 4,
                  fontFamily: 'Raleway', fontWeight: 700, fontSize: 18,
                  cursor: livro.disponivel ? 'pointer' : 'not-allowed',
                  marginBottom: 16,
                }}
              >
                {loadingAlugar ? 'Reservando...' : livro.disponivel ? 'Alugar Livro' : 'Indisponível'}
              </button>
            )}
          </div>
        </div>

        {/* Sinopse */}
        {livro.sinopse && (
          <div style={{ background: 'white', border: '1px solid #A65A49', borderRadius: 15, padding: '24px 32px', marginTop: 24 }}>
            <h3 style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 32, color: '#A65A49', marginBottom: 12 }}>Sinopse</h3>
            <p style={{ fontFamily: 'Raleway', fontWeight: 400, fontSize: 20, color: '#2B3640', lineHeight: 1.6 }}>{livro.sinopse}</p>
          </div>
        )}
      </main>

      <Footer />
    </div>
  )
}
