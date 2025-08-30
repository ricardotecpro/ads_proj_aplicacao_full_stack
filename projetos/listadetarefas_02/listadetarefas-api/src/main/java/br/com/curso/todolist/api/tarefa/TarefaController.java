package br.com.curso.todolist.api.tarefa;

import br.com.curso.todolist.api.dto.TarefaRequestDTO;
import br.com.curso.todolist.api.dto.TarefaResponseDTO;
import br.com.curso.todolist.api.mapper.TarefaMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/tarefas") // Path base ajustado para funcionar com o proxy do Angular
public class TarefaController {

    private final TarefaService tarefaService;
    private final TarefaMapper tarefaMapper;

    public TarefaController(TarefaService tarefaService, TarefaMapper tarefaMapper) {
        this.tarefaService = tarefaService;
        this.tarefaMapper = tarefaMapper;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criarTarefa(@Valid @RequestBody TarefaRequestDTO tarefaDTO) {
        Tarefa tarefa = tarefaMapper.toEntity(tarefaDTO);
        Tarefa novaTarefa = tarefaService.criar(tarefa);

        // Constrói a URI para o novo recurso criado
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novaTarefa.getId())
                .toUri();

        return ResponseEntity.created(location).body(tarefaMapper.toResponseDTO(novaTarefa)); // Retorna 201 Created
    }

    // READ - Listar Todas
    @GetMapping
    public List<TarefaResponseDTO> listarTarefas() {
        return tarefaService.listarTodas().stream()
                .map(tarefaMapper::toResponseDTO)
                .toList();
    }

    // READ - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> buscarTarefaPorId(@PathVariable Long id) {
        return tarefaService.buscarPorId(id)
                .map(tarefa -> ResponseEntity.ok(tarefaMapper.toResponseDTO(tarefa))) // Se encontrar, retorna 200 OK com a tarefa
                .orElse(ResponseEntity.notFound().build()); // Se não, retorna 404 Not Found
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizarTarefa(@PathVariable Long id, @Valid @RequestBody TarefaRequestDTO tarefaDTO) {
        Tarefa tarefa = tarefaMapper.toEntity(tarefaDTO);
        Tarefa tarefaAtualizada = tarefaService.atualizar(id, tarefa);
        return ResponseEntity.ok(tarefaMapper.toResponseDTO(tarefaAtualizada));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content, sucesso sem corpo
    }
}