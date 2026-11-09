package com.example.chess.model

enum class PieceType(
    val symbol: String,
    val algebraicLetter: String,
    val value: Int,
    val indonesianName: String
) {
    PAWN("P", "", 100, "Bidak"),
    KNIGHT("N", "N", 320, "Kuda"),
    BISHOP("B", "B", 330, "Gajah / Luncur"),
    ROOK("R", "R", 500, "Benteng"),
    QUEEN("Q", "Q", 900, "Menteri"),
    KING("K", "K", 20000, "Raja")
}
