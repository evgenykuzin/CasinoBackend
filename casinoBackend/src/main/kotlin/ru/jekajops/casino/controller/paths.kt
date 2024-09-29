package ru.jekajops.casino.controller

import ru.jekajops.casino.tools.Path
import ru.jekajops.casino.tools.ROOT
import ru.jekajops.casino.tools.asFullPath
import ru.jekajops.casino.tools.asPath

object taps : Path() {
    object score : Path(taps, true)
    object friends : Path(taps, true)
    object upgrade : Path(taps, true)
}

object games : Path() {
    object search : Path(games) {
        object byId : Path(search, true)
    }
    object join : Path(games)
}

fun main() {
    fun Path.printFull() = also { println(it.asFullPath) }
    fun Path.print() = also { println(it.asPath) }

    ROOT.print()
    taps.print()
    taps.score.print()
    taps.friends.print()
    taps.upgrade.print()
    games.print()
    games.join.print()
    games.search.print()
    games.search.byId.print()

    ROOT.printFull()
    taps.printFull()
    taps.score.printFull()
    taps.friends.printFull()
    taps.upgrade.printFull()
    games.printFull()
    games.join.printFull()
    games.search.printFull()
    games.search.byId.printFull()
}
