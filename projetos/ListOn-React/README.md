# 📘 Guia Completo: Projeto "Lista de tarefas – ListOn"

Este guia aborda a criação de um sistema completo de lista de tarefas, cobrindo o back-end, um cliente web e um aplicativo mobile.

## ⚙️ Stack Tecnológica (Confirmada)

- **Back-end:** Java 21, Spring Boot (Web, Data JPA, Security), Lombok, PostgreSQL, H2, SpringDoc (Swagger)
- **Front-end Web:** React (com Vite) + MUI (Material-UI)
- **App Mobile:** React Native (com Expo)
- **Banco de Dados:** H2 (Dev) e PostgreSQL (Prod)

---

## 🌎 Parte 1: Preparação do Ambiente

Antes de começar, garanta que você tenha as seguintes ferramentas instaladas:

- **JDK 21:** O kit de desenvolvimento Java.
- **Maven 3.8+:** Para gerenciamento de dependências do back-end.
- **Node.js 18+:** Incluindo `npm` (gerenciador de pacotes).
- **IDE de sua preferência:** IntelliJ IDEA, VS Code ou Eclipse.
- **Cliente SQL:** DBeaver, pgAdmin ou similar.
- **Expo CLI (para mobile):** Instale com `npm install -g expo-cli`.

---

## 🧠 Parte 2: Back-end (Spring Boot API)

Nosso back-end será o cérebro da aplicação, uma API RESTful responsável por todas as regras de negócio e persistência.

### 2.1. Geração do Projeto

Vamos usar o **Spring Initializr** (start.spring.io) com as seguintes configurações:

- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 3.x.x (use a versão estável mais recente)
- **Java:** 21
- **Dependencies:**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Boot DevTools
  - PostgreSQL Driver
  - H2 Database
  - Lombok
  - SpringDoc OpenAPI (substitui o antigo Swagger)

Clique em "Generate" e extraia o projeto.

### 2.2. Estrutura do Projeto (Back-end)

Após abrir na sua IDE, a estrutura principal (dentro de `src/main/java`) ficará assim:

```
com.liston
├── ListonApplication.java
├── config
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── controller
│   └── TaskController.java
├── model
│   └── Task.java
└── repository
    └── TaskRepository.java
```

### 2.3. Configurando o `pom.xml`

Apenas para confirmar, seu `pom.xml` deve conter estas dependências:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.5.0</version>
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
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    </dependencies>
```

### 2.4. Configuração da Aplicação (H2 e PostgreSQL)

Vamos configurar o `src/main/resources/application.properties`. Usaremos "perfis" (profiles) do Spring para alternar entre H2 (desenvolvimento) e PostgreSQL (produção).

Por padrão, usaremos o perfil `dev`.

```properties
# Perfil ativo padrão
spring.profiles.active=dev

# Configurações do SpringDoc (Swagger)
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

---
# Perfil de Desenvolvimento (H2)
spring.profiles=dev
spring.datasource.url=jdbc:h2:mem:listondb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update

---
# Perfil de Produção (PostgreSQL)
spring.profiles=prod
spring.datasource.url=jdbc:postgresql://localhost:5432/listondb
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=admin # <-- Mude para sua senha
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate # Em produção, nunca use 'create' ou 'update'
```

### 2.5. Model (A Entidade `Task`)

Vamos criar nossa entidade JPA.

**`src/main/java/com/liston/model/Task.java`**

```java
package com.liston.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data // Gera Getters, Setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private boolean completed = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
```

### 2.6. Repository (A Camada de Dados)

O Spring Data JPA facilita isso.

**`src/main/java/com/liston/repository/TaskRepository.java`**

```java
package com.liston.repository;

import com.liston.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Nossos métodos CRUD básicos (save, findById, findAll, deleteById)
    // já estão incluídos aqui pelo JpaRepository.
}
```

### 2.7. Controller (A API REST)

Criamos o _endpoint_ para o front-end consumir. Para este CRUD simples, podemos pular a camada de _Service_ e injetar o repositório diretamente no _Controller_.

**`src/main/java/com/liston/controller/TaskController.java`**

```java
package com.liston.controller;

