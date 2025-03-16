package br.com.benchmark.iobound_thread_app

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool

@Configuration
class ThreadConfig(
    @Value("\${thread.pool.type:}") val threadPoolType: String,
    @Value("\${thread.pool.size:8}") val threadPoolSize: Int
) {

    @Bean("myThreadPool")
    fun myThreadPool() =
        when(threadPoolType) {
            "CACHED" -> Executors.newCachedThreadPool()
            "FIXED" -> Executors.newFixedThreadPool(threadPoolSize)
            "VIRTUAL" -> Executors.newVirtualThreadPerTaskExecutor()
            else -> ForkJoinPool.commonPool()
        }

}