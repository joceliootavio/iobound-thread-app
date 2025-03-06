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
- Incluir chamada no DynamoDB
- Incluir Agent datadog

## Comandos úteis

docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemPerc}}\t{{.MemUsage}}"

java -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:off -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar > classlist.txt
java -Xshare:dump -XX:SharedClassListFile=classlist.txt -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:on -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Dspring.aot.enabled=true -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar

### Parametros JVM

-XX:MaxRAMPercentage=75
-XX:+UseParallelGC
-XX:MaxGCPauseMillis=200
-XX:GCTimeRatio=9
-XX:+UseStringDeduplication
-XX:SurvivorRatio=4
-XX:ParallelGCThreads=2
-XX:NewRatio=1
-XX:+UseAdaptiveSizePolicy
-XX:MaxHeapFreeRatio=100

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

### Configurações ambiente

- Api mock p95: 206ms
- Virtual threads: true
- Java opts: Não aplicado
- AOT: Não aplicado
- CDS: Não aplicado
- GraalVM Native: Não aplicado

#### K6
- Inicio de 4 minutos com metade da meta de RPS
- 5 minutos com o valor da meta de RPS
- 2 minutos de pico com 20% acima da meta

### Metas

- Throughput: 400 RPS
- p95: 420ms
- SSL: true
- Uso de cpu: 60%
- Uso de memõria: 60%
- Pico de 50%

### Uso de 0,5 cpu

Observamos os seguintes pontos:
- A aplicação demora mais para estabilizar o tempo de resposta
- Ao atingir o RPS 100 a aplicação atinge o p95 de 404ms
- Ao aumentar em 50% o RPS o uso de cpu topa, o tempo de resposta da aplicação degrada e não atinge o pico de 300 RPS
- Bastante variação no uso de cpu


### Coroutines com operações bloqueantes

- Uso de thread.sleep vai travar a thread e exigir que sejam criadas mais threads até o limite do pool de threads do coroutine
- Virtual threads não vai ajudar se vc não usar um pool virtual em coroutines, ao menos que a coroutine seja usada apenas no metodo do controller.

### Fargate vs EKS

O fargate tem uma regra de alocar no mĩnimo 2x memória para cada 1x vcpu, isso faz com que tenhamos sempre uma boa quantidade
de memõria para trabalhar.

Já o EKS tem a vantagem de variar o uso de cpu e memória entre o requests e o limits, e assim ẽ possĩvel usar o máximo de cpu
na inicialização e nos momentos de pico de uso, funcionando assim como um auto-scaling vertical.

### Calculo de execuções simuntaneas

RPS x (Tempo médio de resposta) / 1000 => 400 x 420 / 1000 = 168 Threads

### Uso de platform threads

Por padrão a JVM aloca para cada thread 1MB, por esse motivo deve-se ter bastante cuidado ao definir o tamanho ideal do pool de threads, pois pode ser que a máquina não tenha memória suficiente.

### RDS

Por padrão no Spring o hikari (pool de conexões) vem com no máximo 10 conexões abertas, ou seja, se a sua aplicação mantém por muito tempo a conexão aberta isso pode virar um gargalo.

### Pedágios

- IO bloqueantes
- Criptografia
- Instrumentação de ferramentas de APM
- Reflexão
- Compilação
  - JIT
  - AOT
  - Native

### Gargalos
- Pool de threads
- Pool de conexões
- Índices
- CPU
- Memória
- Serviços de terceiros
  - Apis
  - Banco de dados