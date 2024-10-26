package ru.jekajops.casino.tools

val Collection<*>?.sizeOrZero: Int
    get() = this?.size ?: 0