package br.com.benchmark.iobound_thread_app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class AiboundThreadAppApplication

fun main(args: Array<String>) {
	runApplication<AiboundThreadAppApplication>(*args)
}