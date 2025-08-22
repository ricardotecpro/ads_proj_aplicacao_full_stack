module br.com.curso.listadetarefas.desktop {
    // Requer as bibliotecas do JavaFX que vamos usar.
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Requer o cliente HTTP nativo do Java.
    requires java.net.http;

    // Requer a biblioteca Jackson para manipulação de JSON.
    requires com.fasterxml.jackson.databind;

    // "Abre" nosso pacote para as bibliotecas que precisam acessá-lo via reflexão.
    // Adicionamos javafx.base à lista.
    opens br.com.curso.listadetarefas.desktop to com.fasterxml.jackson.databind, javafx.fxml, javafx.base;

    // "Exporta" nosso pacote para que o motor do JavaFX possa iniciar a aplicação.
    exports br.com.curso.listadetarefas.desktop to javafx.graphics;
}