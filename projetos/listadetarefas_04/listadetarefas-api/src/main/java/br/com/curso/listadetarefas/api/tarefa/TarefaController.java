package br.com.curso.listadetarefas.api.tarefa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // Combina @Controller e @ResponseBody, simplificando a criação de APIs REST.
@RequestMapping("/api/tarefas") // Mapeia todas as requisições para este Controller para o caminho /api/tarefas.
@CrossOrigin(origins = "*") // Permite que requisições de qualquer origem (ex: localhost:4200) acessem esta API.
public class TarefaController {
    @Autowired
    private TarefaService tarefaService;

    @GetMapping // Mapeia requisições HTTP GET para /api/tarefas.
    public List<Tarefa> listarTarefas() { return tarefaService.listarTodas(); }

    @PostMapping // Mapeia requisições HTTP POST para /api/tarefas.
    public Tarefa criarTarefa(@RequestBody Tarefa tarefa) { return tarefaService.criar(tarefa); }

    @PutMapping("/{id}") // Mapeia requisições HTTP PUT para /api/tarefas/{id}.
    public ResponseEntity<Tarefa> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefa tarefa) {
        try {
            Tarefa atualizada = tarefaService.atualizar(id, tarefa);
            return ResponseEntity.ok(atualizada); // Retorna HTTP 200 OK com a tarefa atualizada.
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 Not Found.
        }
    }

    @DeleteMapping("/{id}") // Mapeia requisições HTTP DELETE para /api/tarefas/{id}.
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        try {
            tarefaService.deletar(id);
            return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content.
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retorna HTTP 404 Not Found.
        }
    }
}