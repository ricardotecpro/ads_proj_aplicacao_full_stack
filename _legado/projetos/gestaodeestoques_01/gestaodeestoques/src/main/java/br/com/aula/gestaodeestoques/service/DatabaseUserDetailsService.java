package br.com.aula.gestaodeestoques.service;

import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.model.Usuario;
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
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public DatabaseUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o login: " + login));

        Set<Papel> papeis = usuarioRepository.findPapeisByUsuarioId(usuario.id());

        Set<GrantedAuthority> authorities = papeis.stream()
                .map(papel -> new SimpleGrantedAuthority(papel.nome()))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                usuario.login(),
                usuario.senha(),
                usuario.ativo(), // Mapeia o campo 'ativo' do seu usuário
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );
    }
}
