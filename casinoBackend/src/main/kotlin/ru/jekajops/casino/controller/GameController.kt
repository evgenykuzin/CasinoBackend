//package ru.jekajops.casino.controller
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import print
//import ru.jekajops.casino.*
//
////@RestController
////@RequestMapping("/games")
//class GameController {
//    @Autowired
//    private lateinit var gameService: GameService
//    @Autowired
//    private lateinit var resultsService: ResultsService
//
//    @PostMapping("/create-game")
//    fun createGame(
//        @RequestBody cg: CreateGame
//    ): ResponseEntity<Response> {
//        return response {
//            println("createGame: $cg")
//            val gameId = gameService.createGame(cg.name ?: "Game", cg.adminId, cg.minPlayers, cg.maxPlayers, cg.minBet, cg.gameType)
//            ResponseEntity.ok(ok(data = gameId)).print()
//        }
//    }
//
//    @PostMapping("/join-game")
//    fun joinGame(
//        @RequestBody jg: JoinGame
//    ): ResponseEntity<Response> {
//        return response {
//            println("joinGame: $jg")
//            gameService.joinGame(jg.gameId, jg.userId, jg.betAmount)
//            ResponseEntity.ok(ok()).print()
//        }
//    }
//
//    @GetMapping("/start-game")
//    private fun startGame(@RequestParam gameId: String): ResponseEntity<Response> {
//        return response {
//            println("startGame: $gameId")
//            gameService.startGame(gameId)
//            ResponseEntity.ok(ok()).print()
//        }
//    }
//
//    @GetMapping("/leave-game")
//    fun leaveGame(@RequestParam gameId: String, @RequestParam userId: String): ResponseEntity<Response> {
//        return response {
//            println("leaveGame: gmid=$gameId; usid=$userId")
//            gameService.leaveGame(gameId, userId)
//            ResponseEntity.ok(ok()).print()
//        }
//    }
//
//    @GetMapping("/get")
//    fun getGame(@RequestParam gameId: String): ResponseEntity<Response> {
//        return response {
//            println("getGame: $gameId")
//            ResponseEntity.ok(ok(gameService.getGame(gameId))).print()
//        }
//    }
//
//    @GetMapping("/get-all")
//    fun getAvailableGames(): ResponseEntity<Response> {
//        return response {
//            println("getAvailableGames")
//            ResponseEntity.ok((ok(gameService.getAvailableGames()))).print()
//        }
//    }
//
//    @GetMapping("/get-my")
//    fun getMyGames(@RequestParam userId: String): ResponseEntity<Response> {
//        return response {
//            println("getMyGames")
//            ResponseEntity.ok((ok(gameService.getMyGames(userId)))).print()
//        }
//    }
//
//    @GetMapping("/get-active")
//    fun getActiveGames(@RequestParam userId: String): ResponseEntity<Response> {
//        return response {
//            println("getMyGames")
//            ResponseEntity.ok((ok(gameService.getActiveGames(userId)))).print()
//        }
//    }
//
//    @DeleteMapping("/delete")
//    fun deleteGame(@RequestParam gameId: String): ResponseEntity<Response> {
//        return response {
//            println("deleteGame")
//            gameService.deleteGame(gameId)
//            ResponseEntity.ok(ok()).print()
//        }
//    }
//
//    @GetMapping("/results")
//    fun getResults(@RequestParam gameId: String): ResponseEntity<Response> {
//        return response {
//            println("getResults")
//            ResponseEntity.ok(ok(resultsService.getResults(gameId))).print()
//        }
//    }
//}
//
//data class CreateGame(
//    val name: String? = "Game",
//    val adminId: String,
//    val minPlayers: Int = 2,
//    val maxPlayers: Int = 5,
//    val minBet: Double = 1.0,
//    val gameType: GameType = GameType.ONE_WINNER
//)
//
//data class JoinGame(
//    val gameId: String, val userId: String, val betAmount: Double
//)
