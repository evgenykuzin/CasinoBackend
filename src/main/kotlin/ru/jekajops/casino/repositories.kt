package ru.jekajops.casino

import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import java.math.BigInteger

//@Repository
@Service
interface GameRepository
    : CoroutineCrudRepository<Game, Long> {
//    operator fun get(key: String): Game? = findByIdOrNull(key)
//    operator fun set(gameId: String, game: Game) {
//        //game.id = gameId
//        save(game)
//    }


    @Query("select * from Game g where max_players > (select count(game_id) from participant where game_id = g.id) and started_at is null and status = 'CREATED'")
    fun findByMaxPlayersGreaterThan(): Flux<Game>

    fun findByAdminId(adminId: Long): Flux<Game>

//    fun findByParticipants_UserIdAndStatusIn(
//        participants_userId: Long,
//        status: MutableCollection<GameStatus> = mutableListOf(
//            GameStatus.CREATED,
//            GameStatus.IN_PROGRESS
//        )
//    ): List<Game>

    @Transactional
    @Modifying
    @org.springframework.data.r2dbc.repository.Query("update Game g set g.status = ?1 where g.id = ?2")
    fun updateStatusById(status: GameStatus, id: Long)

    fun findAllByStatusIn(status: MutableCollection<GameStatus>)

}

@Repository
interface ResultRepository
    : CoroutineCrudRepository<Result, Long>
{
//    operator fun get(key: String): Result? = findByIdOrNull(key)
//    operator fun set(gameId: String, game: Result) {
//        //game.id = gameId
//        save(game)
//    }

    fun findTopByGameId(gameId: Long): Result?

}

@Repository
interface ParticipantRepository
    : CoroutineCrudRepository<Participant, Long>
{
    @org.springframework.data.jpa.repository.Query("select p from Participant p where p.userId = ?1")
    suspend fun findByUserId(userId: Long): Flux<Participant>
//    operator fun get(key: String): Participant? = findByIdOrNull(key)
//    operator fun set(gameId: String, game: Participant) {
//        //game.id = gameId
//        save(game)
//    }

    fun findTopByGame(game: Game): Participant?

    suspend fun findAllByGame_Id(gameId: Long): Flux<Participant>

    suspend fun existsByGameAndUserId(game: Game, userId: Long): Boolean

    //suspend fun findAll(pageable: Pageable): Flow<Participant>

    // suspend fun participants(game: Game): Flux<Participant>? = game.id?.let { findAllByGame_Id(it) }
}

@Repository
interface UserRepository
    : CoroutineCrudRepository<User, Long>
{
//    operator fun get(key: String): User? = findByIdOrNull(key)
//    operator fun set(gameId: String, game: User) {
//        //game.id = gameId
//        save(game)
//    }

    suspend fun existsByUsername(username: String): Boolean

    suspend fun findByBalanceLessThan(balance: BigInteger): List<User>

    suspend fun findByTelegramId(telegramId: String): User?

    suspend fun findByUsername(username: String): User?

    @Query(
        "SELECT DISTINCT * FROM User INNER JOIN PARTICIPANT ON User.id = PARTICIPANT.USER_ID" +
                " WHERE PARTICIPANT.GAME_ID = :gameId"
    )
    suspend fun findUsersByGameId(@Param("gameId") gameId: Long?): Flux<User>

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    suspend fun increaseUserBalance(@Param("userId") userId: Long, @Param("amount") amount: BigInteger)

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.balance = u.balance - :amount WHERE u.id = :userId")
    suspend fun decreaseUserBalance(@Param("userId") userId: Long, @Param("amount") amount: BigInteger)
}