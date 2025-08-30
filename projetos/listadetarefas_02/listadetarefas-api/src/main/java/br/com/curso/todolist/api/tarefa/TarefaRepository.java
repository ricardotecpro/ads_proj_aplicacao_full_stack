package br.com.curso.todolist.api.tarefa;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JpaRepository é uma interface do Spring Data JPA que já vem com métodos CRUD prontos.
 * Precisamos apenas dizer qual a Entidade que ele irá gerenciar (Tarefa) e qual o tipo da chave primária (Long).
 */
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}