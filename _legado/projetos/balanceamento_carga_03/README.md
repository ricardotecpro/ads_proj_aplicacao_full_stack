## Guia do Projeto: Teste de Carga Visual (Java 21, Gatling, Grafana)



### Método 1: Interface Web (start.spring.io)

1.  Acesse `https://start.spring.io`
2.  Configure o "Project Metadata" (Metadados do Projeto):
      * **Project**: `Maven`
      * **Language**: `Java`
      * **Spring Boot**: `3.3.0` (ou a versão estável mais recente que suporte Java 21)
      * **Group**: `com.example`
      * **Artifact**: `servico-a` (repita o processo com `servico-b` para o outro módulo)
      * **Name**: `servico-a`
      * **Packaging**: `Jar`
      * **Java**: `21`
3.  Adicione as Dependências (clique em **"ADD DEPENDENCIES..."**):
      * `Spring Web`: (Para criar controladores REST)
      * `Spring Boot Actuator`: (Para expor endpoints de gerenciamento, como `/health` e `/metrics`)
      * `Prometheus`: (Para formatar as métricas do Actuator para o Prometheus)
4.  Clique em **"GENERATE"**.
5.  Extraia o arquivo `.zip` baixado. Esta é a estrutura base do seu projeto.

-----

### Método 2: Linha de Comando (cURL)

Este método é ideal para automação e evita a interface gráfica.

#### Para o `servico-a`

Execute o seguinte comando no seu terminal. Ele baixa e descompacta o projeto.

```bash
# Baixa o .zip
curl https://start.spring.io/starter.zip \
    -d type=maven-project \
    -d language=java \
    -d platformVersion=3.3.0 \
    -d javaVersion=21 \
    -d groupId=com.example \
    -d artifactId=servico-a \
    -d name=servico-a \
    -d packaging=jar \
    -d dependencies=web,actuator,prometheus \
    -o servico-a.zip

# Descompacta
unzip servico-a.zip -d servico-a
rm servico-a.zip
```

#### Para o `servico-b`

```bash
# Baixa o .zip
curl https://start.spring.io/starter.zip \
    -d type=maven-project \
    -d language=java \
    -d platformVersion=3.3.0 \
    -d javaVersion=21 \
    -d groupId=com.example \
    -d artifactId=servico-b \
    -d name=servico-b \
    -d packaging=jar \
    -d dependencies=web,actuator,prometheus \
    -o servico-b.zip

# Descompacta
unzip servico-b.zip -d servico-b
rm servico-b.zip
```

-----

### Método 3: Dentro da IDE

#### IntelliJ IDEA (Ultimate Edition)

1.  Vá em `File` \> `New` \> `Project...`.
2.  Selecione `Spring Initializr` no painel esquerdo.
3.  Preencha os metadados:
      * **Name**: `servico-a`
      * **Location**: (Escolha o diretório pai)
      * **Language**: `Java`
      * **Type**: `Maven`
      * **Group**: `com.example`
      * **Package name**: `com.example.servicoa`
      * **Java Version**: `SDK 21`
4.  Clique em `Next`.
5.  Na tela de dependências, selecione a versão do Spring Boot (ex: 3.3.0) e procure por:
      * **Web**: `Spring Web`
      * **Ops**: `Spring Boot Actuator`
      * **Observability**: `Prometheus`
6.  Clique em `Create`.

#### Visual Studio Code (com o "Spring Initializr Java Support" do Extension Pack for Java)

1.  Abra a paleta de comandos: `Ctrl+Shift+P` (Windows/Linux) ou `Cmd+Shift+P` (macOS).
2.  Digite e selecione `Spring Initializr: Create a Maven Project...`.
3.  Selecione a versão do Spring Boot (ex: `3.3.0`).
4.  Selecione a linguagem: `Java`.
5.  Digite o Group ID: `com.example`.
6.  Digite o Artifact ID: `servico-a`.
7.  Selecione o tipo de empacotamento: `Jar`.
8.  Selecione a versão do Java: `21`.
9.  Procure e marque as dependências (pressione `Enter` após selecionar):
      * `Spring Web`
      * `Spring Boot Actuator`
      * `Prometheus`
