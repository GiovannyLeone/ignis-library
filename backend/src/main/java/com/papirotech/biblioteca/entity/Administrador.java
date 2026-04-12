package br.com.ignis.biblioteca.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

//1- Anotações da Classe
@Entity  //Essa classe representa os dados que serão slavos no banco de dados
@Data  //Cria os getters e setters durante a compilação

public class Administrador extends Pessoa {    //"extends" garante que adm herde os atributos de Pessoa
    @Column(name = "des_cargo", length = 255)
    //2- Atributos do adm
     //As informaçõe serão guardadas dentro desse coluna
    private String cargo;  //Aqui fica guardado a função do adm

    //3- Método para aplicar penalidades
    public String aplicarPenalidade(Emprestimo emprestimo) {  //Por aqui vai passar apenas conteudo da classe emprestimo
        String status = emprestimo.getStatus().toString();  //Pega o status atual e transforma em string para conseguirmos comparar

        if (status.equals("ATRASADO") || status.equals("DEVOLVIDO_COM_ATRASO")) {  //Verifica se a penalidade precisa ser aplicada por conta do atraso
            emprestimo.getCliente().setStatus(StatusUsuario.BLOQUEADO);  //Acessa o cliente daquele emprestimo e bloqueia o cadastro dele

            return "BLOQUEADO";
        }

        return "ATIVO";  //Se chegar aqui significa que não tem necessidade de bloqueio
    }

    //4- Método para remover penalidades
    public boolean removerPenalidade(Emprestimo emprestimo) {  //Recebe um emprestimo e tem que retornar um valor boolean
        String statusDoCliente = emprestimo.getCliente().getStatus().toString();  //Pega o status atual e transforma em string para conseguirmos comparar

        if (statusDoCliente.equals("BLOQUEADO")) {  //Analisa se o cliente possui status bloqueado

            emprestimo.getCliente().setStatus(StatusUsuario.ATIVO);  //Muda o status do Cliente para ativo

            return true;
        }

        return false; // Se o cliente não estiver bloqueado acaba o fluxo
    }
}