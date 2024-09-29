package ru.jekajops.casino.controller

import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*
//import org.springframework.web.servlet.function.server.ServerResponse

import ru.jekajops.casino.*
import ru.jekajops.casino.tools.Path
import ru.jekajops.casino.tools.ROOT
import ru.jekajops.casino.tools.asPath

@Configuration
class TapController {

    val storage = mutableMapOf<String, Any>()

    final inline fun <reified T : Any> getter(name: String, orDefault: () -> T) = storage[name] as? T ?: orDefault().also {
        storage[name] = it
    }

    final inline fun <reified T : Any> T.setter(name: String, ) {
        storage[name] = this
    }

    var MutableMap<String, Any>.score: Long?
        get() = getter("score") { 0 }
        set(value) {
            value?.setter("score")
        }

    var MutableMap<String, Any>.users: List<User>?
        get() = getter("users") { listOf() }
        set(value) {
            value?.setter("users")
        }

    var MutableMap<String, Any>.friends: List<FriendInfo>?
        get() = getter("friends") { listOf() }
        set(value) {
            value?.setter("friends")
        }

    var MutableMap<String, Any>.games: List<Game>?
        get() = getter("games") { listOf() }
        set(value) {
            value?.setter("games")
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

        storage.games = listOf(
            Game("Темка верняк", listOf(
                User(
                    "1",
                    "1234567890",
                    "Patric",
                    "Batman",
                    "patman666xx",
                    ""
                ),
                User(
                    "2",
                    "0987654321",
                    "Vitya",
                    "AK47",
                    "ak47vitek",
                    ""
                ),
                User(
                    "3",
                    "1234509876",
                    "Сисястая",
                    "Стейси",
                    "e.anastasja",
                    "89523663611"
                ),

            )),
            Game("Темка на лям", listOf(
                User(
                    "1",
                    "1234567890",
                    "Жека",
                    "Джопс",
                    "jekajops",
                    ""
                ),
                User(
                    "2",
                    "0987654321",
                    "Vitya",
                    "AK47",
                    "ak47vitek",
                    ""
                ),
                User(
                    "3",
                    "1234509876",
                    "Сисястая",
                    "Стейси",
                    "e.anastasja",
                    "89523663611"
                ),

                ))
        )
    }

    @Bean
    @FlowPreview
    fun controller() = coRouter {
        infix fun Path.nest(r: CoRouterFunctionDsl.() -> Unit) = asPath.nest(r)

        fun CoRouterFunctionDsl.POST(path: Path, f: suspend (ServerRequest) -> ServerResponse) {
            this.POST(path.asPath, f)
        }

        fun CoRouterFunctionDsl.GET(path: Path, f: suspend (ServerRequest) -> ServerResponse) {
            this.GET(path.asPath, f)
        }

        GET(ROOT) {
            ServerResponse.ok().bodyValue("OK").block()!!
        }

        taps nest {
            POST(taps.score) {
                it.bodyToMono<SendCurrentScoreRequest>().map {
                    println("SendCurrentScoreRequest: $it")
                    storage.score = it.score
                }.flatMap {
                    ServerResponse.ok().bodyValue("OK")
                }.block()!!
            }

            GET(taps.score) {
                println("get score: ${it.path()}")
                val currScore = storage.score ?: 0L
                println("Get Score: $currScore")
                ServerResponse.ok().json().bodyValue(GetCurrentScoreResponse(currScore)).block()!!
            }

            POST(taps.upgrade) {
                ServerResponse.ok().bodyValue(
                    UpgradeResponse(
                        createInvoiceLink(
                            storage.users?.first()?.telegramId ?: "213390901"
                        )
                    )
                ).block()!!
            }

            GET(taps.friends) {
                ServerResponse.ok().json().bodyValue(
                    GetFriendsListResponse(
                        storage.friends ?: listOf()
                    )
                ).block()!!
            }
        }

        games nest {
            GET(games.search) {
                ServerResponse.ok().json().bodyValue(
                    GamesSearchRs(
                        storage.games ?: listOf()
                    )
                ).block()!!
            }

            POST(games.join) {
                ServerResponse.ok().bodyValue(
                    "OK"
                ).block()!!
            }
        }
    }

    @Serializable
    data class Game(
        val name: String,
        val users: List<User>
    )

    @Serializable
    data class GamesSearchRs(
        val games: List<Game>
    )

//    @PostMapping("/score/")
//    fun postScore(
////        @RequestHeader initData: String,
//        @RequestBody scsr: SendCurrentScoreRequest,
//    ): ResponseEntity<String> {
//        println("SendCurrentScoreRequest: $scsr")
//        storage.score = scsr.score
//        return ResponseEntity.ok("OK")
//    }

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
