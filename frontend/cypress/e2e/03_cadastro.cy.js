// cypress/e2e/03_cadastro.cy.js
describe('Cadastro de Cliente', () => {

  beforeEach(() => {
    cy.visit('/cadastro')
  })

  it('deve exibir o formulário de cadastro', () => {
    cy.contains('Criar Conta').should('be.visible')
    cy.get('input[placeholder*="nome"]').should('be.visible')
    cy.get('input[type="email"]').should('be.visible')
    cy.get('input[placeholder*="CPF"]').should('be.visible')
    cy.get('input[type="date"]').should('be.visible')
    cy.get('select').should('be.visible')
    cy.get('input[type="password"]').should('have.length', 2)
  })

  it('deve exibir erro se as senhas não coincidem', () => {
    cy.get('input[placeholder*="nome"]').type('Teste Usuario')
    cy.get('input[type="email"]').type('teste@email.com')
    cy.get('input[placeholder*="CPF"]').type('12345678901')
    cy.get('input[type="date"]').type('2000-01-01')
    cy.get('select').select('Masculino')
    cy.get('input[placeholder*="Mínimo"]').type('senha123')
    cy.get('input[placeholder*="Repita"]').type('senhadiferente')
    cy.get('button[type="submit"]').click()
    cy.contains('Senhas não coincidem').should('be.visible')
  })

  it('deve ter link para a página de login', () => {
    cy.contains('Entrar').click()
    cy.url().should('include', '/login')
  })

  it('deve cadastrar novo cliente com sucesso', () => {
    const timestamp = Date.now()
    cy.get('input[placeholder*="nome"]').type('Cliente Cypress')
    cy.get('input[type="email"]').type(`cypress${timestamp}@test.com`)
    cy.get('input[placeholder*="CPF"]').type('99988877766')
    cy.get('input[type="date"]').type('2000-05-15')
    cy.get('select').select('Masculino')
    cy.get('input[placeholder*="Mínimo"]').type('senha123')
    cy.get('input[placeholder*="Repita"]').type('senha123')
    cy.get('button[type="submit"]').click()
    cy.contains('Conta criada').should('be.visible')
    cy.url().should('include', '/login')
  })
})
