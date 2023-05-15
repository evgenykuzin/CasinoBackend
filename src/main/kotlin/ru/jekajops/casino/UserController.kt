package ru.jekajops.casino

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import print
import java.time.Instant

@RestController
@RequestMapping("/users")
class UserController {
    @Autowired
    private lateinit var userService: UsersService

    @PostMapping("/registration")
    fun registerUser(@RequestBody userRegistration: UserRegistration): ResponseEntity<Response> {
        return response {
            println("registerUser: $userRegistration")
            val registeredUser = userService.registerUser(userRegistration.telegramId!!, userRegistration.username,
                userRegistration.firstName, userRegistration.lastName, userRegistration.phone)
            ResponseEntity.ok(ok(registeredUser)).print()
        }
    }

    @GetMapping("/get-id")
    fun getUserById(@RequestParam("id") id: String): ResponseEntity<Response> {
        return response {
            println("getUserById: $id")
            val user = userService.getUserById(id)
            (user?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/get-username")
    fun getUserByUsername(@RequestParam("username") username: String): ResponseEntity<Response> {
        return response {
            println("getUserByUsername: $username")
            val user = userService.getUserByUsername(username)
            (user?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/get-tgid")
    fun getUserByTelegramId(@RequestParam("telegramId") telegramId: String): ResponseEntity<Response> {
        return response {
            println("getUserByTelegramId: $telegramId")
            val user = userService.getUserByTelegramId(telegramId)
            (user?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/get-all")
    fun getAllUsers(): ResponseEntity<Response> {
        return response {
            println("getAllUsers")
            val users = userService.getAllUsers()
            ResponseEntity.ok(ok(users)).print()
        }
    }

    @GetMapping("/update-balance/{telegramId}/{amount}")
    fun getMoney(@PathVariable("telegramId") telegramId: String, @PathVariable("amount") amount: Int): ResponseEntity<Response> {
        return response {
            println("getMoney: tgid=$telegramId; amount=$amount")
            val updatedUser = userService.updateBalance(telegramId, amount)
            (updatedUser?.let {
                ResponseEntity.ok(ok(it))
            } ?: ResponseEntity.ok(Error.NOT_FOUND.response)).print()
        }
    }

    @GetMapping("/game-participants")
    fun getParticipantsByGameId(@RequestParam("gameId") gameId: String): ResponseEntity<Response> {
        return response {
            println("getParticipantsByGameId: gameId=$gameId")
            userService.getUsersOfGame(gameId).let {
                ResponseEntity.ok(ok(it))
            }.print()
        }
    }
}

data class UserRegistration (
    val telegramId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val phone: String? = null,
    var balance: Double = 0.0,
    val credit: Int = 0,
    var deleted: Boolean = false,
    val timestamp: Instant? = null
)
