package com.example.chess.model

data class Square(val file: Int, val rank: Int) {
    init {
        require(file in 0..7 && rank in 0..7) { "Square out of bounds: ($file, $rank)" }
    }

    val isLight: Boolean
        get() = (file + rank) % 2 != 0

    val algebraic: String
        get() = "${('a' + file)}${rank + 1}"

    companion object {
        fun fromAlgebraic(alg: String): Square {
            require(alg.length == 2) { "Invalid algebraic square: $alg" }
            val f = alg[0].lowercaseChar() - 'a'
            val r = alg[1].digitToInt() - 1
            return Square(f, r)
        }

        fun fromCoords(file: Int, rank: Int): Square? {
            return if (file in 0..7 && rank in 0..7) Square(file, rank) else null
        }
    }
}
