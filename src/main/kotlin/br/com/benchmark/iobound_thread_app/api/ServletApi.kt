package br.com.benchmark.iobound_thread_app.api

import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServletConfig {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun meuServlet(): ServletRegistrationBean<HttpServlet> {
        return ServletRegistrationBean(object : HttpServlet() {
            override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
                logger.info("getting from servlet")

                resp.contentType = "text/plain"
                resp.writer.write("Olá, Servlet registrado como Bean no Spring Boot com Kotlin!")
            }
        }, "/api/servlet")
    }
}

