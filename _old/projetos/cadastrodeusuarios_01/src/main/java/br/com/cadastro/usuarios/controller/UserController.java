package br.com.cadastro.usuarios.controller;

import br.com.cadastro.usuarios.entity.User;
import br.com.cadastro.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userForm", new User()); // Objeto para o formulário
        return "index";
    }

    @PostMapping("/save")
    public String saveUser(User user) {
        // Codifica a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/"; // Redireciona para a página principal
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/"; // Se não achar, volta para o início
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userForm", userOpt.get());
        return "index"; // Reutiliza a mesma página, mas com o formulário preenchido
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}