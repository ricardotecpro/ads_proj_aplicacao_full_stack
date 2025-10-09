# ----- ESTÁGIO 1: Build com Maven e JDK -----
# Usamos a imagem oficial do Maven que já contém o JDK 21
FROM maven:3.9-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de configuração do Maven primeiro para aproveitar o cache do Docker
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Baixa as dependências
RUN mvn dependency:go-offline

# Copia o restante do código-fonte da aplicação
COPY src ./src

# Compila a aplicação, gera o .jar e pula os testes para acelerar o build da imagem
RUN mvn clean package -DskipTests


# ----- ESTÁGIO 2: Runtime com JRE -----
# Usamos uma imagem base leve, contendo apenas o Java Runtime Environment (JRE)
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o arquivo .jar final do estágio de build para a nossa imagem final
COPY --from=builder /app/target/gestaodeestoques-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta 8080 para que possamos nos conectar ao nosso container de fora
EXPOSE 8080

# Comando que será executado quando o container iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]