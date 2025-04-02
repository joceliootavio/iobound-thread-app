package br.com.benchmark.iobound_thread_app.adapter.out.webclient

import br.com.benchmark.iobound_thread_app.adapter.`in`.api.response.User
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.*

@Component
class WebClientAdapter(
    val webClient: WebClient
) {

    fun getFromApi(delay: Long?, jsonFileName: String?): Mono<String> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/mock/json")
                    .queryParamIfPresent("delay", Optional.ofNullable(delay))
                    .queryParamIfPresent("jsonFileName", Optional.ofNullable(jsonFileName))
                    .build()
            }
            .retrieve()
            .bodyToMono(String::class.java)
    }

    fun getUser(delay: Long?): Mono<User> {
        return webClient.get()
            .uri { uriBuilder ->
                uriBuilder.path("/mock/user")
                    .queryParamIfPresent("delay", Optional.ofNullable(delay))
                    .build()
            }
            .retrieve()
            .bodyToMono(User::class.java)
    }
}
