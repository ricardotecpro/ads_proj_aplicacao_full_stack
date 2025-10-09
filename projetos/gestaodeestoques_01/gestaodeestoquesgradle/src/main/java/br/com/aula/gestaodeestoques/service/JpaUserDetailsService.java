package br.com.aula.gestaodeestoques.service;

import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.model.Usuario;
import br.com.aula.gestaodeestoques.repository.PapelRepository;
import br.com.aula.gestaodeestoques.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;

    public JpaUserDetailsService(UsuarioRepository usuarioRepository, PapelRepository papelRepository) {
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário pelo login
        Usuario usuario = usuarioRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        // Busca os papéis (roles) associados ao usuário
        Set<Papel> papeis = papelRepository.findPapeisByUsuarioId(usuario.id());

        // Converte os papéis em GrantedAuthority para o Spring Security
        Set<GrantedAuthority> authorities = papeis.stream()
                .map(papel -> new SimpleGrantedAuthority(papel.nome()))
                .collect(Collectors.toSet());

        // Retorna um objeto UserDetails que o Spring Security pode usar
        return new org.springframework.security.core.userdetails.User(
                usuario.login(),
                usuario.senha(),
                authorities
        );
    }
}
