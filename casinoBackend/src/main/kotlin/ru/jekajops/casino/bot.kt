package ru.jekajops.casino

import dev.inmo.tgbotapi.extensions.api.send.payments.createInvoiceLink
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.types.payments.LabeledPrice
import dev.inmo.tgbotapi.types.requireEmailField

object settings {
    const val TELEGRAM_PAYMENTS_PROVIDER_TOKEN = "7953268785:AAHQh5G156Dp2Und490JZnFOT8cpCkZjeuM"
}

suspend fun bot() = telegramBotWithBehaviourAndLongPolling(settings.TELEGRAM_PAYMENTS_PROVIDER_TOKEN) {
}

suspend fun createInvoiceLink(userId: String): String {
    return bot().first.createInvoiceLink(
        title="Оплата премиум статуса",
        description="С премиумом тапается лучше! Проложи дорогу к криптоинвестициям",
        payload="user_${userId}",
        providerToken=settings.TELEGRAM_PAYMENTS_PROVIDER_TOKEN,
        currency = "RUB",
        prices = listOf(LabeledPrice(label="Премиум статус", amount=5000)),
        requireName = true,
        requireEmail = true,
        requirePhoneNumber =false,
        requireShippingAddress = false,
    )
}