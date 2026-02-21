package br.com.aula.gestaodeestoques.exception;

import br.com.aula.gestaodeestoques.dto.ErrorResponseDTO;
import br.com.aula.gestaodeestoques.dto.ValidationErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        ValidationErrorResponseDTO validationError = new ValidationErrorResponseDTO(
                Instant.now(), HttpStatus.BAD_REQUEST.value(), "Erro de Validação",
                "Um ou mais campos contêm dados inválidos.", request.getRequestURI(), fieldErrors);
        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            Instant.now(), HttpStatus.NOT_FOUND.value(), "Recurso Não Encontrado",
            ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorResponseDTO error = new ErrorResponseDTO(
            Instant.now(), HttpStatus.BAD_REQUEST.value(), "Violação de Integridade de Dados",
            "Não é possível executar a operação. O recurso pode estar em uso por outros itens.",
            request.getRequestURI());
        logger.warn("Tentativa de violação de integridade de dados: {}", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Ocorreu um erro inesperado: ", ex);
        ErrorResponseDTO error = new ErrorResponseDTO(
            Instant.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro Interno no Servidor",
            "Ocorreu um erro inesperado. Por favor, contate o suporte.", request.getRequestURI());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}