package com.example.chess.model

data class Piece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false
) {
    val fenChar: Char
        get() {
            val c = when (type) {
                PieceType.PAWN -> 'p'
                PieceType.KNIGHT -> 'n'
                PieceType.BISHOP -> 'b'
                PieceType.ROOK -> 'r'
                PieceType.QUEEN -> 'q'
                PieceType.KING -> 'k'
            }
            return if (color == PieceColor.WHITE) c.uppercaseChar() else c
        }
}
