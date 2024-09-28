plugins {
    //java
    id("org.jetbrains.kotlin.multiplatform") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply  false
    id("org.jetbrains.compose") version "1.6.11" apply false
    id("dev.icerock.mobile.multiplatform-resources") version "0.24.1" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    kotlin("jvm") version "2.0.0" apply false
    kotlin("plugin.spring") version "2.0.0" apply false
    id("org.springframework.boot") version "3.3.4" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
    //`maven-publish`
}

//apply {
    //java
   // plugin("kotlin")
//}

//repositories {
//    mavenLocal()
//    maven {
//        url = uri("https://repo.maven.apache.org/maven2/")
//    }
//}

subprojects {

}

group = "org.jekajops.casinotemka"
version = "1.0-SNAPSHOT"
description = "CasinoTemka"
//java.sourceCompatibility = JavaVersion.VERSION_17

//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}

//tasks.withType<JavaCompile>() {
//    options.encoding = "UTF-8"
//}
