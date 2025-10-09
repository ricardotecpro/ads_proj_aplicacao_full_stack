# 💎 Guia Didático: Gestão de Estoques com Spring Boot

-----

## Módulo 1: 🏛️ Fundamentos e Persistência de Dados

**Objetivo:** Construir a base da aplicação. Ao final deste módulo, o aluno terá uma aplicação Spring Boot funcional que se conecta a um banco de dados em memória, com todo o modelo de dados definido e pronto para ser manipulado.

-----

### \#\#\# Aula 1.1: Gênese do Projeto (Configuração no Spring Initializr)

**Conceito-Chave:** O **Spring Initializr** é uma ferramenta que automatiza a criação da estrutura de projetos Spring, garantindo que todas as dependências e configurações iniciais estejam corretas.

**Ação:** Siga os passos abaixo para criar o esqueleto da nossa aplicação.

1.  **Acesse o site:** Abra o seu navegador e vá para [**start.spring.io**](https://start.spring.io).

2.  **Configure os Metadados do Projeto:** Preencha a seção à esquerda com as seguintes informações.

      * **Project:** `Maven`
      * **Language:** `Java`
      * **Spring Boot:** `3.2.x` (use a versão estável mais recente)
      * **Project Metadata:**
          * **Group:** `br.com.aula`
          * **Artifact:** `gestaodeestoques`
          * **Name:** `gestaodeestoques`
          * **Description:** `API REST para Gestão de Estoques`
          * **Package name:** `br.com.aula.gestaodeestoques`
      * **Packaging:** `Jar`
      * **Java:** `21`

3.  **Adicione as Dependências:** No lado direito, em "Dependencies", adicione as seguintes bibliotecas. Para a API REST que estamos construindo, **não precisaremos** do `Thymeleaf`.

      * `Spring Web`: Essencial para criar APIs REST.
      * `Spring Data JDBC`: Para acesso simplificado ao banco de dados.
      * `H2 Database`: Nosso banco de dados em memória para desenvolvimento.
      * `Spring Security`: Para proteger nossa API.
      * `Validation`: Para validar os dados que recebemos.
      * `Spring Boot DevTools`: Para reinicializações automáticas durante o desenvolvimento.

4.  **Gere e Importe:** Clique em **"GENERATE"**, descompacte o arquivo `.zip` e abra o projeto na sua IDE.

O resultado será um arquivo `pom.xml` que define nosso projeto.

#### Código: `pom.xml` (Arquivo Completo)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.2.5</version>
		<relativePath/>
	</parent>
	<groupId>br.com.aula</groupId>
	<artifactId>gestaodeestoques</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>gestaodeestoques</name>
	<description>API REST para Gestão de Estoques</description>
	<properties>
		<java.version>21</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jdbc</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
</project>
```

-----

### \#\#\# Aula 1.2: Modelagem do Banco de Dados

**Conceito-Chave:** O **Diagrama de Entidade-Relacionamento (ER)** é o mapa que guia a construção do nosso banco de dados. Ele define as entidades (tabelas) e como elas se conectam.

#### Diagrama ER

```mermaid
erDiagram
    CATEGORIA { int id PK, varchar nome }
    FORNECEDOR { int id PK, varchar nome, varchar cnpj }
    PRODUTO { int id PK, varchar nome, int categoria_id FK, int fornecedor_id FK }
    USUARIO { bigint id PK, varchar login }
    PAPEL { bigint id PK, varchar nome }
    "USUARIO_PAPEL" { bigint usuario_id PK, FK, bigint papel_id PK, FK }

    CATEGORIA ||--|{ PRODUTO : "classifica"
    FORNECEDOR ||--|{ PRODUTO : "fornece"
    USUARIO }o--o{ PAPEL : "possui"
```

**Ação:** Para traduzir este diagrama em um banco de dados, criaremos um arquivo `schema.sql`. O Spring Boot irá executá-lo automaticamente na inicialização. Também configuraremos o `application.properties` para habilitar o console do H2.

#### Código: `src/main/resources/application.properties`

```properties
# Configuração do Banco de Dados H2 em Memória
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Habilita o console do H2 para visualização no navegador
spring.h2.console.enabled=true

# Garante que o schema.sql seja sempre executado na inicialização
spring.sql.init.mode=always
```

#### Código: `src/main/resources/schema.sql`

```sql
-- Garante que o banco seja recriado a cada reinicialização, ótimo para desenvolvimento
DROP TABLE IF EXISTS produto, categoria, fornecedor, usuario_papel, usuario, papel;

CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE fornecedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE
);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE papel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE usuario_papel (
    usuario_id BIGINT NOT NULL,
    papel_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, papel_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (papel_id) REFERENCES papel(id)
);

CREATE TABLE produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    categoria_id INT,
    fornecedor_id INT,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id),
    FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);
