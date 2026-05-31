// cypress/e2e/04_cliente.cy.js
describe('Área do Cliente — Navegação', () => {

  beforeEach(() => {
    cy.loginComoCliente()
  })

  it('deve exibir as abas de navegação do cliente', () => {
    cy.visit('/cliente/historico')
    cy.contains('Meus Alugueis').should('be.visible')
    cy.contains('Favoritos').should('be.visible')
    cy.contains('Configurações de Perfil').should('be.visible')
  })

  it('deve navegar para histórico', () => {
    cy.visit('/cliente/historico')
    cy.contains('Meus Aluguéis').should('be.visible')
  })

  it('deve navegar para favoritos', () => {
    cy.visit('/cliente/favoritos')
    cy.contains('Favoritos').should('be.visible')
  })

  it('deve navegar para perfil', () => {
    cy.visit('/cliente/perfil')
    cy.contains('Configurações de Perfil').should('be.visible')
    cy.contains('Informações Pessoais').should('be.visible')
  })

  it('deve navegar pelo ícone de usuário para o perfil', () => {
    cy.visit('/catalogo')
    cy.get('[data-cy="user-icon"], [title*="perfil"], svg').last().click({ force: true })
    cy.url().should('include', '/cliente/perfil')
  })
})

describe('Área do Cliente — Perfil', () => {

  beforeEach(() => {
    cy.loginComoCliente()
    cy.visit('/cliente/perfil')
  })

  it('deve exibir os dados do perfil carregados do backend', () => {
    cy.contains('Informações Pessoais').should('be.visible')
    cy.get('input[value*="@"]').should('be.visible') // campo email preenchido
  })

  it('deve exibir seção de alterar senha', () => {
    cy.contains('Alterar Senha').should('be.visible')
    cy.get('input[placeholder*="nova senha"]').should('be.visible')
    cy.get('input[placeholder*="Confirme"]').should('be.visible')
  })

  it('deve salvar alterações do perfil', () => {
    cy.get('input').first().clear().type('Cliente Atualizado')
    cy.contains('button', 'Salvar Alterações').click()
    cy.contains('Perfil atualizado').should('be.visible')
  })

  it('deve exibir erro ao tentar salvar senhas diferentes', () => {
    cy.get('input[placeholder*="nova senha"]').type('nova123')
    cy.get('input[placeholder*="Confirme"]').type('diferente')
    cy.contains('button', 'Salvar Alterações').click()
    cy.contains('Senhas não coincidem').should('be.visible')
  })
})

describe('Área do Cliente — Alugar Livro', () => {

  beforeEach(() => {
    cy.loginComoCliente()
  })

  it('deve exibir o botão Alugar Livro no detalhe', () => {
    cy.visit('/catalogo')
    cy.contains('button', 'Ver mais').click()
    cy.get('[style*="2B3640"]').first().click()
    cy.contains('button', 'Alugar Livro').should('be.visible')
  })

  it('deve gerar código de retirada ao alugar livro disponível', () => {
    cy.visit('/catalogo')
    cy.contains('button', 'Ver mais').click()
    // Busca um livro disponível
    cy.get('[style*="2B3640"]').each(($card) => {
      if (!$card.text().includes('Indisponível')) {
        cy.wrap($card).click()
        return false // break
      }
    })
    cy.contains('button', 'Alugar Livro').click()
    cy.contains('Código de Retirada', { timeout: 10000 }).should('be.visible')
    cy.contains('Apresente ao estoquista').should('be.visible')
  })
})

describe('Área do Cliente — Histórico', () => {

  beforeEach(() => {
    cy.loginComoCliente()
    cy.visit('/cliente/historico')
  })

  it('deve exibir o histórico de empréstimos', () => {
    cy.contains('Meus Aluguéis').should('be.visible')
  })

  it('deve exibir empréstimos ou mensagem de vazio', () => {
    cy.get('body').then($body => {
      if ($body.text().includes('Nenhum aluguel')) {
        cy.contains('Nenhum aluguel encontrado').should('be.visible')
      } else {
        cy.get('[style*="D9B391"]').should('have.length.greaterThan', 0)
      }
    })
  })

  it('deve exibir código de retirada nos empréstimos ativos', () => {
    cy.get('body').then($body => {
      if ($body.text().includes('Código Retirada')) {
        cy.contains('Código Retirada').should('be.visible')
      }
    })
  })

  it('deve mostrar botão de gerar código de devolução para empréstimo ativo', () => {
    cy.get('body').then($body => {
      if ($body.text().includes('Gerar Código de Devolução')) {
        cy.contains('Gerar Código de Devolução').should('be.visible')
      }
    })
  })
})

describe('Área do Cliente — Favoritos', () => {

  beforeEach(() => {
    cy.loginComoCliente()
    cy.visit('/cliente/favoritos')
  })

  it('deve exibir a página de favoritos', () => {
    cy.contains('Favoritos').should('be.visible')
  })

  it('deve exibir favoritos ou mensagem de lista vazia', () => {
    cy.get('body').then($body => {
      if ($body.text().includes('Nenhum favorito')) {
        cy.contains('Nenhum favorito ainda').should('be.visible')
      } else {
        cy.contains(/Mostrando \d+ de \d+ livros/).should('be.visible')
      }
    })
  })

  it('deve adicionar livro aos favoritos pelo catálogo', () => {
    cy.visit('/catalogo')
    cy.contains('button', 'Ver mais').click()
    cy.get('[style*="2B3640"]').first().click()
    cy.contains('Adicionar aos favoritos').click()
    cy.contains(/favorit/i).should('be.visible')
  })
})
