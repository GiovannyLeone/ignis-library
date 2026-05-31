# 🔥 IGNIS — Sistema de Gerenciamento de Biblioteca

> Sistema web completo para gerenciamento de biblioteca com controle de acervo, empréstimos, devoluções e penalidades.

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

O **IGNIS** é uma aplicação web fullstack que gerencia de forma eficiente o acervo literário, o cadastro de clientes e as operações de empréstimo e devolução de uma biblioteca. O sistema atende três perfis de usuário — **Administrador**, **Estoquista** e **Cliente** — com permissões controladas via JWT e ACL.

### Funcionalidades principais

- 📚 Cadastro e gerenciamento de acervo de livros com categorias
- 👤 Cadastro e gerenciamento de clientes com status ATIVO/BLOQUEADO
- 🔄 Fluxo completo de empréstimo e devolução via código único
- ⚠️ Aplicação e remoção de penalidades por atraso
- ❤️ Favoritar e desfavoritar livros do catálogo
- 📊 Histórico de empréstimos por cliente
- 🔒 Autenticação JWT com redirecionamento por perfil
- 🖼️ Capas de livros via Open Library e Google Books API

---

## 🚀 Tecnologias

### Backend
- **Java 21** + **Spring Boot 3.2.3**
- **Spring Security** + **JWT** (jjwt 0.12.5)
- **JPA/Hibernate** — herança SINGLE_TABLE
- **MySQL 5.7+**
- **Lombok**, **SpringDoc OpenAPI**

### Frontend
- **React 18** + **Vite 5**
- **React Router v6**
- **Axios** com interceptors
- **Lucide React** (ícones)
- **Fonte Raleway** (Google Fonts)

### Testes
- **JUnit 5** + **Mockito** (testes unitários)
- **H2** in-memory (testes de integração)
- **MockMvc** (testes de controller)
- **Cypress** (testes E2E)
- **Postman** (testes de API)

---

## 🏗️ Arquitetura

```
ignis-library/
├── backend/                  # Java 21 + Spring Boot
│   └── src/main/java/.../
│       ├── config/           # Security, JWT, CORS, Mapper
│       ├── controller/       # REST endpoints
│       ├── service/          # Regras de negócio
│       ├── entity/           # Entidades JPA
│       ├── repository/       # Spring Data JPA
│       ├── dto/              # Request/Response DTOs
│       ├── enums/            # StatusCliente, StatusEmprestimo
│       └── exception/        # GlobalExceptionHandler
│
└── frontend/                 # React + Vite
    └── src/
        ├── context/          # AuthContext (JWT)
        ├── services/         # api.js (Axios)
        ├── utils/            # dateUtils (timezone BR)
        ├── components/       # Navbar, LivroCard, CapaLivro, Toast
        └── pages/
            ├── public/       # Login, Cadastro, Catálogo, Detalhe
            ├── cliente/      # Histórico, Favoritos, Perfil
            ├── estoquista/   # Retirada, Devolução
            └── admin/        # Dashboard, Livros, Clientes, Empréstimos
```

---

## 👥 Perfis de Usuário

| Perfil | Acesso | Login padrão |
|---|---|---|
| **CLIENTE** | Catálogo, empréstimos, favoritos, perfil | `cliente@biblioteca.com` |
| **ESTOQUISTA** | Registrar retirada e devolução via código | `EST001` |
| **ADMINISTRADOR** | Dashboard, livros, clientes, empréstimos, penalidades | `admin@biblioteca.com` |

> Cada perfil é redirecionado para sua área exclusiva após o login. Acesso cruzado entre perfis é bloqueado.

---

## 🔄 Fluxo de Empréstimo

```
Cliente reserva livro
        ↓
Recebe código de retirada (16 chars)
        ↓
Estoquista registra retirada via código → status: ATIVO
        ↓
Cliente gera código de devolução (17 chars, termina em D)
        ↓
Estoquista registra devolução via código → status: DEVOLVIDO
        ↓
Admin aplica penalidade se houver atraso → Cliente: BLOQUEADO
```

