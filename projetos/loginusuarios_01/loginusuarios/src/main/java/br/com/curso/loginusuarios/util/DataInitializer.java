package br.com.curso.loginusuarios.util;

import br.com.curso.loginusuarios.model.Role;
import br.com.curso.loginusuarios.model.User;
import br.com.curso.loginusuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Se não houver nenhum utilizador com o perfil ADMIN, cria um
        if (userRepository.findAll().stream().noneMatch(user -> user.getRole() == Role.ADMIN)) {
            User admin = new User();
            admin.setNome("Administrador");
            admin.setEmail("admin@email.com");
            admin.setSenha(passwordEncoder.encode("admin123")); // Senha inicial
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Utilizador ADMIN padrão criado com sucesso!");
        }
    }
}