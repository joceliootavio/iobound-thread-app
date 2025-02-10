package br.com.benchmark.iobound_thread_app.adapter.out.feign

import feign.Response
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "customerClient", url = "\${rest.uri}")
interface FeignWebClient {

    @GetMapping("/api/v1/customers/mock")
    fun getFromApi(@RequestParam("delay") delay: Long): String?
}
