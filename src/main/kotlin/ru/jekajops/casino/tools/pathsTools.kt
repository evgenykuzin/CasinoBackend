package ru.jekajops.casino.tools

object ROOT : Path(null) {
    override fun toString(): String = "/"
}

val <T : Path> T.asPath: String
    get() = toString()

val <T : Path> T.asFullPath: String
    get() = (parent?.asFullPath?.let {
        if (it == "/") {
            ""
        } else if (it.last() == '/')
            it.substring(0, it.lastIndex - 1)
        else
            it
    } ?: "") + asPath

open class Path(val parent: Path? = ROOT, private val closed: Boolean = false) {
    override fun toString(): String = (this::class.simpleName ?: "").let {
        val close = if (closed) "/" else ""
        "/$it$close"
    }
}
