package br.com.aula.gestaodeestoques.mapper;

import br.com.aula.gestaodeestoques.dto.ProdutoDTO;
import br.com.aula.gestaodeestoques.dto.ProdutoFormDTO;
import br.com.aula.gestaodeestoques.model.Categoria;
import br.com.aula.gestaodeestoques.model.Fornecedor;
import br.com.aula.gestaodeestoques.model.Produto;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public ProdutoDTO toDTO(Produto produto, Categoria categoria, Fornecedor fornecedor) {
        return new ProdutoDTO(
                produto.id(),
                produto.nome(),
                produto.quantidade(),
                produto.preco(),
                categoria != null ? categoria.nome() : "N/A",
                fornecedor != null ? fornecedor.nome() : "N/A"
        );
    }

    public Produto toEntity(ProdutoFormDTO dto) {
        return new Produto(
                dto.id(),
                dto.nome(),
                dto.quantidade(),
                dto.preco(),
                dto.categoriaId(), // <- Consistente com o DTO
                dto.fornecedorId()  // <- Consistente com o DTO
        );
    }

    public ProdutoFormDTO toFormDTO(Produto produto) {
        return new ProdutoFormDTO(
                produto.id(),
                produto.nome(),
                produto.quantidade(),
                produto.preco(),
                produto.categoriaId(), // AGORA ESTA LINHA FUNCIONA!
                produto.fornecedorId()  // AGORA ESTA LINHA FUNCIONA!
        );
    }
}