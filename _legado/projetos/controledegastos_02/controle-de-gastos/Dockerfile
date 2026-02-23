# Estágio 1: Build da Aplicação com OpenJDK
FROM openjdk:21-jdk as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Imagem Final de Execução (mais leve)
FROM openjdk:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]