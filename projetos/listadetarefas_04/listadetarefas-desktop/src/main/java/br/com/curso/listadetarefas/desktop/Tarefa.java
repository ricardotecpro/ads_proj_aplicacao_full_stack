package br.com.curso.listadetarefas.desktop;

// A anotação @JsonIgnoreProperties(ignoreUnknown = true) é útil para
// evitar erros caso o JSON da API tenha campos que não existem nesta classe.
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tarefa {
    private Long id;
    private String descricao;
    private boolean concluida;

    // Getters e Setters são necessários para o JavaFX TableView e para o Jackson.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
}