10. Escolha o local para gerar o projeto.


-----

```
projeto-teste-carga/
├── .gitignore
├── docker-compose.yml       <-- (Módulo 2: Orquestrador do Prometheus e Grafana)
├── prometheus.yml           <-- (Módulo 2: Configuração de scrape do Prometheus)
|
├── servico-a/               <-- (Módulo 1: Spring Boot API de Pedidos)
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml              <-- (Dependências: Web, Actuator, Prometheus)
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── example/
│       │   │           └── servicoa/
│       │   │               ├── PedidoController.java
│       │   │               └── ServicoAApplication.java
│       │   └── resources/
│       │       └── application.properties  <-- (server.port=8081)
│       └── test/
│           └── ...
│
├── servico-b/               <-- (Módulo 1: Spring Boot API de Estoque)
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml              <-- (Dependências: Web, Actuator, Prometheus)
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/
│       │   │       └── example/
│       │   │           └── servicob/
│       │   │               ├── EstoqueController.java
│       │   │               └── ServicoBApplication.java
│       │   └── resources/
│       │       └── application.properties  <-- (server.port=8082)
│       └── test/
│           └── ...
│
└── gatling-teste/           <-- (Módulo 3: Projeto de Teste de Carga)
    ├── .mvn/
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml              <-- (Dependências do Gatling, ex: gatling-maven-plugin)
    └── src/
        └── test/
            ├── resources/
            │   └── gatling.conf  <-- (Configurações opcionais do Gatling)
            └── scala/
                └── simulations/
                    └── CargaApiSimulation.scala  <-- (Script do teste de carga)
```

-----



Este guia detalha a criação de um ambiente para testes de carga visuais em tempo real, monitorando a latência e a taxa de transferência entre dois microsserviços Spring Boot 21.






### 1\. Arquitetura Alvo

Utilizaremos uma arquitetura desacoplada para simular um cenário real. O Gatling ataca o `servico-a`, que por sua vez depende do `servico-b`. O Prometheus coleta métricas de ambos os serviços, e o Grafana exibe o dashboard.

```mermaid
flowchart TD
    subgraph "Ferramentas"
        Gatling(Gatling Engine)
        Grafana(Grafana Dashboard)
        Prometheus(Prometheus Scraper)
    end

    subgraph "Backend (Java 21 / Spring Boot)"
        SvcA[Serviço A: Pedidos <br> (Porta: 8081)]
        SvcB[Serviço B: Estoque <br> (Porta: 8082)]
    end

    Gatling -- 1. Carga HTTP (Usuários Virtuais) --> SvcA
    SvcA -- 2. Chamada REST interna --> SvcB
    
    Prometheus -- 3. Scrape (métricas) --> SvcA
    Prometheus -- 3. Scrape (métricas) --> SvcB
    Grafana -- 4. Query (PromQL) --> Prometheus
```

-----

### 2\. Módulo 1: Microsserviços Alvo (Spring Boot 21)

Criaremos dois serviços. Ambos devem ter as mesmas dependências do Micrometer para expor métricas ao Prometheus.

#### 2.1. Dependências Maven (pom.xml)

Adicione a todos os serviços:

```xml
<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

#### 2.2. Configuração (application.properties)

**Serviço B (Estoque): `servico-b/src/main/resources/application.properties`**

```properties
# Porta do Serviço B
server.port=8082
# Nome da Aplicação (para métricas)
spring.application.name=servico-b

# Ativa Virtual Threads (Java 21+)
spring.threads.virtual.enabled=true

# Expor o endpoint /actuator/prometheus
management.endpoints.web.exposure.include=prometheus,health
management.endpoint.health.show-details=always
```

**Serviço A (Pedidos): `servico-a/src/main/resources/application.properties`**

```properties
# Porta do Serviço A
server.port=8081
# Nome da Aplicação (para métricas)
spring.application.name=servico-a

