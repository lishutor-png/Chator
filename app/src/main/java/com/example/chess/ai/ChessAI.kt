package com.example.chess.ai

import com.example.chess.engine.ChessEngine
import com.example.chess.model.AiDifficulty
import com.example.chess.model.GameState
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import kotlin.random.Random

object ChessAI {

    // Piece values
    private const val PAWN_VAL = 100
    private const val KNIGHT_VAL = 320
    private const val BISHOP_VAL = 330
    private const val ROOK_VAL = 500
    private const val QUEEN_VAL = 900
    private const val KING_VAL = 20000

    // Piece-Square Tables (White perspective, rank 7 down to rank 0)
    // 0 is a1, 63 is h8
    private val PAWN_PST = intArrayOf(
        0,  0,  0,  0,  0,  0,  0,  0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5,  5, 10, 25, 25, 10,  5,  5,
        0,  0,  0, 20, 20,  0,  0,  0,
        5, -5,-10,  0,  0,-10, -5,  5,
        5, 10, 10,-20,-20, 10, 10,  5,
        0,  0,  0,  0,  0,  0,  0,  0
    )

    private val KNIGHT_PST = intArrayOf(
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50
    )

    private val BISHOP_PST = intArrayOf(
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 10, 10, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20
    )

    private val ROOK_PST = intArrayOf(
        0,  0,  0,  0,  0,  0,  0,  0,
        5, 10, 10, 10, 10, 10, 10,  5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        0,  0,  0,  5,  5,  0,  0,  0
    )

    private val QUEEN_PST = intArrayOf(
        -20,-10,-10, -5, -5,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5,  5,  5,  5,  0,-10,
        -5,  0,  5,  5,  5,  5,  0, -5,
        0,  0,  5,  5,  5,  5,  0, -5,
        -10,  5,  5,  5,  5,  5,  0,-10,
        -10,  0,  5,  0,  0,  0,  0,-10,
        -20,-10,-10, -5, -5,-10,-10,-20
    )

    private val KING_PST_MIDDLE = intArrayOf(
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -20,-30,-30,-40,-40,-30,-30,-20,
        -10,-20,-20,-20,-20,-20,-20,-10,
        20, 20,  0,  0,  0,  0, 20, 20,
        20, 30, 10,  0,  0, 10, 30, 20
    )

    fun evaluate(state: GameState): Int {
        if (state.isCheckmate) {
            return if (state.turn == PieceColor.WHITE) -30000 else 30000
        }
        if (state.isDraw || state.isStalemate) {
            return 0
        }

        var score = 0
        val isEndgame = state.pieces.size <= 10

        for ((square, piece) in state.pieces) {
            val isWhite = piece.color == PieceColor.WHITE
            val multiplier = if (isWhite) 1 else -1

            val baseVal = when (piece.type) {
                PieceType.PAWN -> PAWN_VAL
                PieceType.KNIGHT -> KNIGHT_VAL
                PieceType.BISHOP -> BISHOP_VAL
                PieceType.ROOK -> ROOK_VAL
                PieceType.QUEEN -> QUEEN_VAL
                PieceType.KING -> KING_VAL
            }

            // PST index
            val rankForPst = if (isWhite) 7 - square.rank else square.rank
            val pstIndex = rankForPst * 8 + square.file

            val posBonus = when (piece.type) {
                PieceType.PAWN -> PAWN_PST[pstIndex]
                PieceType.KNIGHT -> KNIGHT_PST[pstIndex]
                PieceType.BISHOP -> BISHOP_PST[pstIndex]
                PieceType.ROOK -> ROOK_PST[pstIndex]
                PieceType.QUEEN -> QUEEN_PST[pstIndex]
                PieceType.KING -> if (isEndgame) 0 else KING_PST_MIDDLE[pstIndex]
            }

            score += multiplier * (baseVal + posBonus)
        }

        return score
    }

    fun getBestMove(state: GameState, difficulty: AiDifficulty): Move? {
        val legalMoves = ChessEngine.generateLegalMoves(state)
        if (legalMoves.isEmpty()) return null

        return when (difficulty) {
            AiDifficulty.EASY -> {
                // Easy: 40% random move, 60% simple capture heuristic
                if (Random.nextFloat() < 0.40f) {
                    legalMoves.random()
                } else {
                    orderMoves(legalMoves).firstOrNull() ?: legalMoves.random()
                }
            }
            AiDifficulty.MEDIUM -> {
                // Medium: Minimax depth 2 with positional piece-square evaluation
                minimaxRoot(state, depth = 2, isMaximizing = state.turn == PieceColor.WHITE)
            }
            AiDifficulty.HARD -> {
                // Hard: Minimax depth 3-4 with quiescence search on captures
                val depth = if (state.pieces.size <= 10) 4 else 3
                minimaxRoot(state, depth = depth, isMaximizing = state.turn == PieceColor.WHITE)
            }
        }
    }

