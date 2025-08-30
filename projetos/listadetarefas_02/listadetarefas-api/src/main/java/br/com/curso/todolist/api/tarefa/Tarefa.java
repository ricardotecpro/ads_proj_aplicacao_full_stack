package br.com.curso.todolist.api.tarefa;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * @Entity: Marca esta classe como uma entidade JPA (uma tabela no banco de dados).
 * @Table(name = "tb_tarefas"): Especifica o nome da tabela no banco.
 * @Data (Lombok): Gera automaticamente getters, setters, toString, equals e hashCode.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tb_tarefas")
public class Tarefa {

    /**
     * @Id: Marca este campo como a chave primária da tabela.
     * @GeneratedValue: Configura a estratégia de geração da chave primária.
     * IDENTITY significa que o próprio banco de dados irá gerar e gerenciar o valor.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private boolean concluida;
}