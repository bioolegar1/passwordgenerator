# Estágio 1: Build da Aplicação com Maven e JDK completo
# Usamos uma imagem oficial do Maven com Java 21 para compilar o projeto.
FROM maven:3.9-eclipse-temurin-21 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache de camadas do Docker.
# Se as dependências não mudarem, o Docker não vai baixá-las novamente.
COPY pom.xml .
COPY .mvn .mvn
RUN mvn dependency:go-offline

# Copia o restante do código-fonte
COPY src ./src

# Executa o build do Maven para gerar o arquivo .jar, pulando os testes.
RUN mvn clean package -DskipTests


# Estágio 2: Imagem Final de Execução
# Usamos uma imagem base leve, apenas com o Java Runtime Environment (JRE).
# Isso torna a imagem final muito menor e mais segura.
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho
WORKDIR /app

# Copia apenas o arquivo .jar gerado no estágio anterior para a imagem final
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta 8080, que é a porta padrão do Spring Boot
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado
ENTRYPOINT ["java", "-jar", "app.jar"]