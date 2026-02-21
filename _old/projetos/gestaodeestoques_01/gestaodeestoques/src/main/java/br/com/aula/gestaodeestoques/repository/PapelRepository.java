package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Papel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PapelRepository extends CrudRepository<Papel, Long> {}