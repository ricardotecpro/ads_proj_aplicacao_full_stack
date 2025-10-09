package br.com.aula.gestaodeestoques.repository;

import br.com.aula.gestaodeestoques.model.Fornecedor;
import org.springframework.data.repository.CrudRepository;

public interface FornecedorRepository extends CrudRepository<Fornecedor, Integer> {}
