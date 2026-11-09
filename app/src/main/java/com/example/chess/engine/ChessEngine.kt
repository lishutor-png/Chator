package com.example.chess.engine

import com.example.chess.model.CastlingRights
import com.example.chess.model.GameState
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square

object ChessEngine {

    fun newGame(): GameState {
        val pieces = mutableMapOf<Square, Piece>()

        // White pieces (rank 0 = rank 1, rank 1 = rank 2)
        pieces[Square(0, 0)] = Piece(PieceType.ROOK, PieceColor.WHITE)
        pieces[Square(1, 0)] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
        pieces[Square(2, 0)] = Piece(PieceType.BISHOP, PieceColor.WHITE)
        pieces[Square(3, 0)] = Piece(PieceType.QUEEN, PieceColor.WHITE)
        pieces[Square(4, 0)] = Piece(PieceType.KING, PieceColor.WHITE)
        pieces[Square(5, 0)] = Piece(PieceType.BISHOP, PieceColor.WHITE)
        pieces[Square(6, 0)] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
        pieces[Square(7, 0)] = Piece(PieceType.ROOK, PieceColor.WHITE)
        for (f in 0..7) {
            pieces[Square(f, 1)] = Piece(PieceType.PAWN, PieceColor.WHITE)
        }

        // Black pieces (rank 7 = rank 8, rank 6 = rank 7)
        pieces[Square(0, 7)] = Piece(PieceType.ROOK, PieceColor.BLACK)
        pieces[Square(1, 7)] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
        pieces[Square(2, 7)] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        pieces[Square(3, 7)] = Piece(PieceType.QUEEN, PieceColor.BLACK)
        pieces[Square(4, 7)] = Piece(PieceType.KING, PieceColor.BLACK)
        pieces[Square(5, 7)] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        pieces[Square(6, 7)] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
        pieces[Square(7, 7)] = Piece(PieceType.ROOK, PieceColor.BLACK)
        for (f in 0..7) {
            pieces[Square(f, 6)] = Piece(PieceType.PAWN, PieceColor.BLACK)
        }

        val initialState = GameState(
            pieces = pieces,
            turn = PieceColor.WHITE,
            castlingRights = CastlingRights(),
            enPassantTarget = null,
            halfmoveClock = 0,
            fullmoveNumber = 1
        )
        return updateStateStatus(initialState)
    }

