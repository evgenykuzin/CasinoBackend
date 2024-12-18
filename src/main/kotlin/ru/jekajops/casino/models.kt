package ru.jekajops.casino

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.data.relational.core.mapping.Column
import java.time.Instant
import org.springframework.data.relational.core.mapping.Table
import kotlin.jvm.Transient

@Entity(name = "user_g")
@Table("user_g")
data class User(
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    @Column("telegram_id")
    var telegramId: String? = null,
    @Column("first_name")
    var firstName: String? = null,
    @Column("last_name")
    var lastName: String? = null,
    var username: String? = null,
    var phone: String? = null,
    var balance: Int = 0,
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
@Table
data class Game(
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String = "Game",
    @Column("admin_id")
    var adminId: Long? = null,
    @Column("min_players")
    var minPlayers: Int = 2,
    @Column("max_players")
    var maxPlayers: Int = 5,
    @Column("min_bet")
    var minBet: Int = 10,
    @Column("game_type")
    var gameType: GameType = GameType.ONE_WINNER,
    //@OneToMany(cascade = [CascadeType.PERSIST])
    //@org.springframework.data.annotation.Transient
//    var participants: MutableList<Participant>? = null,
    @Column("started_at")
    var startedAt: Instant? = null,
    var status: GameStatus = GameStatus.CREATED,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Game

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "Game(id=$id, name='$name', adminId=$adminId, minPlayers=$minPlayers, maxPlayers=$maxPlayers, minBet=$minBet, gameType=$gameType, startedAt=$startedAt, status=$status)"
    }


}

@Entity
@Table
data class Participant(
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "GAME_ID", referencedColumnName = "ID")
    @Column("game_id")
    var gameId: Long? = null,
    @Column("user_id")
    var userId: Long? = null,
    @Column("bet_amount")
    var betAmount: Int? = null,
    var status: ParticipantStatus = ParticipantStatus.PENDING
) {

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
        return "Participant(id=$id, gameId=${gameId}, userId=$userId, betAmount=$betAmount, status=$status)"
    }


}

enum class ParticipantStatus { PENDING, IN_PROGRESS, COMPLETE }

enum class GameStatus { CREATED, IN_PROGRESS, COMPLETE }

@Entity
@Table
data class Result(
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column("game_id")
    var gameId: Long? = null,
//    @OneToMany
//    var winnerIds: List<Participant> = mutableListOf(),
    //@OneToMany(cascade = [CascadeType.ALL])
    @Transient
    var resultAmounts: List<ResultAmount> = mutableListOf(),
//    @ElementCollection
//    var winnersAmount: List<Double?> = mutableListOf(),
//    @ElementCollection
//    var losersAmount: List<Double?> = mutableListOf(),
    @Column("game_type")
    var gameType: GameType? = null,
    @Column("started_at")
    var startedAt: Instant? = null,
    @Column("finished_at")
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
@Table
data class ResultAmount(
    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "resultId", referencedColumnName = "id")
    var result: Result? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "participantId", referencedColumnName = "id")
    var participant: Participant? = null,
    var amount: Int? = 0
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
