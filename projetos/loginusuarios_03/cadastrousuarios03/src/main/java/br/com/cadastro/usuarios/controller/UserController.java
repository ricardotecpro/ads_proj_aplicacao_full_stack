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

import java.util.List; // Import necessário
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
        // Lógica para lidar com a senha
        // Se o usuário é novo (id null) ou a senha foi alterada
        if (user.getId() == null || (user.getPassword() != null && !user.getPassword().isEmpty())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Se a senha veio vazia, significa que não queremos alterá-la.
            // Buscamos a senha atual no banco e a mantemos.
            userRepository.findById(user.getId()).ifPresent(existingUser -> {
                user.setPassword(existingUser.getPassword());
            });
        }
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

    /**
     * Mapeia a URL /relatorios para exibir a página de relatórios.
     * @param model O modelo para adicionar atributos para a view.
     * @return O nome do template Thymeleaf a ser renderizado ('relatorios.html').
     */
    @GetMapping("/relatorios")
    public String showReportPage(Model model) {
        // Busca todos os usuários do banco.
        List<User> todosUsuarios = userRepository.findAll();

        // Adiciona a lista ao modelo para que o Thymeleaf possa usá-la.
        // O nome "listaDeUsuariosFiltrada" deve ser o mesmo usado no th:each do HTML.
        model.addAttribute("listaDeUsuariosFiltrada", todosUsuarios);

        // Retorna o nome do arquivo HTML sem a extensão .html
        return "relatorios";
    }
}
