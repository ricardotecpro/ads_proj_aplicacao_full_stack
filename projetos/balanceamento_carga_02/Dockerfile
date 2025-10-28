# Estágio 1: Build (Compilação)
# Usa uma imagem com o Maven e JDK para compilar o projeto
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila e gera o .jar
RUN mvn clean package -DskipTests

# Estágio 2: Run (Execução)
# Usa uma imagem Java "slim" (menor) para rodar a aplicação
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copia o .jar gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar
# A porta 8080 é a padrão do Spring Boot
EXPOSE 8080
# A variável SERVER_ID será injetada pelo Docker Compose
ENTRYPOINT ["java", "-jar", "app.jar"]