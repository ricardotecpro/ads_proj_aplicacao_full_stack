package br.com.aula.gestaodeestoques.controller;

import br.com.aula.gestaodeestoques.dto.CategoriaDTO;
import br.com.aula.gestaodeestoques.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para gestão de categorias")
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @Operation(summary = "Lista todas as categorias")
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Busca uma categoria por ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cria uma nova categoria")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> create(@Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO newDto = service.create(categoriaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @Operation(summary = "Atualiza uma categoria existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> update(@PathVariable Integer id, @Valid @RequestBody CategoriaDTO categoriaDTO) {
        CategoriaDTO updatedDto = service.update(id, categoriaDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @Operation(summary = "Apaga uma categoria")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
