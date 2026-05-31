Cypress.Commands.add('loginComoCliente', () => {
  cy.visit('/login')
  cy.get('input[placeholder*="e-mail"]').type('cliente@biblioteca.com')
  cy.get('input[type="password"]').type('cliente123')
  cy.get('button[type="submit"]').click()
  cy.url().should('include', '/cliente/historico')
})

Cypress.Commands.add('loginComoAdmin', () => {
  cy.visit('/login')
  cy.get('input[placeholder*="e-mail"]').type('admin@biblioteca.com')
  cy.get('input[type="password"]').type('admin123')
  cy.get('button[type="submit"]').click()
  cy.url().should('include', '/admin/dashboard')
})

Cypress.Commands.add('loginComoEstoquista', () => {
  cy.visit('/login')
  cy.get('input[placeholder*="e-mail"]').type('EST001')
  cy.get('input[type="password"]').type('estoque123')
  cy.get('button[type="submit"]').click()
  cy.url().should('include', '/estoquista/retirada')
})

Cypress.Commands.add('logout', () => {
  cy.contains('button', 'Sair').click()
  cy.url().should('include', '/login')
})
