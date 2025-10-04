package br.com.curso.loginusuarios.controller;

import br.com.curso.loginusuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "usuarios";
    }

    // Endpoint para apagar um utilizador, acessível via POST para segurança
    @PostMapping("/apagar/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        // Adicionar verificação para não se auto-apagar, se necessário
        userRepository.deleteById(id);
        return "redirect:/usuarios?deleted";
    }
}