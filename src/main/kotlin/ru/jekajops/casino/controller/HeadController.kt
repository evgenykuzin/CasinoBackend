package ru.jekajops.casino.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.jekajops.casino.Response
import ru.jekajops.casino.ok
import ru.jekajops.casino.response

@RestController
@RequestMapping
class HeadController {
    @GetMapping
    suspend fun checkHealth(): ResponseEntity<Response> = response {
        ok("OK").responseEntityOk()
    }
}