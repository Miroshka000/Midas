plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "miroshka.allayshop"
version = "1.0.0"
description = "Configurable form shop for AllayMC"

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
    api = "0.27.0"
    plugin {
        name = "AllayShop"
        entrance = ".AllayShopPlugin"
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
