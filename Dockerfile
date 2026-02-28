# ESTÁGIO 1: Compilação (Build)
# Usando Maven com Java 8 da Temurin (comunidade ativa)
FROM maven:3.9.6-eclipse-temurin-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ESTÁGIO 2: Execução (Runtime)
# Usando a imagem JRE 8 oficial da Eclipse Temurin
FROM eclipse-temurin:8-jre
WORKDIR /app

# Copia o JAR gerado (ajustado para pegar qualquer nome de arquivo .jar)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Comando para rodar
ENTRYPOINT ["java", "-Xmx512m", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
