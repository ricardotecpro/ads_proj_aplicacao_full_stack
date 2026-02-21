package br.com.liston.api.repository;

import br.com.liston.api.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // O Spring Data JPA cria a query automaticamente pelo nome do método
    List<Tarefa> findByTituloContainingIgnoreCase(String titulo);
}