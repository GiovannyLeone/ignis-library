package com.papirotech.biblioteca.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        log.info(">>> DatabaseInitializer: verificando estrutura do banco...");
        corrigirTbUsuario();
        adicionarFks();
        log.info(">>> DatabaseInitializer: banco verificado e atualizado.");
    }

    private void corrigirTbUsuario() {
        adicionarColunaSeNaoExistir("tb_usuario", "des_discriminador",
            "ALTER TABLE tb_usuario ADD COLUMN des_discriminador VARCHAR(50) NOT NULL DEFAULT 'CLIENTE'");
        adicionarColunaSeNaoExistir("tb_usuario", "des_cargo",
            "ALTER TABLE tb_usuario ADD COLUMN des_cargo VARCHAR(255) NULL");
        adicionarColunaSeNaoExistir("tb_usuario", "des_nome",
            "ALTER TABLE tb_usuario ADD COLUMN des_nome VARCHAR(255) NOT NULL DEFAULT ''");
        adicionarColunaSeNaoExistir("tb_usuario", "des_email",
            "ALTER TABLE tb_usuario ADD COLUMN des_email VARCHAR(255) NOT NULL DEFAULT ''");
        adicionarColunaSeNaoExistir("tb_usuario", "dat_nascimento",
            "ALTER TABLE tb_usuario ADD COLUMN dat_nascimento DATE NOT NULL DEFAULT '1900-01-01'");
        adicionarColunaSeNaoExistir("tb_usuario", "des_senha",
            "ALTER TABLE tb_usuario ADD COLUMN des_senha VARCHAR(255) NOT NULL DEFAULT ''");
        adicionarColunaSeNaoExistir("tb_usuario", "des_cpf",
            "ALTER TABLE tb_usuario ADD COLUMN des_cpf VARCHAR(255) NOT NULL DEFAULT ''");
        adicionarColunaSeNaoExistir("tb_usuario", "des_sexo",
            "ALTER TABLE tb_usuario ADD COLUMN des_sexo VARCHAR(255) NOT NULL DEFAULT ''");
        adicionarIndiceSeNaoExistir("tb_usuario", "uk_usuario_email",
            "ALTER TABLE tb_usuario ADD UNIQUE KEY uk_usuario_email (des_email)");
    }

    private void adicionarFks() {
        adicionarFkSeNaoExistir("tb_usuario", "fk_usuario_status",
            "ALTER TABLE tb_usuario ADD CONSTRAINT fk_usuario_status FOREIGN KEY (id_status_usuario) REFERENCES tb_status_usuario (id_status_usuario)");
        adicionarFkSeNaoExistir("tb_usuario", "fk_usuario_acl",
            "ALTER TABLE tb_usuario ADD CONSTRAINT fk_usuario_acl FOREIGN KEY (id_acl) REFERENCES tb_acl (id_acl)");
        adicionarFkSeNaoExistir("tb_livro", "fk_livro_categoria",
            "ALTER TABLE tb_livro ADD CONSTRAINT fk_livro_categoria FOREIGN KEY (id_categoria) REFERENCES tb_categoria (id_categoria)");
        adicionarFkSeNaoExistir("tb_favorito", "fk_favorito_usuario",
            "ALTER TABLE tb_favorito ADD CONSTRAINT fk_favorito_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id_usuario)");
        adicionarFkSeNaoExistir("tb_favorito", "fk_favorito_livro",
            "ALTER TABLE tb_favorito ADD CONSTRAINT fk_favorito_livro FOREIGN KEY (id_livro) REFERENCES tb_livro (id_livro)");
        adicionarFkSeNaoExistir("tb_emprestimo", "fk_emprestimo_usuario",
            "ALTER TABLE tb_emprestimo ADD CONSTRAINT fk_emprestimo_usuario FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id_usuario)");
        adicionarFkSeNaoExistir("tb_emprestimo", "fk_emprestimo_livro",
            "ALTER TABLE tb_emprestimo ADD CONSTRAINT fk_emprestimo_livro FOREIGN KEY (id_livro) REFERENCES tb_livro (id_livro)");
        adicionarFkSeNaoExistir("tb_emprestimo", "fk_emprestimo_status",
            "ALTER TABLE tb_emprestimo ADD CONSTRAINT fk_emprestimo_status FOREIGN KEY (id_status_emprestimo) REFERENCES tb_status_emprestimo (id_status_emprestimo)");
        adicionarFkSeNaoExistir("tb_exemplar", "fk_exemplar_livro",
            "ALTER TABLE tb_exemplar ADD CONSTRAINT fk_exemplar_livro FOREIGN KEY (id_livro) REFERENCES tb_livro (id_livro)");
        adicionarFkSeNaoExistir("tb_exemplar", "fk_exemplar_status",
            "ALTER TABLE tb_exemplar ADD CONSTRAINT fk_exemplar_status FOREIGN KEY (id_status) REFERENCES tb_status_exemplar (id_status_exemplar)");
    }

    private void adicionarColunaSeNaoExistir(String tabela, String coluna, String sql) {
        try {
            Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=? AND COLUMN_NAME=?",
                Integer.class, tabela, coluna);
            if (existe == null || existe == 0) { jdbc.execute(sql); log.info(">>> Coluna adicionada: {}.{}", tabela, coluna); }
        } catch (Exception e) { log.warn(">>> Não foi possível adicionar coluna {}.{}: {}", tabela, coluna, e.getMessage()); }
    }

    private void adicionarIndiceSeNaoExistir(String tabela, String nome, String sql) {
        try {
            Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=? AND INDEX_NAME=?",
                Integer.class, tabela, nome);
            if (existe == null || existe == 0) { jdbc.execute(sql); log.info(">>> Índice adicionado: {}.{}", tabela, nome); }
        } catch (Exception e) { log.warn(">>> Não foi possível adicionar índice {}.{}: {}", tabela, nome, e.getMessage()); }
    }

    private void adicionarFkSeNaoExistir(String tabela, String nome, String sql) {
        try {
            Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=? AND CONSTRAINT_NAME=?",
                Integer.class, tabela, nome);
            if (existe == null || existe == 0) { jdbc.execute(sql); log.info(">>> FK adicionada: {}.{}", tabela, nome); }
        } catch (Exception e) { log.warn(">>> Não foi possível adicionar FK {}.{}: {}", tabela, nome, e.getMessage()); }
    }
}
