# Testes de Performance

## Tecnologias Utilizadas

### Ferramentas de Teste de Performance
- Docker
- k6
- Grafana
- Prometheus
- InfluxDB
- Java App Mock
- Mission Control
- VisualVM
- Micrometer

### Tecnologias do Microserviço
- Java 21
- Spring Boot 3.4
- Kotlin 1.9.0
- Open Feign
- PostgreSQL
- Undertow

## Pontos de Atenção
- Experimentar diferentes configurações
- Limitar recursos nos containers
- Manter a máquina ligada durante os testes
- Considerar que, localmente, não há latência adicional de rede AWS
- No ambiente de produção, o Datadog Agent adiciona 20% ao uso de CPU
- Uso de HTTPS impacta o consumo de CPU devido ao processo de criptografia
- Configuração inicial do Java App: 2 vCPU e 1GB de memória
- Testes realizados com apenas 1 container para avaliar a capacidade unitária do microserviço
- Importante testar múltiplas tasks/pods em ambiente AWS e validar auto-scaling

## Requisitos Não Funcionais (SLO)
- DAU (Daily Active Users)
- Throughput
- RPS Médio
- RPS Pico
- Tempo de resposta (p95, latência)

## Importância dos Percentis na Latência

### Exemplo de Média Enganosa
```plaintext
Latências: [100ms, 110ms, 105ms, 120ms, 150ms, 180ms, 2500ms]
Média = 463,57ms
Percentil 95 (p95) = **2500ms**
```

## Threads

### Pool de Threads
![Pool de Threads](images/pool_threads.png)

### Virtual Threads
![Threads Virtuais](images/virtual_threads.png)

## Comandos Úteis

### Monitoramento de Containers
```bash
docker stats --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemPerc}}\t{{.MemUsage}}"
```

### Parâmetros JVM Testados
```bash
java -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:off -XX:+UnlockDiagnosticVMOptions -XX:+LogTouchedMethods -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar > classlist.txt
java -Xshare:dump -XX:SharedClassListFile=classlist.txt -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Xshare:on -XX:SharedArchiveFile=spring-cds.jsa -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
java -Dspring.aot.enabled=true -jar target/iobound-thread-app-0.0.1-SNAPSHOT.jar
```

### Ajustes de Performance na JVM
```bash
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
```

### Geração de Certificado
```bash
keytool -genkeypair -alias mycert -keyalg RSA -keysize 2048 -storetype PKCS12 \
-keystore keystore.p12 -validity 3650 \
-dname "CN=localhost, OU=Dev, O=Company, L=City, ST=State, C=BR" \
-storepass changeit -keypass changeit
```

### Copiar Certificados do Container para a Máquina Local
```bash
docker cp java_app_mock:/app/certs/. ./certs
```

## Estratégia de Testes

### Meta
- RPS Inicial: 250
- RPS Final: 500
- 2 chamadas de API síncronas de 100ms

### Configuração Vencedora
- Web Container: Undertow
- Threads Virtuais: false
- Threads: 200
- CPU: 2 vCPU
- Memória: 1GB
- Parâmetros JVM: não aplicados

### Observações
- Aumento para 200 threads de plataforma não impacta o tempo de resposta, mesmo com aumento no RPS
- Pico de CPU de 56% ao dobrar o RPS, estabilizando em 30%

### Mock
- Suporta até 3000 RPS com no máximo 10ms de overhead

### Impacto do HTTPS
- Chamadas HTTPS aumentam o uso de CPU e tempo de resposta

### Primeira Requisição (First Request)
- Overhead 5x maior ao usar objetos serializados, possivelmente causado por lazy loading do Spring + reflection

## Configurações de Ambiente
- API Mock p95: 206ms
- Virtual Threads: true
- Java Opts: não aplicados
- AOT: não aplicado
- CDS: não aplicado
- GraalVM Native: não aplicado

### Configuração de Teste no K6
- 4 minutos com metade do RPS alvo
- 5 minutos com RPS na meta
- 2 minutos de pico com 20% acima da meta

### Metas
- Throughput: 400 RPS
- p95: 420ms
- SSL: true
- Uso de CPU: 60%
- Uso de Memória: 60%
- Pico de 50%

## Comparação entre Fargate e EKS
- **Fargate** aloca no mínimo 2x a memória para cada 1x vCPU
- **EKS** permite variação dinâmica de CPU e memória, funcionando como auto-scaling vertical

## Cálculo de Execuções Simultâneas
```plaintext
RPS x (Tempo médio de resposta) / 1000 = 400 x 420 / 1000 = 168 Threads
```

## Considerações sobre Threads
- Cada plataforma thread na JVM aloca 1MB de memória
- O pool de conexões do Hikari no Spring, por padrão, mantém 10 conexões abertas

## Principais Gargalos
- Pool de Threads
- Pool de Conexões
- Índices de Banco
- CPU e Memória
- Dependências externas (APIs, bancos de dados, SNS/SQS, Kafka)

## Cuidados ao Executar Testes
- Reiniciar a aplicação antes de cada teste para evitar otimizações do JIT
- Aumentar o RPS gradualmente

## Latência em Ambiente AWS
- Latência média de 7ms para cada chamada de API REST

## Impacto do Alto Acoplamento em Microserviços
- Dependências com tempos de resposta variáveis degradam a performance

## Estratégias de Auto-Scaling
- Auto-scaling baseado apenas em CPU/memória pode ser ineficiente

## Configuração de VUs no K6
- Número máximo de VUs deve refletir execuções simultâneas esperadas
- Preferir `progressive vus` ao invés de `progressive rps`

## Comparação: HttpClient vs Feign Client
- **HttpClient**: Suportou 800 RPS com tempo máximo de 1s
- **Feign Client**: Falhou antes de atingir 50 RPS

## Testes com Programação Reativa
- Pico de 500 RPS no `progressive_rps`

## Próximos Passos
- Incluir chamadas ao DynamoDB
- Incluir Datadog Agent
- Testar GraalVM

