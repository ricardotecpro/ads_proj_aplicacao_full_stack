package br.com.aula.gestaodeestoques.dto;

import java.time.Instant;
import java.util.Map;

/**
 * DTO específico para retornar respostas de erro de validação (@Valid).
 * Ele estende a estrutura de erro padrão, adicionando um mapa
 * para detalhar os erros de cada campo específico.
 */
public record ValidationErrorResponseDTO(
    Instant timestamp,
    Integer status,
    String error,
    String message,
    String path,
    Map<String, String> fieldErrors
) {}