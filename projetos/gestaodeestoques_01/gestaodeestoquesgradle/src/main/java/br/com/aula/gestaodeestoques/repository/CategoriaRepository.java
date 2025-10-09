package br.com.aula.gestaodeestoques.repository;

import br.com.aula.gestaodeestoques.model.Categoria;
import org.springframework.data.repository.CrudRepository;

public interface CategoriaRepository extends CrudRepository<Categoria, Integer> {}
