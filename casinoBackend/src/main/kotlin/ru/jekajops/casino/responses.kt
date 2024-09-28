package ru.jekajops.casino

import org.springframework.http.ResponseEntity

fun ok(data: Any? = 1): Response {
    return Response(0, data?:0)
}

fun unknownError(): Response {
    return Response(-500, "Unknown error")
}

fun error(status: Int, desc: String): Response {
    return Response(status, desc)
}

fun response(block:() -> ResponseEntity<Response>): ResponseEntity<Response> {
    return try {
        block()
    } catch (t: Throwable) {
        return if (t is StatusException) {
            ResponseEntity.ok(error(t.status, t.desc))
        } else {
            t.printStackTrace()
            ResponseEntity.ok(unknownError())
        }
    }
}

//fun errorMap(status: Int) = when(status) {
//        -1 ->
//    }


enum class Error(val response: Response) {
    NOT_FOUND(error(-1, "NOT_FOUND"));
}