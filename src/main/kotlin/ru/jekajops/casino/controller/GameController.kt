package ru.jekajops.casino.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import print
import reactor.core.publisher.Flux
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
    private lateinit var resultsService: ResultsService

    @PostMapping("/create-game")
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

    @PostMapping("/join-game")
    suspend fun joinGame(
        @RequestBody jg: JoinGame,
    ): ResponseEntity<Response> {
        return response {
            println("joinGame: $jg")
            gameService.joinGame(jg.gameId.toLong(), jg.userId.toLong(), jg.betAmount)
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/start-game")
    suspend fun startGame(@RequestParam gameId: String): ResponseEntity<Response> {
        return response {
            println("startGame: $gameId")
            gameService.startGame(gameId.toLong())
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/leave-game")
    suspend fun leaveGame(@RequestParam gameId: String, @RequestParam userId: String): ResponseEntity<Response> {
        return response {
            println("leaveGame: gmid=$gameId; usid=$userId")
            gameService.leaveGame(gameId.toLong(), userId.toLong())
            ResponseEntity.ok(ok()).print()
        }
    }

    @GetMapping("/get")
    suspend fun getGame(@RequestParam gameId: String): ResponseEntity<Response> {
        return response {
            println("getGame: $gameId")
            ResponseEntity.ok(ok(gameService.getGame(gameId.toLong()))).print()
        }
    }

    @GetMapping("/get-all")
    suspend fun getAvailableGames(): ResponseEntity<Flux<GameDto>> {
        println("getAvailableGames")
        return ResponseEntity.ok((gameService.getAvailableGames())).print()
    }

    @GetMapping("/get-my")
    suspend fun getMyGames(@RequestParam userId: String): ResponseEntity<Flux<GameDto>> {
        println("getMyGames")
        return ResponseEntity.ok(gameService.getMyGames(userId.toLong())).print()
    }

    @GetMapping("/get-active")
    suspend fun getActiveGames(@RequestParam userId: String): ResponseEntity<Flux<GameDto>> {
        println("getActiveGames")
        return ResponseEntity.ok(gameService.getActiveGames(userId.toLong())).print()
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
