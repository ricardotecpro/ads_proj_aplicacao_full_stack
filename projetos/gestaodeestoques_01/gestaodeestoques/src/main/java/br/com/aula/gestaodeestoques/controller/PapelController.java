package br.com.aula.gestaodeestoques.controller;

import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.service.PapelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/papeis")
@Tag(name = "Papéis", description = "Endpoints para visualização de papéis de usuário")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')") // Protege TODA a classe
public class PapelController {

    // 1. Injeção de dependência via construtor (melhor prática)
    private final PapelService papelService;

    public PapelController(PapelService papelService) {
        this.papelService = papelService;
    }

    // 2. Implementação do método findAll
    @GetMapping
    @Operation(summary = "Lista todos os papéis (roles) do sistema")
    public ResponseEntity<List<Papel>> findAll() {
        List<Papel> papeis = papelService.findAll();
        return ResponseEntity.ok(papeis);
    }
}