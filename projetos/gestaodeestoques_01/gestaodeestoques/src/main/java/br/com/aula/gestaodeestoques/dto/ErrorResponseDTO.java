package br.com.aula.gestaodeestoques.dto;

import java.time.Instant;

/**
 * DTO padrão para respostas de erro da API.
 */
public record ErrorResponseDTO(
    Instant timestamp,
    Integer status,
    String error,
    String message,
    String path
) {}