import com.liston.model.Task;
import com.liston.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks") // Define o prefixo para todos os endpoints
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    // 1. CREATE
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Remove o ID para garantir que é uma criação
        task.setId(null);
        Task newTask = taskRepository.save(task);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    // 2. READ (All)
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return ResponseEntity.ok(tasks);
    }

    // 3. READ (By ID)
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(ResponseEntity::ok) // Se achar, retorna 200 OK
                .orElse(ResponseEntity.notFound().build()); // Se não, retorna 404
    }

    // 4. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(existingTask -> {
                    existingTask.setTitle(taskDetails.getTitle());
                    existingTask.setDescription(taskDetails.getDescription());
                    existingTask.setCompleted(taskDetails.isCompleted());
                    Task updatedTask = taskRepository.save(existingTask);
                    return ResponseEntity.ok(updatedTask);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskRepository.delete(task);
                    return ResponseEntity.noContent().build(); // Retorna 204 No Content
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
```

### 2.8. Configuração de Segurança e CORS

Precisamos configurar o CORS (Cross-Origin Resource Sharing) para que o React e o React Native possam acessar a API. Também configuramos o Spring Security (que solicitamos) para ser permissivo por enquanto e habilitar o H2 Console.

**`src/main/java/com/liston/config/CorsConfig.java`**

```java
package com.liston.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite CORS para nossa API
                .allowedOrigins("http://localhost:5173", "http://localhost:3000") // Origem do React (Vite/CRA)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

**`src/main/java/com/liston/config/SecurityConfig.java`**

```java
package com.liston.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF (comum em APIs stateless)
            .csrf(csrf -> csrf.disable())

            // Configura o CORS (usará o @Bean de CorsConfig)
            .cors(cors -> {})

            // Define a política de sessão como STATELESS (não guarda estado)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Regras de autorização
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso ao H2 console
                .requestMatchers(toH2Console()).permitAll()
                // Permite acesso ao Swagger
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Permite acesso à nossa API de tarefas (sem autenticação por enquanto)
                .requestMatchers("/api/tasks/**").permitAll()
                // Qualquer outra requisição precisa de autenticação
                .anyRequest().authenticated()
            )

            // Configuração específica para o H2 Console funcionar em frames
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                ))
            );

        return http.build();
    }
}
```

### 2.9. Rodando o Back-end

Neste ponto, seu back-end está pronto.

1.  Execute a classe `ListonApplication.java` na sua IDE.
2.  Acesse `http://localhost:8080/swagger-ui.html` para ver a documentação da API (Swagger).
3.  Acesse `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:listondb`, User: `sa`, Pass: `password`) para ver o banco H2.

Seu back-end está 100% funcional.

---

## ⚛️ Parte 3: Front-end Web (React + MUI)

Vamos construir a interface web. Usaremos o **Vite** por ser mais rápido que o `create-react-app`.

### 3.1. Geração do Projeto (Vite)

No seu terminal, navegue até a pasta raiz do seu projeto (onde está o back-end) e execute:

```sh
# Cria um novo projeto React com Vite
npm create vite@latest liston-web -- --template react-swc

# Entre na pasta
cd liston-web

# Instale as dependências
npm install

# Instale as dependências do projeto: Axios (para API) e MUI (para UI)
npm install axios @mui/material @emotion/react @emotion/styled @mui/icons-material
```

### 3.2. Estrutura do Projeto (Front-end)

Dentro de `liston-web/src/`, vamos criar esta estrutura:

```
src/
├── api
│   └── apiService.js   (Nosso cliente Axios)
├── components
│   ├── TaskForm.jsx
│   └── TaskList.jsx
├── App.jsx             (Componente principal)
└── main.jsx            (Ponto de entrada)
```

### 3.3. Configurando o `apiService.js`

