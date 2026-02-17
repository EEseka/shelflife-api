import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.eeseka"
version = "0.0.1-SNAPSHOT"

tasks {
    named<BootJar>("bootJar") {
        from(project(":user").projectDir.resolve("src/main/resources")) {
            into("")
        }

        from(project(":notification").projectDir.resolve("src/main/resources")) {
            into("")
        }
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":user"))
    implementation(project(":pantry"))
    implementation(project(":insight"))
    implementation(project(":notification"))

    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.security)

    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.amqp)
    implementation(libs.spring.boot.starter.mail)

    implementation(libs.kotlin.reflect)

    runtimeOnly(libs.postgresql)
}