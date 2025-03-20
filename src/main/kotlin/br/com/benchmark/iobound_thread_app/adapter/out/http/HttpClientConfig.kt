package br.com.benchmark.iobound_thread_app.adapter.out.http

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient
import java.time.Duration
import java.util.concurrent.ExecutorService

@Configuration
class HttpClientConfig(
    @Qualifier("httpClientThreadPool")
    val executor: ExecutorService
) {

    @Bean
    fun clientFatory() = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5)) // Tempo limite para conexão
        .executor(executor)
        .build()
}