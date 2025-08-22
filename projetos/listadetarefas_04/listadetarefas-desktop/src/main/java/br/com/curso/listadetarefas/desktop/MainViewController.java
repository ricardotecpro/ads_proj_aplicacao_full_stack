package br.com.curso.listadetarefas.desktop;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell; // 1. IMPORTE A CLASSE NECESSÁRIA
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    // ... (Declarações FXML permanecem as mesmas) ...
    @FXML private TableView<Tarefa> tabelaTarefas;
    @FXML private TableColumn<Tarefa, Boolean> colunaConcluida;
    @FXML private TableColumn<Tarefa, String> colunaDescricao;
    @FXML private TableColumn<Tarefa, Void> colunaAcoes;
    @FXML private TextField campoNovaTarefa;
    @FXML private Button botaoAdicionar;
    @FXML private Label labelStatus;
    @FXML private Button botaoAtualizar;

    private final TarefaApiService tarefaApiService = new TarefaApiService();
    private final ObservableList<Tarefa> tarefasObservaveis = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Vincula a lista observável à tabela.
        tabelaTarefas.setItems(tarefasObservaveis);

        // 2. TORNA A TABELA EDITÁVEL
        tabelaTarefas.setEditable(true);

        // Configura como a coluna de descrição irá obter os dados.
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        // 3. CONFIGURA A CÉLULA DA DESCRIÇÃO PARA SER UM CAMPO DE TEXTO EDITÁVEL
        colunaDescricao.setCellFactory(TextFieldTableCell.forTableColumn());

        // 4. DEFINE O QUE ACONTECE QUANDO A EDIÇÃO É CONCLUÍDA (ex: ao pressionar Enter)
        colunaDescricao.setOnEditCommit(event -> {
            // Obtém a tarefa que foi editada
            Tarefa tarefaEditada = event.getRowValue();
            // Define a nova descrição
            tarefaEditada.setDescricao(event.getNewValue());
            // Chama o método para enviar a atualização para a API
            atualizarTarefa(tarefaEditada);
        });

        // Configura a coluna "Concluída" para renderizar um CheckBox.
        colunaConcluida.setCellValueFactory(new PropertyValueFactory<>("concluida"));
        colunaConcluida.setCellFactory(tc -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(event -> {
                    Tarefa tarefa = getTableRow().getItem();
                    if (tarefa != null) {
                        tarefa.setConcluida(checkBox.isSelected());
                        atualizarTarefa(tarefa);
                    }
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); }
                else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        });

        // Configura a coluna "Ações" para renderizar um botão "Deletar".
        Callback<TableColumn<Tarefa, Void>, TableCell<Tarefa, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnDeletar = new Button("Deletar");
            {
                btnDeletar.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex());
                    deletarTarefa(tarefa);
                });
            }
            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); }
                else { setGraphic(btnDeletar); }
            }
        };
        colunaAcoes.setCellFactory(cellFactory);

        // Carrega os dados da API ao iniciar a tela.
        carregarTarefas();
    }

    // ... (O restante dos métodos permanece o mesmo) ...
    @FXML
    private void atualizarListaDeTarefas() {
        carregarTarefas();
    }

    @FXML
    private void adicionarTarefa() {
        String descricao = campoNovaTarefa.getText();
        if (descricao == null || descricao.trim().isEmpty()) {
            labelStatus.setText("Status: Descrição não pode ser vazia.");
            return;
        }

        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setDescricao(descricao.trim());
        novaTarefa.setConcluida(false);

        executarEmBackground(() -> {
            Tarefa tarefaCriada = tarefaApiService.adicionarTarefa(novaTarefa);
            if (tarefaCriada != null) {
                Platform.runLater(() -> {
                    tarefasObservaveis.add(tarefaCriada);
                    campoNovaTarefa.clear();
                    labelStatus.setText("Status: Tarefa adicionada com sucesso!");
                });
            }
        });
    }

    private void carregarTarefas() {
        executarEmBackground(() -> {
            List<Tarefa> tarefasDaApi = tarefaApiService.listarTarefas();
            Platform.runLater(() -> {
                tarefasObservaveis.setAll(tarefasDaApi);
                labelStatus.setText("Status: Tarefas carregadas.");
            });
        });
    }

    private void atualizarTarefa(Tarefa tarefa) {
        executarEmBackground(() -> {
            tarefaApiService.atualizarTarefa(tarefa);
            Platform.runLater(() -> labelStatus.setText("Status: Tarefa '" + tarefa.getDescricao() + "' atualizada."));
        });
    }

    private void deletarTarefa(Tarefa tarefa) {
        executarEmBackground(() -> {
            tarefaApiService.deletarTarefa(tarefa.getId());
            Platform.runLater(() -> {
                tarefasObservaveis.remove(tarefa);
                labelStatus.setText("Status: Tarefa deletada.");
            });
        });
    }

    private void executarEmBackground(Runnable acao) {
        labelStatus.setText("Status: Processando...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                acao.run();
                return null;
            }
        };
        task.setOnFailed(e -> {
            task.getException().printStackTrace();
            Platform.runLater(() -> labelStatus.setText("Status: Erro na operação. Veja o console."));
        });
        new Thread(task).start();
    }
}