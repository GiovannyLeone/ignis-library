import { useState, useEffect } from 'react'
import Navbar from '../../components/common/Navbar'
import Footer from '../../components/common/Footer'
import LivroCard from '../../components/common/LivroCard'
import { livroService } from '../../services/api'

const inputStyle = {
  background: '#F2E2C4', border: '1px solid #A65A49', borderRadius: 4,
  height: 34, padding: '0 12px', fontFamily: 'Raleway', fontWeight: 600,
  fontSize: 16, color: '#2B3640', width: 200, outline: 'none',
}

function FiltroBox({ filtros, setFiltros, onBuscar, onLimpar }) {
  return (
    <div style={{ background: '#D9B391', borderRadius: 15, padding: '20px 28px', width: '100%' }}>
      <div style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 24, color: '#2B3640', marginBottom: 16 }}>Filtros</div>
      <form onSubmit={onBuscar} style={{ display: 'flex', gap: 16, flexWrap: 'wrap', alignItems: 'flex-end' }}>
        <div>
          <div style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#2B3640', marginBottom: 4 }}>Categoria</div>
          <select style={inputStyle}><option>Todas as categorias</option></select>
        </div>
        <div>
          <div style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#2B3640', marginBottom: 4 }}>Título</div>
          <input style={inputStyle} placeholder="Pesquisar título..." value={filtros.titulo}
            onChange={e => setFiltros(f => ({ ...f, titulo: e.target.value }))} />
        </div>
        <div>
          <div style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#2B3640', marginBottom: 4 }}>Autor</div>
          <input style={inputStyle} placeholder="Pesquisar autor..." value={filtros.autor}
            onChange={e => setFiltros(f => ({ ...f, autor: e.target.value }))} />
        </div>
        <div>
          <div style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#2B3640', marginBottom: 4 }}>Editora</div>
          <input style={inputStyle} placeholder="Pesquisar Editora..." value={filtros.editora}
            onChange={e => setFiltros(f => ({ ...f, editora: e.target.value }))} />
        </div>
        <div>
          <div style={{ fontFamily: 'Raleway', fontWeight: 600, fontSize: 16, color: '#2B3640', marginBottom: 4 }}>Ano de lançamento</div>
          <input style={{ ...inputStyle, width: 130 }} placeholder="Ex: 2020" value={filtros.ano}
            onChange={e => setFiltros(f => ({ ...f, ano: e.target.value }))} />
        </div>
        <button type="button" onClick={onLimpar} style={{ background: '#A65A49', color: 'white', padding: '8px 20px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 18, height: 38 }}>
          Limpar filtros
        </button>
        <button type="submit" style={{ background: '#2B3640', color: 'white', padding: '8px 20px', borderRadius: 4, fontFamily: 'Raleway', fontWeight: 700, fontSize: 18, height: 38 }}>
          Buscar
        </button>
      </form>
    </div>
  )
}

