package ru.jekajops.casino

import dev.inmo.micro_utils.common.ifTrue
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ru.jekajops.casino.dto.GameDto
import ru.jekajops.casino.dto.GameDto.Companion.toDto
import ru.jekajops.casino.tools.sizeOrZero
import ru.jekajops.casino.tools.toMono
import java.math.BigDecimal
import java.math.BigInteger
import java.math.BigInteger.ZERO
import java.math.RoundingMode
import java.time.Instant
import kotlin.random.Random.Default.nextDouble

@Service
class GameService {
    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var resultRepository: ResultRepository

    @Autowired
    private lateinit var participantRepository: ParticipantRepository

    @Autowired
    lateinit var payoutService: PayoutService

    private inline fun <R> withParticipantsRepo(f: ParticipantRepository.() -> R): R = with(participantRepository, f)

    suspend fun Game.participants() = withParticipantsRepo { id?.let { findAllByGameId(it) } }

    suspend fun createGame(
        name: String,
        adminId: Long,
        minPlayers: Int = 2,
        maxPlayers: Int = 5,
        minBet: BigInteger = BigInteger.TEN,
        gameType: GameType = GameType.ONE_WINNER,
    ): Long {
        try {
            val game = Game(null, name, adminId, minPlayers, maxPlayers, minBet.toInt(), gameType)
            val gameId = gameRepository.save(game).id ?: throw StatusException(-10, "Error while creating game")
            addParticipant(gameId, adminId, minBet)
            return gameId
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addParticipant(gameId: Long, userId: Long, minBet: BigInteger) {
        val game = gameRepository.findById(gameId)
        if (game?.let { participantRepository.existsByGameIdAndUserId(it, userId) } == true) {
            throw StatusException(-30, "you already joined the game")
        }
        val p = Participant(gameId = game?.id, userId = userId, betAmount = minBet.toInt())
        participantRepository.save(p)
        //game?.participants?.add(p)
        //game?.let { games.save(it) }
    }

    suspend fun joinGame(gameId: Long, userId: Long, betAmount: BigInteger) {
        val game = gameRepository.findById(gameId) ?: throw RuntimeException("Invalid game ID")

        if (game.startedAt != null) {
            throw StatusException(-2, "Game has already started")
        }

        val user = userRepository.findById(userId) ?: throw RuntimeException("Invalid user ID")
        if (user.balance < betAmount.toInt()) {
            throw StatusException(-3, "Insufficient balance")
        }
        userRepository.decreaseUserBalance(userId, betAmount)

        addParticipant(gameId, userId, betAmount)

        game.participants()
            ?.count()
            ?.let { participantsCount ->
                (participantsCount >= game.minPlayers).ifTrue {
                    mono {
                        startGame(gameId)
                    }
                }
            }
    }

    suspend fun startGame(gameId: Long): Game? {
        return kotlin.runCatching {
            val game = gameRepository.findById(gameId) ?: throw StatusException(-4, "Invalid game ID")
            gameRepository.updateStatusById(GameStatus.IN_PROGRESS, gameId)
            (game.participants()?.toList() ?: mutableListOf()).let {
                val participants: List<Participant> = it.shuffled().toMutableList()

                game.startedAt = Instant.now()

                if (participants.sizeOrZero < game.minPlayers) {
                    throw StatusException(-5, "Game needs ${game.minPlayers} min players to start")
                }

                fun process(winFunction: (p: List<Participant>, tb: BigInteger) -> List<ResultAmount>) {
                    val totalBet = participants.sumOf { it.betAmount ?: BigInteger.ZERO.toInt() }
                    val resultAmounts: List<ResultAmount> = winFunction(participants, totalBet.toBigInteger())
                    val finishedAt = game.startedAt!!.plusSeconds(30)
                    mono {
                        payoutService.payout(resultAmounts)
                        resultRepository.save(
                            Result(
                                null,
                                gameId,
                                resultAmounts,
                                game.gameType,
                                game.startedAt!!,
                                finishedAt
                            )
                        )
                    }.subscribe()
                }

                when (game.gameType) {
                    GameType.TOP3_WINNERS -> {
                        process { _, totalBet ->
                            fun Participant.toResultAmount(percent: Double): ResultAmount {
                                return ResultAmount(
                                    participant = this,
                                    amount = totalBet.toBigDecimal().multiply(percent.toBigDecimal()).toInt()
                                )
                            }

                            val winners = participants.shuffled().take(3)
                            val resultAmounts: MutableList<ResultAmount> = mutableListOf()
                            resultAmounts.add(winners[0].toResultAmount(0.6))
                            resultAmounts.add(winners[1].toResultAmount(0.3))
                            resultAmounts.add(winners[2].toResultAmount(0.1))
                            participants.filterNot { winners.contains(it) }
                                .forEach {
                                    resultAmounts.add(it.toResultAmount(0.0))
                                }
                            resultAmounts
                        }
                    }

                    GameType.ONE_WINNER -> {
                        if (participants.isEmpty()) {
                            throw StatusException(-7, "No participants in game ${game.id} (${game.name})")
                        }
                        process { _, totalBet ->
                            val winners = participants.shuffled().take(1)
                            winners.map {
                                ResultAmount(participant = it, amount = totalBet.toInt())
                            }
                        }
                    }

                    GameType.RANDOM_WINNERS -> {
                        process { prtsp, totalBet ->
                            val n = prtsp.size
                            var lastAmount: BigDecimal = totalBet.toBigDecimal()
                            var numberLast: BigDecimal = n.toBigDecimal()
                            var resultAounts: List<ResultAmount> = mutableListOf()
                            val zero = BigDecimal.ZERO
                            fun countAmount(): BigDecimal {
                                if (lastAmount <= zero) {
                                    return zero
                                }
                                if (numberLast <= zero) {
                                    numberLast = n.toBigDecimal()
                                }
                                var startBound = 0.001
                                val bound: Double
                                if (lastAmount < (totalBet / n.toBigInteger()).toBigDecimal()) {
                                    startBound = 0.5
                                    bound = 1.0
                                } else {
                                    bound = numberLast
                                        .toDouble()
                                        .also { numberLast = numberLast.dec() }
                                        .let { nl -> nl / 100.0 }
                                }

                                val percentage = nextDouble(startBound, bound)
                                    .toBigDecimal()
                                    .setScale(3, RoundingMode.DOWN)
                                    .toDouble() // Получаем случайный процент
                                return lastAmount.multiply(percentage.toBigDecimal()) // Вычисляем выигрыш участника
                                    .also {
                                        lastAmount = if (it > lastAmount) zero else lastAmount.minus(it)
                                    }
                            }

                            val last = 5.0.toBigDecimal()
                            while (lastAmount > last) {
                                resultAounts = if (resultAounts.isEmpty()) {
                                    participants.map {
                                        ResultAmount(participant = it, amount = countAmount().toInt())
                                    }
                                } else {
                                    resultAounts.map { ra ->
                                        ra.also {
                                            it.amount = countAmount()
                                                .plus(it.amount?.toBigDecimal() ?: zero)
                                                .setScale(2, RoundingMode.DOWN)
                                                .toInt()
                                        }
                                    }
                                }
                            }
                            resultAounts
                        }
                    }
                }
                game.status = GameStatus.COMPLETE
                mono {
                    gameRepository.updateStatusById(game.status, gameId)
                }.flatMap {
                    mono {
                        participantRepository.deleteAll(participants)
                    }
                }.subscribe()
                game
            }
        }.onSuccess {
            Thread.sleep(30000)
            return it
        }.onFailure {
            throw it
        }.getOrNull()
    }

    suspend fun leaveGame(gameId: Long, userId: Long) {
        val game = gameRepository.findById(gameId) ?: throw StatusException(-4, "Invalid game ID")
        game.participants()
            ?.filter {
                it.userId == userId
            }?.map {
//            val participant = game.participants?.find { it.userId == userId } ?: throw StatusException(
//                -6,
//                "You are not a participant of this game"
//            )
                when (game.status) {
                    GameStatus.IN_PROGRESS, GameStatus.COMPLETE -> return@map
                    GameStatus.CREATED -> {
                        mono {
                            participantRepository.delete(it)
                            //gameRepository.save(game)
                            //participantRepository.delete(it)
                            userRepository.increaseUserBalance(userId, it.betAmount?.toBigInteger() ?: ZERO)
                        }
                    }
                }
            }


    }

    suspend fun participants(gameId: Long) = participantRepository
        .findAllByGameId(gameId)

    suspend fun Game.toDto(): GameDto = participants(id!!)
        .toList()
        .let {
            this.toDto(it.toMutableList())
        }

    suspend fun <I : Flow<Game>> I.mapToDto(): Flow<GameDto> = map {
        it.toDto()
    }

    suspend fun getGame(gameId: Long): GameDto {
        return gameRepository
            .findById(gameId)
            ?.toDto() ?: throw StatusException(-4, "Invalid game ID")
    }

    suspend fun getAllGames(): Flow<GameDto> {
        return gameRepository.findAll().mapToDto()
    }

    suspend fun getAvailableGames(): Flow<GameDto> {
        return gameRepository.findAllAvailable().mapToDto()
    }

    suspend fun getMyGames(userId: Long): Flow<GameDto> {
        return gameRepository.findByAdminId(userId).mapToDto()
    }

    suspend fun getActiveGames(userId: Long): Flow<GameDto> {
        return participantRepository.findByUserId(userId).flatMap {
            it.gameId?.let { id ->
                mono {
                    gameRepository.findById(id)
                }
            }
        }.filter {
            it.status in setOf(GameStatus.CREATED, GameStatus.IN_PROGRESS)
        }.asFlow()
            .mapToDto()
    }

    suspend fun deleteGame(gameId: Long) {
        gameRepository.deleteById(gameId)
    }

}

@Service
class PayoutService {
    @Autowired
    private lateinit var games: GameRepository

    @Autowired
    private lateinit var users: UserRepository

    @Autowired
    private lateinit var results: ResultRepository

    suspend fun payout(resultAmounts: List<ResultAmount>) {
        resultAmounts.forEach { ra ->
            users.increaseUserBalance(ra.participant?.userId!!, ra.amount?.toBigInteger() ?: ZERO)
        }
    }
}

@Service
class ResultsService {
    @Autowired
    private lateinit var games: GameRepository

    @Autowired
    private lateinit var users: UserRepository

    @Autowired
    private lateinit var results: ResultRepository
    suspend fun getResults(gameId: Long): Result? {
        return results.findTopByGameId(gameId)
    }

}

@Service
class UsersService {
    @Autowired
    private lateinit var games: GameRepository

    @Autowired
    private lateinit var users: UserRepository

    @Autowired
    private lateinit var results: ResultRepository

    suspend fun registerUser(
        telegramId: String,
        username: String?,
        firstName: String?,
        lastName: String?,
        phone: String?,
    ): User {
        val user = User(
            //id = UUID.randomUUID().toString(),
            telegramId = telegramId,
            username = username,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            balance = ZERO.toInt(),
            credit = 0,
            deleted = false,
            timestamp = Instant.now()
        )
        return users.save(user)
    }

    suspend fun getUserById(id: Long): User? {
        return users.findById(id)
    }

    suspend fun getUserByUsername(username: String): User? {
        return users.findByUsername(username)
    }

    suspend fun getUserByTelegramId(telegramId: String): User? {
        return users.findByTelegramId(telegramId)
    }

    suspend fun getAllUsers(): Flux<User> {
        return users.findAll().asFlux()
    }

    suspend fun updateBalance(telegramId: String, amount: BigDecimal): User? {
        val user = users.findByTelegramId(telegramId)
        if (user == null) return user
        val updatedBalance = user.balance.toBigDecimal() + amount
        if (updatedBalance < BigDecimal.ZERO) {
            throw NotEnoughBalanceException(userId = user.id!!)
        }
        user.balance = updatedBalance.toInt()
        return users.save(user)
    }

    suspend fun getUsersOfGame(gameId: Long): Flux<User> {
        return users.findUsersByGameId(gameId)
    }

}