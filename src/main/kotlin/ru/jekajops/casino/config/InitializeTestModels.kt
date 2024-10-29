package ru.jekajops.casino.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.map
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Service
import ru.jekajops.casino.*

@OptIn(DelicateCoroutinesApi::class)
@Configuration
@DependsOn("databaseInitializer")
class InitializeTestModels {
    @Autowired
    lateinit var modelInitializationService: ModelInitializationService

    @PostConstruct
    fun initModels() {
        GlobalScope.async {
            modelInitializationService.runCatching {
                initializeModelsFromJson()
            }.onFailure {
                println("Test-models were not initialized, cause: ")
                it.printStackTrace()
            }.onSuccess {
                println("Successful initialized test-models")
            }
        }
    }
}


@Service
class ModelInitializationService(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
    private val participantRepository: ParticipantRepository,
    private val objectMapper: ObjectMapper // библиотека Jackson для работы с JSON
) {

    @org.springframework.beans.factory.annotation.Value("\${test.context.json}")
    private lateinit var jsonFileName: String

    //@PostConstruct
    suspend fun initializeModelsFromJson() {
        val testContext = javaClass.classLoader.getResourceAsStream(jsonFileName)?.use { inputStream ->
            objectMapper.readValue(inputStream, TestContext::class.java)
        } ?: throw IllegalStateException("Could not read test context from $jsonFileName")
        println("test context loaded from $jsonFileName")
        //Сохраняем пользователей
        val users = testContext.users.also {
            println(it)
        }.let { users ->
            users.filter {
                !userRepository.existsByUsername(it.username!!)
            }.map {
                println("saving $it")
                userRepository.save(it.copy(id = null)).also {
                    println(it)
                }
            }
        }

        println("users initialized")

        // Сохраняем игры
        val games: List<Game> = testContext.games.let { games ->
            games.filter {
                it.id?.let { id -> !gameRepository.existsById(id) } ?: true
            }.map {
                println("saving $it")
                val participants = it.participants
                gameRepository.save(it.toEntity().copy(id = null)).also { saved ->
                    println("game saved: $saved")
                    println("saving participants: ${participants}")
                    participants.map { p ->
                        println("participat in game: $p")
                        p.gameId = saved.id
                        p
                    }.forEach {
                        participantRepository.save(it).also {
                            println("Saved participant ($it)")
                        }
                    }
                }
            }
        }

        println("games initialized")

        // Сохраняем участников
//        val participants = testContext.participants.also {
//            println(it)
//        }.map { participant ->
//            val notExists = participant.id
//                ?.let { id ->
//                    !participantRepository.existsById(id)
//                } != false
//            if (notExists) {
//                println("saving $participant")
//                participantRepository.save(participant.copy(id = null))
//            }
//        }

        //println("participants initialized")
    }
}