Este arquivo centraliza a configuração do Axios.

**`src/api/apiService.js`**

```javascript
import axios from "axios";

// Cria uma instância do Axios com a URL base da nossa API
const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

// Funções de CRUD para Tarefas
export const getTasks = () => api.get("/tasks");
export const createTask = (task) => api.post("/tasks", task);
export const updateTask = (id, task) => api.put(`/tasks/${id}`, task);
export const deleteTask = (id) => api.delete(`/tasks/${id}`);

export default api;
```

### 3.4. O Componente Principal `App.jsx`

Este componente vai gerenciar o estado principal da aplicação (a lista de tarefas).

**`src/App.jsx`**

```javascript
import { useState, useEffect } from "react";
import {
  Container,
  Typography,
  Box,
  CssBaseline,
  AppBar,
  Toolbar,
} from "@mui/material";
import TaskList from "./components/TaskList";
import TaskForm from "./components.TaskForm";
import { getTasks, createTask, updateTask, deleteTask } from "./api/apiService";

function App() {
  const [tasks, setTasks] = useState([]);

  // Função para carregar as tarefas da API
  const fetchTasks = async () => {
    try {
      const response = await getTasks();
      setTasks(response.data);
    } catch (error) {
      console.error("Erro ao buscar tarefas:", error);
    }
  };

  // useEffect para carregar as tarefas quando o componente montar
  useEffect(() => {
    fetchTasks();
  }, []);

  // --- Funções de Manipulação de Dados ---

  const handleAddTask = async (title, description) => {
    try {
      const newTask = { title, description, completed: false };
      await createTask(newTask);
      fetchTasks(); // Recarrega a lista
    } catch (error) {
      console.error("Erro ao adicionar tarefa:", error);
    }
  };

  const handleToggleTask = async (task) => {
    try {
      const updatedTask = { ...task, completed: !task.completed };
      await updateTask(task.id, updatedTask);
      fetchTasks(); // Recarrega a lista
    } catch (error) {
      console.error("Erro ao atualizar tarefa:", error);
    }
  };

  const handleDeleteTask = async (id) => {
    try {
      await deleteTask(id);
      fetchTasks(); // Recarrega a lista
    } catch (error) {
      console.error("Erro ao deletar tarefa:", error);
    }
  };

  return (
    <>
      <CssBaseline />
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6">ListOn - Lista de Tarefas</Typography>
        </Toolbar>
      </AppBar>
      <Container maxWidth="md">
        <Box mt={4}>
          <Typography variant="h4" gutterBottom>
            Nova Tarefa
          </Typography>
          <TaskForm onAddTask={handleAddTask} />
        </Box>
        <Box mt={4}>
          <Typography variant="h4" gutterBottom>
            Tarefas
          </Typography>
          <TaskList
            tasks={tasks}
            onToggle={handleToggleTask}
            onDelete={handleDeleteTask}
          />
        </Box>
      </Container>
    </>
  );
}

export default App;
```

### 3.5. Componente `TaskForm.jsx`

**`src/components/TaskForm.jsx`**

```javascript
import { useState } from "react";
import { TextField, Button, Box } from "@mui/material";

function TaskForm({ onAddTask }) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!title.trim()) return; // Não adiciona se o título estiver vazio

    onAddTask(title, description);
    setTitle("");
    setDescription("");
  };

  return (
    <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
      <TextField
        margin="normal"
        required
        fullWidth
        id="title"
        label="Título da Tarefa"
        name="title"
        autoFocus
        value={title}
        onChange={(e) => setTitle(e.target.value)}
      />
      <TextField
        margin="normal"
        fullWidth
        id="description"
        label="Descrição (Opcional)"
        name="description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
      />
      <Button type="submit" variant="contained" sx={{ mt: 3, mb: 2 }}>
        Adicionar Tarefa
      </Button>
    </Box>
  );
}

export default TaskForm;
```

### 3.6. Componente `TaskList.jsx`