# Ativa Virtual Threads (Java 21+)
spring.threads.virtual.enabled=true

# Expor o endpoint /actuator/prometheus
management.endpoints.web.exposure.include=prometheus,health
management.endpoint.health.show-details=always

# URL do serviço dependente
app.servico-b.url=http://localhost:8082
```

#### 2.3. Código: Serviço B (Estoque)

Simula uma operação custosa (ex: consulta em banco de dados) com um atraso.

```java
// servico-b/src/main/java/com/example/servicob/EstoqueController.java
package com.example.servicob;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class EstoqueController {

    @GetMapping("/estoque/{produtoId}")
    public String getEstoque(@PathVariable String produtoId) {
        try {
            // Simula latência variável (ex: I/O de banco)
            long delay = ThreadLocalRandom.current().nextLong(50, 200);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Produto " + produtoId + ": 10 unidades";
    }
}
```

#### 2.4. Código: Serviço A (Pedidos)

Chama o Serviço B usando `RestTemplate` (ou `WebClient`).

```java
// servico-a/src/main/java/com/example/servicoa/PedidoController.java
package com.example.servicoa;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@RestController
public class PedidoController {

    private final RestTemplate restTemplate;
    private final String servicoBUrl;

    public PedidoController(RestTemplateBuilder builder, @Value("${app.servico-b.url}") String servicoBUrl) {
        this.restTemplate = builder.build();
        this.servicoBUrl = servicoBUrl;
    }

    // Endpoint principal que será testado pelo Gatling
    @GetMapping("/fazer-pedido")
    public String fazerPedido() {
        // Simula a busca por um produto aleatório
        int produtoId = new Random().nextInt(1000);
        
        // Chama o Serviço B
        String estoqueInfo = restTemplate.getForObject(
                servicoBUrl + "/estoque/" + produtoId,
                String.class
        );

        return "Pedido criado. Info: " + estoqueInfo;
    }
}

```

-----

### 3\. Módulo 2: Pilha de Observabilidade (Docker)

Usaremos Docker Compose para subir o Prometheus e o Grafana.

#### 3.1. Configuração Prometheus (`prometheus.yml`)

Crie este arquivo na raiz do projeto. Ele instrui o Prometheus a "raspar" (scrape) as métricas dos endpoints `/actuator/prometheus` dos nossos serviços.

```yaml
# prometheus.yml
global:
  scrape_interval: 10s # Intervalo de coleta

scrape_configs:
  # Coleta métricas do Serviço A (Pedidos)
  - job_name: 'servico-a'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8081'] # 'host.docker.internal' permite ao Docker acessar o host local

  # Coleta métricas do Serviço B (Estoque)
  - job_name: 'servico-b'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8082']
```

#### 3.2. Docker Compose (`docker-compose.yml`)

Este arquivo orquestra a pilha de monitoramento.

```yaml
# docker-compose.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    # Permite que o container acesse os serviços no host
    extra_hosts:
      - "host.docker.internal:host-gateway" 

  grafana:
    image: grafana/grafana-oss:latest
    container_name: grafana
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
    environment:
      # Define o usuário/senha padrão
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

-----

### 4\. Módulo 3: Teste de Carga (Gatling)

#### 4.1. Estrutura do Projeto Gatling

Gatling usa Scala. A estrutura de pastas deve ser:

```
gatling-teste/
  src/
    test/
      scala/
        simulations/
          CargaApiSimulation.scala  <-- Nosso script de teste
```

#### 4.2. Script de Simulação (Gatling)

Este script simula 100 usuários acessando o endpoint `/fazer-pedido` simultaneamente por 30 segundos.

```scala
// CargaApiSimulation.scala
package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class CargaApiSimulation extends Simulation {

  // 1. Configuração do Protocolo HTTP
  val httpProtocol = http
    .baseUrl("http://localhost:8081") // URL base do Serviço A
    .acceptHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  // 2. Definição do Cenário
  val scn = scenario("Cenário de Carga API Pedidos")
    .exec(
      http("Req: Fazer Pedido")
        .get("/fazer-pedido")
        .check(status.is(200)) // Verifica se a resposta é HTTP 200
    )

  // 3. Configuração da Carga (Injeção de Usuários)
  setUp(
    scn.inject(
      // rampUsersPerSec(10).to(100).during(10.seconds), // Rampa de subida
      // nothingFor(5.seconds), // Pausa
      constantUsersPerSec(100).during(30.seconds) // Carga constante
    )
  ).protocols(httpProtocol)
}

```

-----

### 5\. Módulo 4: Execução e Análise Visual

#### 5.1. Passo 1: Iniciar os Serviços Java

Inicie ambos os serviços Spring Boot (em terminais separados ou pela IDE):

```bash
# Terminal 1: Iniciar Serviço B (Estoque)
cd servico-b/
./mvnw spring-boot:run
```

```bash
# Terminal 2: Iniciar Serviço A (Pedidos)
cd servico-a/
./mvnw spring-boot:run
```

*Verificação:* Acesse `http://localhost:8081/actuator/prometheus` e `http://localhost:8082/actuator/prometheus`. Você deve ver as métricas.

#### 5.2. Passo 2: Iniciar a Pilha de Monitoramento

```bash
# Terminal 3: Iniciar Prometheus e Grafana
docker-compose up
```

*Verificação:*

  * Acesse `http://localhost:9090` (Prometheus).
  * Acesse `http://localhost:3000` (Grafana). Login: `admin`/`admin`.

#### 5.3. Passo 3: Configurar o Grafana

1.  Acesse o Grafana (`http://localhost:3000`).
2.  Vá para **Connections** -\> **Data sources** -\> **Add new data source**.
3.  Selecione **Prometheus**.
4.  No campo "Prometheus server URL", insira: `http://prometheus:9090` (Grafana e Prometheus estão na mesma rede Docker).
5.  Clique em **Save & Test**.

#### 5.4. Passo 4: Criar o Dashboard de Análise

1.  Vá em **Dashboards** -\> **New** -\> **New Dashboard**.
2.  Clique em **Add visualization**.
3.  Selecione o Data Source **Prometheus**.

**Painel 1: Taxa de Requisições (RPS) no Serviço A**

Use a query PromQL para medir a taxa de requisições HTTP no `servico-a`.

```promql
// Mede a taxa de requisições por segundo (RPS) no endpoint /fazer-pedido
rate(http_server_requests_seconds_count{job="servico-a", uri="/fazer-pedido"}[1m])
```

**Painel 2: Latência P95 (Serviço A vs Serviço B)**

Use esta query para ver a latência (percentil 95) das chamadas.

```promql
// Latência P95 do endpoint /fazer-pedido (visão do Svc A)
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job="servico-a", uri="/fazer-pedido"}[1m])) by (le))

// Latência P95 da chamada interna (visão do Svc B)
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job="servico-b", uri="/estoque/{produtoId}"}[1m])) by (le))
```

**Painel 3: Requisições com Erro (HTTP 5xx)**

```promql
// Taxa de erros 5xx no Serviço A
rate(http_server_requests_seconds_count{job="servico-a", status=~"5.*"}[1m])
```

#### 5.5. Passo 5: Executar o Gatling e Observar

1.  Execute o Gatling (usando o Maven ou o script `gatling.sh`).

<!-- end list -->

```bash
# Terminal 4: Executar o Teste
cd gatling-teste/
# Se estiver usando o plugin Maven do Gatling
./mvnw gatling:test -Dgatling.simulationClass=simulations.CargaApiSimulation
```

2.  Enquanto o Gatling executa, observe o dashboard do Grafana (`http://localhost:3000`). Você verá os gráficos de RPS subindo, a latência aumentando e, eventualmente, erros (se o sistema não suportar a carga).


