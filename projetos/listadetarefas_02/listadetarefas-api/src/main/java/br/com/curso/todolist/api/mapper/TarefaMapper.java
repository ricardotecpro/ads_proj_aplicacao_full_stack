package br.com.curso.todolist.api.mapper;

import br.com.curso.todolist.api.dto.TarefaRequestDTO;
import br.com.curso.todolist.api.dto.TarefaResponseDTO;
import br.com.curso.todolist.api.tarefa.Tarefa;
import org.springframework.stereotype.Component;

@Component
public class TarefaMapper {

    public Tarefa toEntity(TarefaRequestDTO dto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setConcluida(dto.concluida());
        return tarefa;
    }

    public TarefaResponseDTO toResponseDTO(Tarefa tarefa) {
        return new TarefaResponseDTO(tarefa.getId(), tarefa.getTitulo(), tarefa.getDescricao(), tarefa.isConcluida());
    }
}