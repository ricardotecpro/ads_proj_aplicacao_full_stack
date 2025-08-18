# 🚀 Sistema Full Stack Completo 02

## 🗺️ Visão Geral da Arquitetura

Neste projeto, construiremos um sistema completo de "Lista de Tarefas" (To-Do List), demonstrando como diferentes aplicações cliente podem consumir uma única fonte de dados central (API). A arquitetura final será:

  * **Um Backend (API REST):** O cérebro do sistema, desenvolvido em Java com Spring Boot.
  * **Três Clientes:**
    1.  Uma aplicação **Web** com Angular.
    2.  Uma aplicação **Desktop** nativa com JavaFX.
    3.  Uma aplicação **Mobile** nativa com Android e Jetpack Compose.
  * **Um Painel de Controle:** Um script PowerShell para automação e gerenciamento do ambiente.

### Diagrama da Arquitetura

```mermaid
graph TD
    subgraph "🎛️ Gerenciamento e Automação"
        Script["🛠️ Painel de Controle (listadetarefas-painel.ps1)"]
    end

    subgraph "📱 Clientes (Frontends)"
        Web["💻 Frontend Web (Angular)"]
        Desktop["🖥️ Frontend Desktop (JavaFX)"]
        Mobile["📱 Frontend Mobile (Android)"]
    end

    subgraph "⚙️ Serviços (Backend)"
        API["🔌 Backend API (Spring Boot)"]
        DB[("🗄️ Banco de Dados Em Memória H2")]
    end

    %% Conexões de Dados
    Web -->|Requisições HTTP/JSON| API
    Desktop -->|Requisições HTTP/JSON| API
    Mobile -->|Requisições HTTP/JSON| API
    API --- DB

    %% Conexões de Gerenciamento
    Script -- Gerencia --> API
    Script -- Gerencia --> Web
    Script -- Gerencia --> Desktop
    Script -- Gerencia --> Mobile

```

-----

## ⚙️ Módulo 1: A Fundação – Backend com Spring Boot (`listadetarefas-api`)

**Objetivo:** Criar o serviço central que irá gerenciar os dados das tarefas, servindo como a única fonte de verdade para todos os clientes.

### 🛠️ Ferramentas Necessárias

  * **Java Development Kit (JDK):** Versão 17 ou superior.
  * **Apache Maven:** Ferramenta de automação de build.
  * **IDE (Ambiente de Desenvolvimento):** IntelliJ IDEA ou Eclipse.
  * **Cliente REST:** Postman ou Insomnia (para testes).

### \#\#\# 📂 Passo 1: Criação do Projeto

