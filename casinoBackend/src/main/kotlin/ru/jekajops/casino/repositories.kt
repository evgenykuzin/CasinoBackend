package ru.jekajops.casino

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface GameRepository
    : CrudRepository<Game, String>
{
    operator fun get(key: String): Game? = findByIdOrNull(key)
    operator fun set(gameId: String, game: Game) {
        //game.id = gameId
        save(game)
    }

    @Query("select g from Game g where g.maxPlayers > g.participants.size and g.startedAt is null and g.status = 0")
    fun findByMaxPlayersGreaterThan(): List<Game>

    fun findByAdminId(adminId: String): List<Game>

    fun findByParticipants_UserIdAndStatusIn(
        participants_userId: String,
        status: MutableCollection<GameStatus> = mutableListOf(
            GameStatus.CREATED,
            GameStatus.IN_PROGRESS
        )
    ): List<Game>

    @Transactional
    @Modifying
    @Query("update Game g set g.status = ?1 where g.id = ?2")
    fun updateStatusById(status: GameStatus, id: String)

    fun findAllByStatusIn(status: MutableCollection<GameStatus>)

}

@Repository
interface ResultRepository
    : CrudRepository<Result, String>
{
    operator fun get(key: String): Result? = findByIdOrNull(key)
    operator fun set(gameId: String, game: Result) {
        //game.id = gameId
        save(game)
    }

    fun findTopByGameId(gameId: String): Result?

}

@Repository
interface ParticipantRepository
    : CrudRepository<Participant, String>
{
    operator fun get(key: String): Participant? = findByIdOrNull(key)
    operator fun set(gameId: String, game: Participant) {
        //game.id = gameId
        save(game)
    }

    fun findTopByGame(game: Game): Participant?

}

@Repository
interface UserRepository
    : CrudRepository<User, String>
{
    operator fun get(key: String): User? = findByIdOrNull(key)
    operator fun set(gameId: String, game: User) {
        //game.id = gameId
        save(game)
    }


    fun findByBalanceLessThan(balance: Double): List<User>

    fun findByTelegramId(telegramId: String): User?

    fun findByUsername(username: String): User?

    @Query(
        "SELECT DISTINCT * FROM User INNER JOIN PARTICIPANT ON User.id = PARTICIPANT.USER_ID" +
                " WHERE PARTICIPANT.GAME_ID = :gameId", nativeQuery = true
    )
    fun findUsersByGameId(@Param("gameId") gameId: String?): List<User>

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    fun increaseUserBalance(@Param("userId") userId: String, @Param("amount") amount: Double)

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :userId")
    fun decreaseUserBalance(@Param("userId") userId: String, @Param("amount") amount: Double)
}