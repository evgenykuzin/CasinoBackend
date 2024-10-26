package ru.jekajops.casino

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity

data class Response(
    @JsonProperty val status: Int,
    @JsonProperty val data: Any,
) {

    fun responseEntityOk() = ResponseEntity.ok().body(this)
}