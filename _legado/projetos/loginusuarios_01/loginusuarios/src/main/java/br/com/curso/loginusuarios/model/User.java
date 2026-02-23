package br.com.curso.loginusuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING) // Diz ao JPA para guardar o nome do enum (ex: "ADMIN")
    @Column(nullable = false)
    private Role role;

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}