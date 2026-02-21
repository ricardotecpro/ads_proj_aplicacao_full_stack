package br.com.aula.gestaodeestoques.config;

import br.com.aula.gestaodeestoques.model.*;
import br.com.aula.gestaodeestoques.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PapelRepository papelRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Esta verificação garante que o seeder só rode se o banco estiver vazio,
        // evitando duplicação de dados a cada reinicialização com o DevTools.
        if (papelRepository.count() == 0) {
            System.out.println("Nenhum dado encontrado. Populando o banco de dados...");

            // 1. Criar Papéis (Roles)
            Papel adminPapel = papelRepository.save(new Papel(null, "ROLE_ADMIN"));
            Papel userPapel = papelRepository.save(new Papel(null, "ROLE_USER"));

            // 2. Criar Usuários com Senhas Criptografadas
            Usuario admin = new Usuario(null, "admin", passwordEncoder.encode("admin123"), true);
            Usuario user = new Usuario(null, "user", passwordEncoder.encode("user123"), true);
            Usuario savedAdmin = usuarioRepository.save(admin);
            Usuario savedUser = usuarioRepository.save(user);

            // 3. Associar Papéis aos Usuários
            usuarioRepository.adicionarPapel(savedAdmin.id(), adminPapel.id());
            usuarioRepository.adicionarPapel(savedUser.id(), userPapel.id());

            // 4. Criar Categorias e Fornecedores
            Categoria hardware = categoriaRepository.save(new Categoria(null, "Hardware"));
            Categoria software = categoriaRepository.save(new Categoria(null, "Software"));
            Categoria perifericos = categoriaRepository.save(new Categoria(null, "Periféricos"));

            Fornecedor fornecedorA = fornecedorRepository.save(new Fornecedor(null, "Tech Distribuidora", "11.111.111/0001-11"));
            Fornecedor fornecedorB = fornecedorRepository.save(new Fornecedor(null, "Componentes SA", "22.222.222/0001-22"));
            Fornecedor fornecedorC = fornecedorRepository.save(new Fornecedor(null, "Soft World", "33.333.333/0001-33"));

            // 5. Criar Produtos, associando-os às categorias e fornecedores já salvos
            List<Produto> produtos = Arrays.asList(
                    new Produto(null, "Notebook Gamer Pro", 15, new BigDecimal("8500.00"), hardware.id(), fornecedorA.id()),
                    new Produto(null, "Mouse Óptico Sem Fio", 150, new BigDecimal("120.50"), perifericos.id(), fornecedorB.id()),
                    new Produto(null, "Teclado Mecânico RGB", 75, new BigDecimal("450.75"), perifericos.id(), fornecedorB.id()),
                    new Produto(null, "Licença Sistema Operacional", 200, new BigDecimal("799.90"), software.id(), fornecedorC.id()),
                    new Produto(null, "SSD NVMe 2TB", 50, new BigDecimal("950.00"), hardware.id(), fornecedorA.id()),
                    new Produto(null, "Monitor Ultrawide 34\"", 25, new BigDecimal("2800.00"), perifericos.id(), fornecedorA.id())
            );
            produtoRepository.saveAll(produtos);

            System.out.println("Banco de dados populado com sucesso!");
        } else {
            System.out.println("O banco de dados já contém dados. O seeder não será executado.");
        }
    }
}