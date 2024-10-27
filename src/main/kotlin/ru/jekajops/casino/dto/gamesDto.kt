package ru.jekajops.casino.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.jekajops.casino.Game
import ru.jekajops.casino.GameStatus
import ru.jekajops.casino.GameType
import ru.jekajops.casino.Participant
import java.math.BigInteger
import java.time.Instant


data class GameDto(
    var id: Long,
    var adminId: Long,
    var name: String = "Game",
    var minPlayers: Int = 2,
    var maxPlayers: Int = 5,
    var minBet: BigInteger = BigInteger.TEN,
    var gameType: GameType = GameType.ONE_WINNER,
    var startedAt: Instant = Instant.now(),
    var status: GameStatus = GameStatus.CREATED,
    var participants: MutableList<Participant> = mutableListOf(),
    var inGameNow: Int = participants.size,
) {

    companion object {
        @JsonIgnore
        fun Game.toDto(participants: MutableList<Participant>) = GameDto(
            id!!,
            adminId!!,
            name,
            minPlayers,
            maxPlayers,
            minBet.toBigInteger(),
            gameType,
            startedAt ?: Instant.now(),
            status,
            participants,
            participants.size,
        )

        @JsonIgnore
        fun <I : Iterable<Game>> I.mapToDto(participants: MutableList<Participant>) = map {
            it.toDto(participants)
        }
    }

    @JsonIgnore
    fun toEntity(): Game = Game(
        id,
        name,
        adminId,
        minPlayers,
        maxPlayers,
        minBet.toInt(),
        gameType,
        startedAt,
        status
    )

    @JsonIgnore
    fun <I : Iterable<GameDto>> I.toEntityList() = map { it.toEntity() }
}

data class CreateGame(
    val name: String? = "Game",
    val adminId: String,
    val minPlayers: Int = 2,
    val maxPlayers: Int = 5,
    val minBet: BigInteger = BigInteger.TEN,
    val gameType: GameType = GameType.ONE_WINNER,
)

data class JoinGame(
    val gameId: String, val userId: String, val betAmount: BigInteger,
)
