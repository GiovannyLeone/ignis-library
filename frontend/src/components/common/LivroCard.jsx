import { useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { favoritoService } from '../../services/api'
import { useState } from 'react'
import { Heart } from 'lucide-react'
import CapaLivro from './CapaLivro'
import Toast from './Toast'

export default function LivroCard({ livro, onFavoritoChange, mostrarAno }) {
  const navigate = useNavigate()
  const { token, perfil } = useAuth()
  const [loading, setLoading] = useState(false)
  const [toast, setToast] = useState(null)

  const handleFav = async (e) => {
    e.stopPropagation()
    if (!token || perfil !== 'CLIENTE') { navigate('/login'); return }
    setLoading(true)
    try {
      const { data } = await favoritoService.toggle(livro.idLivro)
      setToast({ mensagem: data.mensagem || 'Favorito atualizado!', tipo: 'sucesso' })
      onFavoritoChange?.()
    } catch {
      setToast({ mensagem: 'Erro ao favoritar.', tipo: 'erro' })
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      {toast && <Toast {...toast} onClose={() => setToast(null)} />}
      <div
        onClick={() => navigate(`/livros/${livro.idLivro}`)}
        style={{
          background: '#2B3640',
          borderRadius: 10,
          boxShadow: '0px 4px 4px rgba(0,0,0,0.25)',
          cursor: 'pointer',
          width: 230,
          overflow: 'hidden',
          transition: 'transform 0.2s',
          flexShrink: 0,
        }}
        onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-4px)'}
        onMouseLeave={e => e.currentTarget.style.transform = 'translateY(0)'}
      >
        {/* Capa */}
        <div style={{ position: 'relative' }}>
          <CapaLivro
            isbn={livro.isbn}
            titulo={livro.titulo}
            autor={livro.autor}
            idLivro={livro.idLivro}
            height={197}
            borderRadius="10px 10px 0 0"
          />
          {/* Coração */}
          <button
            onClick={handleFav}
            disabled={loading}
            style={{
              position: 'absolute', top: 8, right: 8,
              background: 'rgba(0,0,0,0.45)', border: 'none',
              borderRadius: '50%', width: 32, height: 32,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              cursor: 'pointer', opacity: loading ? 0.5 : 1,
            }}
            title="Favoritar"
          >
            <Heart size={16} color="#A65A49" fill="#A65A49" />
          </button>
          {/* Badge indisponível */}
          {!livro.disponivel && (
            <div style={{
              position: 'absolute', bottom: 6, left: 6,
              background: 'rgba(0,0,0,0.6)', color: 'white',
              padding: '2px 8px', borderRadius: 4,
              fontSize: 11, fontFamily: 'Raleway', fontWeight: 600,
            }}>
              Indisponível
            </div>
          )}
        </div>

        {/* Info */}
        <div style={{ padding: '12px 14px 14px' }}>
          <p style={{
            fontFamily: 'Raleway', fontWeight: 700, fontSize: 15,
            color: 'white', marginBottom: 4, lineHeight: 1.3,
            whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
          }}>
            {livro.titulo}
          </p>
          <p style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(255,255,255,0.7)', marginBottom: 2 }}>
            {livro.categoria?.descricao}
          </p>
          <p style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(255,255,255,0.7)' }}>
            {livro.autor}
          </p>
          {mostrarAno && livro.anoPublicacao > 0 && (
            <p style={{ fontFamily: 'Raleway', fontSize: 13, color: 'rgba(255,255,255,0.6)', marginTop: 2 }}>
              Ano: {livro.anoPublicacao}
            </p>
          )}
        </div>
      </div>
    </>
  )
}
