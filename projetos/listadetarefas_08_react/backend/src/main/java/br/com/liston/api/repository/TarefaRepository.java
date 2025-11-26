package br.com.liston.api.repository;

import br.com.liston.api.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // Spring Data JPA automatically creates the query by method name
    List<Tarefa> findByTituloContainingIgnoreCase(String titulo);
}