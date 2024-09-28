fun <T> T.print(appendable: String = ""): T {
    println(this.toString().plus(appendable))
    return this
}