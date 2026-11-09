package com.example.chess.model

data class Move(
    val from: Square,
    val to: Square,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val promotion: PieceType? = null,
    val isCapture: Boolean = false,
    val isEnPassant: Boolean = false,
    val isCastling: Boolean = false,
    val isCheckmate: Boolean = false,
    val san: String = ""
)

data class CastlingRights(
    val whiteKingSide: Boolean = true,
    val whiteQueenSide: Boolean = true,
    val blackKingSide: Boolean = true,
    val blackQueenSide: Boolean = true
)
