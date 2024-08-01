plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "1.4.20"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "com.mirage.mafiaplugin"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly(fileTree("./libs"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