1.  Acesse o **Spring Initializr** ([https://start.spring.io](https://start.spring.io)).
2.  Preencha os metadados do projeto:
      * **Project:** `Maven`
      * **Language:** `Java`
      * **Spring Boot:** Versão estável mais recente (ex: 3.x.x)
      * **Group:** `br.com.curso`
      * **Artifact:** `listadetarefas-api`
      * **Package name:** `br.com.curso.listadetarefas.api`
3.  Adicione as seguintes dependências (`Dependencies`):
      * `Spring Web`, `Spring Data JPA`, `H2 Database`, `Lombok`.
4.  Clique em **GENERATE**, baixe o projeto, descompacte-o e abra na sua IDE.

#### Estrutura Inicial de Pastas

Após criar o projeto, sua estrutura de pastas principal será:

```
listadetarefas-api/
├── src/
│   └── main/
│       ├── java/
│       │   └── br/com/curso/listadetarefas/api/
│       │       └── ListadetarefasApiApplication.java
│       └── resources/
│           └── application.properties
└── pom.xml
```

### \#\#\# ⚙️ Passo 2: Configuração do Projeto

Abra o arquivo `src/main/resources/application.properties` e substitua seu conteúdo por este:

```properties
# Permite que o servidor aceite conexões de qualquer endereço de rede da máquina.
server.address=0.0.0.0

# Habilita o console web do H2
spring.h2.console.enabled=true
# Define o caminho para acessar o console
spring.h2.console.path=/h2-console

# Configurações do Datasource para H2
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### \#\#\# 📝 Passo 3: Modelagem dos Dados

Vamos definir a estrutura da nossa tabela de tarefas.

#### Diagrama Entidade-Relacionamento (ER)

```mermaid
erDiagram
    TB_TAREFAS {
        BIGINT id PK "Auto-incremento"
        VARCHAR descricao
        BOOLEAN concluida
    }
```

1.  Dentro de `src/main/java/br/com/curso/listadetarefas/api`, crie um novo pacote chamado `tarefa`.
2.  Dentro do pacote `tarefa`, crie a classe `Tarefa.java`.

<!-- end list -->

```java
// src/main/java/br/com/curso/listadetarefas/api/tarefa/Tarefa.java
package br.com.curso.listadetarefas.api.tarefa;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tb_tarefas")
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private boolean concluida;
}
```

#### Estrutura de Pastas Após a Criação do Modelo

```
api/
└── src/main/java/br/com/curso/listadetarefas/api/
    ├── tarefa/
    │   └── Tarefa.java  # <- Arquivo criado
    └── ListadetarefasApiApplication.java
```

### \#\#\# 🏗️ Passo 4: Construção das Camadas de Serviço

Agora, criaremos as classes que formam a arquitetura da nossa API: `Repository` (acesso a dados), `Service` (regras de negócio) e `Controller` (endpoints HTTP).

#### Diagrama de Classes

```mermaid
classDiagram
    TarefaController ..> TarefaService : Usa
    TarefaService ..> TarefaRepository : Usa
    TarefaRepository ..> Tarefa : Gerencia
    class TarefaController {
        +List~Tarefa~ listarTarefas()
        +Tarefa criarTarefa(Tarefa)
        +ResponseEntity~Tarefa~ atualizarTarefa(Long, Tarefa)
        +ResponseEntity~Void~ deletarTarefa(Long)
    }
    class TarefaService {
        +List~Tarefa~ listarTodas()
        +Tarefa criar(Tarefa)
        +Tarefa atualizar(Long, Tarefa)
        +void deletar(Long)
    }
    class TarefaRepository {
        <<Interface>>
    }
    class Tarefa {
        -Long id
        -String descricao
        -boolean concluida
    }
```

1.  Dentro do pacote `tarefa`, crie as seguintes classes e interfaces:

**`TarefaRepository.java`**

```java
package br.com.curso.listadetarefas.api.tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}
```

**`TarefaService.java`**

```java
package br.com.curso.listadetarefas.api.tarefa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTodas() { return tarefaRepository.findAll(); }
    public Tarefa criar(Tarefa tarefa) { return tarefaRepository.save(tarefa); }
    public Tarefa atualizar(Long id, Tarefa tarefaAtualizada) {
        return tarefaRepository.findById(id)
            .map(tarefaExistente -> {
                tarefaExistente.setDescricao(tarefaAtualizada.getDescricao());
                tarefaExistente.setConcluida(tarefaAtualizada.isConcluida());
                return tarefaRepository.save(tarefaExistente);
            }).orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o id: " + id));
    }
    public void deletar(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new RuntimeException("Tarefa não encontrada com o id: " + id);
        }
        tarefaRepository.deleteById(id);
    }
}
```

**`TarefaController.java`**

```java
package br.com.curso.listadetarefas.api.tarefa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@CrossOrigin(origins = "*")
public class TarefaController {
    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public List<Tarefa> listarTarefas() { return tarefaService.listarTodas(); }
    @PostMapping
    public Tarefa criarTarefa(@RequestBody Tarefa tarefa) { return tarefaService.criar(tarefa); }
    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizarTarefa(@PathVariable Long id, @RequestBody Tarefa tarefa) {
        try {
            Tarefa atualizada = tarefaService.atualizar(id, tarefa);
            return ResponseEntity.ok(atualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        try {
            tarefaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
```

#### Estrutura de Pastas Final do Backend

```
api/
└── src/main/java/br/com/curso/listadetarefas/api/
    ├── tarefa/
    │   ├── Tarefa.java
    │   ├── TarefaController.java
    │   ├── TarefaRepository.java
    │   └── TarefaService.java
    └── ListadetarefasApiApplication.java
```

### \#\#\# ✅ Passo 5: Execução e Teste da API

#### Diagrama de Casos de Uso da API

```mermaid
usecaseDiagram
    Usuário as "Usuário (via Cliente)"
    package "Sistema de Tarefas" {
        usecase "Listar todas as tarefas" as UC1
        usecase "Adicionar nova tarefa" as UC2
        usecase "Atualizar uma tarefa" as UC3
        usecase "Deletar uma tarefa" as UC4
    }
    Usuário --> UC1
    Usuário --> UC2
    Usuário --> UC3
    Usuário --> UC4
```

1.  **Execute a Aplicação:**

      * Na sua IDE, execute a classe `ListadetarefasApiApplication.java`.
      * Ou, via terminal na raiz do projeto: `./mvnw spring-boot:run`

2.  **Teste com Cliente REST (ex: Postman):**

      * Use um cliente REST para fazer requisições para `http://localhost:8080/api/tarefas` e verifique todas as operações de CRUD (GET, POST, PUT, DELETE) como detalhado no guia anterior.

3.  **Teste com o Console H2:**

      * Com a API rodando, acesse `http://localhost:8080/h2-console` no navegador.
      * Use as seguintes credenciais para logar:
          * **JDBC URL:** `jdbc:h2:mem:testdb`
          * **User Name:** `sa`
          * **Password:** (em branco)
      * Após criar tarefas via API, execute o comando SQL `SELECT * FROM TB_TAREFAS;` para ver os dados diretamente no banco.

-----

## 💻 Módulo 2: Cliente Web com Angular (`listadetarefas-web`)

**Objetivo:** Criar uma interface web moderna e reativa para interagir com a API.

### 🛠️ Ferramentas Necessárias

  * **Node.js e npm:** Ambiente de execução e gerenciador de pacotes.
  * **Angular CLI:** (`npm install -g @angular/cli`)
  * **Editor de Código:** Visual Studio Code.

### \#\#\# 📂 Passo 1: Criação do Projeto

1.  No terminal, crie o projeto:
    ```bash
    ng new listadetarefas-web --standalone --style=css
    ```
2.  Navegue até a pasta `cd listadetarefas-web`.
3.  Gere os arquivos necessários:
    ```bash
    ng generate interface models/tarefa
    ng generate service services/tarefa
    ng generate component components/task-list
    ```

#### Estrutura de Pastas Após Geração

```
listadetarefas-web/
└── src/
    └── app/
        ├── components/
        │   └── task-list/
        │       ├── task-list.component.css
        │       ├── task-list.component.html
        │       └── task-list.component.ts
        ├── models/
        │   └── tarefa.ts
        └── services/
            └── tarefa.service.ts
```

### \#\#\# ✍️ Passo 2: Codificação do Cliente Web

Siga os passos e use os códigos fornecidos no guia anterior para os seguintes arquivos:

  * `src/app/models/tarefa.ts`
  * `src/app/services/tarefa.service.ts`
  * `src/app/components/task-list/task-list.component.ts`
  * `src/app/components/task-list/task-list.component.html`
  * `src/app/components/task-list/task-list.component.css` (use a versão melhorada e com variáveis).

### \#\#\# 🔗 Passo 3: Integração Final

Para corrigir o erro `'app-task-list' is not a known element'`, integre o componente principal:

1.  **Configure o HttpClient:** Verifique se `provideHttpClient(withFetch())` está em `src/app/app.config.ts`.
2.  **Importe o Componente:** Altere o `src/app/app.component.ts` para importar e usar o `TaskListComponent`:

<!-- end list -->

```typescript
// src/app/app.component.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TaskListComponent } from './components/task-list/task-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ RouterOutlet, TaskListComponent ], // Importado aqui
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent { }
```

3.  **Atualize o Template Principal:** Limpe o `src/app/app.component.html` e adicione apenas:

<!-- end list -->

```html
<app-task-list></app-task-list>
```

### \#\#\# ✅ Passo 4: Execução e Teste

1.  **Pré-requisito:** A API backend deve estar rodando.
2.  **Execute:** No terminal (na pasta `listadetarefas-web`), rode `ng serve --open`.
3.  **Teste:** Abra as ferramentas de desenvolvedor do navegador (F12) e teste todas as funcionalidades: adicionar, editar com duplo clique, marcar como concluída e deletar.

-----

## 🖥️ Módulo 3: Cliente Desktop com JavaFX (`listadetarefas-desktop`)

**Objetivo:** Criar uma aplicação desktop nativa e funcional que consome a API backend.

### 🛠️ Ferramentas Necessárias

  * **Java Development Kit (JDK):** Versão 17 ou superior.
  * **IDE:** IntelliJ IDEA ou VS Code com o "Extension Pack for Java".

### \#\#\# 📂 Passo 1: Criação e Configuração do Projeto

1.  **Crie um projeto Maven** na sua IDE para `listadetarefas-desktop` (siga as instruções detalhadas do guia anterior para IntelliJ ou VS Code).
2.  **Substitua o `pom.xml`** pelo código completo fornecido no guia anterior, que inclui JavaFX, Jackson e o `maven-shade-plugin`.
3.  **Crie o arquivo `module-info.java`** em `src/main/java` com a versão final e corrigida, contendo todos os `requires`, `opens` e `exports` necessários.

### \#\#\# 🏗️ Passo 2: Estrutura de Código e UI

Siga os passos e use os códigos completos e detalhados do guia anterior para criar a estrutura final.

#### Diagrama de Classes do Cliente Desktop

```mermaid
classDiagram
    MainApp --|> Application
    MainApp ..> MainViewController : Carrega
    MainViewController ..> TarefaApiService : Usa
    TarefaApiService ..> Tarefa : Manipula
    class MainApp {
        +start(Stage)
    }
    class MainViewController {
        -TableView~Tarefa~ tabelaTarefas
        +initialize()
        +adicionarTarefa()
        +atualizarListaDeTarefas()
    }
    class TarefaApiService {
        +List~Tarefa~ listarTarefas()
    }
    class Tarefa {
        -Long id
        -String descricao
        -boolean concluida
    }
```

#### Estrutura de Pastas e Arquivos Final do Desktop

```
listadetarefas-desktop/
├── src/
│   └── main/
│       ├── java/
│       │   ├── br/com/curso/listadetarefas/desktop/
│       │   │   ├── Launcher.java
│       │   │   ├── MainApp.java
│       │   │   ├── MainViewController.java
│       │   │   ├── Tarefa.java
│       │   │   └── TarefaApiService.java
│       │   └── module-info.java
│       └── resources/
│           └── br/com/curso/listadetarefas/desktop/
│               └── MainView.fxml
└── pom.xml
```

### \#\#\# ✅ Passo 3: Construção e Teste

1.  **Pré-requisito:** A API backend deve estar rodando.
2.  **Construa:** No terminal, na raiz do projeto, rode `mvn clean package`.
3.  **Execute:** Rode o JAR gerado: `java -jar target/listadetarefas-desktop-1.0-SNAPSHOT.jar`.
4.  **Teste:** Verifique todas as funcionalidades: adicionar, deletar, atualizar a lista, e editar a descrição com duplo clique.

-----

## 📱 Módulo 4: Cliente Mobile com Android (`listadetarefas-android`)

**Objetivo:** Completar o ecossistema com um cliente Android nativo e moderno, utilizando as melhores práticas recomendadas pelo Google, como Jetpack Compose e ViewModel.

### 🛠️ Ferramentas Necessárias

  * **Android Studio:** A IDE oficial para desenvolvimento Android (versão "Hedgehog" ou mais recente).
  * **Android SDK:** Instalado via Android Studio.
  * **Emulador Android (AVD)** ou um dispositivo físico.

### \#\#\# 📂 Passo 1: Criação do Projeto

1.  No **Android Studio**, vá em **File \> New \> New Project...**.
2.  Selecione o template **Empty Activity** (com o logo do Compose).
3.  Configure o projeto:
      * **Name:** `listadetarefas-android`
      * **Package name:** `br.com.curso.listadetarefas.android`
      * **Minimum SDK:** API 24 ou superior.
4.  Clique em **Finish**.

#### Estrutura Inicial de Pastas

O Android Studio gerará uma estrutura complexa. Focaremos na pasta principal do nosso código:

```
listadetarefas-android/
└── app/
    └── src/
        └── main/
            ├── java/
            │   └── br/com/curso/listadetarefas/android/
            │       └── MainActivity.kt
            └── AndroidManifest.xml
```

### \#\#\# ⚙️ Passo 2: Configuração do Projeto

1.  **Adicionar Dependências:** Abra o arquivo `app/build.gradle.kts` e adicione as dependências para Retrofit (cliente HTTP) e Gson (conversor JSON) na seção `dependencies { ... }`.

```kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "br.com.curso.listadetarefas.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.curso.listadetarefas.android"
        // --- CORREÇÃO AQUI ---
        minSdk = 26 // Alterado de 24 para 26 para suportar ícones adaptativos
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // --- Dependências Principais do AndroidX ---
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // --- Jetpack Compose ---
    val composeBom = platform("androidx.compose:compose-bom:2024.02.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // --- ViewModel com Compose ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // --- Networking: Retrofit e OkHttp ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // --- Testes ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

2.  **Adicionar Permissões de Rede:** Abra o arquivo `app/src/main/AndroidManifest.xml` e adicione a permissão de internet e a permissão para tráfego de texto limpo (necessário para `localhost` em desenvolvimento).

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:usesCleartextTraffic="true"
        android:theme="@style/Theme.Listadetarefasandroid">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Listadetarefasandroid">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

### \#\#\# 🔗 Passo 3: Configuração da Rede para o Emulador

1.  **Pré-requisito:** A API backend deve estar rodando com `server.address=0.0.0.0`.
2.  **Crie o Túnel Reverso:** Com o emulador Android em execução, abra um terminal e execute o comando:
    ```bash
    adb reverse tcp:8080 tcp:8080
    ```
      * **Explicação:** Este comando redireciona as requisições feitas para a porta `8080` do emulador para a porta `8080` da sua máquina (onde a API está rodando).

### \#\#\# ✍️ Passo 4: Codificação da Camada de Dados e Rede

1.  Dentro do pacote `br.com.curso.listadetarefas.android`, crie os seguintes arquivos Kotlin:

**`Tarefa.kt` (Modelo de Dados)**

```kotlin
package br.com.curso.listadetarefas.android
// data class gera automaticamente getters, setters, equals, etc.
data class Tarefa(
    val id: Long?,
    var descricao: String?,
    var concluida: Boolean
)
```

**`TarefaApiService.kt` (Interface da API)**

```kotlin
package br.com.curso.listadetarefas.android
import retrofit2.Response
import retrofit2.http.*

interface TarefaApiService {
    @GET("tarefas")
    suspend fun getTarefas(): List<Tarefa>
    @POST("tarefas")
    suspend fun addTarefa(@Body tarefa: Tarefa): Tarefa
    @PUT("tarefas/{id}")
    suspend fun updateTarefa(@Path("id") id: Long, @Body tarefa: Tarefa): Tarefa
    @DELETE("tarefas/{id}")
    suspend fun deleteTarefa(@Path("id") id: Long): Response<Void>
}
```

**`RetrofitClient.kt` (Cliente HTTP)**

```kotlin
package br.com.curso.listadetarefas.android
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 127.0.0.1 é o endereço de localhost para o emulador Android (após o adb reverse).
    private const val BASE_URL = "http://127.0.0.1:8080/api/"
    val instance: TarefaApiService by lazy {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        retrofit.create(TarefaApiService::class.java)
    }
}
```

#### Estrutura de Pastas Após a Criação da Rede

```
android/
└── app/src/main/java/br/com/curso/listadetarefas/android/
    ├── MainActivity.kt
    ├── RetrofitClient.kt     # <- Arquivo criado
    ├── Tarefa.kt             # <- Arquivo criado
    └── TarefaApiService.kt   # <- Arquivo criado
```

### \#\#\# 🏗️ Passo 5: Construção do ViewModel e da UI com Compose

#### Diagrama de Classes do Cliente Mobile

```mermaid
classDiagram
    MainActivity ..> TarefaViewModel : Observa
    TarefaViewModel ..> RetrofitClient : Usa
    RetrofitClient ..> TarefaApiService : Cria
    TarefaViewModel ..> Tarefa : Gerencia Estado (UiState)
    class MainActivity {
        +TarefaApp() Composable
    }
    class TarefaViewModel {
        -StateFlow~TarefaUiState~ uiState
        +carregarTarefas()
        +adicionarTarefa(String)
        +updateTarefa(Tarefa)
        +deleteTarefa(Long)
    }
    class RetrofitClient {
        <<Object>>
        +TarefaApiService instance
    }
    class TarefaApiService {
        <<Interface>>
    }
```

1.  Crie o arquivo `TarefaViewModel.kt` e substitua o conteúdo de `MainActivity.kt`.

**`TarefaViewModel.kt`**

```kotlin
// Cole o código completo da classe TarefaViewModel do guia anterior aqui.
// Ele contém a classe TarefaUiState e a lógica para carregar, adicionar,
// atualizar e deletar tarefas usando Coroutines.
package br.com.curso.listadetarefas.android

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TarefaUiState(
    val tarefas: List<Tarefa> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
class TarefaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TarefaUiState())
    val uiState: StateFlow<TarefaUiState> = _uiState.asStateFlow()
    private val TAG = "TarefaViewModel"

    init { carregarTarefas() }

    fun carregarTarefas() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val tarefasDaApi = withContext(Dispatchers.IO) { RetrofitClient.instance.getTarefas() }
                _uiState.update { it.copy(isLoading = false, tarefas = tarefasDaApi, error = null) }
            } catch (e: Exception) {
                Log.e(TAG, "Falha ao carregar tarefas", e)
                _uiState.update { it.copy(isLoading = false, error = "Falha ao carregar tarefas") }
            }
        }
    }
    fun adicionarTarefa(descricao: String) {
        viewModelScope.launch {
            try {
                val tarefaAdicionada = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.addTarefa(Tarefa(id = null, descricao = descricao, concluida = false))
                }
                _uiState.update { it.copy(tarefas = it.tarefas + tarefaAdicionada) }
            } catch (e: Exception) { Log.e(TAG, "Falha ao adicionar tarefa", e) }
        }
    }
    fun updateTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            try {
                tarefa.id?.let {
                    val tarefaAtualizada = withContext(Dispatchers.IO) { RetrofitClient.instance.updateTarefa(it, tarefa) }
                    _uiState.update { currentState ->
                        currentState.copy(tarefas = currentState.tarefas.map { t -> if (t.id == tarefaAtualizada.id) tarefaAtualizada else t })
                    }
                }
            } catch (e: Exception) { Log.e(TAG, "Falha ao atualizar tarefa", e) }
        }
    }
    fun deleteTarefa(id: Long?) {
        viewModelScope.launch {
            try {
                id?.let {
                    withContext(Dispatchers.IO) { RetrofitClient.instance.deleteTarefa(it) }
                    _uiState.update { currentState -> currentState.copy(tarefas = currentState.tarefas.filter { t -> t.id != id }) }
                }
            } catch (e: Exception) { Log.e(TAG, "Falha ao deletar tarefa", e) }
        }
    }
}
```

**`MainActivity.kt`**

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)
package br.com.curso.listadetarefas.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
// --- REMOVER IMPORTS ANTIGOS ---
// import androidx.compose.material.pullrefresh.PullRefreshIndicator
// import androidx.compose.material.pullrefresh.pullRefresh
// import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
// --- ADICIONAR NOVO IMPORT DO MATERIAL 3 ---
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.curso.listadetarefas.android.ui.theme.listadetarefasAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            listadetarefasAndroidTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    TarefaApp()
                }
            }
        }
    }
}

@Composable
fun TarefaApp(tarefaViewModel: TarefaViewModel = viewModel()) {
    val uiState by tarefaViewModel.uiState.collectAsState()
    var tarefaParaEditar by remember { mutableStateOf<Tarefa?>(null) }

    // --- MUDANÇA: Usar o rememberPullToRefreshState do Material 3 ---
    val pullToRefreshState = rememberPullToRefreshState()

    // Lógica para lidar com a atualização
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            tarefaViewModel.carregarTarefas()
        }
    }

    // Lógica para parar a animação de refresh quando o carregamento terminar
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("To-Do List Android") }) }) { paddingValues ->
        // --- MUDANÇA: Usar PullToRefreshBox ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            if (uiState.error != null) {
                Text(
                    text = "Erro: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            } else {
                // O conteúdo da tela (a lista) vai aqui dentro
                TarefaScreen(
                    tarefas = uiState.tarefas,
                    isLoading = uiState.isLoading,
                    onAddTask = tarefaViewModel::adicionarTarefa,
                    onUpdateTask = tarefaViewModel::updateTarefa,
                    onDeleteTask = tarefaViewModel::deleteTarefa,
                    onTaskClick = { tarefa -> tarefaParaEditar = tarefa }
                )
            }

            // O indicador de refresh agora é o PullToRefreshContainer
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // O diálogo de edição permanece o mesmo
            tarefaParaEditar?.let { tarefa ->
                EditTaskDialog(
                    tarefa = tarefa,
                    onDismiss = { tarefaParaEditar = null },
                    onSave = { novaDescricao ->
                        val tarefaAtualizada = tarefa.copy(descricao = novaDescricao)
                        tarefaViewModel.updateTarefa(tarefaAtualizada)
                        tarefaParaEditar = null
                    }
                )
            }
        }
    }
}

@Composable
fun TarefaScreen(
    tarefas: List<Tarefa>,
    isLoading: Boolean,
    onAddTask: (String) -> Unit,
    onUpdateTask: (Tarefa) -> Unit,
    onDeleteTask: (Long?) -> Unit,
    onTaskClick: (Tarefa) -> Unit
) {
    var textoNovaTarefa by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = textoNovaTarefa,
                onValueChange = { textoNovaTarefa = it },
                label = { Text("Nova tarefa") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (textoNovaTarefa.isNotBlank()) {
                    onAddTask(textoNovaTarefa)
                    textoNovaTarefa = ""
                }
            }) { Text("Add") }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // A lógica de exibição (loading, lista vazia, lista com itens) permanece a mesma
        if (isLoading && tarefas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (tarefas.isEmpty()) {
            Text(
                text = "Nenhuma tarefa encontrada.\nPuxe para atualizar ou adicione uma nova!",
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tarefas, key = { it.id!! }) { tarefa ->
                    TarefaItem(
                        tarefa = tarefa,
                        onCheckedChange = { isChecked -> onUpdateTask(tarefa.copy(concluida = isChecked)) },
                        onDeleteClick = { onDeleteTask(tarefa.id) },
                        onTaskClick = { onTaskClick(tarefa) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TarefaItem(
    tarefa: Tarefa,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = tarefa.concluida, onCheckedChange = onCheckedChange)
        Text(
            text = tarefa.descricao ?: "",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            style = if (tarefa.concluida) LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough) else LocalTextStyle.current
        )
        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Delete, contentDescription = "Deletar Tarefa")
        }
    }
}

@Composable
fun EditTaskDialog(tarefa: Tarefa, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var textoEditado by remember { mutableStateOf(tarefa.descricao ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarefa") },
        text = {
            OutlinedTextField(
                value = textoEditado,
                onValueChange = { textoEditado = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = { Button(onClick = { if (textoEditado.isNotBlank()) { onSave(textoEditado) } }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
```

