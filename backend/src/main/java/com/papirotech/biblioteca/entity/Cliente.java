package br.com.ignis.biblioteca.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID  // Ferramenta de sorteio de códigos importada

@Entity  // Avisa o banco de dados que isso vai ser mapeado nas tabelas
@Data  // O Lombok cria todos os getters e setters invisíveis para poupar tempo

public class Cliente extends Pessoa {

    @OneToMany(mappedBy = "cliente")  // Lista de todas as fichas de aluguel desse cliente
    private List<Emprestimo> historico;

    @ManyToMany
    @JoinTable(name = "tb_favorito")  // Cria a tabela do meio no banco para os favoritos
    private List<Livro> favoritos;  // Lista dos livros que ele deu "coraçãozinho"

    public boolean verificarStatus() {
        // Confere se o cliente tá de boa no sistema
        if (this.getStatus() == StatusUsuario.ATIVO) {
            return true;
        }

        return false;
    }

    public void favoritarLivro(Livro livro) {
        // Se o livro já tá na lista, tira (desfavorita). Se não tá, coloca. Tipo botão de curtir.
        if (this.favoritos.contains(livro)) {
            this.favoritos.remove(livro);
        } else {
            this.favoritos.add(livro);
        }
    }

    public List<Emprestimo> consultarHistorico(){
        return this.historico;  // Só devolve a lista de tudo que ele já alugou
    }

    public Emprestimo gerarCodigoEmprestimo(Cliente cliente, Livro livro){
        // 1. Barra a operação se o cliente tiver devendo
        if (!this.verificarStatus()) {
            throw new ClienteComPendenciaException();
        }

        // 2. Barra a operação se não tiver exemplar na estante
        if (!livro.verificarDisponibilidade()) {
            throw new LivroIndisponivelException();
        }

        // 3. Pega uma ficha de aluguel em branco
        Emprestimo emprestimo = new Emprestimo();

        // 4. Anota o nome do cliente, o livro e diz que tá reservado
        emprestimo.setCliente(cliente);
        emprestimo.setLivro(livro);
        emprestimo.setStatus(StatusEmprestimo.RESERVADO);

        // 5. Sorteia 5 letras/números pra ser a senha de buscar o livro
        String codigo = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        emprestimo.setCodigoRetiradaEmprestimo(codigo);

        // 6. Pega o dia de hoje e soma 7 dias pra data limite de entrega
        LocalDate dataHoje = LocalDate.now();
        emprestimo.setDataEmprestimo(dataHoje); // Hoje
        emprestimo.setDataDevolucaoPrevista(dataHoje.plusDays(7));

        // 7. Tira 1 livro da contagem da estante do sistema
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);

        return emprestimo;
    }

    public String gerarCodigoDevolucao(Emprestimo emprestimo) {

        // 1. Se NÃO for ativo E TAMBÉM NÃO for atrasado, é porque tem rolo (já devolveu, etc). Bloqueia.
        if (emprestimo.getStatus() == StatusEmprestimo.ATIVO || emprestimo.getStatus() == StatusEmprestimo.ATRASADO)  {
                throw new RuntimeException("Status não permite devolução");
        }

        // 2. Troca a etiqueta pra "trazendo de volta" normal ou "trazendo de volta atrasado"
        if (emprestimo.getStatus() == StatusEmprestimo.ATIVO) {
            emprestimo.setStatus(StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO);
        } else if (emprestimo.getStatus() == StatusEmprestimo.ATRASADO) {
            emprestimo.setStatus(StatusEmprestimo.EM_PROCESSO_DE_DEVOLUCAO_ATRASADO);
        }

        // 3. Sorteia mais uma senha de 5 letras para a devolução
        String codigoDevolucao = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        emprestimo.setCodigoDevolucaoEmprestimo(codigoDevolucao);

        return codigoDevolucao;
    }
}