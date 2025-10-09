package br.com.aula.gestaodeestoques.service.impl;

import br.com.aula.gestaodeestoques.dto.FornecedorDTO;
import br.com.aula.gestaodeestoques.exception.ResourceNotFoundException;
import br.com.aula.gestaodeestoques.mapper.FornecedorMapper;
import br.com.aula.gestaodeestoques.model.Fornecedor;
import br.com.aula.gestaodeestoques.repository.FornecedorRepository;
import br.com.aula.gestaodeestoques.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class FornecedorServiceImpl implements FornecedorService {

    @Autowired
    private FornecedorRepository repository;

    @Autowired
    private FornecedorMapper mapper;

    @Override
    @Transactional
    public FornecedorDTO create(FornecedorDTO fornecedorDTO) {
        // Garante que o ID seja nulo para uma operação de criação
        Fornecedor fornecedor = mapper.toEntity(new FornecedorDTO(null, fornecedorDTO.nome(), fornecedorDTO.cnpj()));
        Fornecedor savedFornecedor = repository.save(fornecedor);
        return mapper.toDTO(savedFornecedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FornecedorDTO> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FornecedorDTO findById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com o ID: " + id));
    }

    @Override
    @Transactional
    public FornecedorDTO update(Integer id, FornecedorDTO fornecedorDTO) {
        // Verifica se o fornecedor a ser atualizado existe
        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com o ID: " + id));

        // Mapeia o DTO para a entidade, garantindo que o ID correto seja usado
        Fornecedor fornecedorParaAtualizar = mapper.toEntity(new FornecedorDTO(id, fornecedorDTO.nome(), fornecedorDTO.cnpj()));

        Fornecedor fornecedorAtualizado = repository.save(fornecedorParaAtualizar);
        return mapper.toDTO(fornecedorAtualizado);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // Verifica se o fornecedor existe antes de tentar deletar
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Fornecedor não encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }
}