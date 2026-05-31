/**
 * Formata data do backend sem conversão de timezone.
 * new Date("2026-05-28") interpreta como UTC midnight, resultando em 27/05 no UTC-3.
 * Solução: parsear a string diretamente sem usar o objeto Date.
 */
export function formatarData(valor) {
  if (!valor) return '—'
  const parte = typeof valor === 'string' ? valor.split('T')[0] : String(valor)
  if (parte.includes('-')) {
    const [ano, mes, dia] = parte.split('-')
    return `${dia}/${mes}/${ano}`
  }
  return '—'
}