### \#\#\# ✅ Passo 1: Garantir que a API está a ser executada e o túnel de rede está ativo

Antes de iniciar a aplicação Android, é essencial que a API já esteja a ser executada.

Use a opção 1 no seu painel de controlo para Iniciar a API.

Confirme que o status da "API Backend" muda para RUNNING.

Use a opção 7 para Iniciar a App Android. O script irá executar automaticamente o comando adb reverse, que cria a ponte de rede necessária.

### \#\#\# ✅ Passo 2: Permitir Tráfego de Rede no Android (Configuração Essencial)

Por defeito, as versões mais recentes do Android bloqueiam a comunicação com endereços que não sejam seguros (não-HTTPS), como é o caso do nosso ambiente de desenvolvimento local. Precisamos de dizer explicitamente à aplicação que esta comunicação é permitida.

### \#\#\# ✅ Passo 3: Crie um novo ficheiro de configuração:

Na estrutura de pastas do seu projeto Android, navegue para app/src/main/res.

Crie uma nova pasta chamada xml.

Dentro da pasta xml, crie um novo ficheiro chamado network_security_config.xml.

### \#\#\# ✅ Passo 4: Adicione o seguinte conteúdo a network_security_config.xml:

```XML
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">127.0.0.1</domain>
    </domain-config>
</network-security-config>
```

