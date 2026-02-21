package br.com.aula.gestaodeestoques.service;
import br.com.aula.gestaodeestoques.dto.CategoriaDTO;
import java.util.List;
public interface CategoriaService {
    CategoriaDTO create(CategoriaDTO categoriaDTO);
    List<CategoriaDTO> findAll();
    CategoriaDTO findById(Integer id);
    CategoriaDTO update(Integer id, CategoriaDTO categoriaDTO);
    void delete(Integer id);
}