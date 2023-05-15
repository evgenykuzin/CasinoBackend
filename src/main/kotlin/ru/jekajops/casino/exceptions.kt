package ru.jekajops.casino

class UserNotFoundException(val telegramId: Int) : Throwable() {

}

class NotEnoughBalanceException(val userId: String) : Throwable() {

}

class StatusException(val status: Int, val desc: String) : Throwable() {

}