```

-----

### \#\#\# Aula 1.3: Mapeamento Objeto-Relacional

**Conceito-Chave:** Usaremos `records` do Java para criar representações imutáveis e concisas das nossas tabelas. As anotações `@Table` e `@Id` do Spring Data conectam a classe Java à tabela e sua chave primária correspondente.

**Ação:** Crie o pacote `br.com.aula.gestaodeestoques.model` e adicione um arquivo `.java` para cada entidade.

#### Código: `model/Categoria.java`

```java
package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("CATEGORIA")
public record Categoria(@Id Integer id, String nome) {}
```

#### Código: `model/Fornecedor.java`

```java
package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("FORNECEDOR")
public record Fornecedor(@Id Integer id, String nome, String cnpj) {}
```

#### Código: `model/Papel.java`

```java
package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("PAPEL")
public record Papel(@Id Long id, String nome) {}
```

#### Código: `model/Usuario.java`

```java
package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("USUARIO")
public record Usuario(@Id Long id, String login, String senha, boolean ativo) {}
```

#### Código: `model/Produto.java`

```java
package br.com.aula.gestaodeestoques.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
@Table("PRODUTO")
public record Produto(@Id Integer id, String nome, int quantidade, BigDecimal preco, Integer categoria_id, Integer fornecedor_id) {}
```

-----

### \#\#\# Aula 1.4: A Camada de Acesso a Dados

**Conceito-Chave:** O padrão **Repository** provê uma abstração para a fonte de dados. O Spring Data eleva isso a outro nível: ao estender `CrudRepository`, ganhamos todos os métodos de CRUD (salvar, buscar, deletar) sem precisar implementá-los.

**Ação:** Crie o pacote `br.com.aula.gestaodeestoques.repository` e adicione as interfaces para cada entidade.

#### Código: `repository/CategoriaRepository.java`

```java
package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoriaRepository extends Cr.CrudRepository<Categoria, Integer> {}
```

#### Código: `repository/FornecedorRepository.java`

```java
package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Fornecedor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface FornecedorRepository extends CrudRepository<Fornecedor, Integer> {}
```

#### Código: `repository/PapelRepository.java`

```java
package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Papel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PapelRepository extends CrudRepository<Papel, Long> {}
```

#### Código: `repository/ProdutoRepository.java`

```java
package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Produto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProdutoRepository extends CrudRepository<Produto, Integer> {}
```

#### Código: `repository/UsuarioRepository.java`

```java
package br.com.aula.gestaodeestoques.repository;
import br.com.aula.gestaodeestoques.model.Papel;
import br.com.aula.gestaodeestoques.model.Usuario;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Set;
@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);

    @Query("SELECT p.* FROM papel p JOIN usuario_papel up ON p.id = up.papel_id WHERE up.usuario_id = :usuarioId")
    Set<Papel> findPapeisByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("INSERT INTO usuario_papel (usuario_id, papel_id) VALUES (:usuarioId, :papelId)")
    void adicionarPapel(@Param("usuarioId") Long usuarioId, @Param("papelId") Long papelId);
}
```

-----

### Conclusão do Módulo 1 e Próximos Passos

**Parabéns\!** Você construiu a espinha dorsal da nossa aplicação. Neste ponto, temos:

  - Um projeto Spring Boot configurado.
  - Um esquema de banco de dados completo.
  - Classes Java que representam nossas tabelas.
  - Uma camada de acesso a dados poderosa e pronta para uso.

Se você executar a aplicação agora (`GestaodeestoquesApplication.java`) e acessar `http://localhost:8080/h2-console`, você poderá se conectar ao banco (`JDBC URL: jdbc:h2:mem:testdb`, `User Name: sa`) e ver todas as tabelas que criamos, prontas para receber dados.

No **próximo módulo**, vamos dar vida a essa estrutura, criando a **camada de serviço** onde a lógica de negócio da nossa aplicação irá residir.
