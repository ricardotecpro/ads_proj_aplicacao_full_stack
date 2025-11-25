package com.liston.repository;

import com.liston.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Nossos métodos CRUD básicos (save, findById, findAll, deleteById)
    // já estão incluídos aqui pelo JpaRepository.
}