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

    object createGame : Path(games)
    object joinGame : Path(games)
    object startGame : Path(games)
    object leaveGame : Path(games)
    object deleteGame : Path(games)
    object get : Path(games) {
        object one : Path(get) {
            object byId: Path(one)
            object myLastActive : Path(one)
            object myLastCompleted : Path(one)
        }
        object list : Path(get) {
            object all: Path(list)
            object onlyMine: Path(list)
            object active: Path(list)
            object completed : Path(list)
        }
    }
    object result : Path(games)
}

object users : Path() {
    object registration : Path(users)
    object getById: Path(users)
    object getByUsername : Path(users)
    object getByTgId : Path(users)
    object getAll : Path(users)
    object updateBalance : Path(users)
    object gameParticipants : Path(users)
}

fun test() {
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
