package ru.jekajops.casino

import com.fasterxml.jackson.annotation.JsonProperty

data class Response(
    @JsonProperty val status: Int,
    @JsonProperty val data: Any,
) {

}