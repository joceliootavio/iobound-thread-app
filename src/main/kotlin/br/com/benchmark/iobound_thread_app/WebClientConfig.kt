package br.com.benchmark.iobound_thread_app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${rest.uri}") val restUri: String
) {

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .baseUrl(restUri)
            .build()
    }
}
