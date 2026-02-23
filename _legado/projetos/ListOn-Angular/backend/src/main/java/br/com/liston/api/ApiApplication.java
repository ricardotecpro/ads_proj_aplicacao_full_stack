package br.com.liston.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// ATENÇÃO: A anotação 'exclude' desativa completamente a segurança do Spring.
// Isso é ideal para simplificar o desenvolvimento, mas NUNCA deve ser usado em produção.
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
