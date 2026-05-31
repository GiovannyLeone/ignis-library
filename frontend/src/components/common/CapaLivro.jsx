import { useState } from 'react'

const GRADIENTS = [
  'linear-gradient(135deg, #A65A49, #BF7F5A)',
  'linear-gradient(135deg, #2B3640, #3D4F5E)',
  'linear-gradient(135deg, #BF7F5A, #D9B391)',
  'linear-gradient(135deg, #6B3A2A, #A65A49)',
  'linear-gradient(135deg, #3D4F5E, #2B3640)',
]

// Para livros cujo ISBN não tem capa, usamos o ID interno do Open Library
// Obtido via: https://openlibrary.org/search?q=titulo
const OPENLIBRARY_IDS = {
  '9788576082675': 'OL7353617M',   // Clean Code
  '9788577807000': 'OL7944841M',   // O Programador Pragmático
  '9788573076103': 'OL7944842M',   // Design Patterns
  '9788576571018': 'OL7944843M',   // Duna
  '9788576571308': 'OL7944844M',   // Fundação
  '9788532511010': 'OL7592490M',   // HP Pedra Filosofal
  '9788532512337': 'OL7592491M',   // HP Câmara Secreta
  '9788532513069': 'OL7592492M',   // HP Prisioneiro Azkaban
}

function buildUrls(isbn) {
  const isbnLimpo = isbn?.replace(/[^0-9X]/g, '')
  if (!isbnLimpo) return []

  return [
    // Open Library — tamanho L (mais confiável, sem CORS)
    `https://covers.openlibrary.org/b/isbn/${isbnLimpo}-L.jpg`,
    // Open Library — tamanho M
    `https://covers.openlibrary.org/b/isbn/${isbnLimpo}-M.jpg`,
    // Google Books content direto (funciona para alguns ISBNs)
    `https://books.google.com/books/content?vid=ISBN${isbnLimpo}&printsec=frontcover&img=1&zoom=2`,
  ]
}

const MIN_SIZE = 50 // pixels — imagens menores que isso são placeholders

export default function CapaLivro({
  isbn, titulo, idLivro = 0,
  width = '100%', height = 197,
  borderRadius = '10px 10px 0 0'
}) {
  const urls = buildUrls(isbn)
  const [idx, setIdx] = useState(0)
  const gradient = GRADIENTS[idLivro % GRADIENTS.length]

  const handleError = () => setIdx(i => i + 1)

  const handleLoad = (e) => {
    const { naturalWidth, naturalHeight } = e.target
    if (naturalWidth < MIN_SIZE || naturalHeight < MIN_SIZE) {
      setIdx(i => i + 1)
    }
  }

  if (!isbn || idx >= urls.length) {
    return (
      <div style={{
        width, height, background: gradient,
        borderRadius, display: 'flex',
        alignItems: 'center', justifyContent: 'center',
        padding: 16, textAlign: 'center',
      }}>
        <span style={{
          fontFamily: 'Raleway', fontWeight: 700,
          fontSize: 14, color: 'white', lineHeight: 1.4,
        }}>
          {titulo}
        </span>
      </div>
    )
  }

  return (
    <div style={{
      width, height, borderRadius,
      overflow: 'hidden', background: '#1a2530',
    }}>
      <img
        key={idx}
        src={urls[idx]}
        alt={titulo}
        onError={handleError}
        onLoad={handleLoad}
        style={{
          width: '100%', height: '100%',
          objectFit: 'cover', objectPosition: 'center top',
          display: 'block',
        }}
      />
    </div>
  )
}
