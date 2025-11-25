package com.liston.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF (comum em APIs stateless)
            .csrf(csrf -> csrf.disable())

            // Configura o CORS (usará o @Bean de CorsConfig)
            .cors(cors -> {})

            // Define a política de sessão como STATELESS (não guarda estado)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Regras de autorização
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso ao H2 console
                .requestMatchers(toH2Console()).permitAll()
                // Permite acesso ao Swagger
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Permite acesso à nossa API de tarefas (sem autenticação por enquanto)
                .requestMatchers("/api/tasks/**").permitAll()
                // Qualquer outra requisição precisa de autenticação
                .anyRequest().authenticated()
            )

            // Configuração específica para o H2 Console funcionar em frames
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                ))
            );

        return http.build();
    }
}