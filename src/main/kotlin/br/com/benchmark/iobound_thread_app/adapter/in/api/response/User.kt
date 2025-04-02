package br.com.benchmark.iobound_thread_app.adapter.`in`.api.response

data class User(
    val id: Int,
    val name: String,
    val age: Int,
    val email: String,
    val address: Address,
    val phoneNumbers: List<String>,
    val isActive: Boolean,
    val registrationDate: String,
    val lastLogin: String,
    val preferences: Preferences,
    val company: Company,
    val socialMedia: SocialMedia,
    val projects: List<Project>,
    val skills: List<String>,
    val hobbies: List<String>,
    val subscription: Subscription,
    val notes: String
)

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zip: String
)

data class Preferences(
    val language: String,
    val timezone: String,
    val currency: String
)

data class Company(
    val name: String,
    val role: String,
    val department: String
)

data class SocialMedia(
    val facebook: String,
    val twitter: String,
    val linkedin: String
)

data class Project(
    val projectName: String,
    val deadline: String,
    val status: String
)

data class Subscription(
    val type: String,
    val expiresOn: String
)
