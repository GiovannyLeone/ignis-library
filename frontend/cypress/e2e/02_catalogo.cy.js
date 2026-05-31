// cypress/e2e/02_catalogo.cy.js
describe('Catálogo — Público', () => {

  beforeEach(() => {
    cy.loginComoCliente()
    cy.visit('/catalogo')
  })

  it('deve exibir a home com banner e livros recentes', () => {
    cy.contains('Sua jornada Pelo Conhecimento Começa Aqui').should('be.visible')
    cy.contains('Recentemente adicionados').should('be.visible')
    cy.contains('Filtros').should('be.visible')
  })

  it('deve exibir o total de livros corretamente', () => {
    cy.contains(/Mostrando \d+ de \d+ livros/).should('be.visible')
  })

  it('deve exibir cards de livros com título, categoria e autor', () => {
    cy.get('[style*="2B3640"]').first().within(() => {
      cy.get('p').should('have.length.greaterThan', 0)
    })
  })

  it('deve ir para o catálogo completo ao clicar em Ver mais', () => {
    cy.contains('button', 'Ver mais').click()
    cy.contains('Catálogo Completo').should('be.visible')
    cy.contains('Catálogo').should('be.visible')
  })

  it('deve buscar livros pelo título', () => {
    cy.get('input[placeholder*="título"]').type('Clean Code')
    cy.contains('button', 'Buscar').click()
    cy.contains('Clean Code').should('be.visible')
  })

  it('deve buscar livros pelo autor', () => {
    cy.get('input[placeholder*="autor"]').type('Stephen King')
    cy.contains('button', 'Buscar').click()
    cy.contains('Stephen King').should('be.visible')
  })

  it('deve limpar filtros corretamente', () => {
    cy.get('input[placeholder*="título"]').type('Clean Code')
    cy.contains('button', 'Limpar filtros').click()
    cy.get('input[placeholder*="título"]').should('have.value', '')
    cy.contains('Recentemente adicionados').should('be.visible')
  })

  it('deve navegar para detalhe ao clicar no livro', () => {
    cy.contains('button', 'Ver mais').click()
    cy.get('[style*="2B3640"]').first().click()
    cy.url().should('include', '/livros/')
    cy.contains('Voltar ao Catálogo').should('be.visible')
  })

  it('deve exibir o botão Ver mais e carregar mais livros no catálogo', () => {
    cy.contains('button', 'Ver mais').click()
    cy.contains('Catálogo Completo').should('be.visible')
    cy.contains(/Mostrando \d+ de \d+ livros/).then($el => {
      const textoAntes = $el.text()
      cy.contains('button', 'Ver mais').then($btn => {
        if ($btn.length) {
          cy.contains('button', 'Ver mais').click()
          cy.contains(/Mostrando \d+ de \d+ livros/).should($el2 => {
            expect($el2.text()).not.to.equal(textoAntes)
          })
        }
      })
    })
  })
})

describe('Detalhe do Livro — Público', () => {

  beforeEach(() => {
    cy.visit('/catalogo')
    cy.contains('button', 'Ver mais').click()
    cy.get('[style*="2B3640"]').first().click()
  })

  it('deve exibir informações do livro', () => {
    cy.contains('Categoria').should('be.visible')
    cy.contains('Editora').should('be.visible')
    cy.contains('Cópias Disponíveis').should('be.visible')
    cy.contains('Período de Aluguel').should('be.visible')
    cy.contains('7').should('be.visible')
    cy.contains('dias').should('be.visible')
    cy.contains('Aluguel Gratuito').should('be.visible')
  })

  it('deve exibir botão Alugar Livro', () => {
    cy.contains('button', 'Alugar Livro').should('be.visible')
  })

  it('deve exibir Adicionar aos favoritos', () => {
    cy.contains('Adicionar aos favoritos').should('be.visible')
  })

  it('deve redirecionar para login ao tentar alugar sem estar logado', () => {
    cy.contains('button', 'Alugar Livro').click()
    cy.url().should('include', '/login')
  })

  it('deve voltar ao catálogo ao clicar em Voltar', () => {
    cy.contains('Voltar ao Catálogo').click()
    cy.url().should('include', '/catalogo')
  })
})
