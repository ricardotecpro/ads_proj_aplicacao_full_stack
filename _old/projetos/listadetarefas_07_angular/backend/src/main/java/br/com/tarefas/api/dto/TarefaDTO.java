package br.com.tarefas.api.dto;

import lombok.Data;

@Data
public class TarefaDTO {
    private String titulo;
    private boolean concluida;
}
