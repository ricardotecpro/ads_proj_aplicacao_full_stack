package br.com.liston.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity // Marca esta classe como uma entidade JPA (tabela no banco)
@Table(name = "tarefas") // Nome da tabela
@Data // Lombok: gera getters, setters, equals, hashCode e toString
@NoArgsConstructor // Lombok: gera um construtor vazio (exigido pelo JPA)
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = true, length = 500)
    private String descricao;

    @Column(nullable = false)
    private boolean concluida = false; // Valor padrão

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Garante que a data de criação seja definida antes de salvar
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Garante que a data de atualização seja definida antes de atualizar
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Tarefa(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }
}