package br.com.curso.listadetarefas.api.tarefa;

import org.springframework.data.jpa.repository.JpaRepository;

// Estendemos JpaRepository, informando a entidade (Tarefa) e o tipo da chave primária (Long).
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}