package br.com.curso.listadetarefas.api.tarefa;

import lombok.Data;

@Data
public class TarefaUpdateDTO {
    // Apenas os campos que podem ser atualizados pelo cliente.
    private String descricao;
    private boolean concluida;
}