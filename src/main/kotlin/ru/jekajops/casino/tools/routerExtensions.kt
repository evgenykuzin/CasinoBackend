package ru.jekajops.casino.tools

import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

fun ServerRequest.pathValue(): Mono<String> {
    pathVariable("")
    return Mono.just(requestPath().contextPath().elements().last().value())
}

fun <T : Any> T.toMono() = Mono.just(this)