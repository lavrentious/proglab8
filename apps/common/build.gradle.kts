plugins {
    java
}

group = "ru.lavrent.lab8"

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.guava:guava:32.1.1-jre")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}