### \#\#\# ✅ Passo 5: Atualize o AndroidManifest.xml:

Abra o ficheiro app/src/main/AndroidManifest.xml.

Adicione a seguinte linha dentro da tag <application>:

```XML
<application
    ...
    android:networkSecurityConfig="@xml/network_security_config">
    ...
</application>
```

Depois de fazer estas alterações no projeto Android, compile e execute a aplicação novamente através do painel de controle.


### \#\#\# ✅ Passo 6: Execução e Teste

1.  **Execute a Aplicação:** No Android Studio, selecione o emulador e clique no botão "Run 'app'".
2.  **Teste e Depure:**
      * **Ferramenta Principal:** **Logcat**. Use a janela do Logcat no Android Studio para ver logs da aplicação, erros de rede e exceções.
      * **Roteiro de Teste:**
        1.  Adicione, edite (tocando na descrição), marque como concluída e delete tarefas.
        2.  Arraste a lista para baixo para testar a funcionalidade de "Puxar para Atualizar".

-----

## 🤖 Módulo 5: Automação com PowerShell (`listadetarefas-painel.ps1`)

**Objetivo:** Criar um painel de controle centralizado para gerenciar todo o ecossistema (iniciar/parar serviços) de forma rápida e fácil.

### 🛠️ Ferramentas Necessárias

  * **Windows Terminal** ou **PowerShell**.

