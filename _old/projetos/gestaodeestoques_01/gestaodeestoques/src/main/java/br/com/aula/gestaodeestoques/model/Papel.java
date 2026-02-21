package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("PAPEL")
public record Papel(@Id Long id, String nome) {}