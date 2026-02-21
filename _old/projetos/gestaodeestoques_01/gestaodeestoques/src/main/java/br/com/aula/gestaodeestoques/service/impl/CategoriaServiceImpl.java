package br.com.aula.gestaodeestoques.service.impl;

import br.com.aula.gestaodeestoques.dto.CategoriaDTO;
import br.com.aula.gestaodeestoques.exception.ResourceNotFoundException;
import br.com.aula.gestaodeestoques.mapper.CategoriaMapper;
import br.com.aula.gestaodeestoques.model.Categoria;
import br.com.aula.gestaodeestoques.repository.CategoriaRepository;
import br.com.aula.gestaodeestoques.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    @Autowired
    private CategoriaMapper mapper;

    @Override
    @Transactional
    public CategoriaDTO create(CategoriaDTO categoriaDTO) {
        // Garante que não estamos tentando criar com um ID já existente
        categoriaDTO = new CategoriaDTO(null, categoriaDTO.nome());
        Categoria categoria = mapper.toEntity(categoriaDTO);
        Categoria savedCategoria = repository.save(categoria);
        return mapper.toDTO(savedCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));
    }

    // MÉTODO 'UPDATE' FALTANTE ADICIONADO AQUI
    @Override
    @Transactional
    public CategoriaDTO update(Integer id, CategoriaDTO categoriaDTO) {
        // 1. Verifica se a categoria que queremos atualizar realmente existe
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));

        // 2. Converte o DTO para uma entidade, garantindo que o ID correto está sendo usado
        Categoria categoriaParaAtualizar = new Categoria(id, categoriaDTO.nome());

        // 3. O método 'save' do Spring Data faz um 'update' se o ID já existir
        Categoria categoriaAtualizada = repository.save(categoriaParaAtualizar);

        return mapper.toDTO(categoriaAtualizada);
    }

    // MÉTODO 'DELETE' FALTANTE ADICIONADO AQUI
    @Override
    @Transactional
    public void delete(Integer id) {
        // 1. Verifica se a categoria que queremos deletar existe antes de tentar a exclusão
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada com o ID: " + id);
        }

        // 2. Se existir, deleta
        repository.deleteById(id);
    }
}