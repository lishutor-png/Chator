package com.example.chess.model

data class GameState(
    val pieces: Map<Square, Piece> = emptyMap(),
    val turn: PieceColor = PieceColor.WHITE,
    val castlingRights: CastlingRights = CastlingRights(),
    val enPassantTarget: Square? = null,
    val halfmoveClock: Int = 0,
    val fullmoveNumber: Int = 1,
    val moveHistory: List<Move> = emptyList(),
    val capturedByWhite: List<Piece> = emptyList(),
    val capturedByBlack: List<Piece> = emptyList(),
    val isCheck: Boolean = false,
    val isCheckmate: Boolean = false,
    val isStalemate: Boolean = false,
    val isDraw: Boolean = false,
    val drawReason: String? = null,
    val lastMove: Move? = null,
    val selectedSquare: Square? = null,
    val legalMovesForSelected: List<Move> = emptyList(),
    val pendingPromotion: Move? = null
) {
    val isGameOver: Boolean
        get() = isCheckmate || isStalemate || isDraw

    fun getPiece(square: Square): Piece? = pieces[square]
    fun getPiece(file: Int, rank: Int): Piece? = Square.fromCoords(file, rank)?.let { pieces[it] }

    val whiteKingSquare: Square?
        get() = pieces.entries.firstOrNull { it.value.type == PieceType.KING && it.value.color == PieceColor.WHITE }?.key

    val blackKingSquare: Square?
        get() = pieces.entries.firstOrNull { it.value.type == PieceType.KING && it.value.color == PieceColor.BLACK }?.key

    val currentKingSquare: Square?
        get() = if (turn == PieceColor.WHITE) whiteKingSquare else blackKingSquare

    val materialAdvantage: Int
        get() {
            var whiteScore = 0
            var blackScore = 0
            for ((_, piece) in pieces) {
                if (piece.type != PieceType.KING) {
                    if (piece.color == PieceColor.WHITE) whiteScore += piece.type.value
                    else blackScore += piece.type.value
                }
            }
            return (whiteScore - blackScore) / 100
        }
}
