package ru.jekajops.casino

import ru.jekajops.casino.dto.GameDto

data class TestContext(
    val games: List<GameDto> = emptyList(),
    val users: List<User> = emptyList(),
    val participants: List<Participant> = emptyList(),
)
