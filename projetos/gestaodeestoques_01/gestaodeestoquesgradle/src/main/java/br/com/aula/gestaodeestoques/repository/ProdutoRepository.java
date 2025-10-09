package br.com.aula.gestaodeestoques.repository;

import br.com.aula.gestaodeestoques.model.Produto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface de repositório para a entidade Produto.
 * Ao estender CrudRepository, o Spring Data JDBC implementa automaticamente
 * os métodos básicos de CRUD (Create, Read, Update, Delete).
 *
 * CrudRepository<Produto, Integer>
 * - Produto: É a entidade que este repositório gerencia.
 * - Integer: É o tipo da chave primária (ID) da entidade Produto.
 */
@Repository
public interface ProdutoRepository extends CrudRepository<Produto, Integer> {
    // O Spring Data JDBC implementará os métodos:
    // - save(Produto produto)
    // - findById(Integer id)
    // - findAll()
    // - deleteById(Integer id)
    // - e outros...
}
