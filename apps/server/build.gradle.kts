val MAIN_CLASS = "ru.lavrent.lab8.server.Main"

plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.hibernate.orm:hibernate-core:6.4.4.Final")
    implementation("jakarta.validation:jakarta.validation-api:3.1.0-M2")
    implementation(project(":apps:common"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set(MAIN_CLASS)
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.shadowJar {
    archiveBaseName.set("lab8Server")
    archiveClassifier.set("")
    minimize {
        exclude(dependency("org.hibernate.orm:.*:.*"))
        exclude(dependency("org.postgresql:.*:.*"))
        exclude(dependency("jakarta.validation:.*:.*"))
    }
}

tasks.jar {
    enabled = false
    manifest.attributes["Main-Class"] = MAIN_CLASS
    dependsOn("shadowJar")
}