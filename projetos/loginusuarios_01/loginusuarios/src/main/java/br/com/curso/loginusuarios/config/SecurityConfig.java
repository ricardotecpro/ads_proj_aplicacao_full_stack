package br.com.curso.loginusuarios.config;

import br.com.curso.loginusuarios.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configurações específicas para o H2 Console
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público a estas páginas, recursos estáticos e ao H2 Console
                .requestMatchers("/registar", "/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                // Apenas ADMINS podem aceder a URLs que começam com /usuarios/apagar
                .requestMatchers(HttpMethod.POST, "/usuarios/apagar/**").hasAuthority(Role.ADMIN.name())
                // Qualquer outro pedido exige autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/usuarios", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL para acionar o logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}