### \#\#\# 📂 Passo 1: Estrutura Final e Configuração

1.  Na **pasta raiz** que contém todos os 4 projetos, crie o arquivo `listadetarefas-painel.ps1`.
2.  **Habilite a Execução de Scripts:** Abra o PowerShell como **Administrador** e execute (apenas uma vez):
    ```powershell
    Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
    ```

#### Estrutura de Pastas Final do Projeto Completo

```
projeto-todolist/
├── listadetarefas-api/
├── listadetarefas-web/
├── listadetarefas-desktop/
├── listadetarefas-android/
└── listadetarefas-painel.ps1  # <- Script de automação
```

### \#\#\# 📜 Passo 2: O Script de Automação

Ele contém as funções Get-ServiceStatus, Start-Service, Stop-Service e o menu interativo.

Copie o código abaixo para o seu arquivo `listadetarefas-painel.ps1`. Ele deve usar os nomes corretos dos projetos e é portátil.

```powershell
<#
.SYNOPSIS
    Painel de controle para gerenciar o projeto To-Do List (API, Web, Desktop, Android).
.DESCRIPTION
    Este script PowerShell fornece um menu interativo para iniciar, parar, construir e depurar
    os diferentes componentes do projeto. Ele detecta automaticamente o status de cada serviço
    e torna o ambiente de desenvolvimento mais produtivo.
.VERSION
    9.4 - Corrigida a lógica de exibição de status no menu
#>

# Força o uso do protocolo TLS 1.2 para compatibilidade com downloads HTTPS (ex: Maven Wrapper).
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

#==============================================================================
# --- CONFIGURAÇÕES GLOBAIS ---
#==============================================================================

$basePath = $PSScriptRoot
$apiPath = Join-Path $basePath "listadetarefas-api"
$webPath = Join-Path $basePath "listadetarefas-web"
$desktopPath = Join-Path $basePath "listadetarefas-desktop"
$androidPath = Join-Path $basePath "listadetarefas-android"

# --- VALIDAÇÃO DE CAMINHOS ---
$projectPaths = @{ "API" = $apiPath; "Web" = $webPath; "Desktop" = $desktopPath; "Android" = $androidPath }
$pathsAreValid = $true
foreach ($project in $projectPaths.Keys) {
    if (-not (Test-Path $projectPaths[$project])) {
        Write-Host "ERRO: O diretório do projeto '$project' não foi encontrado em '$($projectPaths[$project])'" -ForegroundColor Red
        $pathsAreValid = $false
    }
}
if (-not $pathsAreValid) { Read-Host "`nVerifique os nomes das pastas. Pressione Enter para sair."; exit }

