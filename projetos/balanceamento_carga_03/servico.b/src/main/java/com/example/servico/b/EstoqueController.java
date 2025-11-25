package com.example.servico.b;
// servico-b/src/main/java/com/example/servico.b/EstoqueController.java

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class EstoqueController {

    @GetMapping("/estoque/{produtoId}")
    public String getEstoque(@PathVariable String produtoId) {
        try {
            // Simula latência variável (ex: I/O de banco)
            long delay = ThreadLocalRandom.current().nextLong(50, 200);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Produto " + produtoId + ": 10 unidades";
    }
}