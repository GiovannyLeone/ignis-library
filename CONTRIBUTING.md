# 🌿 Guia de Contribuição — IGNIS

## Fluxo de Branches

```
main
 └── develop
      ├── feature/rf01-cadastro-livro
      ├── feature/rf06-cadastro-cliente
      ├── fix/correcao-decremento-quantidade
      └── test/cenarios-emprestimo
```

## Convenção de Commits

Seguimos o padrão **Conventional Commits**:

```
tipo(escopo): descrição curta em português
```

### Tipos permitidos

| Tipo | Quando usar |
|---|---|
| `feat` | Nova funcionalidade / RF implementado |
| `fix` | Correção de bug |
| `test` | Adição ou atualização de testes |
| `docs` | Documentação |
| `refactor` | Refatoração sem mudança de comportamento |
| `chore` | Configurações, dependências, build |
| `db` | Alterações no banco de dados |

### Exemplos

```bash
feat(livro): implementa cadastro de livro com validação de ISBN
feat(emprestimo): adiciona geração de código de retirada
fix(emprestimo): corrige decremento de quantidade disponível
test(cliente): adiciona cenários T06 e T07 de CPF e email duplicado
docs: atualiza diagrama de sequência do fluxo de devolução
db: adiciona tabela tb_favorito
chore: configura conexão com banco MySQL
```

## Fluxo de Trabalho

1. Pegue uma issue no board
2. Crie sua branch a partir de `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/rf01-cadastro-livro
   ```
3. Desenvolva e faça commits seguindo a convenção
4. Abra um Pull Request para `develop`
5. Aguarde revisão de pelo menos 1 colega
6. Após aprovação, faça o merge

## Proteção de Branches

- `main` → exige PR + 1 aprovação
- `develop` → exige PR

## ⚠️ Regras Importantes

- **Nunca** commitar arquivos `.env`
- **Nunca** fazer push direto em `main` ou `develop`
- Sempre referenciar a issue no PR (`Closes #numero`)
- Manter commits pequenos e com propósito único
