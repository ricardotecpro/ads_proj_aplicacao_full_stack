package br.com.aula.gestaodeestoques.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("FORNECEDOR")
public record Fornecedor(@Id Integer id, String nome, String cnpj) {}
