-- ============================================================
-- IGNIS — Sistema de Gerenciamento de Biblioteca
-- Script 01: Criação das Tabelas
-- PapiroTech | Versão 6.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS ignis_library
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE ignis_library;

-- ------------------------------------------------------------
-- tb_acl — Perfis de acesso (ACL)
-- ------------------------------------------------------------
CREATE TABLE tb_acl (
  id_acl  INT          NOT NULL AUTO_INCREMENT,
  des_acl CHAR(255)    NOT NULL,
  PRIMARY KEY (id_acl)
);

-- ------------------------------------------------------------
-- tb_status_usuario — Status possíveis do usuário
-- ------------------------------------------------------------
CREATE TABLE tb_status_usuario (
  id_status_usuario  INT          NOT NULL AUTO_INCREMENT,
  des_status_usuario VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_status_usuario)
);

-- ------------------------------------------------------------
-- tb_usuario — Usuários do sistema (Admin e Cliente)
-- ------------------------------------------------------------
CREATE TABLE tb_usuario (
  id_usuario        INT          NOT NULL AUTO_INCREMENT,
  des_nome          VARCHAR(255) NOT NULL,
  des_email         VARCHAR(255) NOT NULL,
  dat_nascimento    DATE         NOT NULL,
  des_senha         VARCHAR(255) NOT NULL,
  des_cpf           VARCHAR(255) NOT NULL,
  des_sexo          VARCHAR(255) NOT NULL,
  id_status_usuario INT          NOT NULL,
  id_acl            INT          NOT NULL,
  PRIMARY KEY (id_usuario),
  CONSTRAINT fk_usuario_status FOREIGN KEY (id_status_usuario)
    REFERENCES tb_status_usuario (id_status_usuario),
  CONSTRAINT fk_usuario_acl FOREIGN KEY (id_acl)
    REFERENCES tb_acl (id_acl)
);

-- ------------------------------------------------------------
-- tb_categoria — Categorias dos livros
-- ------------------------------------------------------------
CREATE TABLE tb_categoria (
  id_categoria  INT          NOT NULL AUTO_INCREMENT,
  des_categoria VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_categoria)
);

-- ------------------------------------------------------------
-- tb_livro — Acervo de livros
-- ------------------------------------------------------------
CREATE TABLE tb_livro (
  id_livro                 INT          NOT NULL AUTO_INCREMENT,
  des_isbn                 VARCHAR(255) NOT NULL UNIQUE,
  des_titulo               VARCHAR(255) NOT NULL,
  des_autor                VARCHAR(255) NOT NULL,
  des_editora              VARCHAR(255) NOT NULL,
  des_sinopse              VARCHAR(255) NOT NULL,
  dat_cadastro             DATE         NOT NULL,
  dat_ano_publicacao       YEAR         NOT NULL,
  num_quantidade_total     INT          NOT NULL,
  num_quantidade_disponivel INT         NOT NULL,
  id_categoria             INT          NOT NULL,
  PRIMARY KEY (id_livro),
  CONSTRAINT fk_livro_categoria FOREIGN KEY (id_categoria)
    REFERENCES tb_categoria (id_categoria)
);

-- ------------------------------------------------------------
-- tb_status_emprestimo — Status possíveis do empréstimo
-- ------------------------------------------------------------
CREATE TABLE tb_status_emprestimo (
  id_status_emprestimo  INT          NOT NULL AUTO_INCREMENT,
  des_status_emprestimo VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_status_emprestimo)
);

-- ------------------------------------------------------------
-- tb_emprestimo — Registro de empréstimos
-- ------------------------------------------------------------
CREATE TABLE tb_emprestimo (
  id_emprestimo          INT          NOT NULL AUTO_INCREMENT,
  des_codigo_retirada    VARCHAR(255) NOT NULL UNIQUE,
  des_codigo_devolucao   VARCHAR(255) NOT NULL,
  dat_emprestimo         DATETIME     NOT NULL,
  dat_devolucao_prevista DATE         NOT NULL,
  dat_devolucao_real     DATE         NOT NULL,
  bit_penalidade_gerada  TINYINT      NOT NULL DEFAULT 0,
  id_usuario             INT          NOT NULL,
  id_livro               INT          NOT NULL,
  id_status_emprestimo   INT          NOT NULL,
  PRIMARY KEY (id_emprestimo),
  CONSTRAINT fk_emprestimo_usuario FOREIGN KEY (id_usuario)
    REFERENCES tb_usuario (id_usuario),
  CONSTRAINT fk_emprestimo_livro FOREIGN KEY (id_livro)
    REFERENCES tb_livro (id_livro),
  CONSTRAINT fk_emprestimo_status FOREIGN KEY (id_status_emprestimo)
    REFERENCES tb_status_emprestimo (id_status_emprestimo)
);

-- ------------------------------------------------------------
-- tb_favorito — Livros favoritados por clientes (N:M)
-- ------------------------------------------------------------
CREATE TABLE tb_favorito (
  id_favorito INT NOT NULL AUTO_INCREMENT,
  id_usuario  INT NOT NULL,
  id_livro    INT NOT NULL,
  PRIMARY KEY (id_favorito),
  CONSTRAINT fk_favorito_usuario FOREIGN KEY (id_usuario)
    REFERENCES tb_usuario (id_usuario),
  CONSTRAINT fk_favorito_livro FOREIGN KEY (id_livro)
    REFERENCES tb_livro (id_livro)
);

-- ------------------------------------------------------------
-- tb_estoquista — Usuários estoquistas (acesso independente)
-- ------------------------------------------------------------
CREATE TABLE tb_estoquista (
  id_estoquista     INT          NOT NULL AUTO_INCREMENT,
  des_codigo_acesso VARCHAR(255) NOT NULL,
  des_senha         VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_estoquista)
);

-- ------------------------------------------------------------
-- tb_status_exemplar — Status dos exemplares físicos
-- ------------------------------------------------------------
CREATE TABLE tb_status_exemplar (
  id_status_exemplar INT         NOT NULL AUTO_INCREMENT,
  des_stats          VARCHAR(45) NULL,
  PRIMARY KEY (id_status_exemplar)
);

-- ------------------------------------------------------------
-- tb_exemplar — Exemplares físicos de cada livro
-- ------------------------------------------------------------
CREATE TABLE tb_exemplar (
  id_exemplar INT NOT NULL AUTO_INCREMENT,
  id_livro    INT NOT NULL,
  id_status   INT NOT NULL,
  PRIMARY KEY (id_exemplar),
  CONSTRAINT fk_exemplar_livro FOREIGN KEY (id_livro)
    REFERENCES tb_livro (id_livro),
  CONSTRAINT fk_exemplar_status FOREIGN KEY (id_status)
    REFERENCES tb_status_exemplar (id_status_exemplar)
);
