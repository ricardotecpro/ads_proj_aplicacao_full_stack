package br.com.cadastro.usuarios.config;

import br.com.cadastro.usuarios.entity.User;
import br.com.cadastro.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Cria o usuário admin apenas se ele não existir
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@email.com");
                // Aqui você poderia criar e associar roles (perfis)
                userRepository.save(admin);
                System.out.println(">>> Usuário 'admin' criado com senha 'admin'");
            }
        };
    }
}