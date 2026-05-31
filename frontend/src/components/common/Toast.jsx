import { useEffect, useState } from 'react'
export default function Toast({ mensagem, tipo = 'sucesso', onClose }) {
  const [visible, setVisible] = useState(true)
  useEffect(() => {
    const t = setTimeout(() => { setVisible(false); setTimeout(onClose, 300) }, 3500)
    return () => clearTimeout(t)
  }, [])
  const bg = tipo === 'sucesso' ? '#2D7A4F' : tipo === 'erro' ? '#C0392B' : tipo === 'aviso' ? '#D4860B' : '#2B3640'
  return (
    <div style={{
      position: 'fixed', top: 90, right: 24, zIndex: 9999,
      background: bg, color: 'white',
      padding: '14px 20px', borderRadius: 10,
      boxShadow: '0 8px 24px rgba(0,0,0,0.2)',
      maxWidth: 360, fontSize: 15, fontWeight: 600,
      fontFamily: 'Raleway',
      opacity: visible ? 1 : 0, transition: 'opacity 0.3s',
      display: 'flex', alignItems: 'center', gap: 12,
    }}>
      <span>{tipo === 'sucesso' ? '✓' : tipo === 'erro' ? '✕' : '⚠'}</span>
      {mensagem}
      <button onClick={() => { setVisible(false); setTimeout(onClose, 300) }}
        style={{ marginLeft: 'auto', color: 'rgba(255,255,255,0.7)', fontSize: 18 }}>×</button>
    </div>
  )
}
