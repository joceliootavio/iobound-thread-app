package br.com.benchmark.iobound_thread_app.adapter.out.feign

import br.com.benchmark.iobound_thread_app.api.response.User
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "customerClient", url = "\${rest.uri}")
interface FeignWebClient {

    @GetMapping("/mock/json")
    fun getFromApi(
        @RequestParam("delay") delay: Long,
        @RequestParam("jsonFileName") jsonFileName: String?
    ): String

    @GetMapping("/mock/user")
    fun getUser(@RequestParam("delay") delay: Long): User
}
