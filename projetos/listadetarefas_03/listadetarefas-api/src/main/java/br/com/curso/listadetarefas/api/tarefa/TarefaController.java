package br.com.curso.listadetarefas.api.tarefa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // Especialização de @Controller para criar APIs RESTful.
@RequestMapping("/api/tarefas") // Mapeia todas as requisições para este Controller para o caminho /api/tarefas.
@CrossOrigin(origins = "*") // Permite que requisições de qualquer origem acessem esta API.
public class TarefaController {
    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public List<Tarefa> listarTarefas() { return tarefaService.listarTodas(); }

    @PostMapping
    public Tarefa criarTarefa(@RequestBody Tarefa tarefa) { return tarefaService.criar(tarefa); }

    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefa tarefa) {
        try {
            Tarefa atualizada = tarefaService.atualizar(id, tarefa);
            return ResponseEntity.ok(atualizada); // Retorna HTTP 200 OK.
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 Not Found.
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        try {
            tarefaService.deletar(id);
            return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content.
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 Not Found.
        }
    }
}