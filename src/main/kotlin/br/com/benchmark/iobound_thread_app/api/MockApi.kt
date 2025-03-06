package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.api.response.*
import kotlinx.coroutines.delay
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.time.LocalDateTime
import kotlin.random.Random

@RestController
@RequestMapping("/mock")
class MockApi(
    val resourceLoader: ResourceLoader
) {

    private val resourceMap: MutableMap<String, String> = mutableMapOf()

    @GetMapping("/user", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    suspend fun mockGetUser(
        @RequestParam("delay") delay: Long? = null,
        @RequestParam("sleep") sleep: Long? = null
    ): String = mockAnyJsonFile(
        delay = delay,
        sleep = sleep,
        jsonFileName = "user"
    )

    @GetMapping("/json", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    suspend fun mockAnyJsonFile(
        @RequestParam("delay") delay: Long? = null,
        @RequestParam("sleep") sleep: Long? = null,
        @RequestParam("jsonFileName") jsonFileName: String?
    ): String {
        val start = System.currentTimeMillis()

        delay?.let { delay(delay) }
        sleep?.let { sleep(sleep) }

        val response: String?
        if (jsonFileName == null) {
            response = ""
        } else {
            val mapKey = jsonFileName
            if (resourceMap.containsKey(mapKey))
                response = resourceMap[mapKey]
            else {
                val resource: Resource = resourceLoader.getResource("classpath:static/$mapKey.json")
                response = InputStreamReader(resource.inputStream).readText()
                resourceMap[mapKey] = response
            }
        }

        return response!!
            .also {
                println("${LocalDateTime.now()} [${Thread.currentThread().name}]: endpoint mock executado em ${System.currentTimeMillis() - start}ms")
            }
    }

    private fun randomString(length: Int) = (1..length)
        .map { ('a'..'z').random() }
        .joinToString("")

    fun generateFakeUser(): User {
        return User(
            id = Random.nextInt(1, 1000),
            name = randomString(8),
            age = Random.nextInt(18, 60),
            email = "${randomString(5)}@example.com",
            address = Address(
                street = randomString(10),
                city = randomString(6),
                state = randomString(2),
                zip = Random.nextInt(10000, 99999).toString()
            ),
            phoneNumbers = listOf("+55${Random.nextInt(100000000, 999999999)}"),
            isActive = Random.nextBoolean(),
            registrationDate = "2023-01-01",
            lastLogin = "2024-01-01",
            preferences = Preferences(
                language = "en",
                timezone = "GMT-3",
                currency = "USD"
            ),
            company = Company(
                name = randomString(10),
                role = "Developer",
                department = "Engineering"
            ),
            socialMedia = SocialMedia(
                facebook = "fb.com/${randomString(6)}",
                twitter = "twitter.com/${randomString(6)}",
                linkedin = "linkedin.com/in/${randomString(6)}"
            ),
            projects = listOf(
                Project(
                    projectName = randomString(10),
                    deadline = "2024-12-31",
                    status = "In Progress"
                )
            ),
            skills = listOf("Kotlin", "Java", "AWS"),
            hobbies = listOf("Reading", "Gaming"),
            subscription = Subscription(
                type = "Premium",
                expiresOn = "2025-12-31"
            ),
            notes = "Generated user data."
        )
    }

}