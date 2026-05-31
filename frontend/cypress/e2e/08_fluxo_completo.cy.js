// cypress/e2e/08_fluxo_completo.cy.js
// Testa o fluxo completo: Cliente reserva → Estoquista retira → Cliente gera devolução → Estoquista devolve

describe('Fluxo Completo de Empréstimo', () => {

  let codigoRetirada = ''
  let codigoDevolucao = ''

  it('PASSO 1: Cliente reserva um livro e obtém código de retirada', () => {
    cy.loginComoCliente()
    cy.visit('/catalogo')
    cy.contains('button', 'Ver mais').click()

    // Clica no primeiro livro disponível
    cy.get('[style*="2B3640"]').each(($card) => {
      if (!$card.text().includes('Indisponível')) {
        cy.wrap($card).click()
        return false
      }
    })

    cy.contains('button', 'Alugar Livro').click()
    cy.contains('Código de Retirada', { timeout: 10000 }).should('be.visible')

    // Captura o código gerado
    cy.get('[style*="monospace"]').first().invoke('text').then(texto => {
      codigoRetirada = texto.trim()
      cy.log(`Código de retirada: ${codigoRetirada}`)
      expect(codigoRetirada).to.have.length(16)

      // Salva para uso nos próximos testes
      cy.writeFile('cypress/fixtures/codigo_retirada.txt', codigoRetirada)
    })
  })

  it('PASSO 2: Cliente verifica código no histórico', () => {
    cy.loginComoCliente()
    cy.visit('/cliente/historico')
    cy.contains('Meus Aluguéis').should('be.visible')
    cy.contains(/Reservado|RESERVADO/).should('be.visible')
    cy.contains('Código Retirada').should('be.visible')
  })

  it('PASSO 3: Estoquista registra a retirada com o código', () => {
    cy.readFile('cypress/fixtures/codigo_retirada.txt').then(codigo => {
      cy.loginComoEstoquista()
      cy.visit('/estoquista/retirada')
      cy.get('input[placeholder*="código"]').type(codigo)
      cy.contains('button', 'Confirmar Retirada').click()
      cy.contains('Retirada Confirmada', { timeout: 10000 }).should('be.visible')
      cy.contains('Cliente').should('be.visible')
      cy.contains('Devolução Prevista').should('be.visible')
    })
  })

  it('PASSO 4: Cliente gera código de devolução', () => {
    cy.loginComoCliente()
    cy.visit('/cliente/historico')
    cy.contains(/Ativo|ATIVO/).should('be.visible')
    cy.contains('button', /Gerar Código de Devolução/).click()
    cy.contains('Código Devolução', { timeout: 10000 }).should('be.visible')

    // Captura o código de devolução
    cy.get('[style*="A65A49"][style*="monospace"]').first().invoke('text').then(texto => {
      codigoDevolucao = texto.trim()
      cy.log(`Código de devolução: ${codigoDevolucao}`)
      expect(codigoDevolucao).to.have.length(17)
      expect(codigoDevolucao).to.match(/D$/) // deve terminar em D

      cy.writeFile('cypress/fixtures/codigo_devolucao.txt', codigoDevolucao)
    })
  })

  it('PASSO 5: Estoquista registra a devolução com o código', () => {
    cy.readFile('cypress/fixtures/codigo_devolucao.txt').then(codigo => {
      cy.loginComoEstoquista()
      cy.visit('/estoquista/devolucao')
      cy.get('input[placeholder*="código"]').type(codigo)
      cy.contains('button', 'Confirmar Devolução').click()
      cy.contains(/Devolução Confirmada|Devolução com Atraso/, { timeout: 10000 }).should('be.visible')
    })
  })

  it('PASSO 6: Admin verifica empréstimo como devolvido', () => {
    cy.loginComoAdmin()
    cy.visit('/admin/emprestimos')
    cy.contains(/Devolvido/).should('be.visible')
  })
})
