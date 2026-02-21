package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Produto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProdutoRepository extends CrudRepository<Produto, Integer> {}