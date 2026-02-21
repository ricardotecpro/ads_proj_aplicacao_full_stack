package br.com.tarefas.load_balancing_01;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.Date;

@RestController
public class ApiController {

    // Injeta o valor da variável de ambiente 'SERVER_ID'
    // Se não for definida, usará 'API-DEFAULT'
    @Value("${SERVER_ID:API-DEFAULT}")
    private String serverId;

    @GetMapping("/api")
    public Map<String, Object> getApiData() {
        // Imprime no log do contêiner qual servidor respondeu
        System.out.println("[" + serverId + "] Recebeu requisição");

        // Retorna o JSON para o dashboard
        return Map.of(
            "servidor", serverId,
            "timestamp", new Date().toString()
        );
    }
}