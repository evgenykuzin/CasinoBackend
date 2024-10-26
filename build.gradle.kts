plugins {
    java
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    //`maven-publish`
}

group = "org.jekajops.casinotemka"
version = "1.0-SNAPSHOT"
description = "CasinoTemka"


repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}


//apply {
//    java
//    plugin("kotlin")
//}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    //implementation("org.springframework.boot:spring-boot-starter")
    //implementation("org.springframework.boot:spring-boot-autoconfigure")
    //implementation("org.springframework.boot:spring-boot-starter-data-rest")
    //implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-h2")
    //implementation("com.h2database:h2:1.4.200")
    //implementation("org.springframework.boot:spring-boot-actuator-autoconfigure-r2dbc")
    //implementation("io.r2dbc:r2dbc-postgresql:0.8.0.M8")
    //implementation("io.r2dbc:r2dbc-pool")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.4")

    implementation("dev.inmo:tgbotapi:15.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    //implementation("org.springframework.boot:spring-boot-starter-web")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}


tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