# --- CONFIGURAÇÕES ANDROID ---
$sdkPath = Join-Path $env:LOCALAPPDATA "Android\Sdk"
$emulatorPath = Join-Path $sdkPath "emulator"
$platformToolsPath = Join-Path $sdkPath "platform-tools"
$emulatorName = "Medium_Phone"

# --- CONFIGURAÇÕES DOS ARTEFATOS ---
$apiJar = Join-Path $apiPath "target\listadetarefas-api-0.0.1-SNAPSHOT.jar"
$desktopJar = Join-Path $desktopPath "target\listadetarefas-desktop-1.0-SNAPSHOT.jar"
$androidPackage = "br.com.curso.listadetarefas.android"
$desktopWindowTitle = "Minha Lista de Tarefas (Desktop)"
$webUrl = "http://localhost:3000"

#==============================================================================
# --- FUNÇÕES AUXILIARES ---
#==============================================================================

function Get-ServiceStatus($serviceName) {
    try {
        switch ($serviceName) {
            'api' { if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction Stop) { return "RUNNING" } }
            'web' { if (Get-NetTCPConnection -LocalPort 3000 -State Listen -ErrorAction Stop) { return "RUNNING" } }
            'desktop' { if (Get-Process -Name "java", "javaw" -ErrorAction Stop | Where-Object { $_.MainWindowTitle -like "*$desktopWindowTitle*" }) { return "RUNNING" } }
            'android' { if ((& "$platformToolsPath\adb.exe" shell ps) -match $androidPackage) { return "RUNNING" } }
            'emulator' { if ((& "$platformToolsPath\adb.exe" devices) -like "*`tdevice*") { return "RUNNING" } }
        }
    }
    catch { return "STOPPED" }
    return "STOPPED"
}

function Wait-For-AdbDevice {
    param([int]$TimeoutSeconds = 60)
    if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') { return $true }
    Write-Host "Aguardando um emulador/dispositivo ficar online..." -ForegroundColor Cyan
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt $TimeoutSeconds) {
        if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') {
            Write-Host "`nDispositivo detectado." -ForegroundColor Green; $stopwatch.Stop(); Start-Sleep 1; return $true
        }
        Write-Host "." -NoNewline; Start-Sleep 2
    }
    $stopwatch.Stop(); Write-Host "`nTempo esgotado!" -ForegroundColor Red; return $false
}

function Ensure-BuildArtifact {
    param([string]$ArtifactPath, [string]$ProjectPath, [string[]]$BuildCommand, [string]$BuildToolName)
    if (!(Test-Path $ArtifactPath)) {
        $choice = Read-Host "Artefato de build não encontrado em '$ArtifactPath'. Deseja construir agora? (s/n)"
        if ($choice -eq 's') {
            Push-Location $ProjectPath
            Write-Host "Construindo em '$ProjectPath'..." -ForegroundColor Cyan
            if ($BuildToolName -eq "mvnw.cmd" -and -not (Test-Path ".\pom.xml")) {
                Write-Host "ERRO CRÍTICO: 'pom.xml' não encontrado em '$ProjectPath'." -ForegroundColor Red
                Pop-Location; Start-Sleep 3; return $false
            }
            $executableCommand = $null
            if ($BuildToolName -eq "ng") {
                if (Get-Command ng -ErrorAction SilentlyContinue) { $executableCommand = "ng" }
                else { Write-Host "ERRO: O comando 'ng' (Angular CLI) não foi encontrado." -ForegroundColor Red }
            }
            else {
                if ((Test-Path ".\$BuildToolName") -and (Test-Path ".\.mvn\wrapper")) { $executableCommand = ".\$BuildToolName" }
                elseif (Get-Command mvn -ErrorAction SilentlyContinue) { $executableCommand = "mvn"; Write-Host "AVISO: Usando Maven global ('mvn')." -ForegroundColor Yellow }
                else { Write-Host "ERRO: Nenhuma ferramenta de build do Maven foi encontrada." -ForegroundColor Red }
            }
            if (-not $executableCommand) { Pop-Location; Start-Sleep 2; return $false }
            try { & $executableCommand $BuildCommand *>&1 | ForEach-Object { Write-Host $_ } } catch { Write-Host "`nERRO DE BUILD" -ForegroundColor Red; Pop-Location; Start-Sleep 2; return $false }
            if ($LASTEXITCODE -ne 0) { Write-Host "`nERRO DE BUILD (código: $LASTEXITCODE)." -ForegroundColor Red; Pop-Location; Start-Sleep 2; return $false }
            Pop-Location
            if (!(Test-Path $ArtifactPath)) { Write-Host "Build concluído, mas o artefato '$ArtifactPath' não foi encontrado." -ForegroundColor Red; Start-Sleep 2; return $false }
        }
        else { Write-Host "Início cancelado." -ForegroundColor Red; Start-Sleep 2; return $false }
    }
    return $true
}

#==============================================================================
# --- FUNÇÕES DE GERENCIAMENTO DE SERVIÇOS ---
#==============================================================================

