package br.com.curso.listadetarefas.api.tarefa;

import jakarta.persistence.*;
import lombok.Data;

@Data // Anotação do Lombok que gera Getters, Setters, toString, etc.
@Entity // Marca esta classe como uma entidade JPA (será uma tabela no banco de dados).
@Table(name = "tb_tarefas") // Define o nome da tabela.
public class Tarefa {
    @Id // Marca o campo 'id' como a chave primária.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura o 'id' para ser autoincrementado.
    private Long id;
    private String descricao;
    private boolean concluida;
}