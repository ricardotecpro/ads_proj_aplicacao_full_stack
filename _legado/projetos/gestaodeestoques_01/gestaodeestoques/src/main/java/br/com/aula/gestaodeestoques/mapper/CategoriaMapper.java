package br.com.aula.gestaodeestoques.mapper;
import br.com.aula.gestaodeestoques.dto.CategoriaDTO;
import br.com.aula.gestaodeestoques.model.Categoria;
import org.springframework.stereotype.Component;
@Component
public class CategoriaMapper {
    public CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.id(), categoria.nome());
    }
    public Categoria toEntity(CategoriaDTO categoriaDTO) {
        return new Categoria(categoriaDTO.id(), categoriaDTO.nome());
    }
}