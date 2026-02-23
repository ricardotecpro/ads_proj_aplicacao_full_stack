package br.com.liston.api.config;

import br.com.liston.api.model.Tarefa;
import br.com.liston.api.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // Important!

import java.util.List;

@Configuration
@Profile("dev") // 1. Ensures this bean is only created in 'dev' profile (H2)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TarefaRepository tarefaRepository;

    // 2. This 'run' method will be executed as soon as the application starts
    @Override
    public void run(String... args) throws Exception {

        System.out.println("--- INITIALIZING TEST DATA (SEED) ---");

        // Clears the database (if there is anything)
        tarefaRepository.deleteAll();

        // Creates initial tasks
        Tarefa t1 = new Tarefa("Study Spring Boot", "Finish REST API guide");
        t1.setConcluida(true); // Marks the first one as completed

        Tarefa t2 = new Tarefa("Study React", "Learn about React Components");

        Tarefa t3 = new Tarefa("Study React Native", "Prepare mobile environment");

        // Saves all at once
        tarefaRepository.saveAll(List.of(t1, t2, t3));

        System.out.println("--- TEST DATA INSERTED SUCCESSFULLY ---");
    }
}