package br.com.liston.api.controller;

import br.com.liston.api.dto.TarefaRequestDTO;
import br.com.liston.api.dto.TarefaResponseDTO;
import br.com.liston.api.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController // Define a classe como um Controller REST
@RequestMapping("/api/tarefas") // Mapeia todas as requisições para este endpoint
public class TarefaController {

    @Autowired
    private TarefaService service;

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> listarTodasTarefas() {
        List<TarefaResponseDTO> tarefas = service.listarTodas();
        return ResponseEntity.ok(tarefas); // Retorna 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarTarefaPorId(@PathVariable Long id) {
        // Tratamento de exceção (EntityNotFoundException) será feito globalmente depois
        TarefaResponseDTO tarefa = service.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criarTarefa(@RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO novaTarefa = service.criar(dto);

        // Retorna 201 Created com a URL do novo recurso no header 'Location'
        URI location = URI.create("/api/tarefas/" + novaTarefa.id());
        return ResponseEntity.created(location).body(novaTarefa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizarTarefa(@PathVariable Long id, @RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO tarefaAtualizada = service.atualizar(id, dto);
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}