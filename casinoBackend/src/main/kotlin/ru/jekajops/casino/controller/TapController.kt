package ru.jekajops.casino.controller

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.coRouter
//import org.springframework.web.servlet.function.server.ServerResponse

import ru.jekajops.casino.*
import java.util.function.Consumer

@Configuration
//@RestController
//@RequestMapping("/taps")
class TapController {

    companion object {
        fun node(path: String, dsl: Node.() -> Unit) {
            Node(path, dsl)
        }

    }

    class Node(val path: String, dsl: Node.() -> Unit) {
        val children: MutableSet<Node> = mutableSetOf()

        protected fun endpoint(path: String) {
            children.add(Node(path, {}))
        }
        protected fun endpoint(path: String, dsl: Node.() -> Unit) {
            children.add(Node(path, dsl))
        }
        init {
            dsl()
        }
        companion object {
            val TAPS = node("/taps") {
                endpoint("/score") {
                    endpoint("/")
                }
            }
        }
    }
    interface Naming {

    }
    val <T : Path> T.name: String get() = (this::class.simpleName?.lowercase() ?: "").let {
        "/${it}"
    }
    sealed class Path : Naming {
        object TAS : Path()
        sealed class Taps : Path() {
            sealed class Score : Path()
        }
        sealed class Game : Path()
    }

    val storage = mutableMapOf<String, Any>()

    var MutableMap<String, Any>.score: Long?
        get() = storage["score"] as? Long
        set(value) {
            storage["score"] = value as Long
        }

    var MutableMap<String, Any>.users: List<User>?
        get() = storage["users"] as? List<User>? ?: listOf<User>().also { storage["users"] = it }
        set(value) {
            storage["users"] = value as List<User>
        }

    var MutableMap<String, Any>.friends: List<FriendInfo>?
        get() = storage["friends"] as? List<FriendInfo>? ?: listOf<FriendInfo>().also { storage["friends"] = it }
        set(value) {
            storage["friends"] = value as List<FriendInfo>
        }

    init {
        storage.friends = listOf(
            FriendInfo(
                121,
                "John",
                "Snow",
                false
            ),
            FriendInfo(
                122,
                "John",
                "Snow",
                false
            ),
            FriendInfo(
                123,
                "John",
                "Snow",
                false
            ),
            FriendInfo(
                124,
                "John",
                "Snow",
                false
            ),
        )
    }

    @Bean
    suspend fun controller(): RouterFunction<ServerResponse> {
        return coRouter {
           Path.Taps().name.nest {
                POST("/score/") {
                    it.bodyToMono<SendCurrentScoreRequest>().map {
                        println("SendCurrentScoreRequest: $it")
                        storage.score = it.score
                    }.flatMap {
                        ServerResponse.ok().bodyValue("OK")
                    }.block()!!
                }

                GET("/score/") {
                    val currScore = storage.score ?: 0L
                    println("Get Score: $currScore")
                    ServerResponse.ok().bodyValue(GetCurrentScoreResponse(currScore)).block()!!
                }

                POST("/upgrade/") {
                    ServerResponse.ok().bodyValue(UpgradeResponse(createInvoiceLink(
                        storage.users?.first()?.telegramId ?: "213390901"
                    ))).block()!!
                }

                GET("/friends/") {
                    ServerResponse.ok().bodyValue(
                        GetFriendsListResponse(
                            storage.friends ?: listOf()
                        )
                    ).block()!!
                }
            }
        }
    }

//    @PostMapping("/score/")
//    fun postScore(
////        @RequestHeader initData: String,
//        @RequestBody scsr: SendCurrentScoreRequest,
//    ): ResponseEntity<String> {
//        println("SendCurrentScoreRequest: $scsr")
//        storage.score = scsr.score
//        return ResponseEntity.ok("OK")
//    }
//
//    @GetMapping("/score/")
//    fun getScore(
////        @RequestHeader initData: String,
//    ): ResponseEntity<GetCurrentScoreResponse> {
//        return run {
//            val currScore = storage.score ?: 0L
//            println("Get Score: $currScore")
//            ResponseEntity.ok(GetCurrentScoreResponse(currScore))
//        }
//    }
//
//    @PostMapping("/upgrade/")
//    suspend fun upgrade(): ResponseEntity<UpgradeResponse> {
//        return run {
//            ResponseEntity.ok(UpgradeResponse(createInvoiceLink(
//                storage.users?.first()?.telegramId ?: "213390901"
//            )))
//        }
//    }
//
//    @GetMapping("/friends/")
//    fun friends(): ResponseEntity<GetFriendsListResponse> {
//        return run {
//            ResponseEntity.ok(
//                GetFriendsListResponse(
//                    storage.friends ?: listOf()
//                )
//            )
//        }
//    }

}

@Serializable
class UpgradeResponse(
    @SerialName("invoice_link")
    val invoiceLink: String,
)

@Serializable
class SendCurrentScoreRequest(
    val score: Long,
)

@Serializable
class GetCurrentScoreResponse(
    val score: Long,
)

@Serializable
class GetFriendsListResponse(
    val friends: List<FriendInfo>,
)

@Serializable
data class FriendInfo(
    val id: Long,
    @SerialName("first_name")
    val firstName: String? = null,
    @SerialName("username")
    val username: String? = null,
    @SerialName("is_invitation")
    val isInviteYou: Boolean = false,
)
