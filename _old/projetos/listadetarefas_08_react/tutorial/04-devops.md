# 🎓 Módulo 4: DevOps e Deploy

**Objetivo**: Empacotar nossa aplicação para rodar em qualquer lugar usando Docker.

---

## 1. O que é Docker?
Docker cria "containers". Imagine uma lancheira que tem tudo que seu app precisa (Java, Node, código). Você pode levar essa lancheira para qualquer computador e ela vai "abrir" e funcionar igual.

---

## 2. Dockerfile do Backend

Crie `backend/Dockerfile`:

```dockerfile
# Etapa 1: Construção
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Execução
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 3. Docker Compose

Crie `docker-compose.yml` na raiz do projeto para subir tudo junto (Banco + Backend).

```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: liston_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/liston_db
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      - db
```

---

## 🛑 Pare e Teste

1. Certifique-se de ter o Docker instalado.
2. Na raiz: `docker-compose up --build`.
3. O Docker vai baixar o Postgres, compilar seu Java e rodar tudo. Mágico! ✨

---

## 🏆 Desafio Final
Pesquise como fazer o deploy desse container em serviços gratuitos como **Render** ou **Railway**.
