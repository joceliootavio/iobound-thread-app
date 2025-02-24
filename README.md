### To do

    - Incluir payload nas requisições HTTP
    - Serialização de json
    - Coroutines no Controller
    - Uso de CDS com AOT
    - Parametros da JVM
    - Mais chamadas de api

- Utilizar mais cpu e memória
- Incluir chamada no DynamoDB
- Consulta no banco RDS
- Incluir logs de json
- Usar Java 17
- Native com GraalVM
- Incluir Agent e tracing

## Comandos úteis

podman stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemPerc}}\t{{.MemUsage}}"

java -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:off -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar > classlist.txt
java -Xshare:dump -XX:SharedClassListFile=classlist.txt -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:on -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Dspring.aot.enabled=true -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
