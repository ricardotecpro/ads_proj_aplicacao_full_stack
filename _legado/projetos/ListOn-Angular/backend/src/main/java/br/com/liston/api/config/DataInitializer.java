package br.com.liston.api.config;

import br.com.liston.api.model.Tarefa;
import br.com.liston.api.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // Importante!

import java.util.List;

@Configuration
@Profile("dev") // 1. Garante que este bean só será criado no perfil 'dev' (H2)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private TarefaRepository tarefaRepository;

    // 2. Este método 'run' será executado assim que a aplicação iniciar
    @Override
    public void run(String... args) throws Exception {

        System.out.println("--- INICIALIZANDO DADOS DE TESTE (SEED) ---");

        // Limpa o banco (caso haja algo)
        tarefaRepository.deleteAll();

        // Cria as tarefas iniciais
        Tarefa t1 = new Tarefa("Estudar Spring Boot", "Concluir o guia de API REST");
        t1.setConcluida(true); // Marca a primeira como concluída

        Tarefa t2 = new Tarefa("Estudar Angular", "Aprender sobre componentes Standalone");

        Tarefa t3 = new Tarefa("Estudar Ionic", "Preparar o ambiente mobile");

        // Salva todas de uma vez
        tarefaRepository.saveAll(List.of(t1, t2, t3));

        System.out.println("--- DADOS DE TESTE INSERIDOS COM SUCESSO ---");
    }
}