**`src/components/TaskList.jsx`**

```javascript
import {
  List,
  ListItem,
  ListItemText,
  IconButton,
  Checkbox,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";

function TaskList({ tasks, onToggle, onDelete }) {
  return (
    <List>
      {tasks.length === 0 ? (
        <ListItem>
          <ListItemText primary="Nenhuma tarefa encontrada." />
        </ListItem>
      ) : (
        tasks.map((task) => (
          <ListItem
            key={task.id}
            secondaryAction={
              <IconButton
                edge="end"
                aria-label="delete"
                onClick={() => onDelete(task.id)}
              >
                <DeleteIcon />
              </IconButton>
            }
            disablePadding
          >
            <Checkbox
              edge="start"
              checked={task.completed}
              tabIndex={-1}
              disableRipple
              onChange={() => onToggle(task)}
            />
            <ListItemText
              primary={task.title}
              secondary={task.description}
              style={{
                textDecoration: task.completed ? "line-through" : "none",
                color: task.completed ? "grey" : "inherit",
              }}
            />
          </ListItem>
        ))
      )}
    </List>
  );
}

export default TaskList;
```

### 3.7. Rodando o Front-end Web

1.  Certifique-se de que seu back-end (Parte 2) esteja rodando em `localhost:8080`.
2.  Na pasta `liston-web`, rode:
    ```sh
    npm run dev
    ```
3.  Abra seu navegador em `http://localhost:5173`. Você verá sua aplicação web funcionando.

---

## 📱 Parte 4: Aplicativo Mobile (React Native)

Agora, vamos criar o cliente mobile usando **Expo**, que facilita o desenvolvimento.

### 4.1. Geração do Projeto (Expo)

Na pasta raiz do projeto (ao lado do back-end e da web), rode:

```sh
# Cria um novo projeto Expo
npx create-expo-app liston-mobile

# Entre na pasta
cd liston-mobile

# Instale o Axios
npm install axios
```

### 4.2. Estrutura do Projeto (Mobile)

A estrutura será mais simples. Vamos editar o `App.js` e criar um `apiService.js` específico.

```
liston-mobile/
├── api
│   └── apiService.js   (Cliente Axios Mobile)
└── App.js              (Componente principal)
```

### 4.3. Configurando o `apiService.js` (Mobile)

**⚠️ Ponto Crítico:** No mobile, `localhost` não funciona (ele aponta para o próprio dispositivo). Você deve usar o **endereço IP local** da sua máquina que está rodando o back-end (ex: `192.168.1.10`).

> **Como achar seu IP (Windows):** No terminal (cmd), digite `ipconfig` e procure pelo "Endereço IPv4".

**`api/apiService.js`**

```javascript
import axios from "axios";

// 🚨 MUDE ESTE IP para o IP local da sua máquina!
const API_URL = "http://192.168.1.10:8080/api";

const api = axios.create({
  baseURL: API_URL,
});

export const getTasks = () => api.get("/tasks");
export const createTask = (task) => api.post("/tasks", task);
export const updateTask = (id, task) => api.put(`/tasks/${id}`, task);
export const deleteTask = (id) => api.delete(`/tasks/${id}`);
```

### 4.4. O Componente Principal `App.js`

Vamos reescrever o `App.js` padrão do Expo para ser nossa aplicação.

**`App.js`**

