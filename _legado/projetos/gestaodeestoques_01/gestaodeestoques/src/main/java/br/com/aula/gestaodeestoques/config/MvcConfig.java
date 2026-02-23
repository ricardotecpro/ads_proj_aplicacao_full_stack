package br.com.aula.gestaodeestoques.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * Mapeia a URL "/login" diretamente para a view "login.html" do Thymeleaf.
     * Isso evita o erro de "Circular view path" que pode ocorrer ao usar um @GetMapping em um controller.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }
}
