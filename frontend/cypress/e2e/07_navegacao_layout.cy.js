// cypress/e2e/07_navegacao_layout.cy.js
describe('Navegação e Layout', () => {

  it('deve redirecionar / para /login', () => {
    cy.visit('/')
    cy.url().should('include', '/login')
  })

  it('deve redirecionar rota inexistente para /login', () => {
    cy.visit('/rota-inexistente-xyz')
    cy.url().should('include', '/login')
  })

  it('deve exibir o Navbar em todas as páginas públicas', () => {
    const paginas = ['/login']
    paginas.forEach(pagina => {
      cy.visit(pagina)
      cy.contains('PapiroTech').should('be.visible')
    })
  })

  it('deve exibir o Footer no catálogo', () => {
    cy.loginComoCliente()
    cy.visit('/catalogo')
    cy.contains('© 2026 PapiroTech').should('be.visible')
  })

  it('deve navegar para o catálogo pelo link Início', () => {
    cy.loginComoCliente()
    cy.contains('Início').click()
    cy.url().should('include', '/cliente/historico')
  })

  it('deve navegar para o catálogo pelo link Catálogo', () => {
    cy.loginComoCliente()
    cy.visit('/catalogo')
    cy.contains('Catálogo').click()
    cy.url().should('include', '/catalogo')
  })

  it('deve navegar para login pelo link PapiroTech', () => {
    cy.visit('/catalogo')
    cy.contains('PapiroTech').click()
    cy.url().should('include', '/catalogo')
  })

  it('deve exibir botão Sair apenas quando logado', () => {
    cy.visit('/login')
    cy.contains('Sair').should('not.exist')

    cy.loginComoCliente()
    cy.contains('Sair').should('be.visible')
  })

  it('deve manter sessão após recarregar a página', () => {
    cy.loginComoCliente()
    cy.reload()
    cy.contains('Sair').should('be.visible')
    cy.url().should('include', '/cliente/historico')
  })

  it('deve exibir a página de detalhe com todas as seções', () => {
    cy.visit('/catalogo')
    cy.contains('button', 'Ver mais').click()
    cy.get('[style*="2B3640"]').first().click()
    cy.contains('Voltar ao Catálogo').should('be.visible')
    cy.contains('Categoria').should('be.visible')
    cy.contains('Editora').should('be.visible')
    cy.contains('Período de Aluguel').should('be.visible')
    cy.contains('Aluguel Gratuito').should('be.visible')
    cy.contains('Sinopse').should('be.visible')
  })
})

describe('Responsividade básica', () => {

  it('deve funcionar em viewport de tablet (768x1024)', () => {
    cy.viewport(768, 1024)
    cy.visit('/catalogo')
    cy.contains('PapiroTech').should('be.visible')
    cy.contains('Recentemente adicionados').should('be.visible')
  })

  it('deve funcionar em viewport desktop padrão (1440x900)', () => {
    cy.viewport(1440, 900)
    cy.visit('/catalogo')
    cy.contains('Sua jornada Pelo Conhecimento Começa Aqui').should('be.visible')
  })
})
