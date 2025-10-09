package br.com.aula.gestaodeestoques.service;
import br.com.aula.gestaodeestoques.dto.ProdutoDTO;
import br.com.aula.gestaodeestoques.dto.ProdutoFormDTO;
import java.util.List;
public interface ProdutoService {
    ProdutoDTO create(ProdutoFormDTO produtoFormDTO);
    List<ProdutoDTO> findAll();
    ProdutoDTO findById(Integer id);
    ProdutoDTO update(Integer id, ProdutoFormDTO produtoFormDTO);
    void delete(Integer id);
}