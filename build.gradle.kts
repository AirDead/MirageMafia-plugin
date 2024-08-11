plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.paperPlugin)
}

group = "com.mirage.mafiaplugin"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.nikdekur.tech/releases")
    }
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly(fileTree("./libs"))

    compileOnly(libs.ndkore)
    compileOnly(libs.koin)
    compileOnly(libs.minelib)
//    compileOnly("xyz.xenondevs.invui:invui-kotlin:1.34")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
