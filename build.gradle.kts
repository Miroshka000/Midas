plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "miroshka.midas"
version = "0.1.1"
description = "Configurable market, buyer and auto-buyer plugin for AllayMC"

repositories {
    mavenCentral()
    maven("https://repo.okaeri.cloud/releases")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allay {
    api = "0.28.0"
    plugin {
        name = "Midas"
        entrance = "miroshka.midas.MidasPlugin"
        authors += "Miroshka"
        dependency(name = "EconomyAPI", version = ">=0.2.2")
        dependency(name = "Aconomy", version = ">=0.2.0")
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    compileOnly("org.allaymc:economy-api:0.2.2")

    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.6")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:5.0.6")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
