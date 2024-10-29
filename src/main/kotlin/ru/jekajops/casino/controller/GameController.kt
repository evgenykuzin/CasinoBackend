package ru.jekajops.casino.controller

import kotlinx.coroutines.flow.Flow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import print
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import ru.jekajops.casino.*
import ru.jekajops.casino.dto.CreateGame
import ru.jekajops.casino.dto.GameDto
import ru.jekajops.casino.dto.JoinGame

@RestController
@RequestMapping("/games")
class GameController {
    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var usersService: UsersService

    @Autowired
    private lateinit var resultsService: ResultsService

    @PostMapping("/createGame")
    suspend fun createGame(
        @RequestBody cg: CreateGame,
    ): ResponseEntity<Response> {
        return response {
            println("createGame: $cg")
            if (cg.name == null) {
                return@response ResponseEntity.ok(error(-5, "Не задано название игры")).print()
            }
            val gameId = gameService.createGame(
                cg.name,
                cg.adminId.toLong(),
                cg.minPlayers,
                cg.maxPlayers,
                cg.minBet,
                cg.gameType
            )
            ResponseEntity.ok(ok(data = gameId)).print()
        }
    }

    @PostMapping("/joinGame")
    suspend fun joinGame(
        @RequestBody jg: JoinGame,
    ): ResponseEntity<Response> {
        return response {
            println("joinGame: $jg")
            gameService.joinGame(jg.gameId.toLong(), jg.userId.toLong(), jg.betAmount)
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/startGame")
    suspend fun startGame(@RequestParam gameId: String): ResponseEntity<Response> {
        return response {
            println("startGame: $gameId")
            gameService.startGame(gameId.toLong())
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/leaveGame")
    suspend fun leaveGame(@RequestParam gameId: String, @RequestParam userId: String): ResponseEntity<Response> {
        return response {
            println("leaveGame: gmid=$gameId; usid=$userId")
            gameService.leaveGame(gameId.toLong(), userId.toLong())
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/get")
    suspend fun getGame(@RequestParam gameId: String): ResponseEntity<GameDto> {
        return run {
            println("getGame: $gameId")
            ResponseEntity.ok(gameService.getGame(gameId.toLong())).print()
        }
    }

    @GetMapping("/getAll")
    suspend fun getAll(): ResponseEntity<Flow<GameDto>> {
        println("getAll")
        return ResponseEntity.ok((gameService.getAllGames())).print()
    }

    @GetMapping("/getAllAvailable")
    suspend fun getAvailableGames(): ResponseEntity<Flow<GameDto>> {
        println("getAvailableGames")
        Hooks.onOperatorDebug();
        return ResponseEntity.ok((gameService.getAvailableGames())).print()
    }

    @GetMapping("/getMy")
    suspend fun getMyGames(@RequestParam userId: String): ResponseEntity<Flow<GameDto>> {
        println("getMyGames")
        return ResponseEntity.ok(gameService.getMyGames(userId.toLong())).print()
    }

    @GetMapping("/getActive")
    suspend fun getActiveGames(@RequestParam userId: String): ResponseEntity<Flow<GameDto>> {
        println("getActiveGames")
        return ResponseEntity.ok(gameService.getActiveGames(userId.toLong())).print()
    }

    @GetMapping("/participants")
    suspend fun getParticipants(@RequestParam gameId: Long) {
        usersService.getUsersOfGame(gameId).let {
            ResponseEntity.ok(ok(it))
        }
    }

    @DeleteMapping("/delete")
    suspend fun deleteGame(@RequestParam gameId: String): ResponseEntity<Response> {
        return run {
            println("deleteGame")
            gameService.deleteGame(gameId.toLong())
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/results")
    suspend fun getResults(@RequestParam gameId: String): ResponseEntity<Response> {
        return response {
            println("getResults")
            ResponseEntity.ok(ok(resultsService.getResults(gameId.toLong()))).print()
        }
    }
}
