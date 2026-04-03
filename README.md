# 🔥 IGNIS — Sistema de Gerenciamento de Biblioteca

> Sistema web responsivo para gerenciamento de biblioteca com controle de acervo, empréstimos, devoluções e penalidades.

<p align="center">
  <img src="docs/ignis-logo.png" alt="IGNIS Logo" width="200"/>
</p>

---

## 👥 Equipe — PapiroTech

| Função | Nome | RA |
|---|---|---|
| Tech Lead | Giovanny Nepomuceno Leone Barbosa | 2402464 |
| Eng. de Software | Guilherme Ribeiro de Godoy Mendonça | 2403880 |
| Eng. de Software | Guilherme Rauseo Luz | 2402131 |
| Eng. de Software | Marcela Conceição da Silva | 2402932 |

---

## 📋 Sobre o Projeto

O **IGNIS** é uma aplicação web que gerencia de forma eficiente o acervo literário, o cadastro de clientes e as operações de empréstimo e devolução de uma biblioteca. O sistema atende três perfis de usuário — **Administrador**, **Estoquista** e **Cliente** — com permissões controladas via ACL.

### Funcionalidades principais
- 📚 Cadastro e gerenciamento de acervo de livros
- 👤 Cadastro e gerenciamento de clientes
- 🔄 Fluxo completo de empréstimo e devolução via código
- ⚠️ Aplicação e remoção de penalidades por atraso
- ❤️ Favoritar livros do catálogo
- 📊 Histórico de empréstimos por cliente

---

## 🚀 Tecnologias

- **Frontend:** Web responsivo (HTML, CSS, JavaScript)
- **Backend:** Java
- **Banco de Dados:** MySQL
- **Segurança:** bcrypt (senhas e CPFs criptografados)
- **Controle de Acesso:** ACL (Access Control List)

---

## ⚙️ Pré-requisitos

- Java 17+
- MySQL 8+
- Node.js (para o frontend)
- Git

---

## 🛠️ Instalação e Configuração

### 1. Clone o repositório
```bash
git clone https://github.com/papirotech/ignis-library.git
cd ignis-library
```

### 2. Configure o banco de dados
```bash
cd backend
# Execute os scripts SQL na ordem:
mysql -u root -p < docs/sql/01_create_tables.sql
mysql -u root -p < docs/sql/02_insert_initial_data.sql
```

### 3. Configure as variáveis de ambiente
```bash
cp backend/.env.example backend/.env
# Edite o arquivo .env com suas credenciais
```

### 4. Inicie o backend
```bash
cd backend
# instruções de build e execução
```

### 5. Inicie o frontend
```bash
cd frontend
# instruções de execução
```

---

## 🌿 Branches

| Branch | Descrição |
|---|---|
| `main` | Código estável / produção |
| `develop` | Integração contínua |
| `feature/xxx` | Novas funcionalidades |
| `fix/xxx` | Correções de bugs |
| `hotfix/xxx` | Correções urgentes em produção |

---

## 🧪 Testes

```bash
# Rodar todos os testes
# instruções de teste
```

O projeto conta com **27 cenários de teste** documentados, cobrindo cadastro de livros, clientes, fluxo de empréstimo, devolução, penalidades e favoritos.

---

## 📁 Estrutura do Projeto

```
ignis-library/
├── backend/          # Código do servidor
├── frontend/         # Interface web
├── docs/             # Documentação técnica e scripts SQL
│   └── sql/          # Scripts de criação do banco de dados
└── .github/          # Templates de PR e Issues
```

---

## 📄 Documentação

A documentação técnica completa (v6.0) está disponível na pasta `/docs`, incluindo:
- Especificação de Requisitos
- Diagrama de Casos de Uso
- Diagrama de Classes
- Plano de Testes
- Diagramas de Sequência
- Modelagem do Banco de Dados

---

## 📝 Licença

Este projeto foi desenvolvido para fins acadêmicos pela equipe **Ignis**.
