package br.com.aula.gestaodeestoques.exception;

// A anotação @ResponseStatus foi removida para centralizar a lógica no GlobalExceptionHandler.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}