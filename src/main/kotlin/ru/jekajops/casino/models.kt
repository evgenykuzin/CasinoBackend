package ru.jekajops.casino

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.databind.util.Converter
import org.hibernate.Hibernate
import java.time.Instant
import javax.persistence.*
import kotlin.jvm.Transient

@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: String? = null,
    var telegramId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var username: String? = null,
    var phone: String? = null,
    var balance: Double = 0.0,
    var credit: Int = 0,
    var deleted: Boolean = false,
    var timestamp: Instant? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "User(id=$id, telegramId=$telegramId, firstName=$firstName, lastName=$lastName, username=$username, phone=$phone, balance=$balance, credit=$credit, deleted=$deleted, timestamp=$timestamp)"
    }


}

enum class GameType {
    TOP3_WINNERS,
    ONE_WINNER,
    RANDOM_WINNERS;

    override fun toString(): String {
        return this.name
    }
}

@Entity
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: String? = null,
    var name: String = "Game",
    var adminId: String? = "13",
    var minPlayers: Int = 2,
    var maxPlayers: Int = 5,
    var minBet: Double = 1.0,
    var gameType: GameType = GameType.ONE_WINNER,
    @OneToMany(cascade = [CascadeType.ALL])
    var participants: MutableList<Participant> = mutableListOf(),
    var startedAt: Instant? = null,
    var status: GameStatus = GameStatus.CREATED
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Game

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "Game(id=$id, name='$name', adminId=$adminId, minPlayers=$minPlayers, maxPlayers=$maxPlayers, minBet=$minBet, gameType=$gameType, participants=$participants, startedAt=$startedAt, status=$status)"
    }


}

@Entity
data class Participant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: String? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "GAME_ID", referencedColumnName = "ID")
    var game: Game? = null,
    var userId: String? = null,
    var betAmount: Double? = null,
    var status: ParticipantStatus = ParticipantStatus.PENDING
) {

    @JsonProperty("game")
    fun getGameId(): String? {
        return game?.id
    }


//    @Transient
//    var gameId: String? = game?.id
//    @get:JsonProperty("gameId")
//    val gameId: String?
//        get() = game?.id

//    @JsonCreator
//    constructor(
//        @JsonProperty("id") id: String?,
//        @JsonProperty("gameId") gameId: String?,
//        @JsonProperty("userId") userId: String?,
//        @JsonProperty("betAmount") betAmount: Double?,
//        @JsonProperty("status") status: ParticipantStatus = ParticipantStatus.PENDING
//    ) : this(id, Game(id=gameId), userId, betAmount, status) {
//        this.gameId = gameId
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Participant

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "Participant(id=$id, gameId=${game?.id}, userId=$userId, betAmount=$betAmount, status=$status)"
    }


}

enum class ParticipantStatus { PENDING, IN_PROGRESS, COMPLETE }

enum class GameStatus { CREATED, IN_PROGRESS, COMPLETE }

@Entity
data class Result(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: String? = null,
    var gameId: String? = null,
//    @OneToMany
//    var winnerIds: List<Participant> = mutableListOf(),
    @OneToMany(cascade = [CascadeType.ALL])
    var resultAmounts: List<ResultAmount> = mutableListOf(),
//    @ElementCollection
//    var winnersAmount: List<Double?> = mutableListOf(),
//    @ElementCollection
//    var losersAmount: List<Double?> = mutableListOf(),
    var gameType: GameType? = null,
    var startedAt: Instant? = null,
    var finishedAt: Instant = Instant.now()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Result

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "Result(id=$id, gameId=$gameId, resultAmounts=$resultAmounts, gameType=$gameType, startedAt=$startedAt, finishedAt=$finishedAt)"
    }
}

@Entity
data class ResultAmount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: String? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "resultId", referencedColumnName = "id")
    var result: Result? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "participantId", referencedColumnName = "id")
    var participant: Participant? = null,
    var amount: Double? = 0.0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Result

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "ResultAmount(id=$id, result=$result, participant=$participant, amount=$amount)"
    }
}
