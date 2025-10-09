package br.com.aula.gestaodeestoques.repository;

import br.com.aula.gestaodeestoques.model.Papel;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface PapelRepository extends CrudRepository<Papel, Long> {

    @Query("SELECT p.* FROM papel p JOIN usuario_papel up ON p.id = up.papel_id WHERE up.usuario_id = :usuarioId")
    Set<Papel> findPapeisByUsuarioId(@Param("usuarioId") Long usuarioId);
}
