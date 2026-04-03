-- ============================================================
-- IGNIS — Sistema de Gerenciamento de Biblioteca
-- Script 02: Dados Iniciais
-- PapiroTech | Versão 6.0
-- ============================================================

USE ignis_library;

-- ------------------------------------------------------------
-- Perfis de acesso (ACL)
-- ------------------------------------------------------------
INSERT INTO tb_acl (des_acl) VALUES
  ('ADMINISTRADOR'),
  ('CLIENTE');

-- ------------------------------------------------------------
-- Status de usuário
-- ------------------------------------------------------------
INSERT INTO tb_status_usuario (des_status_usuario) VALUES
  ('ATIVO'),
  ('BLOQUEADO');

-- ------------------------------------------------------------
-- Status de empréstimo
-- ------------------------------------------------------------
INSERT INTO tb_status_emprestimo (des_status_emprestimo) VALUES
  ('ATIVO'),
  ('DEVOLVIDO'),
  ('DEVOLVIDO COM ATRASO'),
  ('ATRASADO'),
  ('CANCELADO'),
  ('RESERVADO'),
  ('EM PROCESSO DE DEVOLUÇÃO'),
  ('EM PROCESSO DE DEVOLUÇÃO ATRASADO');

-- ------------------------------------------------------------
-- Status de exemplar
-- ------------------------------------------------------------
INSERT INTO tb_status_exemplar (des_stats) VALUES
  ('DISPONIVEL'),
  ('EMPRESTADO'),
  ('RESERVADO'),
  ('DANIFICADO');

-- ------------------------------------------------------------
-- Categorias de livros
-- ------------------------------------------------------------
INSERT INTO tb_categoria (des_categoria) VALUES
  ('Ficção Científica'),
  ('Romance'),
  ('Técnico'),
  ('História'),
  ('Fantasia'),
  ('Biografia'),
  ('Autoajuda'),
  ('Terror'),
  ('Infantil'),
  ('Quadrinhos');

-- ------------------------------------------------------------
-- Administrador padrão do sistema
-- Senha: admin123 (hash bcrypt — trocar em produção)
-- ------------------------------------------------------------
INSERT INTO tb_usuario (
  des_nome, des_email, dat_nascimento,
  des_senha, des_cpf, des_sexo,
  id_status_usuario, id_acl
) VALUES (
  'Administrador do Sistema',
  'admin@ignis.com',
  '1990-01-01',
  '$2a$10$EXEMPLO_HASH_BCRYPT_TROCAR_EM_PRODUCAO',
  '$2a$10$EXEMPLO_CPF_HASH_BCRYPT',
  'Não informado',
  1, -- ATIVO
  1  -- ADMINISTRADOR
);

-- ------------------------------------------------------------
-- Estoquista padrão
-- Senha: estoque123 (hash bcrypt — trocar em produção)
-- ------------------------------------------------------------
INSERT INTO tb_estoquista (des_codigo_acesso, des_senha) VALUES
  ('EST001', '$2a$10$EXEMPLO_HASH_BCRYPT_TROCAR_EM_PRODUCAO');
