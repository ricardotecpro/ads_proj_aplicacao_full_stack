package br.com.aula.gestaodeestoques.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("USUARIO")
public record Usuario(@Id Long id, String login, String senha) {}
