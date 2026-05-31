import { createContext, useContext, useState, useEffect } from 'react'
const AuthContext = createContext(null)
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(() => localStorage.getItem('ignis_token'))
  const [perfil, setPerfil] = useState(() => localStorage.getItem('ignis_perfil'))
  const login = (tokenData, perfilData, nomeData) => {
    setToken(tokenData); setPerfil(perfilData)
    setUser({ nome: nomeData, perfil: perfilData })
    localStorage.setItem('ignis_token', tokenData)
    localStorage.setItem('ignis_perfil', perfilData)
    localStorage.setItem('ignis_nome', nomeData)
  }
  const logout = () => {
    setToken(null); setPerfil(null); setUser(null)
    localStorage.removeItem('ignis_token')
    localStorage.removeItem('ignis_perfil')
    localStorage.removeItem('ignis_nome')
  }
  useEffect(() => {
    const nome = localStorage.getItem('ignis_nome')
    if (token && perfil && nome) setUser({ nome, perfil })
  }, [])
  return <AuthContext.Provider value={{ user, token, perfil, login, logout }}>{children}</AuthContext.Provider>
}
export const useAuth = () => useContext(AuthContext)
