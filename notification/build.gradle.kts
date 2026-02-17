plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    id("java-library")

}

group = "com.eeseka"
version = "unspecified"

dependencies {
    implementation(project(":common"))

    // --- Firebase (Push Notifications) ---
    implementation(libs.firebase.admin.sdk)

    // --- Spring Boot Infrastructure ---
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)

    runtimeOnly(libs.postgresql)
}