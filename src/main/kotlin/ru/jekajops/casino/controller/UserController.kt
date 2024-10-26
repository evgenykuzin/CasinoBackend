package ru.jekajops.casino.controller

import org.springframework.web.reactive.function.server.*
import print
import reactor.core.publisher.Mono
import ru.jekajops.casino.*
import ru.jekajops.casino.dto.UpdateUserBalance
import ru.jekajops.casino.dto.UserRegistration
import ru.jekajops.casino.tools.GET
import ru.jekajops.casino.tools.POST
import ru.jekajops.casino.tools.nest
import ru.jekajops.casino.tools.pathValue
import java.time.Instant

fun usersController(userService: UsersService) = coRouter {
    nest(users) {
        POST(users.registration) {
            it.awaitBody(UserRegistration::class)
                .let { userRegistration ->
                    println("registerUser: $userRegistration")
                    val registeredUser = userService.registerUser(
                        userRegistration.telegramId!!,
                        userRegistration.username,
                        userRegistration.firstName,
                        userRegistration.lastName,
                        userRegistration.phone
                    )
                    ServerResponse.ok().bodyValueAndAwait(ok(registeredUser)).print()
                }
        }
        GET(users.getById, "{id}") {
            it.pathVariable("id")
                .let { id ->
                    println("getUserById: $id")
                    val user = userService.getUserById(id.toLong())
                    user?.let {
                        ServerResponse.ok().bodyValueAndAwait(ok(it))
                    } ?: ServerResponse.ok().bodyValueAndAwait(Error.NOT_FOUND.response).print()
                }
        }
        GET(users.getByUsername, "{username}") {
            it.pathVariable("username").let { username ->
                println("getUserByUsername: $username")
                val user = userService.getUserByUsername(username)
                (user?.let {
                    ServerResponse.ok().bodyValueAndAwait(ok(it))
                } ?: ServerResponse.ok().bodyValueAndAwait(Error.NOT_FOUND.response)).print()
            }
        }
        GET(users.getByTgId, "{telegramId}") {
            it.pathVariable("telegramId").let { telegramId ->
                println("getUserByTelegramId: $telegramId")
                val user = userService.getUserByTelegramId(telegramId)
                (user?.let {
                    ServerResponse.ok().bodyValueAndAwait(ok(it))
                } ?: ServerResponse.ok().bodyValueAndAwait(Error.NOT_FOUND.response)).print()
            }
        }
        GET(users.getAll) {
            println("getAllUsers")
            val users = userService.getAllUsers()
            ServerResponse.ok().bodyValueAndAwait(ok(users)).print()
        }
        POST(users.updateBalance) {
            it.awaitBody<UpdateUserBalance>().let {
                println("getMoney: tgid=${it.telegramId}; amount=${it.amount}")
                val updatedUser = userService.updateBalance(it.telegramId, it.amount)
                (updatedUser?.let {
                    ServerResponse.ok().bodyValueAndAwait(ok(it))
                } ?: ServerResponse.ok().bodyValueAndAwait(Error.NOT_FOUND.response)).print()
            }
        }
        GET(users.gameParticipants) {
            it.queryParam("gameId").let { gameId ->
                println("getParticipantsByGameId: gameId=$gameId")
                if (gameId.isEmpty) {
                    ServerResponse.badRequest()
                        .bodyValueAndAwait(
                            error(400, "No gameId param found")
                        )
                } else {
                    userService.getUsersOfGame(gameId.get().toLong()).let {
                        ServerResponse.ok().bodyValueAndAwait(ok(it))
                    }.print()
                }
            }
        }
    }
}

//@RestController
//@RequestMapping("/users")
//class UserController {
//    @Autowired
//    private lateinit var userService: UsersService
//
//    @PostMapping("/registration")
//    fun registerUser(@RequestBody userRegistration: UserRegistration): ResponseEntity<Response> {
//        return response {
//            println("registerUser: $userRegistration")
//            val registeredUser = userService.registerUser(userRegistration.telegramId!!, userRegistration.username,
//                userRegistration.firstName, userRegistration.lastName, userRegistration.phone)
//            ResponseEntity.ok(ok(registeredUser)).print()
//        }
//    }
//
//    @GetMapping("/get-id")
//    fun getUserById(@RequestParam("id") id: String): ResponseEntity<Response> {
//        return response {
//            println("getUserById: $id")
//            val user = userService.getUserById(id)
//            (user?.let {
//                ResponseEntity.ok(ok(it))
//            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
//        }
//    }
//
//    @GetMapping("/get-username")
//    fun getUserByUsername(@RequestParam("username") username: String): ResponseEntity<Response> {
//        return response {
//            println("getUserByUsername: $username")
//            val user = userService.getUserByUsername(username)
//            (user?.let {
//                ResponseEntity.ok(ok(it))
//            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
//        }
//    }
//
//    @GetMapping("/get-tgid")
//    fun getUserByTelegramId(@RequestParam("telegramId") telegramId: String): ResponseEntity<Response> {
//        return response {
//            println("getUserByTelegramId: $telegramId")
//            val user = userService.getUserByTelegramId(telegramId)
//            (user?.let {
//                ResponseEntity.ok(ok(it))
//            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
//        }
//    }
//
//    @GetMapping("/get-all")
//    fun getAllUsers(): ResponseEntity<Response> {
//        return response {
//            println("getAllUsers")
//            val users = userService.getAllUsers()
//            ResponseEntity.ok(ok(users)).print()
//        }
//    }
//
//    @GetMapping("/update-balance/{telegramId}/{amount}")
//    fun getMoney(
//        @PathVariable("telegramId") telegramId: String,
//        @PathVariable("amount") amount: Int,
//    ): ResponseEntity<Response> {
//        return response {
//            println("getMoney: tgid=$telegramId; amount=$amount")
//            val updatedUser = userService.updateBalance(telegramId, amount.toBigDecimal())
//            (updatedUser?.let {
//                ResponseEntity.ok(ok(it))
//            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
//        }
//    }
//
//    @GetMapping("/game-participants")
//    fun getParticipantsByGameId(@RequestParam("gameId") gameId: String): ResponseEntity<Response> {
//        return response {
//            println("getParticipantsByGameId: gameId=$gameId")
//            userService.getUsersOfGame(gameId).let {
//                ResponseEntity.ok(ok(it))
//            }.print()
//        }
//    }
//}
