// cypress/e2e/06_admin.cy.js
describe('Admin — Dashboard', () => {

  beforeEach(() => {
    cy.loginComoAdmin()
  })

  it('deve exibir o painel administrativo', () => {
    cy.contains('Painel Administrativo').should('be.visible')
  })

  it('deve exibir cards de estatísticas', () => {
    cy.contains('Total de Livros').should('be.visible')
    cy.contains('Empréstimos').should('be.visible')
    cy.contains('Clientes').should('be.visible')
  })

  it('deve exibir os números das estatísticas', () => {
    cy.get('[style*="Raleway"]').contains(/^\d+$/).should('exist')
  })

  it('deve navegar pelas abas do admin', () => {
    cy.contains('📚 Livros').click()
    cy.url().should('include', '/admin/livros')

    cy.contains('👥 Clientes').click()
    cy.url().should('include', '/admin/clientes')

    cy.contains('📋 Empréstimos').click()
    cy.url().should('include', '/admin/emprestimos')

    cy.contains('📊 Dashboard').click()
    cy.url().should('include', '/admin/dashboard')
  })
})

describe('Admin — Gerenciar Livros', () => {

  beforeEach(() => {
    cy.loginComoAdmin()
    cy.visit('/admin/livros')
  })

  it('deve exibir a tabela de livros', () => {
    cy.contains('Gerenciar Livros').should('be.visible')
    cy.contains('Adicionar Livro').should('be.visible')
    cy.contains('th', 'Título').should('be.visible')
    cy.contains('th', 'Autor').should('be.visible')
    cy.contains('th', 'Ações').should('be.visible')
  })

  it('deve abrir modal de novo livro', () => {
    cy.contains('button', 'Adicionar Livro').click()
    cy.contains('Cadastrar Novo Livro').should('be.visible')
    cy.get('input[placeholder*="ISBN"]').should('be.visible')
    cy.get('input[placeholder*="Título"]').should('not.exist') // usa label, não placeholder
  })

  it('deve fechar modal ao cancelar', () => {
    cy.contains('button', 'Adicionar Livro').click()
    cy.contains('button', 'Cancelar').click()
    cy.contains('Cadastrar Novo Livro').should('not.exist')
  })

  it('deve buscar livros pelo campo de busca', () => {
    cy.get('input[placeholder*="Buscar"]').type('Clean Code')
    cy.contains('button', 'Buscar').click()
    cy.contains('Clean Code').should('be.visible')
  })

  it('deve limpar busca', () => {
    cy.get('input[placeholder*="Buscar"]').type('teste')
    cy.contains('button', 'Buscar').click()
    cy.get('body').then($body => {
      if ($body.find('button:contains("Limpar")').length) {
        cy.contains('button', 'Limpar').click()
        cy.get('input[placeholder*="Buscar"]').should('have.value', '')
      }
    })
  })

  it('deve abrir modal de edição ao clicar em Editar', () => {
    cy.contains('button', 'Editar').first().click()
    cy.contains('Editar:').should('be.visible')
    cy.contains('button', 'Salvar').should('be.visible')
  })

  it('deve cadastrar um novo livro', () => {
    cy.contains('button', 'Adicionar Livro').click()
    cy.get('input').eq(0).type('9780000000001') // ISBN
    cy.get('input').eq(1).type('Livro de Teste Cypress')
    cy.get('input').eq(2).type('Autor Teste')
    cy.get('input').eq(3).type('1')
    cy.get('input').eq(4).type('Editora Teste')
    cy.get('input').eq(5).type('2024')
    cy.get('input').eq(6).type('3')
    cy.get('textarea').type('Sinopse do livro de teste criado pelo Cypress.')
    cy.contains('button', 'Cadastrar').click()
    cy.contains('Livro cadastrado').should('be.visible')
  })
})

describe('Admin — Gerenciar Clientes', () => {

  beforeEach(() => {
    cy.loginComoAdmin()
    cy.visit('/admin/clientes')
  })

  it('deve exibir a lista de clientes', () => {
    cy.contains('Gerenciar Clientes').should('be.visible')
  })

  it('deve exibir clientes com nome, email e status', () => {
    cy.get('body').then($body => {
      if (!$body.text().includes('Nenhum usuário')) {
        cy.contains('@').should('be.visible') // email visível
        cy.contains(/ATIVO|BLOQUEADO/).should('be.visible')
      }
    })
  })

  it('deve exibir filtros de busca', () => {
    cy.contains('Nome').should('be.visible')
    cy.contains('Status').should('be.visible')
  })
})

describe('Admin — Gerenciar Empréstimos', () => {

  beforeEach(() => {
    cy.loginComoAdmin()
    cy.visit('/admin/emprestimos')
  })

  it('deve exibir a lista de empréstimos', () => {
    cy.contains('Gerenciar Empréstimos').should('be.visible')
  })

  it('deve exibir filtros de busca', () => {
    cy.contains('Título do Livro').should('be.visible')
    cy.contains('Nome do Cliente').should('be.visible')
  })

  it('deve exibir empréstimos com status e ações', () => {
    cy.get('body').then($body => {
      if (!$body.text().includes('Nenhum empréstimo')) {
        cy.contains(/Ativo|Reservado|Devolvido|Atrasado/).should('be.visible')
      }
    })
  })

  it('deve bloquear acesso à área de cliente', () => {
    cy.visit('/cliente/historico')
    cy.url().should('include', '/catalogo')
  })
})
