export default function Footer() {
  return (
    <footer style={{ background: '#D9B391', padding: '60px 116px 40px' }}>
      <div style={{ maxWidth: 1168, borderTop: '1px solid #A65A49', paddingTop: 24 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontFamily: 'Raleway', fontWeight: 800, fontSize: 24, color: '#A65A49' }}>
            PapiroTech
          </span>
          <span style={{ fontFamily: 'Raleway', fontWeight: 300, fontSize: 24, color: '#A65A49', textAlign: 'center' }}>
            © 2026 PapiroTech. Todos os direitos reservados.
          </span>
        </div>
      </div>
    </footer>
  )
}
