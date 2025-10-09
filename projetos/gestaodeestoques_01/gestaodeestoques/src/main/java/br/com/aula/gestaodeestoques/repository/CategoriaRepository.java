package br.com.aula.gestaodeestoques.repository;

import br.com.aula.gestaodeestoques.model.Categoria;
import org.springframework.data.repository.CrudRepository; // 1. Garanta que este import está correto.
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Integer> { // 2. Corrigido para usar apenas "CrudRepository".
}