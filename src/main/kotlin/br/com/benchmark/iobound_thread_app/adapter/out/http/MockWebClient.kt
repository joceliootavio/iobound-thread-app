package br.com.benchmark.iobound_thread_app.adapter.out.http

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.time.Instant


@Service
class MockWebClient(
    private val client: HttpClient,
    @Value("\${rest.uri}")
    private val restUri: String
) {

    fun getRequestSuspended(path: String? = null) {
//        getRequest(path)
        sleep(1000)
    }

    fun getRequest(path: String? = null) {
        println("restUri: $restUri")

        val request = HttpRequest.newBuilder()
            .uri(URI.create(path ?: "${restUri}/test"))
            .timeout(Duration.ofSeconds(2)) // Tempo limite para resposta
            .GET()
            .build()

        val start = Instant.now()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        val end = Instant.now()

        println("Status Code: ${response.statusCode()}")
        println("Response Body: ${response.body()}")
        println("Response Time: ${Duration.between(start, end).toMillis()} ms")
    }
}
