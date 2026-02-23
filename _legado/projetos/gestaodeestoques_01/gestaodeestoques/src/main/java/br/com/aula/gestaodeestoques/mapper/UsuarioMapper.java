package br.com.aula.gestaodeestoques.mapper;

import br.com.aula.gestaodeestoques.dto.UsuarioDTO;
import br.com.aula.gestaodeestoques.dto.UsuarioFormDTO;
import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.model.Usuario;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {

    /**
     * Converte uma Entidade Usuario e seus Papéis em um UsuarioDTO para exibição.
     * @param usuario A entidade vinda do banco.
     * @param papeis O conjunto de papéis associados a este usuário.
     * @return um DTO pronto para ser enviado como JSON, sem a senha.
     */
    public UsuarioDTO toDTO(Usuario usuario, Set<Papel> papeis) {
        if (usuario == null) {
            return null;
        }
        // Converte o Set<Papel> em um Set<String> com os nomes dos papéis
        Set<String> nomesPapeis = papeis.stream()
                .map(Papel::nome)
                .collect(Collectors.toSet());

        return new UsuarioDTO(
                usuario.id(),
                usuario.login(),
                usuario.ativo(),
                nomesPapeis
        );
    }

    /**
     * Converte um UsuarioFormDTO (vindo da API) em uma Entidade Usuario.
     * Note que a senha já chega criptografada, pois a criptografia é responsabilidade do Service.
     * @param dto O DTO recebido na requisição.
     * @param senhaCriptografada A senha já processada pelo PasswordEncoder.
     * @return uma entidade pronta para ser salva no banco.
     */
    public Usuario toEntity(UsuarioFormDTO dto, String senhaCriptografada) {
        if (dto == null) {
            return null;
        }
        return new Usuario(
                dto.id(),
                dto.login(),
                senhaCriptografada,
                dto.ativo()
        );
    }
}