package ru.jekajops.casino.tools

import org.springframework.web.reactive.function.server.ServerRequest
import reactor.core.publisher.Mono

fun ServerRequest.pathValueToMono(): Mono<String> {
    return Mono.just(requestPath().contextPath().elements().last().value())
}