```javascript
import React, { useState, useEffect } from "react";
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  Button,
  FlatList,
  SafeAreaView,
  StatusBar,
  TouchableOpacity,
  Platform,
} from "react-native";
import { getTasks, createTask, updateTask, deleteTask } from "./api/apiService";

// Um componente simples de "Checkbox" (React Native não tem um nativo)
const Checkbox = ({ isChecked, onToggle }) => (
  <TouchableOpacity onPress={onToggle} style={styles.checkbox}>
    {isChecked && <Text style={styles.checkmark}>✓</Text>}
  </TouchableOpacity>
);

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");

  const fetchTasks = async () => {
    try {
      const response = await getTasks();
      setTasks(response.data);
    } catch (error) {
      console.error("Erro ao buscar tarefas (Mobile):", error);
    }
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const handleAddTask = async () => {
    if (!title.trim()) return;
    try {
      await createTask({ title, description: "", completed: false });
      fetchTasks();
      setTitle("");
    } catch (error) {
      console.error("Erro ao adicionar:", error);
    }
  };

  const handleToggleTask = async (task) => {
    try {
      const updatedTask = { ...task, completed: !task.completed };
      await updateTask(task.id, updatedTask);
      fetchTasks();
    } catch (error) {
      console.error("Erro ao atualizar:", error);
    }
  };

  const handleDeleteTask = async (id) => {
    try {
      await deleteTask(id);
      fetchTasks();
    } catch (error) {
      console.error("Erro ao deletar:", error);
    }
  };

  const renderTask = ({ item }) => (
    <View style={styles.taskItem}>
      <Checkbox
        isChecked={item.completed}
        onToggle={() => handleToggleTask(item)}
      />
      <Text style={[styles.taskTitle, item.completed && styles.taskCompleted]}>
        {item.title}
      </Text>
      <Button title="X" color="red" onPress={() => handleDeleteTask(item.id)} />
    </View>
  );

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.header}>ListOn Mobile</Text>

      <View style={styles.form}>
        <TextInput
          style={styles.input}
          placeholder="Nova Tarefa"
          value={title}
          onChangeText={setTitle}
        />
        <Button title="Adicionar" onPress={handleAddTask} />
      </View>

      <FlatList
        data={tasks}
        renderItem={renderTask}
        keyExtractor={(item) => item.id.toString()}
      />
    </SafeAreaView>
  );
}

// Estilos
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f5f5f5",
    marginTop: StatusBar.currentHeight || 0,
    paddingHorizontal: 20,
  },
  header: {
    fontSize: 28,
    fontWeight: "bold",
    marginVertical: 20,
    textAlign: "center",
  },
  form: {
    flexDirection: "row",
    marginBottom: 20,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: "#ccc",
    padding: 10,
    marginRight: 10,
    borderRadius: 5,
    backgroundColor: "#fff",
  },
  taskItem: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#fff",
    padding: 15,
    marginBottom: 10,
    borderRadius: 5,
    elevation: 2,
  },
  taskTitle: {
    flex: 1,
    fontSize: 16,
    marginLeft: 10,
  },
  taskCompleted: {
    textDecorationLine: "line-through",
    color: "grey",
  },
  checkbox: {
    width: 24,
    height: 24,
    borderWidth: 2,
    borderColor: "blue",
    borderRadius: 4,
    justifyContent: "center",
    alignItems: "center",
  },
  checkmark: {
    color: "blue",
    fontSize: 14,
    fontWeight: "bold",
  },
});
```

### 4.5. Rodando o App Mobile

1.  Certifique-se de que seu back-end (Parte 2) esteja rodando e acessível pelo IP local que você configurou em `apiService.js`.
2.  Instale o app "Expo Go" no seu celular (Android ou iOS).
3.  Na pasta `liston-mobile`, rode:
    ```sh
    npm start
    ```
4.  Um QR Code aparecerá no seu terminal. Escaneie este QR Code usando o app Expo Go no seu celular.

Sua aplicação mobile será carregada no seu dispositivo, conectada ao seu back-end local.

---

## 🚀 Parte 5: Próximos Passos (Deploy)

- **Back-end:** Você pode fazer o deploy da API Spring Boot no **Render** (que oferece um plano gratuito para PostgreSQL) ou **Heroku**. Lembre-se de mudar o perfil do Spring para `prod`.
- **Front-end Web:** O app React pode ser "deployado" gratuitamente em segundos na **Vercel** ou **Netlify**.
- **Front-end Mobile:** Para publicar na Play Store ou App Store, você usará os comandos `eas build` do Expo, seguindo a documentação oficial.