function Start-Service($serviceName, [switch]$ColdBoot) {
    if ($serviceName -in @('web', 'desktop', 'android')) {
        if ((Get-ServiceStatus 'api') -eq 'STOPPED') {
            $confirm = Read-Host "AVISO: A API está parada. Deseja iniciá-la primeiro? (s/n)"
            if ($confirm -eq 's') { if (-not (Start-Service 'api')) { Write-Host "Falha ao iniciar API." -ForegroundColor Red; Start-Sleep 2; return $false } }
            else { Write-Host "AVISO: '$serviceName' pode não funcionar sem a API." -ForegroundColor Yellow }
        }
    }
    Write-Host "`nTentando iniciar serviço: $serviceName..." -ForegroundColor Yellow
    $commandExecuted = $false
    switch ($serviceName) {
        'api' {
            if (-not (Ensure-BuildArtifact -ArtifactPath $apiJar -ProjectPath $apiPath -BuildCommand @("clean", "package") -BuildToolName "mvnw.cmd")) { break }
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title API-Backend && java -jar `"`"$apiJar`"`"`"" -WorkingDirectory $apiPath
            $commandExecuted = $true
        }
        'web' {
            if (-not (Ensure-BuildArtifact -ArtifactPath (Join-Path $webPath "dist") -ProjectPath $webPath -BuildCommand "build" -BuildToolName "ng")) { break }
            Push-Location $webPath; Start-Process npx -ArgumentList "serve", "dist\listadetarefas-web\browser"; Pop-Location
            $commandExecuted = $true
        }
        'desktop' {
            if (-not (Ensure-BuildArtifact -ArtifactPath $desktopJar -ProjectPath $desktopPath -BuildCommand @("clean", "package") -BuildToolName "mvnw.cmd")) { break }
            Start-Process cmd.exe -ArgumentList "/c start cmd.exe /k `"title App-Desktop && java -jar `"`"$desktopJar`"`"`"" -WorkingDirectory $desktopPath
            $commandExecuted = $true
        }
        'android' {
            if (-not (Wait-For-AdbDevice)) { Write-Host "Nenhum emulador/dispositivo detectado." -ForegroundColor Red; Start-Sleep 2; return $false }
            Write-Host "Criando túnel de rede (adb reverse)..." -ForegroundColor Cyan
            & "$platformToolsPath\adb.exe" reverse tcp:8080 tcp:8080
            Write-Host "Iniciando App Android..."; & "$platformToolsPath\adb.exe" shell am start -n "$androidPackage/$androidPackage.MainActivity"
            $commandExecuted = $true
        }
        'emulator' {
            if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') { Write-Host "Emulador já parece estar rodando." -ForegroundColor Green; return $true }
            $arguments = "-avd", $emulatorName
            if ($ColdBoot) { $arguments += "-no-snapshot-load"; Write-Host "Iniciando emulador em modo Cold Boot..." -ForegroundColor Yellow }
            Push-Location $emulatorPath; Start-Process ".\emulator.exe" -ArgumentList $arguments; Pop-Location
            if (Wait-For-AdbDevice) { return $true } else { return $false }
        }
    }
    if (-not $commandExecuted -and $serviceName -ne 'emulator') { Write-Host "Falha no pré-requisito para '$serviceName'." -ForegroundColor Red; Start-Sleep 2; return $false }
    Write-Host "Comando de início enviado. Verificando status..." -ForegroundColor Green
    $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
    while ($stopwatch.Elapsed.TotalSeconds -lt 45) {
        if ((Get-ServiceStatus $serviceName) -eq 'RUNNING') { Write-Host "`nServiço '$serviceName' parece estar rodando." -ForegroundColor Green; return $true }
        Write-Host "." -NoNewline; Start-Sleep 2
    }
    if ($serviceName -ne 'emulator') { Write-Host "`nERRO: Serviço '$serviceName' não iniciou corretamente." -ForegroundColor Red; Start-Sleep 2 }
    return $false
}

