package br.com.liston.api.controller;

import br.com.liston.api.dto.TarefaRequestDTO;
import br.com.liston.api.dto.TarefaResponseDTO;
import br.com.liston.api.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController // Defines the class as a REST Controller
@RequestMapping("/api/tarefas") // Maps all requests to this endpoint
public class TarefaController {

    @Autowired
    private TarefaService service;

    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> listarTodasTarefas() {
        List<TarefaResponseDTO> tarefas = service.listarTodas();
        return ResponseEntity.ok(tarefas); // Returns 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarTarefaPorId(@PathVariable Long id) {
        // Exception handling (EntityNotFoundException) will be done globally later
        TarefaResponseDTO tarefa = service.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criarTarefa(@RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO novaTarefa = service.criar(dto);

        // Returns 201 Created with the new resource URL in the 'Location' header
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
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }
}