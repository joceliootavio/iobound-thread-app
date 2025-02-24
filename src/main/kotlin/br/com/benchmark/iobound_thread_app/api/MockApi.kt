package br.com.benchmark.iobound_thread_app.api

import br.com.benchmark.iobound_thread_app.api.response.*
import com.github.javafaker.Faker
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/mock")
class MockApi(
    val resourceLoader: ResourceLoader
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val resourceMap: MutableMap<String, String> = mutableMapOf()

    @GetMapping("/user", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun mockGetUser(
        @RequestParam("delay") delay: Long
    ): String = mockAnyJsonFile(delay, "user")

    @GetMapping("/json", produces = ["application/json"])
    @ResponseStatus(HttpStatus.OK)
    fun mockAnyJsonFile(
        @RequestParam("delay") delay: Long,
        @RequestParam("jsonFileName") jsonFileName: String?
    ): String {
        val start = System.currentTimeMillis()
        sleep(delay)

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

    fun generateFakeUser(): User {
        val faker = Faker()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return User(
            id = faker.number().randomDigit(),
            name = faker.name().fullName(),
            age = faker.number().numberBetween(18, 60),
            email = faker.internet().emailAddress(),
            address = Address(
                street = faker.address().streetAddress(),
                city = faker.address().city(),
                state = faker.address().stateAbbr(),
                zip = faker.address().zipCode()
            ),
            phoneNumbers = listOf(faker.phoneNumber().cellPhone(), faker.phoneNumber().cellPhone()),
            isActive = faker.bool().bool(),
            registrationDate = LocalDate.now().format(dateFormatter),
            lastLogin = LocalDate.now().minusDays(1).format(dateFormatter),
            preferences = Preferences(
                language = faker.options().option("en", "es", "fr"),
                timezone = "UTC",
                currency = faker.currency().code()
            ),
            company = Company(
                name = faker.company().name(),
                role = faker.job().title(),
                department = faker.company().industry()
            ),
            socialMedia = SocialMedia(
                facebook = "${faker.name().username()}.fb",
                twitter = "@${faker.name().username()}",
                linkedin = faker.name().username()
            ),
            projects = listOf(
                Project("Project Alpha", "2025-12-31", "In Progress"),
                Project("Project Beta", "2025-06-30", "Completed")
            ),
            skills = listOf("Kotlin", "Java", "JavaScript", "SQL"),
            hobbies = listOf("Reading", "Traveling", "Photography"),
            subscription = Subscription("Premium", "2025-08-01"),
            notes = "User prefers working remotely."
        )
    }

}