package ru.jekajops.casino

class UserNotFoundException(val telegramId: String) : Throwable() {

}

class NotEnoughBalanceException(val userId: Long) : Throwable() {

}

class StatusException(val status: Int, val desc: String) : Throwable() {

}