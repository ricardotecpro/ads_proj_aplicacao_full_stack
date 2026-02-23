package com.liston.controller;

import com.liston.model.Task;
import com.liston.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") // Define o prefixo para todos os endpoints
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // 1. CREATE
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Remove o ID para garantir que é uma criação
        task.setId(null);
        Task newTask = taskRepository.save(task);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    // 2. READ (All)
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    // 3. READ (By ID)
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok) // Se achar, retorna 200 OK
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // Se não, retorna 404
    }

    // 4. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    existingTask.setCompleted(taskDetails.isCompleted());
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found
        }
    }
}
