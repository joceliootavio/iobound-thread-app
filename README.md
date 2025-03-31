### Próximos passos

- Incluir chamada no DynamoDB
- Incluir Agent datadog
- GraalVM

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

docker cp java_app_mock:/app/certs/. ./certs

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

### Https

Chamadas https aumentam o uso de cpu e o tempo de resposta

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

- Uso de thread.sleep vai travar a thread e exigir que sejam criadas mais threads até o limite (64) do pool de threads do coroutine.
- Virtual threads não vai ajudar se vc não usar um pool virtual em coroutines, ao menos que a coroutine seja usada apenas no método do controller.

### Fargate vs EKS

O fargate tem uma regra de alocar no mĩnimo 2x memória para cada 1x vCPU, isso faz com que tenhamos sempre uma boa quantidade
de memõria para trabalhar.

Já o EKS tem a vantagem de variar o uso de cpu e memória entre o requests e o limits, e assim ẽ possível usar o máximo de cpu
na inicialização e nos momentos de pico de uso, funcionando como um auto-scaling vertical.

### Calculo de execuções simuntaneas

RPS x (Tempo médio de resposta) / 1000 => 400 x 420 / 1000 = 168 Threads

### Uso de platform threads

Por padrão a JVM aloca para cada thread 1MB, por esse motivo devemos ter bastante cuidado ao definir o tamanho do pool de threads, pode ser que a máquina não tenha memória suficiente.

### RDS

Por padrão no Spring o hikari (pool de conexões) vem com no máximo 10 conexões abertas, ou seja, se a sua aplicação mantém por muito tempo a conexão aberta isso pode virar um gargalo.

### Pedágios

- IO bloqueantes
- Criptografia
- Instrumentação de ferramentas de APM
- Logs
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
  - SNS/SQS
  - Kafka


### Atenção nos testes

- Reinicie a aplicação sempre que for executar um novo teste pois o JIT pode ter otimizado o código.
- Inicie o teste com no máximo metade do RPS que deseja atingir, simulando cenário de rollout em prod.

### Ambiente AWS
- No ambiente AWS incluímos a latência mẽdia de 7ms para cada chamada de api rest.

### Serviços com auto acoplamento

Quando o microserviço tem um alto acoplamento com suas dependências qualquer oscilação no tempo de resposta de uma das dependências degrada a performance do microserviço e consequentemente sua capacidade.

### Autoscaling

Em aplicações que não é usado o máximo da capacidade dos recursos o autoscaling por cpu ou memória é ineficiente.

### Quantidade de VUs no k6

É importante definir como máximo de VUs o nũmero de execuções simultaneas que queremos atingir, pois usar um valor muito alto pode enfileirar as requisicoes.
Favoreça o uso de progressive vus ao invés de progressive rps

### HttClient x Feign Client com virtual threads

Configuração:
- Rest Client: Http Client
- Serialização: Não
- Async: false
- Virtual threads: true

Suportou o pico de 800 RPS com um tempo máximo de resposta de 1s

Configuração:
- Rest Client: Feign Client
- Serialização: Sim
- Async: false
- Virtual threads: true

Parou de executar ao chegar em menos de 50 RPS

### Reativo

Reativo atinge o pico de 500 RPS no progressive_rps
