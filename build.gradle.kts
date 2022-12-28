import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    application
}

group = "net.geekmc.turing"
version = "1.0-SNAPSHOT"
val outputName = "${project.name}-$version.jar"

application {
    mainClass.set("net.geekmc.turing.TuringServerKt")
}

repositories {
    maven(url = "https://jitpack.io")
    mavenCentral()
    mavenLocal()
}

dependencies {
    // TODO: pin version
    implementation("com.github.Minestom:Minestom:-SNAPSHOT") {
        exclude(group = "org.tinylog")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.ext["version.kotlinx-coroutines-core"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${project.ext["version.kotlinx-serialization-core"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${project.ext["version.kotlinx-serialization-json"]}")
    // Yaml support for kotlinx-serialization
    implementation("com.charleskorn.kaml:kaml:${project.ext["version.kaml"]}")

    implementation("org.apache.logging.log4j:log4j-core:${project.ext["version.log4j-core"]}")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:${project.ext["version.log4j-slf4j-impl"]}")

    implementation("com.github.ajalt.clikt:clikt:${project.ext["version.clikt"]}")
}

publishing {
    publications {
        create<MavenPublication>("turing") {
            group = "net.geekmc.turing"
            artifactId = "turing"

            from(components.getByName("java"))
        }
    }

    repositories {
        mavenLocal()
    }
}

tasks.withType<ShadowJar> {
    transform(Log4j2PluginsCacheFileTransformer::class.java)
    archiveFileName.set(outputName)
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to "net.geekmc.turing.TuringServerKt",
            "Multi-Release" to true
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Jar> {
    archiveFileName.set(outputName)
}