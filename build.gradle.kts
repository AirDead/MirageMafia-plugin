plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.mirage.mafiaplugin"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(fileTree("./libs"))
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    implementation("com.charleskorn.kaml:kaml:0.60.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

val targetDir = file("/home/airdead/Desktop/Server/plugins/")

tasks.register("obfJarAndMove") {
    dependsOn("reobfJar")
    doLast {
        val jarFile = tasks.named<io.papermc.paperweight.tasks.RemapJar>("reobfJar").get().outputs.files.singleFile
        copy {
            from(jarFile)
            into(targetDir)
        }
        println("Moved reobfJar to $targetDir")
    }
}

