val MAIN_CLASS = "ru.lavrent.lab8.client.Main"

plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
}


javafx {
    modules("javafx.base", "javafx.controls", "javafx.fxml")
    version = "21"
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("org.apache.commons:commons-lang3:3.14.0")
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
    archiveBaseName.set("lab8Client")
    archiveClassifier.set("")
    minimize {
        exclude(dependency("org.openjfx:.*:.*"))
    }
}

tasks.jar {
    enabled = false
    manifest.attributes["Main-Class"] = MAIN_CLASS
    dependsOn("shadowJar")
}