package im.vector.app.ext.data.type

data class CustomPair<A>(
    val a: A,
    val b: String
) {
    override fun toString(): String = b
}
