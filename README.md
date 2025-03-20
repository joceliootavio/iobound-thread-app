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
    - First Load Serialization - WebClient reativo
    - Incluir milhões de registro na tabela
    - Buscar registros com sucesso do banco
    - Usar string randomicas nas collections do memoryOps
- Endpoint com servlet
- Incluir chamada no DynamoDB
- Incluir Agent datadog
- GraalVM
- 


### Async Profiler
./asprof -e lock -d 30 -f lock-profile.html 12345

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
  - SNS/SQS
  - Kafka


### Atenção nos testes

- Reinicie a aplicação sempre que for executar um novo teste pois o JIT pode ter otimizado o código.
- Inicie o teste com no máximo metade do RPS que deseja atingir, simulando cenário de rollout em prod.
- 

### Ambiente AWS
- No ambiente AWS incluimos a latência mẽdia de 7ms para cada chamada de api rest.
- Há tambẽm o pedãgio por usar ferramentas de APM.

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

### Apresentação

1. Fala sobre capacidade
3. Testes de performance
   - Uso de VUs elevado
4. Preparar ambiente local
5. Executa teste com memoryOps, 1 vCPU e 1000 RPS (operações em memória O(n2))
6. Adiciona fromRDS
7. Adiciona memoryOps e http client 1x300ms
8. Volta ao passo de requisitos não funcionais (SLO)
  - DAU
  - Throughput
    - RPS Médio
    - RPS Pico
  - Tempo de resposta (p95)
9. Reduz pra 70 RPS
10. Incrementa para 2 chamadas de http client
11. Aumenta para 2 vCPUs
12. Aumenta para 4 vCPUs
13. Aumenta para 600ms o delay das chamadas http client
14. Quanto custa? 
    - R$ 320,00 / vCPU
    - 34 microserviços na VI2
15. Observabilidade
16. Parametros de tunning da JVM
17. GraalVM
18. Uso de CPU e diferença entre metrica k6 e metrica http server
19. Diminui para 30 RPS
20. IOBound x CPUBound
21. Gargalos
    - Pool de threads
    - Pool de conexões
    - CPU
    - Memória
    - Full Scan
    - Serviços de terceiros
        - Apis
        - Banco de dados
        - SNS/SQS
        - Kafka*
20. Calculo de tamanho de pool
    - Analogia com hodómetro
21. Benchmarks de performance da comunidade
    - Tomcat x Undertow
    - IO Blocking x IO NonBlocking (Reactive)
    - Threads x Coroutines (Light threads) (Testar diferençca entre sleep e delay no mock)
    - Analogia com cozinha
22. Melhorar tempo de resposta quando chamadas não são dependentes
24. Assincronismo x Paralelismo x Programação concorrente
25. Volta para 300ms, aumenta para 3 http client, api 3x300ms e assincronismo com threads
26. Cenários de alto throughput
27. Aumenta para 100 RPS
28. WebClient reativo


sudo usermod -aG docker jocelio
newgrp docker
