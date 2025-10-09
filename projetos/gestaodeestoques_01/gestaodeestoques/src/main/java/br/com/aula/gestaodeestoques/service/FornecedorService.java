package br.com.aula.gestaodeestoques.service;

import br.com.aula.gestaodeestoques.dto.FornecedorDTO;
import java.util.List;

public interface FornecedorService {
    // Adicione as assinaturas de todos os métodos que sua implementação terá.

    FornecedorDTO create(FornecedorDTO fornecedorDTO);

    List<FornecedorDTO> findAll();

    FornecedorDTO findById(Integer id);

    FornecedorDTO update(Integer id, FornecedorDTO fornecedorDTO);

    void delete(Integer id);
}