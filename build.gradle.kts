import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.paperPlugin)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.serialization)
}

group = "com.mirage.mafiagame"
version = "1.0.0"

repositories {
    maven {
        url = uri("https://repo.nikdekur.tech/releases")
    }
    mavenCentral()
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly(fileTree("./libs"))
//
    compileOnly(libs.minelib)
    compileOnly(libs.kotlinx.serialization)
//    compileOnly("xyz.xenondevs.invui:invui-kotlin:1.34")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

//tasks.withType<KotlinCompile> {
//    compilerOptions {
//        freeCompilerArgs.addAll("-Xno-param-assertions", "-Xno-call-assertions")
//    }
//}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveBaseName.set("mafia")

    minimize {
        exclude("kotlin/**")
    }
}
