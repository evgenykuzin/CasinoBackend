package ru.jekajops.casino

data class TestContext (
    val games: List<Game> = emptyList(),
    val users: List<User> = emptyList(),
    val participants: List<Participant> = emptyList(),
)