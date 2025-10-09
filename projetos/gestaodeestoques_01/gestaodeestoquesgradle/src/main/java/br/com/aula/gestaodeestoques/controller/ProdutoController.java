package br.com.aula.gestaodeestoques.controller;

import br.com.aula.gestaodeestoques.model.Categoria;
import br.com.aula.gestaodeestoques.model.Fornecedor;
import br.com.aula.gestaodeestoques.model.Produto;
import br.com.aula.gestaodeestoques.repository.CategoriaRepository;
import br.com.aula.gestaodeestoques.repository.FornecedorRepository;
import br.com.aula.gestaodeestoques.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    public ProdutoController(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, FornecedorRepository fornecedorRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @GetMapping
    public String home() {
        return "redirect:/produtos";
    }

    @GetMapping("/produtos")
    public String listarProdutos(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "lista-produtos";
    }

    @GetMapping("/produtos/novo")
    public String mostrarFormularioNovo(Model model) {
        Iterable<Categoria> categorias = categoriaRepository.findAll();
        Iterable<Fornecedor> fornecedores = fornecedorRepository.findAll();

        model.addAttribute("produto", new Produto(null, "", 0, BigDecimal.ZERO, null, null));
        model.addAttribute("categorias", categorias);
        model.addAttribute("fornecedores", fornecedores);
        model.addAttribute("pageTitle", "Adicionar Novo Produto");
        return "form-produto";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(@ModelAttribute Produto produto, RedirectAttributes ra) {
        produtoRepository.save(produto);
        ra.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/produtos/editar/{id}")
    public String mostrarFormularioEdicao(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Optional<Produto> produtoOpt = produtoRepository.findById(id);
        if (produtoOpt.isPresent()) {
            Iterable<Categoria> categorias = categoriaRepository.findAll();
            Iterable<Fornecedor> fornecedores = fornecedorRepository.findAll();

            model.addAttribute("produto", produtoOpt.get());
            model.addAttribute("categorias", categorias);
            model.addAttribute("fornecedores", fornecedores);
            model.addAttribute("pageTitle", "Editar Produto (ID: " + id + ")");
            return "form-produto";
        } else {
            ra.addFlashAttribute("mensagemErro", "Produto não encontrado!");
            return "redirect:/produtos";
        }
    }

    @GetMapping("/produtos/excluir/{id}")
    public String excluirProduto(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            produtoRepository.deleteById(id);
            ra.addFlashAttribute("mensagem", "Produto excluído com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("mensagemErro", "Erro ao excluir o produto.");
        }
        return "redirect:/produtos";
    }
}
