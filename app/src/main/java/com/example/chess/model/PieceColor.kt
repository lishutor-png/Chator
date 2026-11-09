package com.example.chess.model

enum class PieceColor {
    WHITE,
    BLACK;

    val opponent: PieceColor
        get() = if (this == WHITE) BLACK else WHITE

    val displayName: String
        get() = if (this == WHITE) "Putih" else "Hitam"
}