---

## ⚙️ Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **MySQL 5.7+**
- **Node.js 18+**

---

## 🛠️ Instalação e Execução

### 1. Clone o repositório
```bash
git clone https://github.com/GiovannyLeone/ignis-library.git
cd ignis-library
```

### 2. Configure o banco de dados

Crie o banco e execute o script de inicialização:
```sql
CREATE DATABASE ignis_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

O schema é criado automaticamente pelo `DatabaseInitializer` ao subir o backend.

### 3. Configure o backend

Edite `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ignis_library
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.properties.hibernate.jdbc.time_zone=America/Sao_Paulo
spring.jackson.time-zone=America/Sao_Paulo
```

### 4. Inicie o backend
```bash
cd backend
mvn spring-boot:run
# API disponível em http://localhost:8080
# Swagger UI em http://localhost:8080/swagger-ui/index.html
```

### 5. Configure o frontend

Crie o arquivo `frontend/.env`:
```env
VITE_API_URL=http://localhost:8080
```

### 6. Inicie o frontend
```bash
cd frontend
npm install
npm run dev
# Aplicação disponível em http://localhost:3000
```

---

## 🧪 Testes

### Backend — JUnit + Mockito
```bash
cd backend
mvn test
# 61 testes | 0 falhas
```

| Classe | Tipo | Testes |
|---|---|---|
| LivroServiceTest | Unitário | 9 |
| UsuarioServiceTest | Unitário | 5 |
| EmprestimoServiceTest | Unitário | 16 |
| FavoritoServiceTest | Unitário | 7 |
| EmprestimoFluxoIntegrationTest | Integração | 9 |
| PenalidadeFavoritoIntegrationTest | Integração | 7 |
| LivroControllerTest | Controller | 8 |

### Frontend — Cypress E2E
```bash
cd frontend
npm install

# Interface visual (recomendado)
npx cypress open

# Linha de comando
npx cypress run
```

> Requer backend e frontend rodando antes de executar os testes Cypress.

**84 casos de teste** cobrindo autenticação, catálogo, cliente, estoquista, admin e fluxo E2E completo.

### API — Postman

Importe o arquivo `docs/PapiroTech_IGNIS.postman_collection.json` no Postman.

**34 testes** cobrindo todos os endpoints com validação automática de status codes e respostas.

---

## 📡 Endpoints Principais

| Método | Endpoint | Perfil | Descrição |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Login (retorna JWT) |
| POST | `/api/clientes/cadastro` | Público | Cadastrar cliente |
| GET | `/api/livros` | Público | Listar livros |
| GET | `/api/livros/categorias` | Público | Listar categorias |
| POST | `/api/emprestimos/livros/{id}/reservar` | Cliente | Reservar livro |
| POST | `/api/emprestimos/{id}/gerar-codigo-devolucao` | Cliente | Gerar código devolução |
| GET | `/api/emprestimos/meu-historico` | Cliente | Histórico do cliente |
| POST | `/api/estoque/retirada/{codigo}` | Estoquista | Registrar retirada |
| POST | `/api/estoque/devolucao/{codigo}` | Estoquista | Registrar devolução |
| GET | `/api/admin/emprestimos` | Admin | Listar todos empréstimos |
| POST | `/api/admin/emprestimos/{id}/penalidade` | Admin | Aplicar penalidade |
| DELETE | `/api/admin/emprestimos/{id}/penalidade` | Admin | Remover penalidade |

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

## 📁 Documentação

A documentação técnica completa está disponível na pasta `/docs`, incluindo:

- Especificação de Requisitos (RF01–RF16)
- Diagrama de Casos de Uso
- Diagrama de Classes
- Plano de Testes (84 casos de teste)
- Diagramas de Sequência
- Modelagem do Banco de Dados
- Collection Postman

---

## 📝 Licença

Este projeto foi desenvolvido para fins acadêmicos pela equipe **PapiroTech — IGNIS**.