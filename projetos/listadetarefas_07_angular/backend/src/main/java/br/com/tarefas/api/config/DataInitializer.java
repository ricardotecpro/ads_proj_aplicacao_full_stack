package br.com.tarefas.api.config;

import br.com.tarefas.api.model.Tarefa;
import br.com.tarefas.api.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Componente que executa um código na inicialização da aplicação Spring Boot.
 * Ideal para popular o banco de dados com dados de teste para desenvolvimento.
 */
@Component // Anotação que transforma a classe em um Bean gerenciado pelo Spring.
public class DataInitializer implements CommandLineRunner {

    // Injeção de dependência do repositório de tarefas.
    // O Spring fornecerá uma instância de TarefaRepository para esta classe.
    private final TarefaRepository tarefaRepository;

    @Autowired // Boas práticas recomendam a injeção via construtor.
    public DataInitializer(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    /**
     * Este método é executado automaticamente pelo Spring Boot na inicialização.
     * @param args Argumentos de linha de comando (não utilizados aqui).
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        // Limpa o repositório para evitar duplicação a cada reinicialização
        tarefaRepository.deleteAll();

        // Criação de uma lista de tarefas iniciais com temas relevantes ao projeto
        Tarefa t1 = new Tarefa();
        t1.setTitulo("Configurar o backend Spring Boot com MVC");
        t1.setConcluida(true);

        Tarefa t2 = new Tarefa();
        t2.setTitulo("Criar a entidade Tarefa e o Repository");
        t2.setConcluida(true);

        Tarefa t3 = new Tarefa();
        t3.setTitulo("Desenvolver a camada de Serviço e o Controller");
        t3.setConcluida(false);

        Tarefa t4 = new Tarefa();
        t4.setTitulo("Estudar e configurar o CORS para o frontend");
        t4.setConcluida(false);

        Tarefa t5 = new Tarefa();
        t5.setTitulo("Inicializar o projeto Vue.js com a CLI");
        t5.setConcluida(false);

        Tarefa t6 = new Tarefa();
        t6.setTitulo("Criar um serviço no frontend para consumir a API");
        t6.setConcluida(false);

        Tarefa t7 = new Tarefa();
        t7.setTitulo("Desenvolver o componente principal para listar e adicionar tarefas");
        t7.setConcluida(false);

        // Salva todas as tarefas no banco de dados de uma só vez
        // O método saveAll é mais performático do que salvar uma por uma em um loop.
        tarefaRepository.saveAll(Arrays.asList(t1, t2, t3, t4, t5, t6, t7));

        // Feedback no console para o desenvolvedor
        System.out.println("✅ Base de dados inicializada com 7 tarefas.");
    }
}