package ru.jekajops.casino

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableWebFlux
//@EnableJpaRepositories
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}