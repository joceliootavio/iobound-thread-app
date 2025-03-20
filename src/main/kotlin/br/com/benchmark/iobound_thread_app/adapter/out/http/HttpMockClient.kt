package br.com.benchmark.iobound_thread_app.adapter.out.http

import br.com.benchmark.iobound_thread_app.api.response.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant


@Service
class HttpMockClient(
    private val client: HttpClient,
    @Value("\${rest.uri}")
    private val restUri: String,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun getUser(delay: Long): User =
        getJson("/mock/user", delay)
            .run {
                objectMapper.readValue(this, User::class.java)
            }

    fun getJson(path: String? = null, delay: Long): String {
        logger.info("restUri: $restUri")

        val request = HttpRequest.newBuilder()
            .uri(URI.create("${restUri}${path}?delay=${delay}"))
            .timeout(Duration.ofSeconds(2)) // Tempo limite para resposta
            .GET()
            .build()

        val start = Instant.now()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val end = Instant.now()

        logger.info("Status Code: ${response.statusCode()}")
        logger.info("Response Time: ${Duration.between(start, end).toMillis()} ms")

        return response.body()
    }
}