    private fun minimaxRoot(state: GameState, depth: Int, isMaximizing: Boolean): Move? {
        val legalMoves = ChessEngine.generateLegalMoves(state)
        if (legalMoves.isEmpty()) return null

        val orderedMoves = orderMoves(legalMoves)
        var bestMove: Move? = orderedMoves.first()
        var alpha = -100000
        var beta = 100000

        if (isMaximizing) {
            var maxEval = -100000
            for (move in orderedMoves) {
                val nextState = ChessEngine.makeMove(state, move)
                val evaluation = minimax(nextState, depth - 1, alpha, beta, false)
                if (evaluation > maxEval) {
                    maxEval = evaluation
                    bestMove = move
                }
                alpha = maxOf(alpha, evaluation)
                if (beta <= alpha) break
            }
        } else {
            var minEval = 100000
            for (move in orderedMoves) {
                val nextState = ChessEngine.makeMove(state, move)
                val evaluation = minimax(nextState, depth - 1, alpha, beta, true)
                if (evaluation < minEval) {
                    minEval = evaluation
                    bestMove = move
                }
                beta = minOf(beta, evaluation)
                if (beta <= alpha) break
            }
        }

        return bestMove
    }

    private fun minimax(state: GameState, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean): Int {
        if (depth <= 0 || state.isGameOver) {
            return quiescence(state, alpha, beta, isMaximizing, maxQDepth = 2)
        }

        var currentAlpha = alpha
        var currentBeta = beta
        val legalMoves = ChessEngine.generateLegalMoves(state)
        val orderedMoves = orderMoves(legalMoves)

        if (isMaximizing) {
            var maxEval = -100000
            for (move in orderedMoves) {
                val nextState = ChessEngine.makeMove(state, move)
                val eval = minimax(nextState, depth - 1, currentAlpha, currentBeta, false)
                maxEval = maxOf(maxEval, eval)
                currentAlpha = maxOf(currentAlpha, eval)
                if (currentBeta <= currentAlpha) break
            }
            return maxEval
        } else {
            var minEval = 100000
            for (move in orderedMoves) {
                val nextState = ChessEngine.makeMove(state, move)
                val eval = minimax(nextState, depth - 1, currentAlpha, currentBeta, true)
                minEval = minOf(minEval, eval)
                currentBeta = minOf(currentBeta, eval)
                if (currentBeta <= currentAlpha) break
            }
            return minEval
        }
    }

    // Quiescence search on captures to avoid horizon effect
    private fun quiescence(state: GameState, alpha: Int, beta: Int, isMaximizing: Boolean, maxQDepth: Int): Int {
        val standPat = evaluate(state)
        if (maxQDepth <= 0 || state.isGameOver) return standPat

        var currentAlpha = alpha
        var currentBeta = beta

        if (isMaximizing) {
            if (standPat >= beta) return beta
            if (standPat > currentAlpha) currentAlpha = standPat

            val captures = ChessEngine.generateLegalMoves(state).filter { it.isCapture || it.promotion != null }
            val orderedCaptures = orderMoves(captures)

            for (move in orderedCaptures) {
                val nextState = ChessEngine.makeMove(state, move)
                val score = quiescence(nextState, currentAlpha, currentBeta, false, maxQDepth - 1)
                if (score >= currentBeta) return currentBeta
                if (score > currentAlpha) currentAlpha = score
            }
            return currentAlpha
        } else {
            if (standPat <= alpha) return alpha
            if (standPat < currentBeta) currentBeta = standPat

            val captures = ChessEngine.generateLegalMoves(state).filter { it.isCapture || it.promotion != null }
            val orderedCaptures = orderMoves(captures)

            for (move in orderedCaptures) {
                val nextState = ChessEngine.makeMove(state, move)
                val score = quiescence(nextState, currentAlpha, currentBeta, true, maxQDepth - 1)
                if (score <= currentAlpha) return currentAlpha
                if (score < currentBeta) currentBeta = score
            }
            return currentBeta
        }
    }

    // Move ordering (MVV-LVA: Most Valuable Victim - Least Valuable Attacker)
    private fun orderMoves(moves: List<Move>): List<Move> {
        return moves.sortedByDescending { move ->
            var score = 0
            if (move.capturedPiece != null) {
                score += 10 * move.capturedPiece.type.value - move.piece.type.value
            }
            if (move.promotion != null) {
                score += move.promotion.value
            }
            if (move.isCastling) {
                score += 50
            }
            score
        }
    }

    fun getHint(state: GameState): Pair<Move?, String> {
        val bestMove = minimaxRoot(state, depth = 3, isMaximizing = state.turn == PieceColor.WHITE)
        if (bestMove == null) return Pair(null, "Tidak ada langkah yang tersedia.")

        val pieceName = bestMove.piece.type.indonesianName
        val target = bestMove.to.algebraic
        val reason = when {
            bestMove.isCheckmate -> "Langkah kemenangan langsung! Skakmat!"
            bestMove.isCastling -> "Rokade untuk mengamankan Raja dan mengaktifkan Benteng."
            bestMove.promotion != null -> "Promosi bidak menjadi ${bestMove.promotion.indonesianName}!"
            bestMove.isCapture -> "Menangkap ${bestMove.capturedPiece?.type?.indonesianName ?: "bidak"} lawan di $target untuk keuntungan materi."
            bestMove.piece.type == PieceType.KNIGHT -> "Mengembangkan Kuda ke $target untuk menguasai petak sentral."
            bestMove.piece.type == PieceType.BISHOP -> "Membuka diagonal Luncur ke $target untuk menekan posisi lawan."
            bestMove.piece.type == PieceType.PAWN -> "Memajukan bidak ke $target untuk memperkuat struktur pertahanan."
            else -> "Memindahkan $pieceName ke $target untuk posisi yang lebih strategis."
        }
        return Pair(bestMove, reason)
    }
}
