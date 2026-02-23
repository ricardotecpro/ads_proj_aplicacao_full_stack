package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("CATEGORIA")
public record Categoria(@Id Integer id, String nome) {}