export default function CatalogoPage() {
  const [recentes, setRecentes] = useState([])
  const [totalRecentes, setTotalRecentes] = useState(0)
  const [loadingRecentes, setLoadingRecentes] = useState(true)

  const [livros, setLivros] = useState([])
  const [totalElementos, setTotalElementos] = useState(0)
  const [totalPaginas, setTotalPaginas] = useState(1)
  const [pagina, setPagina] = useState(0)
  const [loadingCatalogo, setLoadingCatalogo] = useState(false)

  const [modoCatalogo, setModoCatalogo] = useState(false)
  const [termoBusca, setTermoBusca] = useState('')
  const [filtros, setFiltros] = useState({ titulo: '', autor: '', editora: '', ano: '' })

  // Carrega só os recentes na home
  useEffect(() => {
    livroService.listar(0, 4)
      .then(res => {
        setRecentes(res.data.conteudo || [])
        setTotalRecentes(res.data.totalElementos || 0)
      })
      .catch(console.error)
      .finally(() => setLoadingRecentes(false))
  }, [])

  // Carrega catálogo — ACUMULA livros ao paginar
  const carregarCatalogo = async (p = 0, termo = '', acumular = false) => {
    setLoadingCatalogo(true)
    try {
      const res = termo
        ? await livroService.buscar(termo, p, 12)
        : await livroService.listar(p, 12)
      const novos = res.data.conteudo || []
      setLivros(prev => acumular ? [...prev, ...novos] : novos)
      setTotalPaginas(res.data.totalPaginas || 1)
      setTotalElementos(res.data.totalElementos || 0)
      setPagina(p)
    } catch (err) { console.error(err) }
    finally { setLoadingCatalogo(false) }
  }

  const irParaCatalogo = () => {
    setModoCatalogo(true)
    setTermoBusca('')
    setLivros([])
    carregarCatalogo(0, '', false)
  }

  const handleBusca = (e) => {
    e.preventDefault()
    const termo = filtros.titulo || filtros.autor || filtros.editora
    setTermoBusca(termo)
    setModoCatalogo(true)
    setLivros([])
    carregarCatalogo(0, termo, false)
  }

  const limpar = () => {
    setFiltros({ titulo: '', autor: '', editora: '', ano: '' })
    setTermoBusca('')
    setModoCatalogo(false)
    setLivros([])
  }

  const verMais = () => {
    if (modoCatalogo) {
      // No catálogo: carrega próxima página e ACUMULA
      carregarCatalogo(pagina + 1, termoBusca, true)
    } else {
      // Na home: vai para o catálogo
      irParaCatalogo()
    }
  }

  const temMaisLivros = modoCatalogo && pagina < totalPaginas - 1

  const listaAtual = modoCatalogo ? livros : recentes
  const totalAtual = modoCatalogo ? totalElementos : totalRecentes
  const loadingAtual = modoCatalogo ? loadingCatalogo : loadingRecentes
  const tituloSecao = modoCatalogo ? 'Catálogo' : 'Recentemente adicionados'

  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', background: '#F2E2C4' }}>
      <Navbar />

      {/* Banner hero */}
      {!modoCatalogo && (
        <div style={{ background: '#2B3640', boxShadow: 'inset 0px -14px 37.5px rgba(0,0,0,0.25)', padding: '60px 116px 40px', display: 'flex', flexDirection: 'column', gap: 32 }}>
          <h1 style={{ fontFamily: 'Raleway', fontWeight: 200, fontSize: 34, color: '#F2E2C4', textAlign: 'center' }}>
            Sua jornada Pelo Conhecimento Começa Aqui
          </h1>
          <FiltroBox filtros={filtros} setFiltros={setFiltros} onBuscar={handleBusca} onLimpar={limpar} />
        </div>
      )}

      {/* Banner catálogo */}
      {modoCatalogo && (
        <div style={{ background: '#F2E2C4', padding: '32px 116px 24px' }}>
          <h1 style={{ fontFamily: 'Raleway', fontWeight: 700, fontSize: 34, color: '#A65A49', marginBottom: 20 }}>
            Catálogo Completo
          </h1>
          <FiltroBox filtros={filtros} setFiltros={setFiltros} onBuscar={handleBusca} onLimpar={limpar} />
        </div>
      )}

      <main style={{ flex: 1, padding: '40px 116px' }}>
        {/* Cabeçalho da seção */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <span style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#A65A49' }}>{tituloSecao}</span>
          <span style={{ fontFamily: 'Raleway', fontWeight: 300, fontSize: 24, color: '#A65A49' }}>
            Mostrando {listaAtual.length} de {totalAtual} livros
          </span>
        </div>

        {/* Grid de livros */}
        <div style={{ background: '#BF7F5A', borderRadius: 15, padding: '24px 28px', marginBottom: 16 }}>
          {loadingAtual && listaAtual.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '40px 0', color: 'white', fontFamily: 'Raleway', fontSize: 18 }}>
              Carregando...
            </div>
          ) : listaAtual.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '40px 0', color: 'white', fontFamily: 'Raleway', fontSize: 18 }}>
              Nenhum livro encontrado
            </div>
          ) : (
            <>
              <div style={{ display: 'flex', gap: 20, flexWrap: 'wrap' }}>
                {listaAtual.map(livro => (
                  <LivroCard key={livro.idLivro} livro={livro} mostrarAno={modoCatalogo}
                    onFavoritoChange={() => {}} />
                ))}
              </div>
              {/* Spinner inline ao carregar mais */}
              {loadingCatalogo && modoCatalogo && (
                <div style={{ textAlign: 'center', padding: '20px 0', color: 'white', fontFamily: 'Raleway', fontSize: 16 }}>
                  Carregando mais livros...
                </div>
              )}
            </>
          )}
        </div>

        {/* Botão Ver mais */}
        {(!modoCatalogo || temMaisLivros) && (
          <button onClick={verMais} disabled={loadingCatalogo} style={{
            background: '#A65A49', color: 'white', padding: '8px 28px', borderRadius: 10,
            fontFamily: 'Raleway', fontWeight: 700, fontSize: 18,
            opacity: loadingCatalogo ? 0.6 : 1,
          }}>
            {loadingCatalogo ? 'Carregando...' : 'Ver mais'}
          </button>
        )}
      </main>

      <Footer />
    </div>
  )
}
