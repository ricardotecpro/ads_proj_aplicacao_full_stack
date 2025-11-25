package br.com.aula.gestaodeestoques.service.impl;

import br.com.aula.gestaodeestoques.dto.ProdutoDTO;
import br.com.aula.gestaodeestoques.dto.ProdutoFormDTO;
import br.com.aula.gestaodeestoques.exception.ResourceNotFoundException;
import br.com.aula.gestaodeestoques.mapper.ProdutoMapper;
import br.com.aula.gestaodeestoques.model.Categoria;
import br.com.aula.gestaodeestoques.model.Fornecedor;
import br.com.aula.gestaodeestoques.model.Produto;
import br.com.aula.gestaodeestoques.repository.CategoriaRepository;
import br.com.aula.gestaodeestoques.repository.FornecedorRepository;
import br.com.aula.gestaodeestoques.repository.ProdutoRepository;
import br.com.aula.gestaodeestoques.service.ProdutoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ProdutoMapper produtoMapper;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, FornecedorRepository fornecedorRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.produtoMapper = produtoMapper;
    }

    @Override
    @Transactional
    public ProdutoDTO create(ProdutoFormDTO produtoFormDTO) {
        findCategoriaOrThrow(produtoFormDTO.categoriaId());
        findFornecedorOrThrow(produtoFormDTO.fornecedorId());
        Produto produto = produtoMapper.toEntity(produtoFormDTO);
        Produto savedProduto = produtoRepository.save(produto);
        return mapToProdutoDTO(savedProduto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProdutoDTO> findAll() {
        return StreamSupport.stream(produtoRepository.findAll().spliterator(), false)
                .map(this::mapToProdutoDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProdutoDTO findById(Integer id) {
        Produto produto = findProdutoOrThrow(id);
        return mapToProdutoDTO(produto);
    }

    @Override
    @Transactional
    public ProdutoDTO update(Integer id, ProdutoFormDTO produtoFormDTO) {
        findProdutoOrThrow(id); // Garante que o produto existe
        findCategoriaOrThrow(produtoFormDTO.categoriaId());
        findFornecedorOrThrow(produtoFormDTO.fornecedorId());

        Produto produtoParaAtualizar = new Produto(
            id, 
            produtoFormDTO.nome(), 
            produtoFormDTO.quantidade(), 
            produtoFormDTO.preco(), 
            produtoFormDTO.categoriaId(), 
            produtoFormDTO.fornecedorId()
        );

        Produto produtoAtualizado = produtoRepository.save(produtoParaAtualizar);
        return mapToProdutoDTO(produtoAtualizado);
    }

    // MÉTODO 'DELETE' FALTANTE ADICIONADO AQUI
    @Override
    @Transactional
    public void delete(Integer id) {
        if (!produtoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado com o ID: " + id);
        }
        produtoRepository.deleteById(id);
    }

    // MÉTODOS AUXILIARES
    private ProdutoDTO mapToProdutoDTO(Produto produto) {
        Categoria categoria = findCategoriaOrThrow(produto.categoriaId());
        Fornecedor fornecedor = findFornecedorOrThrow(produto.fornecedorId());
        return produtoMapper.toDTO(produto, categoria, fornecedor);
    }

    private Produto findProdutoOrThrow(Integer id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
    }
    private Categoria findCategoriaOrThrow(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com o ID: " + id));
    }
    private Fornecedor findFornecedorOrThrow(Integer id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com o ID: " + id));
    }
}