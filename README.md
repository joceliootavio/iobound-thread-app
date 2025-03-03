### To do

    - Incluir payload nas requisições HTTP
    - Serialização de json
    - Coroutines no Controller
    - Uso de CDS com AOT
    - Parametros da JVM
    - Mais chamadas de api

    - Usar mais cpu e memoria
    - incluir metodo o(n2)
    - Consulta no banco RDS 
    - First Load Serialization
- GraalVM
- Usar reactor
- Incluir chamada no DynamoDB
- Incluir logs de json
- Usar Java 11
- Incluir Agent datadog

## Comandos úteis

podman stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemPerc}}\t{{.MemUsage}}"

java -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:off -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar > classlist.txt
java -Xshare:dump -XX:SharedClassListFile=classlist.txt -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:on -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Dspring.aot.enabled=true -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar

### Gerar certificado

keytool -genkeypair -alias mycert -keyalg RSA -keysize 2048 -storetype PKCS12 \
-keystore keystore.p12 -validity 3650 \
-dname "CN=localhost, OU=Dev, O=Company, L=City, ST=State, C=BR" \
-storepass changeit -keypass changeit

### Copiar certificados para maquina local

podman cp java_app_mock:/app/certs/. ./certs

### Reiniciar podman

wsl --shutdown
podman machine stop
podman machine start

## Melhor estratégia

### Meta
- RPS Inicial: 250
- RPS Final: 500
- 2 chamadas de api de 100ms síncronas

### Configurações Winner
- Web Container: Undertow
- Threads virtuais: false
- Threads: 200
- CPU: 2 vCPU
- Memória 1GB
- Parametros JVM: não aplicado

Aumentar para 200 Threads de plataforma é a unica estratégia que não afeta o tempo de resposta mesmo quando o RPS aumenta
CPU deu pico de 56% no momento que dobra o RPS mas depois voltou a média de 30%

### Mock
Suporta até 3000 RPS com no máximo 10ms de overhead

### Asincronismo
Inclusao de virtual threads para paralelizar as chamadas do mock reduziu o tempo total de resposta, porém não foi exatamente a metada

### Https
Chamadas https aumentam o uso de cpu e o tempo de resposta

### Pool de conexões JDBC
Usar o gerenciamento padrão de pool do spring faz com que a conexão se mantenha aberta durante todo o processamento da requisição,
portanto quando o tempo de resposta é alto o pool acaba sendo um limitador de throughput.

### First request
Primeira execução tem um overhead 5x maior quando utilizamos objetos serializados, ao que tudo indica nos logs é um overhead
trazido pela estratégia lazy loading do spring + reflection.