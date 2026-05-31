import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { favoritoService } from '../../services/api'
import Toast from '../../components/common/Toast'
import { Heart } from 'lucide-react'
import CapaLivro from '../../components/common/CapaLivro'

export default function FavoritosPage() {
  const [favoritos, setFavoritos] = useState([])
  const [loading, setLoading] = useState(true)
  const [toast, setToast] = useState(null)
  const navigate = useNavigate()

  const carregar = async () => {
    setLoading(true)
    try { const { data } = await favoritoService.listar(); setFavoritos(data || []) }
    catch { console.error() }
    finally { setLoading(false) }
  }

  useEffect(() => { carregar() }, [])

  const remover = async (livroId) => {
    try { const { data } = await favoritoService.toggle(livroId); setToast({ mensagem: data.mensagem || 'Removido dos favoritos.', tipo: 'sucesso' }); carregar() }
    catch { setToast({ mensagem: 'Erro ao remover.', tipo: 'erro' }) }
  }

  if (loading) return <div style={{ padding: 40, fontFamily: 'Raleway', color: '#2B3640' }}>Carregando...</div>

  return (
    <div style={{ paddingTop: 32 }}>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2 style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#A65A49' }}>Favoritos</h2>
        <span style={{ fontFamily: 'Raleway', fontWeight: 300, fontSize: 24, color: '#A65A49' }}>
          Mostrando {favoritos.length} de {favoritos.length} livros
        </span>
      </div>

      {favoritos.length === 0 ? (
        <div style={{ background: '#BF7F5A', borderRadius: 15, padding: '60px 32px', textAlign: 'center' }}>
          <Heart size={48} color="#A65A49" fill="#A65A49" style={{ margin: '0 auto 16px' }} />
          <p style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 20, color: 'white' }}>Nenhum favorito ainda</p>
        </div>
      ) : (
        <div style={{ background: '#BF7F5A', borderRadius: 15, padding: '24px 28px' }}>
          <div style={{ display: 'flex', gap: 20, flexWrap: 'wrap' }}>
            {favoritos.map(fav => {
              const livro = fav.livro
              return (
                <div key={fav.idFavorito}
                  style={{ background: '#2B3640', borderRadius: 10, width: 230, minHeight: 197, padding: '16px 14px 14px', position: 'relative', cursor: 'pointer', transition: 'transform 0.2s', boxShadow: '0px 4px 4px rgba(0,0,0,0.25)' }}
                  onClick={() => navigate(`/livros/${livro.idLivro}`)}
                  onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-4px)'}
                  onMouseLeave={e => e.currentTarget.style.transform = 'translateY(0)'}
                >
                  <div style={{ position: 'relative', borderRadius: '10px 10px 0 0', overflow: 'hidden' }}>
                    <CapaLivro isbn={livro.isbn} titulo={livro.titulo} autor={livro.autor} idLivro={livro.idLivro} height={160} borderRadius="10px 10px 0 0" />
                    <button
                      onClick={(e) => { e.stopPropagation(); remover(livro.idLivro) }}
                      style={{ position: 'absolute', top: 8, right: 8, background: 'rgba(0,0,0,0.35)', border: 'none', borderRadius: '50%', width: 30, height: 30, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer' }}
                      title="Remover"
                    >
                      <Heart size={14} color="#A65A49" fill="#A65A49" />
                    </button>
                  </div>
                  <div style={{ padding: '10px 14px 12px' }}>
                    <p style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 15, color: 'white', marginBottom: 2, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{livro.titulo}</p>
                    <p style={{ fontFamily: 'Raleway', fontWeight: 400, fontSize: 13, color: 'rgba(255,255,255,0.7)', marginBottom: 2 }}>Categoria: {livro.categoria?.descricao}</p>
                    <p style={{ fontFamily: 'Raleway', fontWeight: 400, fontSize: 13, color: 'rgba(255,255,255,0.7)' }}>Autor: {livro.autor}</p>
                  </div>
                </div>
              )
            })}
          </div>

        </div>
      )}
    </div>
  )
}
