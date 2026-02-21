package br.com.aula.gestaodeestoques.repository;

import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.model.Usuario;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    @Query("SELECT p.* FROM papel p JOIN usuario_papel up ON p.id = up.papel_id WHERE up.usuario_id = :usuarioId")
    Set<Papel> findPapeisByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("INSERT INTO usuario_papel (usuario_id, papel_id) VALUES (:usuarioId, :papelId)")
    void adicionarPapel(@Param("usuarioId") Long usuarioId, @Param("papelId") Long papelId);

    // MÉTODO FALTANTE ADICIONADO AQUI
    /**
     * Apaga todas as associações de papéis para um determinado utilizador na tabela de junção.
     * Essencial para as operações de update e delete de utilizadores.
     */
    @Modifying
    @Query("DELETE FROM usuario_papel WHERE usuario_id = :usuarioId")
    void limparPapeis(@Param("usuarioId") Long usuarioId);
}