package br.com.cadastro.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso livre aos arquivos de CSS e JS
                .requestMatchers("/css/**", "/js/**").permitAll()
                // Qualquer outra requisição precisa de autenticação
                .anyRequest().authenticated()
            )
            // Configura o formulário de login
            .formLogin(form -> form
                .loginPage("/login").permitAll() // Diz qual a URL da nossa página de login
                .defaultSuccessUrl("/", true) // Para onde ir após o login com sucesso
            )
            // Configura o logout
            .logout(logout -> logout.logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Codificador de senhas. Essencial para a segurança.
        return new BCryptPasswordEncoder();
    }
}
