// cypress/e2e/01_autenticacao.cy.js
describe('Autenticação', () => {

  beforeEach(() => {
    cy.visit('/login')
  })

  it('deve exibir a página de login corretamente', () => {
    cy.contains('PapiroTech').should('be.visible')
    cy.contains('Bem-vindo à Biblioteca IGNIS').should('be.visible')
    cy.get('input[placeholder*="e-mail"]').should('be.visible')
    cy.get('input[type="password"]').should('be.visible')
    cy.get('button[type="submit"]').contains('Entrar').should('be.visible')
    cy.contains('Cadastre-se').should('be.visible')
  })

  it('deve exibir erro com credenciais inválidas', () => {
    cy.get('input[placeholder*="e-mail"]').type('usuario@invalido.com')
    cy.get('input[type="password"]').type('senhaerrada')
    cy.get('button[type="submit"]').click()
    cy.contains('Credenciais inválidas').should('be.visible')
  })

  it('deve fazer login como CLIENTE e redirecionar para catálogo', () => {
    cy.get('input[placeholder*="e-mail"]').type('cliente@biblioteca.com')
    cy.get('input[type="password"]').type('cliente123')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/cliente/historico')
    cy.contains('Sair').should('be.visible')
  })

  it('deve fazer login como ADMIN e redirecionar para dashboard', () => {
    cy.get('input[placeholder*="e-mail"]').type('admin@biblioteca.com')
    cy.get('input[type="password"]').type('admin123')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/admin/dashboard')
    cy.contains('Painel Administrativo').should('be.visible')
  })

  it('deve fazer login como ESTOQUISTA e redirecionar para retirada', () => {
    cy.get('input[placeholder*="e-mail"]').type('EST001')
    cy.get('input[type="password"]').type('estoque123')
    cy.get('button[type="submit"]').click()
    cy.url().should('include', '/estoquista/retirada')
    cy.contains('Registrar Retirada').should('be.visible')
  })

  it('deve fazer logout corretamente', () => {
    cy.loginComoCliente()
    cy.contains('button', 'Sair').click()
    cy.url().should('include', '/login')
    cy.contains('Sair').should('not.exist')
  })

  it('deve redirecionar para /login ao acessar rota protegida sem token', () => {
    cy.visit('/catalogo')
    cy.url().should('include', '/login')
  })

  it('deve redirecionar para /catalogo ao tentar acessar admin como cliente', () => {
    cy.loginComoCliente()
    cy.visit('/admin/dashboard')
    cy.url().should('include', '/cliente/historico')
  })


})
