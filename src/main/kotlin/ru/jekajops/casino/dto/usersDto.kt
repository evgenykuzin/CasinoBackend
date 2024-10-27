package ru.jekajops.casino.dto

import java.math.BigDecimal
import java.time.Instant

data class UpdateUserBalance(
    val telegramId: String,
    val amount: BigDecimal
)

data class UserRegistration(
    val telegramId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val phone: String? = null,
    var balance: Double = 0.0,
    val credit: Int = 0,
    val timestamp: Instant,
)
