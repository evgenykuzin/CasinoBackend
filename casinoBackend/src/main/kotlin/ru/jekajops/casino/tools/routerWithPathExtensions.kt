package ru.jekajops.casino.tools

import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

inline fun <reified P : Path> CoRouterFunctionDsl.nest(path: P, noinline r: (CoRouterFunctionDsl.() -> Unit)) {
    path.asPath.nest(r)
}

inline fun <reified P : Path> CoRouterFunctionDsl.nestAsFull(path: P, noinline r: (CoRouterFunctionDsl.() -> Unit)) {
    path.asFullPath.nest(r)
}

inline fun <reified P : Path> CoRouterFunctionDsl.POST(path: P, noinline f: suspend (ServerRequest) -> ServerResponse) {
    return POST(path.asPath, f)
}

inline fun <reified P : Path> CoRouterFunctionDsl.POST(path: P, predicate: RequestPredicate, noinline f: suspend (ServerRequest) -> ServerResponse) {
    return POST(path.asPath, predicate, f)
}

inline fun <reified P : Path> CoRouterFunctionDsl.GET(path: P, noinline f: suspend (ServerRequest) -> ServerResponse) {
    return GET(path.asPath, f)
}

inline fun <reified P : Path> CoRouterFunctionDsl.PUT(path: P, noinline f: suspend (ServerRequest) -> ServerResponse) {
    return PUT(path.asPath, f)
}

inline fun <reified P : Path> CoRouterFunctionDsl.PUT(path: P, predicate: RequestPredicate, noinline f: suspend (ServerRequest) -> ServerResponse) {
    return PUT(path.asPath, predicate, f)
}

inline fun <reified P : Path> CoRouterFunctionDsl.DELETE(path: P, noinline f: suspend (ServerRequest) -> ServerResponse) {
    return DELETE(path.asPath, f)
}