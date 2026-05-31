// cypress/e2e/05_estoquista.cy.js
describe('Área do Estoquista', () => {

  beforeEach(() => {
    cy.loginComoEstoquista()
  })

  it('deve exibir as abas de retirada e devolução', () => {
    cy.contains('Registrar Retirada').should('be.visible')
    cy.contains('Registrar Devolução').should('be.visible')
  })

  it('deve exibir o formulário de retirada', () => {
    cy.visit('/estoquista/retirada')
    cy.contains('Registrar Retirada').should('be.visible')
    cy.contains('Código de Retirada').should('be.visible')
    cy.get('input[placeholder*="código"]').should('be.visible')
    cy.contains('button', 'Confirmar Retirada').should('be.visible')
  })

  it('deve exibir o formulário de devolução', () => {
    cy.visit('/estoquista/devolucao')
    cy.contains('Registrar Devolução').should('be.visible')
    cy.contains('Código de Devolução').should('be.visible')
    cy.get('input[placeholder*="código"]').should('be.visible')
    cy.contains('button', 'Confirmar Devolução').should('be.visible')
  })

  it('deve exibir erro ao registrar retirada com código inválido', () => {
    cy.visit('/estoquista/retirada')
    cy.get('input[placeholder*="código"]').type('CODIGOINVALIDO123')
    cy.contains('button', 'Confirmar Retirada').click()
    cy.contains(/inválido|não encontrado/i, { timeout: 8000 }).should('be.visible')
  })

  it('deve exibir erro ao registrar devolução com código inválido', () => {
    cy.visit('/estoquista/devolucao')
    cy.get('input[placeholder*="código"]').type('CODIGOINVALIDOD')
    cy.contains('button', 'Confirmar Devolução').click()
    cy.contains(/inválido|não encontrado/i, { timeout: 8000 }).should('be.visible')
  })

  it('deve aceitar apenas maiúsculas no campo de código', () => {
    cy.visit('/estoquista/retirada')
    cy.get('input[placeholder*="código"]').type('abcdef')
    cy.get('input[placeholder*="código"]').should('have.value', 'ABCDEF')
  })

  it('deve navegar entre retirada e devolução pelas abas', () => {
    cy.visit('/estoquista/retirada')
    cy.contains('Registrar Devolução').click()
    cy.url().should('include', '/estoquista/devolucao')
    cy.contains('Registrar Retirada').click()
    cy.url().should('include', '/estoquista/retirada')
  })

  it('deve bloquear acesso à área de admin', () => {
    cy.visit('/admin/dashboard')
    cy.url().should('include', '/catalogo')
  })
})
