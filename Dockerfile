# Usa a imagem do Maven com JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Define o diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto para o container
COPY . .

# Usa o cache do Maven para evitar downloads desnecessários
VOLUME ["/root/.m2"]

# Executa o build com AOT e gera o .jar
RUN mvn -DskipTests=true clean package

# Usa uma imagem mais enxuta para rodar a aplicação
FROM eclipse-temurin:21-jdk

# Define o diretório de trabalho
WORKDIR /app

# Copia o .jar gerado na etapa anterior
COPY --from=build /app/target/iobound-thread-app-0.0.1-SNAPSHOT.jar app.jar

RUN java -Xshare:dump -XX:+UnlockDiagnosticVMOptions -XX:+PrintSharedArchiveAndExit -XX:SharedArchiveFile=/app/cds.jsa -jar app.jar || true

# Define os parâmetros da JVM otimizados para 1 vCPU
CMD ["java", \
     "-XX:+UseSerialGC", \
     "-XX:+AlwaysPreTouch", \
     "-XX:+UseStringDeduplication", \
     "-XX:+OptimizeStringConcat", \
     "-XX:MaxRAMPercentage=75", \
     "-XX:ActiveProcessorCount=1", \
     "-XX:+TieredCompilation", \
     "-XX:+UseContainerSupport", \
     "-XX:SharedArchiveFile=/app/cds.jsa", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-Dcom.sun.management.jmxremote.port=5000", \
     "-Dcom.sun.management.jmxremote.rmi.port=5000", \
     "-Dcom.sun.management.jmxremote.local.only=false", \
     "-Dcom.sun.management.jmxremote.authenticate=false", \
     "-Dcom.sun.management.jmxremote.ssl=false", \
     "-Djava.rmi.server.hostname=localhost", \
     "-Dspring.aot.enabled=true", \
     "-jar", "app.jar"]
