plugins {
    kotlin("jvm") version "2.0.0"
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
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

val targetDir = file("C:\\Users\\airdead\\Desktop\\Server\\plugins")

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
