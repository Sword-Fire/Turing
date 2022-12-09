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

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    // Yaml support for kotlinx-serialization
    implementation("com.charleskorn.kaml:kaml:0.49.0")

    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    implementation("com.github.ajalt.clikt:clikt:3.5.0")
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