//package ru.jekajops.casino
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import jakarta.annotation.PostConstruct
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.stereotype.Service
//import java.math.BigDecimal
//import java.math.RoundingMode
//import java.time.Instant
//import java.util.*
//import kotlin.random.Random.Default.nextDouble
//
////@Service
//class ModelInitializationService(
//    private val userRepository: UserRepository,
//    private val gameRepository: GameRepository,
//    private val participantRepository: ParticipantRepository,
//    private val objectMapper: ObjectMapper // библиотека Jackson для работы с JSON
//) {
//
//    @org.springframework.beans.factory.annotation.Value("\${test.context.json}")
//    private lateinit var jsonFileName: String
//
//    @PostConstruct
//    fun initializeModelsFromJson() {
//        val testContext = javaClass.classLoader.getResourceAsStream(jsonFileName)?.use { inputStream ->
//            objectMapper.readValue(inputStream, TestContext::class.java)
//        } ?: throw IllegalStateException("Could not read test context from $jsonFileName")
//        // Сохраняем пользователей
////        val users = testContext.users.also {
////            println(it)
////        }.let { users ->
////            users.filter {
////                !userRepository.existsById(it.id!!)
////            }.map {
////                userRepository.save(it).also {
////                    println(it)
////                }
////            }
////        }
//
//        // Сохраняем игры
//        val games: List<Game> = testContext.games.let { games ->
//            games.filter {
//                it.id?.let { id -> !gameRepository.existsById(id) } ?: true
//            }.map {
//                gameRepository.save(it).also { saved ->
//                    println("game saved: $saved")
//                    println("participants saved: ${saved.participants}")
//                    saved.participants.map { p ->
//                        println("participat in game: $p")
//                        p.game = saved
//                        p
//                    }.let { pList ->
//                        participantRepository.saveAll(pList)
//                    }
//                }
//            }
//        }
//
//        // Сохраняем участников
//        val participants = testContext.participants.also {
//            println(it)
//        }.map { participant ->
//            val notExists = participant.id
//                ?.let { id ->
//                    !participantRepository.existsById(id)
//                } != false
//            if (notExists) {
//                participantRepository.save(participant)
//            }
//        }
//    }
//}
//
////@Service
//class GameService {
//    @Autowired
//    private lateinit var games: GameRepository
//
//    @Autowired
//    private lateinit var users: UserRepository
//
//    @Autowired
//    private lateinit var results: ResultRepository
//
//    @Autowired
//    private lateinit var participants: ParticipantRepository
//
//    @Autowired
//    lateinit var payoutService: PayoutService
//
//    fun createGame(
//        name: String,
//        adminId: String,
//        minPlayers: Int = 2,
//        maxPlayers: Int = 5,
//        minBet: Double = 1.0,
//        gameType: GameType = GameType.ONE_WINNER
//    ): String {
//        try {
//            val game = Game(null, name, adminId, minPlayers, maxPlayers, minBet, gameType)
//            val gameId = games.save(game).id ?: throw StatusException(-10, "Error while creating game")
//            addParticipant(gameId, adminId, minBet)
//            return gameId
//        } catch (e: Exception) {
//            throw e
//        }
//    }
//
//    fun addParticipant(gameId: String, userId: String, minBet: Double) {
//        val game = games.findByIdOrNull(gameId)
//        if (game?.participants?.any { it.userId == userId } == true) {
//            throw StatusException(-30, "you already joined the game")
//        }
//        val p = Participant(game = game, userId = userId, betAmount = minBet)
//        participants.save(p)
//        game?.participants?.add(p)
//        game?.let { games.save(it) }
//    }
//
//    fun joinGame(gameId: String, userId: String, betAmount: Double) {
//        val game = games.findByIdOrNull(gameId) ?: throw RuntimeException("Invalid game ID")
//
//        if (game.startedAt != null) {
//            throw StatusException(-2, "Game has already started")
//        }
//
//        val user = users.findByIdOrNull(userId) ?: throw RuntimeException("Invalid user ID")
//        if (user.balance < betAmount) {
//            throw StatusException(-3, "Insufficient balance")
//        }
//        users.decreaseUserBalance(userId, betAmount)
//
//        addParticipant(gameId, userId, betAmount)
//
//        if (game.participants.size >= game.maxPlayers) {
//            startGame(gameId)
//        }
//    }
//
//    fun startGame(gameId: String): Game? {
//        return kotlin.runCatching {
//            val game = games.findByIdOrNull(gameId) ?: throw StatusException(-4, "Invalid game ID")
//            games.updateStatusById(GameStatus.IN_PROGRESS, gameId)
//            val participants: List<Participant> = game.participants.shuffled()
//
//            game.startedAt = Instant.now()
//
//            if (game.participants.size < game.minPlayers) {
//                throw StatusException(-5, "Game needs ${game.minPlayers} min players to start")
//            }
//
//            fun process(winFunction: (p: List<Participant>, tb: Double) -> List<ResultAmount>) {
//                val totalBet = participants.sumByDouble { it.betAmount ?: 0.0 }
//                val resultAmounts: List<ResultAmount> = winFunction(participants, totalBet)
//                val finishedAt = game.startedAt!!.plusSeconds(30)
//                payoutService.payout(resultAmounts)
//                results.save(
//                    Result(
//                        null,
//                        gameId,
//                        resultAmounts,
//                        game.gameType,
//                        game.startedAt!!,
//                        finishedAt
//                    )
//                )
//            }
//            when (game.gameType) {
//                GameType.TOP3_WINNERS -> {
//                    process { _, totalBet ->
//                        fun Participant.toResultAmount(percent: Double): ResultAmount {
//                            return ResultAmount(
//                                participant = this,
//                                amount = totalBet.toBigDecimal().multiply(percent.toBigDecimal()).toDouble()
//                            )
//                        }
//
//                        val winners = participants.shuffled().take(3)
//                        val resultAmounts: MutableList<ResultAmount> = mutableListOf()
//                        resultAmounts.add(winners[0].toResultAmount(0.6))
//                        resultAmounts.add(winners[1].toResultAmount(0.3))
//                        resultAmounts.add(winners[2].toResultAmount(0.1))
//                        participants.filterNot { winners.contains(it) }
//                            .forEach {
//                                resultAmounts.add(it.toResultAmount(0.0))
//                            }
//                        resultAmounts
//                    }
//                }
//                GameType.ONE_WINNER -> {
//                    if (participants.isEmpty()) {
//                        throw StatusException(-7, "No participants in game ${game.id} (${game.name})")
//                    }
//                    process { _, totalBet ->
//                        val winners = participants.shuffled().take(1)
//                        winners.map {
//                            ResultAmount(participant = it, amount = totalBet)
//                        }
//                    }
//                }
//                GameType.RANDOM_WINNERS -> {
//                    process { prtsp, totalBet ->
//                        val n = prtsp.size
//                        var lastAmount: BigDecimal = totalBet.toBigDecimal()
//                        var numberLast: BigDecimal = n.toBigDecimal()
//                        var resultAounts: List<ResultAmount> = mutableListOf()
//                        val zero = BigDecimal.ZERO
//                        fun countAmount(): BigDecimal {
//                            if (lastAmount <= zero) {
//                                return zero
//                            }
//                            if (numberLast <= zero) {
//                                numberLast = n.toBigDecimal()
//                            }
//                            var startBound = 0.001
//                            val bound: Double
//                            if (lastAmount < (totalBet / n).toBigDecimal()) {
//                                startBound = 0.5
//                                bound = 1.0
//                            } else {
//                                bound = numberLast
//                                    .toDouble()
//                                    .also { numberLast = numberLast.dec() }
//                                    .let { nl -> nl / 100.0 }
//                            }
//
//                            val percentage = nextDouble(startBound, bound)
//                                .toBigDecimal()
//                                .setScale(3, RoundingMode.DOWN)
//                                .toDouble() // Получаем случайный процент
//                            return lastAmount.multiply(percentage.toBigDecimal()) // Вычисляем выигрыш участника
//                                .also {
//                                    lastAmount = if (it > lastAmount) zero else lastAmount.minus(it)
//                                }
//                        }
//
//                        val last = 5.0.toBigDecimal()
//                        while (lastAmount > last) {
//                            resultAounts = if (resultAounts.isEmpty()) {
//                                participants.map {
//                                    ResultAmount(participant = it, amount = countAmount().toDouble())
//                                }
//                            } else {
//                                resultAounts.map { ra ->
//                                    ra.also {
//                                        it.amount = countAmount()
//                                            .plus(it.amount?.toBigDecimal() ?: zero)
//                                            .setScale(2, RoundingMode.DOWN)
//                                            .toDouble()
//                                    }
//                                }
//                            }
//                        }
//                        resultAounts
//                    }
//                }
//            }
//            game.also {
//                game.status = GameStatus.COMPLETE
//                games.updateStatusById(game.status, gameId)
//                it.participants.clear()
//            }
//        }.onSuccess {
//            Thread.sleep(30000)
//            return it
//        }.onFailure {
//            throw it
//        }.getOrNull()
//    }
//
//    fun leaveGame(gameId: String, userId: String) {
//        val game = games.findByIdOrNull(gameId) ?: throw StatusException(-4, "Invalid game ID")
//        val participant = game.participants.find { it.userId == userId } ?: throw StatusException(
//            -6,
//            "You are not a participant of this game"
//        )
//
//        when (game.status) {
//            GameStatus.IN_PROGRESS, GameStatus.COMPLETE -> return
//            GameStatus.CREATED -> {
//                game.participants.remove(participant)
//                games.save(game)
//                participants.delete(participant)
//                users.increaseUserBalance(userId, participant.betAmount?:0.0)
//            }
//        }
//
//    }
//
//    fun getGame(gameId: String): Game {
//        return games.findByIdOrNull(gameId) ?: throw StatusException(-4, "Invalid game ID")
//    }
//
//    fun getAvailableGames(): List<Game> {
//        return games.findByMaxPlayersGreaterThan()
//    }
//
//    fun getMyGames(userId: String): List<Game> {
//        return games.findByAdminId(userId)
//    }
//
//    fun getActiveGames(userId: String): List<Game> {
//        return games.findByParticipants_UserIdAndStatusIn(userId)
//    }
//
//    fun deleteGame(gameId: String) {
//        games.deleteById(gameId)
//    }
//
//}
//
////@Service
//class PayoutService {
//    @Autowired
//    private lateinit var games: GameRepository
//
//    @Autowired
//    private lateinit var users: UserRepository
//
//    @Autowired
//    private lateinit var results: ResultRepository
//
//    fun payout(resultAmounts: List<ResultAmount>) {
//        resultAmounts.forEach { ra ->
//            users.increaseUserBalance(ra.participant?.userId!!,ra.amount ?: 0.0)
//        }
//    }
//}
//
////@Service
//class ResultsService {
//    @Autowired
//    private lateinit var games: GameRepository
//
//    @Autowired
//    private lateinit var users: UserRepository
//
//    @Autowired
//    private lateinit var results: ResultRepository
//    fun getResults(gameId: String): Result? {
//        return results.findTopByGameId(gameId)
//    }
//
//}
//
////@Service
//class UsersService {
//    @Autowired
//    private lateinit var games: GameRepository
//
//    @Autowired
//    private lateinit var users: UserRepository
//
//    @Autowired
//    private lateinit var results: ResultRepository
//
//    fun registerUser(
//        telegramId: String,
//        username: String?,
//        firstName: String?,
//        lastName: String?,
//        phone: String?
//    ): User {
//        val user = User(
//            id = UUID.randomUUID().toString(),
//            telegramId = telegramId,
//            username = username,
//            firstName = firstName,
//            lastName = lastName,
//            phone = phone,
//            balance = 0.0,
//            credit = 0,
//            deleted = false,
//            timestamp = Instant.now()
//        )
//        return users.save(user)
//    }
//
//    fun getUserById(id: String): User? {
//        return users.findById(id).orElse(null)
//    }
//
//    fun getUserByUsername(username: String): User? {
//        return users.findByUsername(username)
//    }
//
//    fun getUserByTelegramId(telegramId: String): User? {
//        return users.findByTelegramId(telegramId)
//    }
//
//    fun getAllUsers(): List<User> {
//        return users.findAll().toList()
//    }
//
//    fun updateBalance(telegramId: String, amount: Int): User? {
//        val user = users.findByTelegramId(telegramId)
//        if (user == null) return user
//        val updatedBalance = user.balance + amount
//        if (updatedBalance < 0) {
//            throw NotEnoughBalanceException(user.id!!)
//        }
//        user.balance = updatedBalance
//        return users.save(user)
//    }
//
//    fun getUsersOfGame(gameId: String): List<User> {
//       return users.findUsersByGameId(gameId)
//    }
//
//}