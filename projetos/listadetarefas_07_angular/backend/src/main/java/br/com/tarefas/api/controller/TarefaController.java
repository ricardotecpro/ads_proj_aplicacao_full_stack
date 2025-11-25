package br.com.tarefas.api.controller;

import br.com.tarefas.api.dto.TarefaDTO;
import br.com.tarefas.api.dto.TarefaStatusDTO;
import br.com.tarefas.api.dto.TarefaTituloDTO;
import br.com.tarefas.api.model.Tarefa;
import br.com.tarefas.api.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public ResponseEntity<List<Tarefa>> listar() {
        List<Tarefa> tarefas = tarefaService.listarTodas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> buscarPorId(@PathVariable Long id) {
        Tarefa tarefa = tarefaService.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<Tarefa> criar(@RequestBody TarefaDTO tarefaDTO) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setConcluida(tarefaDTO.isConcluida());

        Tarefa novaTarefa = tarefaService.criarTarefa(tarefa);
        return new ResponseEntity<>(novaTarefa, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizar(@PathVariable Long id, @RequestBody TarefaDTO tarefaDTO) {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(tarefaDTO.getTitulo());
        tarefa.setConcluida(tarefaDTO.isConcluida());

        Tarefa tarefaAtualizada = tarefaService.atualizar(id, tarefa);
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Tarefa> atualizarStatus(@PathVariable Long id, @RequestBody TarefaStatusDTO statusDTO) {
        Tarefa tarefaAtualizada = tarefaService.atualizarStatus(id, statusDTO.isConcluida());
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @PatchMapping("/{id}/titulo")
    public ResponseEntity<Tarefa> atualizarTitulo(@PathVariable Long id, @RequestBody TarefaTituloDTO tituloDTO) {
        Tarefa tarefaAtualizada = tarefaService.atualizarTitulo(id, tituloDTO.getTitulo());
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tarefaService.deletarTarefa(id);
        return ResponseEntity.noContent().build();
    }
}
