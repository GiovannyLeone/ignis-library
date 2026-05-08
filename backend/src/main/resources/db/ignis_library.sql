CREATE TABLE IF NOT EXISTS tb_status_usuario (
    id_status_usuario  INT NOT NULL AUTO_INCREMENT,
    des_status_usuario VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_status_usuario),
    UNIQUE KEY uk_status_usuario (des_status_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_acl (
    id_acl  INT NOT NULL AUTO_INCREMENT,
    des_acl CHAR(255) NOT NULL,
    PRIMARY KEY (id_acl),
    UNIQUE KEY uk_acl (des_acl)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_usuario (
    id_usuario        INT NOT NULL AUTO_INCREMENT,
    des_nome          VARCHAR(255) NOT NULL,
    des_email         VARCHAR(255) NOT NULL,
    dat_nascimento    DATE NOT NULL,
    des_senha         VARCHAR(255) NOT NULL,
    des_cpf           VARCHAR(255) NOT NULL,
    des_sexo          VARCHAR(255) NOT NULL,
    id_status_usuario INT NOT NULL,
    id_acl            INT NOT NULL,
    des_discriminador VARCHAR(50) NOT NULL DEFAULT 'CLIENTE',
    des_cargo         VARCHAR(255) NULL,
    PRIMARY KEY (id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_categoria (
    id_categoria  INT NOT NULL AUTO_INCREMENT,
    des_categoria VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_livro (
    id_livro                  INT NOT NULL AUTO_INCREMENT,
    des_isbn                  VARCHAR(255) NOT NULL,
    des_titulo                VARCHAR(255) NOT NULL,
    des_autor                 VARCHAR(255) NOT NULL,
    des_editora               VARCHAR(255) NOT NULL,
    des_sinopse               VARCHAR(255) NOT NULL,
    dat_cadastro              DATE NOT NULL,
    dat_ano_publicacao        YEAR NOT NULL,
    num_quantidade_total      INT NOT NULL,
    num_quantidade_disponivel INT NOT NULL,
    id_categoria              INT NOT NULL,
    PRIMARY KEY (id_livro),
    UNIQUE KEY uk_livro_isbn (des_isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_status_emprestimo (
    id_status_emprestimo  INT NOT NULL AUTO_INCREMENT,
    des_status_emprestimo VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_status_emprestimo),
    UNIQUE KEY uk_status_emprestimo (des_status_emprestimo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_emprestimo (
    id_emprestimo          INT NOT NULL AUTO_INCREMENT,
    des_codigo_retirada    VARCHAR(255) NOT NULL,
    des_codigo_devolucao   VARCHAR(255) NULL,
    dat_emprestimo         DATETIME NOT NULL,
    dat_devolucao_prevista DATE NOT NULL,
    dat_devolucao_real     DATE NULL,
    bit_penalidade_gerada  TINYINT NOT NULL DEFAULT 0,
    id_usuario             INT NOT NULL,
    id_livro               INT NOT NULL,
    id_status_emprestimo   INT NOT NULL,
    PRIMARY KEY (id_emprestimo),
    UNIQUE KEY uk_emprestimo_codigo_retirada (des_codigo_retirada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_favorito (
    id_favorito INT NOT NULL AUTO_INCREMENT,
    id_usuario  INT NOT NULL,
    id_livro    INT NOT NULL,
    PRIMARY KEY (id_favorito),
    UNIQUE KEY uk_favorito (id_usuario, id_livro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_estoquista (
    id_estoquista     INT NOT NULL AUTO_INCREMENT,
    des_codigo_acesso VARCHAR(255) NOT NULL,
    des_senha         VARCHAR(255) NOT NULL,
    PRIMARY KEY (id_estoquista),
    UNIQUE KEY uk_estoquista_codigo (des_codigo_acesso)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_status_exemplar (
    id_status_exemplar INT NOT NULL AUTO_INCREMENT,
    des_stats          VARCHAR(45) NULL,
    PRIMARY KEY (id_status_exemplar)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tb_exemplar (
    id_exemplar INT NOT NULL AUTO_INCREMENT,
    id_livro    INT NOT NULL,
    id_status   INT NOT NULL,
    PRIMARY KEY (id_exemplar)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO tb_acl (des_acl) VALUES ('ADMINISTRADOR'), ('CLIENTE'), ('ESTOQUISTA');
INSERT IGNORE INTO tb_status_usuario (des_status_usuario) VALUES ('ATIVO'), ('BLOQUEADO');
INSERT IGNORE INTO tb_status_emprestimo (des_status_emprestimo) VALUES
    ('RESERVADO'), ('ATIVO'), ('ATRASADO'),
    ('EM_PROCESSO_DE_DEVOLUCAO'), ('EM_PROCESSO_DE_DEVOLUCAO_ATRASADO'),
    ('DEVOLVIDO'), ('DEVOLVIDO_COM_ATRASO'), ('CANCELADO');
INSERT IGNORE INTO tb_categoria (des_categoria) VALUES
    ('Ficção Científica'), ('Romance'), ('Fantasia'), ('Suspense'), ('Terror'),
    ('Biografia'), ('História'), ('Tecnologia'), ('Autoajuda'), ('Infantil'),
    ('HQ / Graphic Novel'), ('Técnico');
