package ru.jekajops.casino.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

@RestController
@RequestMapping("/users")
class UserController {
    @Autowired
    private lateinit var userService: UsersService

    @PostMapping("/registration")
    suspend fun registerUser(@RequestBody userRegistration: UserRegistration): ResponseEntity<Response> {
        return run {
            println("registerUser: $userRegistration")
            val registeredUser = userService.registerUser(
                userRegistration.telegramId!!,
                userRegistration.username,
                userRegistration.firstName,
                userRegistration.lastName,
                userRegistration.phone
            )
            ResponseEntity.ok(ok(registeredUser)).print()
        }
    }

    @GetMapping("/get")
    suspend fun getUserById(@RequestParam("id") id: Long): ResponseEntity<Response> {
        return response {
            println("getUserById: $id")
            val user = userService.getUserById(id)
            (user?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/getByUsername")
    suspend fun getUserByUsername(@RequestParam("username") username: String): ResponseEntity<Response> {
        return response {
            println("getUserByUsername: $username")
            val user = userService.getUserByUsername(username)
            (user?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/getByTG")
    suspend fun getUserByTelegramId(@RequestParam("tgId") telegramId: String): ResponseEntity<Response> {
        return response {
            println("getUserByTelegramId: $telegramId")
            val user = userService.getUserByTelegramId(telegramId)
            (user?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/geAll")
    suspend fun   getAllUsers(): ResponseEntity<Response> {
        return response {
            println("getAllUsers")
            val users = userService.getAllUsers()
            ResponseEntity.ok(ok(users)).print()
        }
    }

    @GetMapping("/updateBalance")
    suspend fun getMoney(
        @RequestParam("telegramId") telegramId: String,
        @RequestParam("amount") amount: Int,
    ): ResponseEntity<Response> {
        return response {
            println("getMoney: tgid=$telegramId; amount=$amount")
            val updatedUser = userService.updateBalance(telegramId, amount.toBigDecimal())
            (updatedUser?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/gameParticipants")
    suspend fun getParticipantsByGameId(@RequestParam("gameId") gameId: Long): ResponseEntity<Response> {
        return response {
            println("getGameParticipants: gameId=$gameId")
            userService.getUsersOfGame(gameId).let {
                ResponseEntity.ok(ok(it))
            }.print()
        }
    }
}