function Stop-Service($serviceName) {
    Write-Host "`nParando serviço: $serviceName..." -ForegroundColor Yellow
    switch ($serviceName) {
        'api' { $p = Get-NetTCPConnection -LocalPort 8080 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'web' { $p = Get-NetTCPConnection -LocalPort 3000 -State Listen -EA 0; if ($p) { Stop-Process -Id $p.OwningProcess -Force } }
        'desktop' { Get-Process -Name "java", "javaw" -EA 0 | Where-Object { $_.MainWindowTitle -like "*$desktopWindowTitle*" } | Stop-Process -Force }
        'android' { & "$platformToolsPath\adb.exe" shell am force-stop $androidPackage }
        'emulator' { & "$platformToolsPath\adb.exe" emu kill }
    }
    Write-Host "Comando de parada enviado." -ForegroundColor Green; Start-Sleep 1
}

function Clean-Project {
    Clear-Host; Write-Host "--- LIMPANDO CACHES E BUILDS ---" -ForegroundColor Yellow
    Write-Host "`nLimpando API..." -ForegroundColor Cyan; Push-Location $apiPath; & ".\mvnw.cmd" clean; Pop-Location
    Write-Host "`nLimpando Desktop..." -ForegroundColor Cyan; Push-Location $desktopPath; & ".\mvnw.cmd" clean; Pop-Location
    Write-Host "`nLimpando Web..." -ForegroundColor Cyan
    $angularCache = Join-Path $webPath ".angular"; $angularDist = Join-Path $webPath "dist"
    if (Test-Path $angularCache) { Remove-Item -Recurse -Force $angularCache }
    if (Test-Path $angularDist) { Remove-Item -Recurse -Force $angularDist }
    Write-Host "`n--- LIMPEZA CONCLUÍDA ---" -ForegroundColor Green; Read-Host "Pressione Enter..."
}

#==============================================================================
# --- FERRAMENTAS DE DEBUG (ANDROID) ---
#==============================================================================

function Invoke-AdbTool($toolName) {
    if (-not (Wait-For-AdbDevice)) { Read-Host "`nOperação ADB cancelada. Pressione Enter..."; return }
    Clear-Host; Write-Host "--- Ferramenta ADB: $toolName ---" -ForegroundColor Yellow
    switch ($toolName) {
        'reset' { & "$platformToolsPath\adb.exe" kill-server; & "$platformToolsPath\adb.exe" start-server }
        'devices' { & "$platformToolsPath\adb.exe" devices }
        'logcat' {
            Write-Host "Iniciando logcat... Feche a nova janela para parar."
            $command = "& `"$platformToolsPath\adb.exe`" logcat '*:S' `"$androidPackage:V`""
            Start-Process powershell -ArgumentList "-NoExit", "-Command", $command; return
        }
        'reverse' {
            & "$platformToolsPath\adb.exe" reverse tcp:8080 tcp:8080
            Write-Host "Verificando túneis:"; & "$platformToolsPath\adb.exe" reverse --list
        }
    }
    Read-Host "`nPressione Enter para voltar ao menu"
}

#==============================================================================
# --- INTERFACE DO USUÁRIO (MENU) ---
#==============================================================================

function Show-Menu {
    Clear-Host
    Write-Host "=================================================" -ForegroundColor Cyan
    Write-Host "      PAINEL DE CONTROLE - PROJETO TO-DO LIST      " -ForegroundColor White
    Write-Host "=================================================" -ForegroundColor Cyan

    # CORREÇÃO: A lógica de coleta de status foi separada para evitar erros de referência nula.
    $emulatorStatus = Get-ServiceStatus 'emulator'
    $statuses = @{
        'Emulador'     = $emulatorStatus;
        'API Backend'  = Get-ServiceStatus 'api';
        'Servidor Web' = Get-ServiceStatus 'web';
        'App Desktop'  = Get-ServiceStatus 'desktop';
        'App Android'  = if ($emulatorStatus -eq 'RUNNING') { Get-ServiceStatus 'android' } else { "OFFLINE" }
    }

    Write-Host "`nSTATUS ATUAL:"
    $statuses.GetEnumerator() | ForEach-Object {
        $color = if ($_.Value -eq 'RUNNING') { 'Green' } else { 'Red' }
        Write-Host ("  {0,-15}" -f $_.Name) -NoNewline; Write-Host $_.Value -ForegroundColor $color
    }
    Write-Host "`n--- OPÇÕES ---" -ForegroundColor Yellow
    Write-Host " GERAL                     SERVIÇOS INDIVIDUAIS"
    Write-Host "  9. Iniciar TUDO          1. Iniciar API          5. Iniciar Desktop"
    Write-Host " 10. Parar TUDO             2. Parar API            6. Parar Desktop"
    Write-Host "  L. Limpar Caches         3. Iniciar Web          7. Iniciar App Android"
    Write-Host "                           4. Parar Web            8. Parar App Android"
    Write-Host "-----------------------------------------------------------------"
    Write-Host " FERRAMENTAS ANDROID                               NAVEGAÇÃO"
    Write-Host "  A. Iniciar Emulador      D. Resetar Servidor ADB R. Atualizar Status"
    Write-Host "  B. Parar Emulador        E. Listar Dispositivos  Q. Sair"
    Write-Host "  H. Ligar (Cold Boot)     F. Ver Logs (logcat)"
    Write-Host "  C. Abrir Web no Browser  G. Criar Túnel de Rede`n"
}

#==============================================================================
# --- LÓGICA PRINCIPAL (LOOP DO MENU) ---
#==============================================================================

while ($true) {
    Show-Menu
    $choice = Read-Host "Digite sua opção e pressione Enter"
    switch ($choice.ToLower()) {
        '1' { Start-Service 'api' }
        '2' { Stop-Service 'api' }
        '3' { Start-Service 'web' }
        '4' { Stop-Service 'web' }
        '5' { Start-Service 'desktop' }
        '6' { Stop-Service 'desktop' }
        '7' { Start-Service 'android' }
        '8' { Stop-Service 'android' }
        '9' {
            if ((Get-ServiceStatus 'emulator') -eq 'STOPPED') { if (-not (Start-Service 'emulator')) { Read-Host "Falha ao iniciar Emulador."; continue } }
            if (-not (Start-Service 'api')) { Read-Host "Falha ao iniciar API."; continue }
            Start-Service 'web'; Start-Service 'desktop'; Start-Service 'android'
            Read-Host "`n--- SEQUÊNCIA CONCLUÍDA ---`nPressione Enter..."
        }
        '10' {
            Stop-Service 'android'; Stop-Service 'desktop'; Stop-Service 'web'; Stop-Service 'api'
            if ((Get-ServiceStatus 'emulator') -eq 'RUNNING') {
                if ((Read-Host "Deseja parar o Emulador também? (s/n)") -eq 's') { Stop-Service 'emulator' }
            }
        }
        'a' { Start-Service 'emulator' }
        'b' { Stop-Service 'emulator' }
        'h' { Start-Service 'emulator' -ColdBoot }
        'c' { if ((Get-ServiceStatus 'web') -eq 'RUNNING') { Start-Process $webUrl } else { Write-Host "Servidor web precisa estar rodando." -ForegroundColor Red; Start-Sleep 2 } }
        'd' { Invoke-AdbTool 'reset' }
        'e' { Invoke-AdbTool 'devices' }
        'f' { Invoke-AdbTool 'logcat' }
        'g' { Invoke-AdbTool 'reverse' }
        'l' { Clean-Project }
        'r' { }
        'q' { break }
        default { Write-Host "Opção inválida!" -ForegroundColor Red; Start-Sleep 2 }
    }
}
```

### \#\#\# ✅ Passo 3: Teste do Painel de Controle

1.  Abra o terminal na pasta raiz do projeto.
2.  Execute o script: `.\listadetarefas-painel.ps1`
3.  Teste as opções do menu (Iniciar API, Parar API, Iniciar TUDO, etc.) para garantir que o painel está gerenciando todos os componentes do ecossistema corretamente.

-----

## 🎉 Módulo 6: Conclusão e Próximos Passos

**Parabéns\!** Ao final deste guia, você construiu e testou um ecossistema de software completo e funcional, aplicando conceitos de:

  * **🔌 Backend:** Criação de uma API REST robusta com Spring Boot.
  * **💻 Frontend:** Desenvolvimento de um cliente web reativo com Angular.
  * **🌍 Multiplataforma:** Extensão do sistema para clientes Desktop (JavaFX) e Mobile (Android).
  * **🤖 Automação:** Otimização do fluxo de trabalho com um painel de controle em PowerShell.
  * **🛠️ DevOps Básico:** Gerenciamento de um ambiente de desenvolvimento complexo.

### 🚀 Desafios Futuros (Próximos Passos):

  * **🔒 Segurança:** Implemente autenticação na API com `Spring Security` e `JWT`.
  * **🔄 Comunicação em Tempo Real:** Use `WebSockets` para sincronização automática entre os clientes.
  * **🧪 Testes Automatizados:** Escreva testes unitários e de integração para a API (`JUnit`/`Mockito`) e testes de UI para os frontends (`Jasmine`/`Karma`/`Espresso`).
  * **☁️ Deployment:** Empacote suas aplicações com **Docker** e faça o deploy em um provedor de nuvem como Heroku, AWS ou Google Cloud.

---

### 🚀 [ricardotecpro.github.io](https://ricardotecpro.github.io/)

