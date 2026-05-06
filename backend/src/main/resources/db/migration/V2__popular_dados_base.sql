-- =============================================================
-- Migration V2 — Dados base obrigatórios para o sistema funcionar
-- Executada UMA VEZ pelo Flyway. Nunca será repetida.
-- =============================================================

-- ─── tb_acl — enum PerfilAcesso ──────────────────────────────────────────────
INSERT IGNORE INTO tb_acl (des_acl) VALUES
    ('ADMINISTRADOR'),
    ('CLIENTE'),
    ('ESTOQUISTA');

-- ─── tb_status_usuario — enum StatusCliente ──────────────────────────────────
INSERT IGNORE INTO tb_status_usuario (des_status_usuario) VALUES
    ('ATIVO'),
    ('SUSPENDO');

-- ─── tb_status_emprestimo — enum StatusEmprestimo ────────────────────────────
INSERT IGNORE INTO tb_status_emprestimo (des_status_emprestimo) VALUES
    ('RESERVADO'),
    ('ATIVO'),
    ('ATRASADO'),
    ('EM_PROCESSO_DE_DEVOLUCAO'),
    ('EM_PROCESSO_DE_DEVOLUCAO_ATRASADO'),
    ('DEVOLVIDO'),
    ('DEVOLVIDO_COM_ATRASO'),
    ('CANCELADO');

-- ─── tb_categoria — categorias iniciais ──────────────────────────────────────
INSERT IGNORE INTO tb_categoria (des_categoria) VALUES
    ('Ficção Científica'),
    ('Romance'),
    ('Fantasia'),
    ('Suspense'),
    ('Terror'),
    ('Biografia'),
    ('História'),
    ('Tecnologia'),
    ('Autoajuda'),
    ('Infantil'),
    ('HQ / Graphic Novel'),
    ('Técnico');
