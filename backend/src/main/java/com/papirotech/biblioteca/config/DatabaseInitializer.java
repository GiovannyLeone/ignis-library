package com.papirotech.biblioteca.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * DatabaseInitializer — roda logo após o SQL init e antes do DataSeeder.
 *
 * Responsabilidades:
 *   1. Adicionar colunas que possam estar faltando em tabelas já existentes
 *   2. Adicionar FKs e índices que possam estar faltando
 *   3. Corrigir registros corrompidos (status/perfil com null)
 *
 * Toda operação verifica o estado atual do banco antes de agir.
 * Seguro para rodar múltiplas vezes — idempotente.
 */
@Component
@Order(0) // Roda antes do DataSeeder (@Order(1))
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        log.info(">>> DatabaseInitializer: verificando estrutura do banco...");
        corrigirTbUsuario();
        adicionarFksTbUsuario();
        adicionarFksTbLivro();
        adicionarFksTbEmprestimo();
        adicionarFksTbFavorito();
        adicionarFksTbExemplar();
        corrigirRegistrosCorretos();
        log.info(">>> DatabaseInitializer: banco verificado e atualizado.");
    }

    // ─── tb_usuario: garante que todas as colunas existem ────────────────────
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
        adicionarColunaSeNaoExistir("tb_usuario", "id_status_usuario",
                "ALTER TABLE tb_usuario ADD COLUMN id_status_usuario INT NOT NULL DEFAULT 1");
        adicionarColunaSeNaoExistir("tb_usuario", "id_acl",
                "ALTER TABLE tb_usuario ADD COLUMN id_acl INT NOT NULL DEFAULT 1");

        // Garante o índice único em des_email
        adicionarIndiceSeNaoExistir("tb_usuario", "uk_usuario_email",
                "ALTER TABLE tb_usuario ADD UNIQUE KEY uk_usuario_email (des_email)");
    }

    // ─── FKs de tb_usuario ────────────────────────────────────────────────────
    private void adicionarFksTbUsuario() {
        adicionarFkSeNaoExistir("tb_usuario", "fk_usuario_status",
                "ALTER TABLE tb_usuario ADD CONSTRAINT fk_usuario_status " +
                "FOREIGN KEY (id_status_usuario) REFERENCES tb_status_usuario (id_status_usuario)");
        adicionarFkSeNaoExistir("tb_usuario", "fk_usuario_acl",
                "ALTER TABLE tb_usuario ADD CONSTRAINT fk_usuario_acl " +
                "FOREIGN KEY (id_acl) REFERENCES tb_acl (id_acl)");
    }

    // ─── FKs de tb_livro ──────────────────────────────────────────────────────
    private void adicionarFksTbLivro() {
        adicionarFkSeNaoExistir("tb_livro", "fk_livro_categoria",
                "ALTER TABLE tb_livro ADD CONSTRAINT fk_livro_categoria " +
                "FOREIGN KEY (id_categoria) REFERENCES tb_categoria (id_categoria)");
    }

    // ─── FKs de tb_emprestimo ─────────────────────────────────────────────────
    private void adicionarFksTbEmprestimo() {
        adicionarFkSeNaoExistir("tb_emprestimo", "fk_emprestimo_usuario",
                "ALTER TABLE tb_emprestimo ADD CONSTRAINT fk_emprestimo_usuario " +
                "FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id_usuario)");
        adicionarFkSeNaoExistir("tb_emprestimo", "fk_emprestimo_livro",
                "ALTER TABLE tb_emprestimo ADD CONSTRAINT fk_emprestimo_livro " +
                "FOREIGN KEY (id_livro) REFERENCES tb_livro (id_livro)");
        adicionarFkSeNaoExistir("tb_emprestimo", "fk_emprestimo_status",
                "ALTER TABLE tb_emprestimo ADD CONSTRAINT fk_emprestimo_status " +
                "FOREIGN KEY (id_status_emprestimo) REFERENCES tb_status_emprestimo (id_status_emprestimo)");
    }

    // ─── FKs de tb_favorito ───────────────────────────────────────────────────
    private void adicionarFksTbFavorito() {
        adicionarFkSeNaoExistir("tb_favorito", "fk_favorito_usuario",
                "ALTER TABLE tb_favorito ADD CONSTRAINT fk_favorito_usuario " +
                "FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id_usuario)");
        adicionarFkSeNaoExistir("tb_favorito", "fk_favorito_livro",
                "ALTER TABLE tb_favorito ADD CONSTRAINT fk_favorito_livro " +
                "FOREIGN KEY (id_livro) REFERENCES tb_livro (id_livro)");
    }

    // ─── FKs de tb_exemplar ───────────────────────────────────────────────────
    private void adicionarFksTbExemplar() {
        adicionarFkSeNaoExistir("tb_exemplar", "fk_exemplar_livro",
                "ALTER TABLE tb_exemplar ADD CONSTRAINT fk_exemplar_livro " +
                "FOREIGN KEY (id_livro) REFERENCES tb_livro (id_livro)");
        adicionarFkSeNaoExistir("tb_exemplar", "fk_exemplar_status",
                "ALTER TABLE tb_exemplar ADD CONSTRAINT fk_exemplar_status " +
                "FOREIGN KEY (id_status) REFERENCES tb_status_exemplar (id_status_exemplar)");
    }

    // ─── Corrige registros com status/acl inválidos ───────────────────────────
    private void corrigirRegistrosCorretos() {
        // Remove usuários que foram criados com id_status_usuario ou id_acl inválidos
        // (criados antes dos dados base existirem no banco)
        try {
            Integer corrompidos = jdbc.queryForObject(
                "SELECT COUNT(*) FROM tb_usuario u " +
                "LEFT JOIN tb_status_usuario s ON u.id_status_usuario = s.id_status_usuario " +
                "LEFT JOIN tb_acl a ON u.id_acl = a.id_acl " +
                "WHERE s.des_status_usuario IS NULL OR a.des_acl IS NULL",
                Integer.class);

            if (corrompidos != null && corrompidos > 0) {
                jdbc.update(
                    "DELETE u FROM tb_usuario u " +
                    "LEFT JOIN tb_status_usuario s ON u.id_status_usuario = s.id_status_usuario " +
                    "LEFT JOIN tb_acl a ON u.id_acl = a.id_acl " +
                    "WHERE s.des_status_usuario IS NULL OR a.des_acl IS NULL");
                log.warn(">>> DatabaseInitializer: {} registro(s) corrompido(s) removido(s) de tb_usuario. " +
                         "Serão recriados pelo DataSeeder.", corrompidos);
            }
        } catch (Exception e) {
            log.warn(">>> DatabaseInitializer: não foi possível verificar registros corrompidos: {}", e.getMessage());
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void adicionarColunaSeNaoExistir(String tabela, String coluna, String sql) {
        try {
            Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?",
                Integer.class, tabela, coluna);

            if (existe == null || existe == 0) {
                jdbc.execute(sql);
                log.info(">>> Coluna adicionada: {}.{}", tabela, coluna);
            }
        } catch (Exception e) {
            log.warn(">>> Não foi possível adicionar coluna {}.{}: {}", tabela, coluna, e.getMessage());
        }
    }

    private void adicionarIndiceSeNaoExistir(String tabela, String nomeIndice, String sql) {
        try {
            Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.STATISTICS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND INDEX_NAME = ?",
                Integer.class, tabela, nomeIndice);

            if (existe == null || existe == 0) {
                jdbc.execute(sql);
                log.info(">>> Índice adicionado: {}.{}", tabela, nomeIndice);
            }
        } catch (Exception e) {
            log.warn(">>> Não foi possível adicionar índice {}.{}: {}", tabela, nomeIndice, e.getMessage());
        }
    }

    private void adicionarFkSeNaoExistir(String tabela, String nomeFk, String sql) {
        try {
            Integer existe = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND CONSTRAINT_NAME = ?",
                Integer.class, tabela, nomeFk);

            if (existe == null || existe == 0) {
                jdbc.execute(sql);
                log.info(">>> FK adicionada: {}.{}", tabela, nomeFk);
            }
        } catch (Exception e) {
            log.warn(">>> Não foi possível adicionar FK {}.{}: {}", tabela, nomeFk, e.getMessage());
        }
    }
}
// Este bloco é automaticamente ignorado pois está fora da classe
