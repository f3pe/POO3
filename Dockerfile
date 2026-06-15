# === ESTÁGIO 1: BUILD ===
# Utiliza uma imagem oficial do Maven com Java 21 para compilar o código
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia primeiro o pom.xml e descarrega as dependências
# (Isto otimiza o cache do Docker e acelera os próximos builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila o projeto gerando o .jar (ignorando testes para ser mais rápido)
COPY src ./src
RUN mvn clean package -DskipTests -Pproduction

# === ESTÁGIO 2: EXECUÇÃO ===
# Utiliza uma imagem muito mais leve apenas com o JRE (Java 21 Runtime Environment)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia apenas o ficheiro .jar gerado no Estágio 1
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring Boot e Vaadin
EXPOSE 8080

# Comando para iniciar o nosso ERP
ENTRYPOINT ["java", "-jar", "app.jar"]