    fun fromFen(fen: String): GameState {
        val parts = fen.trim().split("\\s+".toRegex())
        val boardPart = parts.getOrNull(0) ?: ""
        val turnPart = parts.getOrNull(1) ?: "w"
        val castlingPart = parts.getOrNull(2) ?: "-"
        val epPart = parts.getOrNull(3) ?: "-"
        val halfClock = parts.getOrNull(4)?.toIntOrNull() ?: 0
        val fullNum = parts.getOrNull(5)?.toIntOrNull() ?: 1

        val pieces = mutableMapOf<Square, Piece>()
        val ranks = boardPart.split("/")
        for (rIndex in ranks.indices) {
            val rank = 7 - rIndex
            var file = 0
            val rankStr = ranks[rIndex]
            for (char in rankStr) {
                if (char.isDigit()) {
                    file += char.digitToInt()
                } else {
                    val color = if (char.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
                    val type = when (char.lowercaseChar()) {
                        'p' -> PieceType.PAWN
                        'n' -> PieceType.KNIGHT
                        'b' -> PieceType.BISHOP
                        'r' -> PieceType.ROOK
                        'q' -> PieceType.QUEEN
                        'k' -> PieceType.KING
                        else -> PieceType.PAWN
                    }
                    if (file in 0..7 && rank in 0..7) {
                        pieces[Square(file, rank)] = Piece(type, color)
                    }
                    file++
                }
            }
        }

        val turn = if (turnPart.lowercase() == "b") PieceColor.BLACK else PieceColor.WHITE
        val castling = CastlingRights(
            whiteKingSide = castlingPart.contains('K'),
            whiteQueenSide = castlingPart.contains('Q'),
            blackKingSide = castlingPart.contains('k'),
            blackQueenSide = castlingPart.contains('q')
        )
        val epSquare = if (epPart != "-") try { Square.fromAlgebraic(epPart) } catch (_: Exception) { null } else null

        val state = GameState(
            pieces = pieces,
            turn = turn,
            castlingRights = castling,
            enPassantTarget = epSquare,
            halfmoveClock = halfClock,
            fullmoveNumber = fullNum
        )
        return updateStateStatus(state)
    }

    fun isSquareAttacked(square: Square, byColor: PieceColor, pieces: Map<Square, Piece>): Boolean {
        // Pawn attacks
        val pawnDir = if (byColor == PieceColor.WHITE) 1 else -1
        val pawnAttackRank = square.rank - pawnDir
        if (pawnAttackRank in 0..7) {
            if (square.file - 1 >= 0) {
                val p = pieces[Square(square.file - 1, pawnAttackRank)]
                if (p?.color == byColor && p.type == PieceType.PAWN) return true
            }
            if (square.file + 1 <= 7) {
                val p = pieces[Square(square.file + 1, pawnAttackRank)]
                if (p?.color == byColor && p.type == PieceType.PAWN) return true
            }
        }

        // Knight attacks
        val knightOffsets = listOf(
            Pair(1, 2), Pair(2, 1), Pair(-1, 2), Pair(-2, 1),
            Pair(1, -2), Pair(2, -1), Pair(-1, -2), Pair(-2, -1)
        )
        for ((df, dr) in knightOffsets) {
            val f = square.file + df
            val r = square.rank + dr
            if (f in 0..7 && r in 0..7) {
                val p = pieces[Square(f, r)]
                if (p?.color == byColor && p.type == PieceType.KNIGHT) return true
            }
        }

        // King attacks
        for (df in -1..1) {
            for (dr in -1..1) {
                if (df == 0 && dr == 0) continue
                val f = square.file + df
                val r = square.rank + dr
                if (f in 0..7 && r in 0..7) {
                    val p = pieces[Square(f, r)]
                    if (p?.color == byColor && p.type == PieceType.KING) return true
                }
            }
        }

        // Bishop / Queen diagonals
        val diagDirs = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))
        for ((df, dr) in diagDirs) {
            var step = 1
            while (true) {
                val f = square.file + df * step
                val r = square.rank + dr * step
                if (f !in 0..7 || r !in 0..7) break
                val targetSquare = Square(f, r)
                val p = pieces[targetSquare]
                if (p != null) {
                    if (p.color == byColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break // Blocked by piece
                }
                step++
            }
        }

        // Rook / Queen orthogonals
        val orthoDirs = listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1))
        for ((df, dr) in orthoDirs) {
            var step = 1
            while (true) {
                val f = square.file + df * step
                val r = square.rank + dr * step
                if (f !in 0..7 || r !in 0..7) break
                val targetSquare = Square(f, r)
                val p = pieces[targetSquare]
                if (p != null) {
                    if (p.color == byColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                step++
            }
        }

        return false
    }

    fun isKingInCheck(color: PieceColor, pieces: Map<Square, Piece>): Boolean {
        val kingSquare = pieces.entries.firstOrNull {
            it.value.type == PieceType.KING && it.value.color == color
        }?.key ?: return false

        return isSquareAttacked(kingSquare, color.opponent, pieces)
    }

    fun generateLegalMoves(state: GameState): List<Move> {
        val pseudoMoves = generatePseudoLegalMoves(state)
        val legalMoves = mutableListOf<Move>()

        for (move in pseudoMoves) {
            if (isMoveLegal(move, state)) {
                legalMoves.add(move)
            }
        }
        return legalMoves
    }

    private fun isMoveLegal(move: Move, state: GameState): Boolean {
        // For castling, king cannot be in check currently, nor can pass through check
        if (move.isCastling) {
            if (isKingInCheck(state.turn, state.pieces)) return false
            val rank = move.from.rank
            val transitFile = if (move.to.file > move.from.file) 5 else 3
            val transitSquare = Square(transitFile, rank)
            if (isSquareAttacked(transitSquare, state.turn.opponent, state.pieces)) return false
        }

        // Simulate board after move
        val nextPieces = simulateMovePieces(move, state)
        // Moving player's king must not be in check after the move
        return !isKingInCheck(state.turn, nextPieces)
    }

    private fun simulateMovePieces(move: Move, state: GameState): Map<Square, Piece> {
        val newPieces = state.pieces.toMutableMap()
        newPieces.remove(move.from)

        if (move.isEnPassant) {
            // Remove captured pawn
            val capturedPawnRank = move.from.rank
            val capturedPawnSquare = Square(move.to.file, capturedPawnRank)
            newPieces.remove(capturedPawnSquare)
        } else if (move.isCastling) {
            val rank = move.from.rank
            if (move.to.file == 6) { // Kingside
                val rook = newPieces.remove(Square(7, rank))
                if (rook != null) newPieces[Square(5, rank)] = rook.copy(hasMoved = true)
            } else if (move.to.file == 2) { // Queenside
                val rook = newPieces.remove(Square(0, rank))
                if (rook != null) newPieces[Square(3, rank)] = rook.copy(hasMoved = true)
            }
        }

        val placedPiece = if (move.promotion != null) {
            Piece(move.promotion, move.piece.color, hasMoved = true)
        } else {
            move.piece.copy(hasMoved = true)
        }

        newPieces[move.to] = placedPiece
        return newPieces
    }

    private fun generatePseudoLegalMoves(state: GameState): List<Move> {
        val moves = mutableListOf<Move>()
        for ((square, piece) in state.pieces) {
            if (piece.color != state.turn) continue

            when (piece.type) {
                PieceType.PAWN -> generatePawnMoves(square, piece, state, moves)
                PieceType.KNIGHT -> generateKnightMoves(square, piece, state, moves)
                PieceType.BISHOP -> generateBishopMoves(square, piece, state, moves)
                PieceType.ROOK -> generateRookMoves(square, piece, state, moves)
                PieceType.QUEEN -> generateQueenMoves(square, piece, state, moves)
                PieceType.KING -> generateKingMoves(square, piece, state, moves)
            }
        }
        return moves
    }

    private fun generatePawnMoves(square: Square, piece: Piece, state: GameState, moves: MutableList<Move>) {
        val dir = if (piece.color == PieceColor.WHITE) 1 else -1
        val startRank = if (piece.color == PieceColor.WHITE) 1 else 6
        val promoRank = if (piece.color == PieceColor.WHITE) 7 else 0

        // Single step forward
        val oneStepRank = square.rank + dir
        if (oneStepRank in 0..7) {
            val oneStepSquare = Square(square.file, oneStepRank)
            if (state.pieces[oneStepSquare] == null) {
                if (oneStepRank == promoRank) {
                    addPromotions(square, oneStepSquare, piece, null, moves)
                } else {
                    moves.add(Move(from = square, to = oneStepSquare, piece = piece))
                }

                // Two steps forward from start rank
                if (square.rank == startRank) {
                    val twoStepRank = square.rank + 2 * dir
                    val twoStepSquare = Square(square.file, twoStepRank)
                    if (state.pieces[twoStepSquare] == null) {
                        moves.add(Move(from = square, to = twoStepSquare, piece = piece))
                    }
                }
            }
        }

        // Diagonal captures
        for (df in listOf(-1, 1)) {
            val targetFile = square.file + df
            val targetRank = square.rank + dir
            if (targetFile in 0..7 && targetRank in 0..7) {
                val targetSquare = Square(targetFile, targetRank)
                val targetPiece = state.pieces[targetSquare]

                if (targetPiece != null && targetPiece.color != piece.color) {
                    if (targetRank == promoRank) {
                        addPromotions(square, targetSquare, piece, targetPiece, moves)
                    } else {
                        moves.add(
                            Move(
                                from = square,
                                to = targetSquare,
                                piece = piece,
                                capturedPiece = targetPiece,
                                isCapture = true
                            )
                        )
                    }
                } else if (targetSquare == state.enPassantTarget) {
                    // En passant
                    val epCapturedSquare = Square(targetFile, square.rank)
                    val epCapturedPiece = state.pieces[epCapturedSquare]
                    moves.add(
                        Move(
                            from = square,
                            to = targetSquare,
                            piece = piece,
                            capturedPiece = epCapturedPiece,
                            isCapture = true,
                            isEnPassant = true
                        )
                    )
                }
            }
        }
    }

    private fun addPromotions(
        from: Square,
        to: Square,
        piece: Piece,
        captured: Piece?,
        moves: MutableList<Move>
    ) {
        listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach { promoType ->
            moves.add(
                Move(
                    from = from,
                    to = to,
                    piece = piece,
                    capturedPiece = captured,
                    promotion = promoType,
                    isCapture = captured != null
                )
            )
        }
    }

    private fun generateKnightMoves(square: Square, piece: Piece, state: GameState, moves: MutableList<Move>) {
        val offsets = listOf(
            Pair(1, 2), Pair(2, 1), Pair(-1, 2), Pair(-2, 1),
            Pair(1, -2), Pair(2, -1), Pair(-1, -2), Pair(-2, -1)
        )
        for ((df, dr) in offsets) {
            val f = square.file + df
            val r = square.rank + dr
            if (f in 0..7 && r in 0..7) {
                val target = Square(f, r)
                val destPiece = state.pieces[target]
                if (destPiece == null) {
                    moves.add(Move(from = square, to = target, piece = piece))
                } else if (destPiece.color != piece.color) {
                    moves.add(Move(from = square, to = target, piece = piece, capturedPiece = destPiece, isCapture = true))
                }
            }
        }
    }

    private fun generateRayMoves(
        square: Square,
        piece: Piece,
        state: GameState,
        dirs: List<Pair<Int, Int>>,
        moves: MutableList<Move>
    ) {
        for ((df, dr) in dirs) {
            var step = 1
            while (true) {
                val f = square.file + df * step
                val r = square.rank + dr * step
                if (f !in 0..7 || r !in 0..7) break
                val target = Square(f, r)
                val destPiece = state.pieces[target]
                if (destPiece == null) {
                    moves.add(Move(from = square, to = target, piece = piece))
                } else {
                    if (destPiece.color != piece.color) {
                        moves.add(Move(from = square, to = target, piece = piece, capturedPiece = destPiece, isCapture = true))
                    }
                    break
                }
                step++
            }
        }
    }

    private fun generateBishopMoves(square: Square, piece: Piece, state: GameState, moves: MutableList<Move>) {
        generateRayMoves(square, piece, state, listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1)), moves)
    }

    private fun generateRookMoves(square: Square, piece: Piece, state: GameState, moves: MutableList<Move>) {
        generateRayMoves(square, piece, state, listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)), moves)
    }

    private fun generateQueenMoves(square: Square, piece: Piece, state: GameState, moves: MutableList<Move>) {
        generateRayMoves(
            square, piece, state,
            listOf(
                Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1),
                Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)
            ),
            moves
        )
    }

    private fun generateKingMoves(square: Square, piece: Piece, state: GameState, moves: MutableList<Move>) {
        for (df in -1..1) {
            for (dr in -1..1) {
                if (df == 0 && dr == 0) continue
                val f = square.file + df
                val r = square.rank + dr
                if (f in 0..7 && r in 0..7) {
                    val target = Square(f, r)
                    val destPiece = state.pieces[target]
                    if (destPiece == null) {
                        moves.add(Move(from = square, to = target, piece = piece))
                    } else if (destPiece.color != piece.color) {
                        moves.add(Move(from = square, to = target, piece = piece, capturedPiece = destPiece, isCapture = true))
                    }
                }
            }
        }

        // Castling moves
        if (piece.color == PieceColor.WHITE && square.file == 4 && square.rank == 0) {
            // Kingside
            if (state.castlingRights.whiteKingSide &&
                state.pieces[Square(5, 0)] == null &&
                state.pieces[Square(6, 0)] == null &&
                state.pieces[Square(7, 0)]?.let { it.type == PieceType.ROOK && it.color == PieceColor.WHITE } == true
            ) {
                moves.add(Move(from = square, to = Square(6, 0), piece = piece, isCastling = true))
            }
            // Queenside
            if (state.castlingRights.whiteQueenSide &&
                state.pieces[Square(3, 0)] == null &&
                state.pieces[Square(2, 0)] == null &&
                state.pieces[Square(1, 0)] == null &&
                state.pieces[Square(0, 0)]?.let { it.type == PieceType.ROOK && it.color == PieceColor.WHITE } == true
            ) {
                moves.add(Move(from = square, to = Square(2, 0), piece = piece, isCastling = true))
            }
        } else if (piece.color == PieceColor.BLACK && square.file == 4 && square.rank == 7) {
            // Kingside
            if (state.castlingRights.blackKingSide &&
                state.pieces[Square(5, 7)] == null &&
                state.pieces[Square(6, 7)] == null &&
                state.pieces[Square(7, 7)]?.let { it.type == PieceType.ROOK && it.color == PieceColor.BLACK } == true
            ) {
                moves.add(Move(from = square, to = Square(6, 7), piece = piece, isCastling = true))
            }
            // Queenside
            if (state.castlingRights.blackQueenSide &&
                state.pieces[Square(3, 7)] == null &&
                state.pieces[Square(2, 7)] == null &&
                state.pieces[Square(1, 7)] == null &&
                state.pieces[Square(0, 7)]?.let { it.type == PieceType.ROOK && it.color == PieceColor.BLACK } == true
            ) {
                moves.add(Move(from = square, to = Square(2, 7), piece = piece, isCastling = true))
            }
        }
    }

    fun makeMove(state: GameState, move: Move): GameState {
        val nextPieces = simulateMovePieces(move, state)

        // Updated captured pieces
        val capturedWhite = state.capturedByWhite.toMutableList()
        val capturedBlack = state.capturedByBlack.toMutableList()
        val captured = move.capturedPiece
        if (captured != null) {
            if (state.turn == PieceColor.WHITE) {
                capturedWhite.add(captured)
            } else {
                capturedBlack.add(captured)
            }
        }

        // Updated castling rights
        var wKs = state.castlingRights.whiteKingSide
        var wQs = state.castlingRights.whiteQueenSide
        var bKs = state.castlingRights.blackKingSide
        var bQs = state.castlingRights.blackQueenSide

        // If king moved
        if (move.piece.type == PieceType.KING) {
            if (move.piece.color == PieceColor.WHITE) {
                wKs = false
                wQs = false
            } else {
                bKs = false
                bQs = false
            }
        }
        // If rook moved from home corner
        if (move.piece.type == PieceType.ROOK) {
            if (move.from == Square(0, 0)) wQs = false
            if (move.from == Square(7, 0)) wKs = false
            if (move.from == Square(0, 7)) bQs = false
            if (move.from == Square(7, 7)) bKs = false
        }
        // If rook is captured on home corner
        if (move.to == Square(0, 0)) wQs = false
        if (move.to == Square(7, 0)) wKs = false
        if (move.to == Square(0, 7)) bQs = false
        if (move.to == Square(7, 7)) bKs = false

        // Updated en passant target
        val nextEpTarget = if (move.piece.type == PieceType.PAWN && kotlin.math.abs(move.to.rank - move.from.rank) == 2) {
            val midRank = (move.to.rank + move.from.rank) / 2
            Square(move.from.file, midRank)
        } else {
            null
        }

        // Halfmove clock (resets on pawn move or capture)
        val nextHalfClock = if (move.piece.type == PieceType.PAWN || move.capturedPiece != null) {
            0
        } else {
            state.halfmoveClock + 1
        }

        val nextFullNumber = if (state.turn == PieceColor.BLACK) state.fullmoveNumber + 1 else state.fullmoveNumber

        // Generate Standard Algebraic Notation (SAN)
        val san = computeSan(move, state, nextPieces)
        val executedMove = move.copy(san = san, isCheckmate = san.endsWith("#"))

        val nextState = GameState(
            pieces = nextPieces,
            turn = state.turn.opponent,
            castlingRights = CastlingRights(wKs, wQs, bKs, bQs),
            enPassantTarget = nextEpTarget,
            halfmoveClock = nextHalfClock,
            fullmoveNumber = nextFullNumber,
            moveHistory = state.moveHistory + executedMove,
            capturedByWhite = capturedWhite,
            capturedByBlack = capturedBlack,
            lastMove = executedMove
        )

        return updateStateStatus(nextState)
    }

    private fun updateStateStatus(state: GameState): GameState {
        val check = isKingInCheck(state.turn, state.pieces)
        val legalMoves = generateLegalMoves(state)

        var checkmate = false
        var stalemate = false
        var draw = false
        var drawReason: String? = null

        if (legalMoves.isEmpty()) {
            if (check) {
                checkmate = true
            } else {
                stalemate = true
                drawReason = "Paten / Remis (Tidak ada langkah legal)"
            }
        } else if (state.halfmoveClock >= 100) {
            draw = true
            drawReason = "Remis (Aturan 50 langkah tanpa penangkapan/bidak)"
        } else if (hasInsufficientMaterial(state.pieces)) {
            draw = true
            drawReason = "Remis (Materi tidak cukup untuk skakmat)"
        }

        return state.copy(
            isCheck = check,
            isCheckmate = checkmate,
            isStalemate = stalemate,
            isDraw = draw,
            drawReason = drawReason
        )
    }

    private fun hasInsufficientMaterial(pieces: Map<Square, Piece>): Boolean {
        if (pieces.size <= 2) return true // Only kings
        if (pieces.size == 3) {
            // King + Knight or King + Bishop vs King
            return pieces.values.any { it.type == PieceType.BISHOP || it.type == PieceType.KNIGHT }
        }
        if (pieces.size == 4) {
            val nonKings = pieces.values.filter { it.type != PieceType.KING }
            if (nonKings.all { it.type == PieceType.BISHOP } && nonKings[0].color != nonKings[1].color) {
                // If bishops are on same square color
                val bSquares = pieces.filter { it.value.type == PieceType.BISHOP }.keys.toList()
                if (bSquares.size == 2 && bSquares[0].isLight == bSquares[1].isLight) {
                    return true
                }
            }
        }
        return false
    }

    private fun computeSan(move: Move, prevState: GameState, nextPieces: Map<Square, Piece>): String {
        val check = isKingInCheck(prevState.turn.opponent, nextPieces)
        val pseudoNextState = prevState.copy(pieces = nextPieces, turn = prevState.turn.opponent)
        val mate = check && generateLegalMoves(pseudoNextState).isEmpty()

        val suffix = when {
            mate -> "#"
            check -> "+"
            else -> ""
        }

        if (move.isCastling) {
            return if (move.to.file == 6) "O-O$suffix" else "O-O-O$suffix"
        }

        val piecePrefix = if (move.piece.type == PieceType.PAWN) "" else move.piece.type.algebraicLetter
        val captureStr = if (move.capturedPiece != null || move.isEnPassant) {
            if (move.piece.type == PieceType.PAWN) "${('a' + move.from.file)}x" else "x"
        } else ""

        val promoStr = if (move.promotion != null) "=${move.promotion.algebraicLetter}" else ""
        val destStr = move.to.algebraic

        return "$piecePrefix$captureStr$destStr$promoStr$suffix"
    }

    fun toFen(state: GameState): String {
        val sb = StringBuilder()
        for (rank in 7 downTo 0) {
            var emptyCount = 0
            for (file in 0..7) {
                val piece = state.pieces[Square(file, rank)]
                if (piece == null) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) {
                        sb.append(emptyCount)
                        emptyCount = 0
                    }
                    sb.append(piece.fenChar)
                }
            }
            if (emptyCount > 0) sb.append(emptyCount)
            if (rank > 0) sb.append("/")
        }

        sb.append(if (state.turn == PieceColor.WHITE) " w " else " b ")

        val cr = state.castlingRights
        val crStr = StringBuilder()
        if (cr.whiteKingSide) crStr.append("K")
        if (cr.whiteQueenSide) crStr.append("Q")
        if (cr.blackKingSide) crStr.append("k")
        if (cr.blackQueenSide) crStr.append("q")
        sb.append(if (crStr.isEmpty()) "-" else crStr.toString())

        sb.append(" ")
        sb.append(state.enPassantTarget?.algebraic ?: "-")
        sb.append(" ${state.halfmoveClock} ${state.fullmoveNumber}")

        return sb.toString()
    }

    fun generatePgn(
        state: GameState,
        whitePlayer: String = "Putih",
        blackPlayer: String = "Hitam",
        result: String = "*"
    ): String {
        val sb = StringBuilder()
        sb.appendLine("[Event \"Chator Mobile Match\"]")
        sb.appendLine("[Site \"Chator Android\"]")
        val dateStr = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.US).format(java.util.Date())
        sb.appendLine("[Date \"$dateStr\"]")
        sb.appendLine("[White \"$whitePlayer\"]")
        sb.appendLine("[Black \"$blackPlayer\"]")
        sb.appendLine("[Result \"$result\"]")
        sb.appendLine()

        val movePairs = state.moveHistory.chunked(2)
        for (i in movePairs.indices) {
            val pair = movePairs[i]
            sb.append("${i + 1}. ${pair[0].san} ")
            if (pair.size > 1) {
                sb.append("${pair[1].san} ")
            }
        }
        sb.append(result)
        return sb.toString()
    }
}

