-- =============================================================
-- SISTEMA DE GERENCIAMENTO DE BIBLIOTECA - IGNIS
-- PapiroTech | Versão 6.0
-- Migration V1 — Criação de todas as tabelas base
-- Executada UMA VEZ pelo Flyway. Nunca será repetida.
-- =============================================================

-- ─── tb_acl — perfis de acesso (enum PerfilAcesso) ───────────────────────────
CREATE TABLE IF NOT EXISTS tb_acl (
    id_acl              INT             NOT NULL AUTO_INCREMENT,
    des_acl             VARCHAR(50)     NOT NULL,
    PRIMARY KEY (id_acl),
    UNIQUE KEY uk_acl_des (des_acl)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_status_usuario — status do cliente (enum StatusCliente) ──────────────
CREATE TABLE IF NOT EXISTS tb_status_usuario (
    id_status_usuario   INT             NOT NULL AUTO_INCREMENT,
    des_status_usuario  VARCHAR(50)     NOT NULL,
    PRIMARY KEY (id_status_usuario),
    UNIQUE KEY uk_status_usuario_des (des_status_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_status_emprestimo — status do empréstimo (enum StatusEmprestimo) ─────
CREATE TABLE IF NOT EXISTS tb_status_emprestimo (
    id_status_emprestimo    INT             NOT NULL AUTO_INCREMENT,
    des_status_emprestimo   VARCHAR(100)    NOT NULL,
    PRIMARY KEY (id_status_emprestimo),
    UNIQUE KEY uk_status_emp_des (des_status_emprestimo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_categoria ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_categoria (
    id_categoria    INT             NOT NULL AUTO_INCREMENT,
    des_categoria   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_pessoa — superclasse abstrata (herança JOINED) ───────────────────────
CREATE TABLE IF NOT EXISTS tb_pessoa (
    id              INT             NOT NULL AUTO_INCREMENT,
    des_nome        VARCHAR(255)    NOT NULL,
    des_cpf         VARCHAR(255)    NOT NULL,
    dat_nascimento  DATE            NOT NULL,
    des_sexo        VARCHAR(50)     NOT NULL,
    des_email       VARCHAR(255)    NOT NULL,
    des_senha       VARCHAR(255)    NOT NULL,
    id_acl          INT             NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pessoa_email (des_email),
    CONSTRAINT fk_pessoa_acl FOREIGN KEY (id_acl) REFERENCES tb_acl (id_acl)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_usuario — subclasse Cliente (JOINED com tb_pessoa) ───────────────────
CREATE TABLE IF NOT EXISTS tb_usuario (
    id_usuario          INT     NOT NULL,
    id_status_usuario   INT     NOT NULL,
    PRIMARY KEY (id_usuario),
    CONSTRAINT fk_usuario_pessoa        FOREIGN KEY (id_usuario)        REFERENCES tb_pessoa (id),
    CONSTRAINT fk_usuario_status        FOREIGN KEY (id_status_usuario) REFERENCES tb_status_usuario (id_status_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_administrador — subclasse Administrador (JOINED com tb_pessoa) ───────
CREATE TABLE IF NOT EXISTS tb_administrador (
    id_administrador    INT             NOT NULL,
    des_cargo           VARCHAR(255),
    PRIMARY KEY (id_administrador),
    CONSTRAINT fk_admin_pessoa FOREIGN KEY (id_administrador) REFERENCES tb_pessoa (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_estoquista — subclasse Estoquista (JOINED com tb_pessoa) ─────────────
CREATE TABLE IF NOT EXISTS tb_estoquista (
    id_estoquista       INT             NOT NULL,
    des_codigo_acesso   VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id_estoquista),
    UNIQUE KEY uk_estoquista_codigo (des_codigo_acesso),
    CONSTRAINT fk_estoquista_pessoa FOREIGN KEY (id_estoquista) REFERENCES tb_pessoa (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_livro ────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_livro (
    id_livro                    INT             NOT NULL AUTO_INCREMENT,
    des_isbn                    VARCHAR(255)    NOT NULL,
    des_titulo                  VARCHAR(255)    NOT NULL,
    des_autor                   VARCHAR(255)    NOT NULL,
    des_editora                 VARCHAR(255)    NOT NULL,
    des_sinopse                 TEXT            NOT NULL,
    dat_cadastro                DATE            NOT NULL,
    dat_ano_publicacao          INT             NOT NULL,
    num_quantidade_total        INT             NOT NULL,
    num_quantidade_disponivel   INT             NOT NULL,
    id_categoria                INT             NOT NULL,
    PRIMARY KEY (id_livro),
    UNIQUE KEY uk_livro_isbn (des_isbn),
    CONSTRAINT fk_livro_categoria FOREIGN KEY (id_categoria) REFERENCES tb_categoria (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_emprestimo ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_emprestimo (
    id_emprestimo           INT             NOT NULL AUTO_INCREMENT,
    des_codigo_retirada     VARCHAR(255)    NOT NULL,
    des_codigo_devolucao    VARCHAR(255),
    dat_emprestimo          DATETIME        NOT NULL,
    dat_devolucao_prevista  DATE            NOT NULL,
    dat_devolucao_real      DATE,
    bit_penalidade_gerada   TINYINT(1)      NOT NULL DEFAULT 0,
    id_usuario              INT             NOT NULL,
    id_livro                INT             NOT NULL,
    id_status_emprestimo    INT             NOT NULL,
    PRIMARY KEY (id_emprestimo),
    UNIQUE KEY uk_emp_codigo_retirada (des_codigo_retirada),
    CONSTRAINT fk_emp_usuario   FOREIGN KEY (id_usuario)           REFERENCES tb_usuario (id_usuario),
    CONSTRAINT fk_emp_livro     FOREIGN KEY (id_livro)             REFERENCES tb_livro (id_livro),
    CONSTRAINT fk_emp_status    FOREIGN KEY (id_status_emprestimo) REFERENCES tb_status_emprestimo (id_status_emprestimo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── tb_favorito ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS tb_favorito (
    id_favorito INT NOT NULL AUTO_INCREMENT,
    id_usuario  INT NOT NULL,
    id_livro    INT NOT NULL,
    PRIMARY KEY (id_favorito),
    UNIQUE KEY uk_favorito (id_usuario, id_livro),
    CONSTRAINT fk_fav_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id_usuario),
    CONSTRAINT fk_fav_livro   FOREIGN KEY (id_livro)   REFERENCES tb_livro (id_livro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
