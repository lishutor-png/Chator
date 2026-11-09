package com.example.chess.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.ai.ChessAI
import com.example.chess.audio.SoundAndHaptics
import com.example.chess.data.ChessPreferences
import com.example.chess.engine.ChessEngine
import com.example.chess.model.AiDifficulty
import com.example.chess.model.BoardTheme
import com.example.chess.model.GameMode
import com.example.chess.model.GameState
import com.example.chess.model.MatchRecord
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square
import com.example.chess.model.TimeSetting
import com.example.chess.puzzles.ChessPuzzleRepository
import com.example.chess.puzzles.Puzzle
import com.example.chess.training.TrainingLesson
import com.example.chess.training.TrainingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

data class GameUiState(
    val gameState: GameState = ChessEngine.newGame(),
    val gameMode: GameMode = GameMode.VS_AI,
    val playerColorVsAi: PieceColor = PieceColor.WHITE,
    val aiDifficulty: AiDifficulty = AiDifficulty.MEDIUM,
    val boardTheme: BoardTheme = BoardTheme.WOOD,
    val timeSetting: TimeSetting = TimeSetting.UNLIMITED,
    val whiteTimeMillis: Long = 0L,
    val blackTimeMillis: Long = 0L,
    val isTimerRunning: Boolean = false,
    val isTimerPaused: Boolean = false,
    val isThinking: Boolean = false,
    val isBoardFlipped: Boolean = false,
    val hintMove: Move? = null,
    val hintExplanation: String? = null,
    val showPromotionDialog: Boolean = false,
    // Puzzles
    val currentPuzzleIndex: Int = 0,
    val puzzleMessage: String? = null,
    val isPuzzleSolved: Boolean = false,
    val puzzleCategory: String = "All",
    // Training
    val currentTrainingIndex: Int = 0,
    val trainingMessage: String? = null,
    val isTrainingCompleted: Boolean = false,
    val player1Name: String = "Player 1",
    val player2Name: String = "Player 2",
    val autoRotateBoard: Boolean = false,
    // History & Undo/Redo
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val lastCompletedMatchPgn: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val preferences = ChessPreferences(application)
    val soundAndHaptics = SoundAndHaptics(application)

    private val _uiState = MutableStateFlow(
        GameUiState(
            aiDifficulty = preferences.aiDifficulty,
            boardTheme = preferences.boardTheme,
            timeSetting = preferences.timeSetting,
            player1Name = preferences.player1Name,
            player2Name = preferences.player2Name,
            autoRotateBoard = preferences.autoRotateBoard
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<GameState>()
    private val redoStack = mutableListOf<GameState>()
    private var timerJob: Job? = null
    private var aiJob: Job? = null
    private var lastTickTime: Long = 0L

    init {
        soundAndHaptics.isSoundEnabled = preferences.isSoundEnabled
        soundAndHaptics.isHapticsEnabled = preferences.isHapticsEnabled
        startNewGame(GameMode.VS_AI)
    }

    fun setPlayerNames(p1: String, p2: String) {
        val cleanP1 = p1.trim().ifEmpty { "Player 1" }
        val cleanP2 = p2.trim().ifEmpty { "Player 2" }
        preferences.player1Name = cleanP1
        preferences.player2Name = cleanP2
        _uiState.update { it.copy(player1Name = cleanP1, player2Name = cleanP2) }
    }

    fun startNewGame(
        mode: GameMode = _uiState.value.gameMode,
        playerColor: PieceColor = _uiState.value.playerColorVsAi,
        difficulty: AiDifficulty = _uiState.value.aiDifficulty,
        time: TimeSetting = _uiState.value.timeSetting,
        customMinutes: Long = 5L,
        customIncrement: Long = 0L,
        p1Name: String = _uiState.value.player1Name,
        p2Name: String = _uiState.value.player2Name,
        autoRotate: Boolean? = null
    ) {
        aiJob?.cancel()
        timerJob?.cancel()
        undoStack.clear()
        redoStack.clear()

        val cleanP1 = p1Name.trim().ifEmpty { "Player 1" }
        val cleanP2 = p2Name.trim().ifEmpty { "Player 2" }
        preferences.player1Name = cleanP1
        preferences.player2Name = cleanP2

        if (autoRotate != null) {
            preferences.autoRotateBoard = autoRotate
        }
        val currentAutoRotate = autoRotate ?: preferences.autoRotateBoard

        val actualPlayerColor = if (playerColor == PieceColor.WHITE || playerColor == PieceColor.BLACK) {
            playerColor
        } else {
            if (Random.nextBoolean()) PieceColor.WHITE else PieceColor.BLACK
        }

        val initialState = ChessEngine.newGame()
        val initFlipped = if (mode == GameMode.VS_AI) {
            actualPlayerColor == PieceColor.BLACK
        } else {
            // In PASS_AND_PLAY: if fixed board, always start with White at bottom
            false
        }

        val initialMillis = if (time == TimeSetting.CUSTOM) {
            customMinutes * 60 * 1000L
        } else {
            time.initialSeconds * 1000L
        }

        _uiState.update {
            it.copy(
                gameState = initialState,
                gameMode = mode,
                playerColorVsAi = actualPlayerColor,
                aiDifficulty = difficulty,
                timeSetting = time,
                player1Name = cleanP1,
                player2Name = cleanP2,
                autoRotateBoard = currentAutoRotate,
                whiteTimeMillis = initialMillis,
                blackTimeMillis = initialMillis,
                isTimerRunning = time != TimeSetting.UNLIMITED,
                isTimerPaused = false,
                isThinking = false,
                isBoardFlipped = initFlipped,
                hintMove = null,
                hintExplanation = null,
                showPromotionDialog = false,
                puzzleMessage = null,
                isPuzzleSolved = false,
                trainingMessage = null,
                isTrainingCompleted = false,
                canUndo = false,
                canRedo = false
            )
        }

        if (time != TimeSetting.UNLIMITED) {
            startTimer()
        }

        // If AI plays White, trigger AI move
        if (mode == GameMode.VS_AI && actualPlayerColor == PieceColor.BLACK) {
            triggerAiMove(initialState)
        }
    }

    fun loadPuzzle(index: Int) {
        val puzzles = ChessPuzzleRepository.puzzles
        if (index !in puzzles.indices) return

        aiJob?.cancel()
        timerJob?.cancel()
        undoStack.clear()
        redoStack.clear()

        val puzzle = puzzles[index]
        val state = ChessEngine.fromFen(puzzle.fen)

        _uiState.update {
            it.copy(
                gameState = state,
                gameMode = GameMode.PUZZLES,
                currentPuzzleIndex = index,
                isBoardFlipped = puzzle.turn == PieceColor.BLACK,
                hintMove = null,
                hintExplanation = null,
                puzzleMessage = null,
                isPuzzleSolved = false,
                canUndo = false,
                canRedo = false
            )
        }
    }

    fun loadDailyPuzzle() {
        val daily = ChessPuzzleRepository.getDailyPuzzle()
        val index = ChessPuzzleRepository.puzzles.indexOf(daily).coerceAtLeast(0)
        loadPuzzle(index)
    }

    fun nextPuzzle() {
        val nextIdx = (_uiState.value.currentPuzzleIndex + 1) % ChessPuzzleRepository.puzzles.size
        loadPuzzle(nextIdx)
    }

    fun prevPuzzle() {
        val prevIdx = if (_uiState.value.currentPuzzleIndex > 0) {
            _uiState.value.currentPuzzleIndex - 1
        } else {
            ChessPuzzleRepository.puzzles.size - 1
        }
        loadPuzzle(prevIdx)
    }

    fun loadTrainingLesson(index: Int) {
        val lessons = TrainingRepository.lessons
        if (index !in lessons.indices) return

        aiJob?.cancel()
        timerJob?.cancel()
        undoStack.clear()
        redoStack.clear()

        val lesson = lessons[index]
        val state = ChessEngine.fromFen(lesson.initialFen)

        _uiState.update {
            it.copy(
                gameState = state,
                gameMode = GameMode.TRAINING,
                currentTrainingIndex = index,
                isBoardFlipped = lesson.turn == PieceColor.BLACK,
                hintMove = null,
                hintExplanation = null,
                trainingMessage = null,
                isTrainingCompleted = false,
                canUndo = false,
                canRedo = false
            )
        }
    }

    fun nextTrainingLesson() {
        val nextIdx = (_uiState.value.currentTrainingIndex + 1) % TrainingRepository.lessons.size
        loadTrainingLesson(nextIdx)
    }

    fun prevTrainingLesson() {
        val prevIdx = if (_uiState.value.currentTrainingIndex > 0) {
            _uiState.value.currentTrainingIndex - 1
        } else {
            TrainingRepository.lessons.size - 1
        }
        loadTrainingLesson(prevIdx)
    }

    fun onSquareClicked(square: Square) {
        val currentState = _uiState.value.gameState
        if (currentState.isGameOver || _uiState.value.isThinking || _uiState.value.isTimerPaused) return

        // In VS_AI mode, ignore click if it's AI's turn
        if (_uiState.value.gameMode == GameMode.VS_AI && currentState.turn != _uiState.value.playerColorVsAi) {
            return
        }

        val selected = currentState.selectedSquare

        if (selected == null) {
            val piece = currentState.pieces[square]
            if (piece != null && piece.color == currentState.turn) {
                val legalMoves = ChessEngine.generateLegalMoves(currentState).filter { it.from == square }
                _uiState.update {
                    it.copy(
                        gameState = currentState.copy(
                            selectedSquare = square,
                            legalMovesForSelected = legalMoves
                        )
                    )
                }
            }
        } else {
            val clickedPiece = currentState.pieces[square]
            if (clickedPiece != null && clickedPiece.color == currentState.turn) {
                val legalMoves = ChessEngine.generateLegalMoves(currentState).filter { it.from == square }
                _uiState.update {
                    it.copy(
                        gameState = currentState.copy(
                            selectedSquare = square,
                            legalMovesForSelected = legalMoves
                        )
                    )
                }
                return
            }

            val move = currentState.legalMovesForSelected.firstOrNull { it.to == square }
            if (move != null) {
                val isPawnPromotion = move.piece.type == PieceType.PAWN &&
                    ((move.piece.color == PieceColor.WHITE && square.rank == 7) ||
                     (move.piece.color == PieceColor.BLACK && square.rank == 0))

                if (isPawnPromotion && move.promotion == null) {
                    _uiState.update {
                        it.copy(
                            gameState = currentState.copy(pendingPromotion = move),
                            showPromotionDialog = true
                        )
                    }
                    return
                }

                executePlayerMove(move)
            } else {
                _uiState.update {
                    it.copy(
                        gameState = currentState.copy(
                            selectedSquare = null,
                            legalMovesForSelected = emptyList()
                        )
                    )
                }
            }
        }
    }

    fun selectPromotion(promoType: PieceType) {
        val pending = _uiState.value.gameState.pendingPromotion ?: return
        val finalMove = pending.copy(promotion = promoType)
        _uiState.update {
            it.copy(
                showPromotionDialog = false,
                gameState = it.gameState.copy(pendingPromotion = null)
            )
        }
        executePlayerMove(finalMove)
    }

    private fun executePlayerMove(move: Move) {
        val prevState = _uiState.value.gameState
        undoStack.add(prevState)
        redoStack.clear()

        val nextState = ChessEngine.makeMove(prevState, move)

        // Add clock increment to the moving player
        val incMillis = _uiState.value.timeSetting.incrementSeconds * 1000L
        if (prevState.turn == PieceColor.WHITE) {
            _uiState.update { it.copy(whiteTimeMillis = it.whiteTimeMillis + incMillis) }
        } else {
            _uiState.update { it.copy(blackTimeMillis = it.blackTimeMillis + incMillis) }
        }

        // Sound & Haptics
        if (nextState.isCheckmate) {
            soundAndHaptics.playWinSound()
            soundAndHaptics.vibrateCheck()
        } else if (nextState.isCheck) {
            soundAndHaptics.playCheckSound()
            soundAndHaptics.vibrateCheck()
        } else if (move.isCapture) {
            soundAndHaptics.playCaptureSound()
            soundAndHaptics.vibrateCapture()
        } else {
            soundAndHaptics.playMoveSound()
            soundAndHaptics.vibrateMove()
        }

        // Training mode check
        if (_uiState.value.gameMode == GameMode.TRAINING) {
            val lesson = TrainingRepository.lessons[_uiState.value.currentTrainingIndex]
            val isTarget = lesson.goalMove == null || move.san == lesson.goalMove || move.to.algebraic == lesson.goalMove.takeLast(2)
            if (isTarget) {
                _uiState.update {
                    it.copy(
                        gameState = nextState,
                        trainingMessage = "Bagus sekali! ${lesson.keyRule}",
                        isTrainingCompleted = true,
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false
                    )
                }
                return
            } else {
                _uiState.update {
                    it.copy(
                        gameState = nextState,
                        trainingMessage = "Langkah lain dicoba. ${lesson.practicePrompt}",
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false
                    )
                }
                return
            }
        }

        // Puzzle mode check
        if (_uiState.value.gameMode == GameMode.PUZZLES) {
            val puzzle = ChessPuzzleRepository.puzzles[_uiState.value.currentPuzzleIndex]
            val expectedMove = puzzle.targetMoves.firstOrNull()
            if (expectedMove != null && (move.san == expectedMove || move.to.algebraic == expectedMove.takeLast(2))) {
                preferences.incrementPuzzleSolved()
                _uiState.update {
                    it.copy(
                        gameState = nextState,
                        puzzleMessage = "Tepat sekali! ${puzzle.explanation}",
                        isPuzzleSolved = true,
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false
                    )
                }
                return
            } else {
                _uiState.update {
                    it.copy(
                        gameState = nextState,
                        puzzleMessage = "Kurang tepat. Coba kembali atau gunakan petunjuk!",
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false
                    )
                }
                return
            }
        }

        // Game over check & save match record
        if (nextState.isGameOver) {
            val (result, resultDesc) = when {
                nextState.isCheckmate -> {
                    val winner = nextState.turn.opponent
                    if (winner == PieceColor.WHITE) "1-0" to "Putih Menang via Skakmat" else "0-1" to "Hitam Menang via Skakmat"
                }
                nextState.isStalemate -> "1/2-1/2" to "Remis (Paten/Stalemate)"
                else -> "1/2-1/2" to (nextState.drawReason ?: "Remis")
            }

            if (_uiState.value.gameMode == GameMode.VS_AI) {
                val playerColor = _uiState.value.playerColorVsAi
                val won = (result == "1-0" && playerColor == PieceColor.WHITE) || (result == "0-1" && playerColor == PieceColor.BLACK)
                val draw = result == "1/2-1/2"
                preferences.recordGameResult(if (won) "WIN" else if (draw) "DRAW" else "LOSS")
            }

            val pgn = ChessEngine.generatePgn(
                nextState,
                whitePlayer = if (_uiState.value.gameMode == GameMode.VS_AI && _uiState.value.playerColorVsAi == PieceColor.BLACK) "Chator AI" else "Pemain",
                blackPlayer = if (_uiState.value.gameMode == GameMode.VS_AI && _uiState.value.playerColorVsAi == PieceColor.WHITE) "Chator AI" else "Pemain",
                result = result
            )

            preferences.saveMatchRecord(
                MatchRecord(
                    id = UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis(),
                    mode = _uiState.value.gameMode.title,
                    playerColor = _uiState.value.playerColorVsAi.displayName,
                    opponentName = if (_uiState.value.gameMode == GameMode.VS_AI) "Chator AI (${_uiState.value.aiDifficulty.title})" else "Pemain 2",
                    result = result,
                    resultDescription = resultDesc,
                    totalMoves = nextState.moveHistory.size,
                    pgn = pgn
                )
            )

            _uiState.update { it.copy(lastCompletedMatchPgn = pgn) }
        }

        val newFlipped = if (_uiState.value.gameMode == GameMode.PASS_AND_PLAY) {
            if (_uiState.value.autoRotateBoard) nextState.turn == PieceColor.BLACK else false
        } else {
            _uiState.value.isBoardFlipped
        }

        _uiState.update {
            it.copy(
                gameState = nextState,
                isBoardFlipped = newFlipped,
                hintMove = null,
                hintExplanation = null,
                canUndo = undoStack.isNotEmpty(),
                canRedo = false
            )
        }

        // Trigger AI move if VS_AI
        if (_uiState.value.gameMode == GameMode.VS_AI && !nextState.isGameOver && nextState.turn != _uiState.value.playerColorVsAi) {
            triggerAiMove(nextState)
        }
    }

    private fun triggerAiMove(state: GameState) {
        aiJob?.cancel()
        _uiState.update { it.copy(isThinking = true) }

        aiJob = viewModelScope.launch {
            // Natural thinking delay based on difficulty
            val delayMs = when (_uiState.value.aiDifficulty) {
                AiDifficulty.EASY -> Random.nextLong(200, 450)
                AiDifficulty.MEDIUM -> Random.nextLong(400, 700)
                AiDifficulty.HARD -> Random.nextLong(600, 1000)
            }
            delay(delayMs)

            val bestMove = withContext(Dispatchers.Default) {
                ChessAI.getBestMove(state, _uiState.value.aiDifficulty)
            }

            if (bestMove != null) {
                val nextState = ChessEngine.makeMove(state, bestMove)

                // Add increment to AI
                val incMillis = _uiState.value.timeSetting.incrementSeconds * 1000L
                if (state.turn == PieceColor.WHITE) {
                    _uiState.update { it.copy(whiteTimeMillis = it.whiteTimeMillis + incMillis) }
                } else {
                    _uiState.update { it.copy(blackTimeMillis = it.blackTimeMillis + incMillis) }
                }

                if (nextState.isCheckmate) {
                    soundAndHaptics.playWinSound()
                    soundAndHaptics.vibrateCheck()
                } else if (nextState.isCheck) {
                    soundAndHaptics.playCheckSound()
                    soundAndHaptics.vibrateCheck()
                } else if (bestMove.isCapture) {
                    soundAndHaptics.playCaptureSound()
                    soundAndHaptics.vibrateCapture()
                } else {
                    soundAndHaptics.playMoveSound()
                    soundAndHaptics.vibrateMove()
                }

                if (nextState.isGameOver) {
                    val result = if (nextState.isCheckmate) {
                        if (nextState.turn.opponent == PieceColor.WHITE) "1-0" else "0-1"
                    } else "1/2-1/2"
                    val desc = if (nextState.isCheckmate) "Skakmat oleh ${nextState.turn.opponent.displayName}" else "Remis"

                    preferences.recordGameResult(if (nextState.isCheckmate) "LOSS" else "DRAW")

                    val pgn = ChessEngine.generatePgn(
                        nextState,
                        whitePlayer = if (_uiState.value.playerColorVsAi == PieceColor.WHITE) "Pemain" else "Chator AI",
                        blackPlayer = if (_uiState.value.playerColorVsAi == PieceColor.BLACK) "Pemain" else "Chator AI",
                        result = result
                    )

                    preferences.saveMatchRecord(
                        MatchRecord(
                            id = UUID.randomUUID().toString(),
                            timestamp = System.currentTimeMillis(),
                            mode = _uiState.value.gameMode.title,
                            playerColor = _uiState.value.playerColorVsAi.displayName,
                            opponentName = "Chator AI (${_uiState.value.aiDifficulty.title})",
                            result = result,
                            resultDescription = desc,
                            totalMoves = nextState.moveHistory.size,
                            pgn = pgn
                        )
                    )
                }

                _uiState.update {
                    it.copy(
                        gameState = nextState,
                        isThinking = false,
                        canUndo = undoStack.isNotEmpty(),
                        canRedo = false
                    )
                }
            } else {
                _uiState.update { it.copy(isThinking = false) }
            }
        }
    }

    fun undoMove() {
        if (undoStack.isEmpty() || _uiState.value.isThinking) return
        val current = _uiState.value.gameState
        redoStack.add(current)

        val targetState = if (_uiState.value.gameMode == GameMode.VS_AI && undoStack.size >= 2) {
            redoStack.add(undoStack.removeLast())
            undoStack.removeLast()
        } else {
            undoStack.removeLast()
        }

        _uiState.update {
            it.copy(
                gameState = targetState,
                hintMove = null,
                hintExplanation = null,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun redoMove() {
        if (redoStack.isEmpty() || _uiState.value.isThinking) return
        val current = _uiState.value.gameState
        undoStack.add(current)

        val targetState = if (_uiState.value.gameMode == GameMode.VS_AI && redoStack.size >= 2) {
            undoStack.add(redoStack.removeLast())
            redoStack.removeLast()
        } else {
            redoStack.removeLast()
        }

        _uiState.update {
            it.copy(
                gameState = targetState,
                hintMove = null,
                hintExplanation = null,
                canUndo = undoStack.isNotEmpty(),
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun requestHint() {
        val state = _uiState.value.gameState
        if (state.isGameOver || _uiState.value.isThinking) return

        viewModelScope.launch {
            val (move, explanation) = withContext(Dispatchers.Default) {
                ChessAI.getHint(state)
            }
            _uiState.update {
                it.copy(
                    hintMove = move,
                    hintExplanation = explanation
                )
            }
        }
    }

    fun toggleBoardFlip() {
        _uiState.update { it.copy(isBoardFlipped = !it.isBoardFlipped) }
    }

    fun togglePauseTimer() {
        _uiState.update { it.copy(isTimerPaused = !it.isTimerPaused) }
        if (!_uiState.value.isTimerPaused) {
            lastTickTime = System.currentTimeMillis()
        }
    }

    fun resignGame() {
        val state = _uiState.value.gameState
        if (state.isGameOver) return

        val nextState = state.copy(
            isCheckmate = true,
            drawReason = "Menyerah"
        )
        if (_uiState.value.gameMode == GameMode.VS_AI) {
            preferences.recordGameResult("LOSS")
        }
        _uiState.update { it.copy(gameState = nextState) }
    }

    fun offerDraw() {
        val state = _uiState.value.gameState
        if (state.isGameOver) return

        val nextState = state.copy(
            isDraw = true,
            drawReason = "Remis atas kesepakatan bersama"
        )
        if (_uiState.value.gameMode == GameMode.VS_AI) {
            preferences.recordGameResult("DRAW")
        }
        _uiState.update { it.copy(gameState = nextState) }
    }

    fun setBoardTheme(theme: BoardTheme) {
        preferences.boardTheme = theme
        _uiState.update { it.copy(boardTheme = theme) }
    }

    fun setAiDifficulty(difficulty: AiDifficulty) {
        preferences.aiDifficulty = difficulty
        _uiState.update { it.copy(aiDifficulty = difficulty) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        preferences.isSoundEnabled = enabled
        soundAndHaptics.isSoundEnabled = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        preferences.isHapticsEnabled = enabled
        soundAndHaptics.isHapticsEnabled = enabled
    }

    fun setAutoRotateBoard(enabled: Boolean) {
        preferences.autoRotateBoard = enabled
        _uiState.update {
            it.copy(
                autoRotateBoard = enabled,
                isBoardFlipped = if (!enabled && it.gameMode == GameMode.PASS_AND_PLAY) false else it.isBoardFlipped
            )
        }
    }

    fun toggleAutoRotateBoard() {
        setAutoRotateBoard(!_uiState.value.autoRotateBoard)
    }

    /**
     * Standard Chess Clock Rule:
     * When Player A presses their timer button, their timer stops (+ increment),
     * and the opponent's timer starts running, passing the active clock turn.
     */
    fun pressPlayerClock(playerColor: PieceColor) {
        val state = _uiState.value.gameState
        if (state.isGameOver || _uiState.value.isTimerPaused) return

        // In chess, a player only presses their own clock when it is their turn
        if (state.turn == playerColor) {
            val incMillis = _uiState.value.timeSetting.incrementSeconds * 1000L
            if (playerColor == PieceColor.WHITE) {
                _uiState.update { it.copy(whiteTimeMillis = it.whiteTimeMillis + incMillis) }
            } else {
                _uiState.update { it.copy(blackTimeMillis = it.blackTimeMillis + incMillis) }
            }

            soundAndHaptics.playMoveSound()
            soundAndHaptics.vibrateMove()

            val nextTurn = playerColor.opponent
            val newFlipped = if (_uiState.value.gameMode == GameMode.PASS_AND_PLAY) {
                if (_uiState.value.autoRotateBoard) nextTurn == PieceColor.BLACK else false
            } else {
                _uiState.value.isBoardFlipped
            }

            _uiState.update {
                it.copy(
                    gameState = it.gameState.copy(
                        turn = nextTurn,
                        selectedSquare = null,
                        legalMovesForSelected = emptyList()
                    ),
                    isBoardFlipped = newFlipped
                )
            }

            // In VS_AI mode, if next turn belongs to AI, trigger AI move
            if (_uiState.value.gameMode == GameMode.VS_AI && nextTurn != _uiState.value.playerColorVsAi) {
                triggerAiMove(_uiState.value.gameState)
            }
        }
    }

    fun getStats(): Map<String, Int> {
        return mapOf(
            "gamesPlayed" to preferences.gamesPlayed,
            "winsVsAi" to preferences.winsVsAi,
            "lossesVsAi" to preferences.lossesVsAi,
            "drawsVsAi" to preferences.drawsVsAi,
            "puzzlesSolved" to preferences.puzzlesSolved
        )
    }

    fun getMatchHistory(): List<MatchRecord> {
        return preferences.getMatchHistory()
    }

    fun resetStats() {
        preferences.resetStats()
    }

    private fun startTimer() {
        timerJob?.cancel()
        lastTickTime = System.currentTimeMillis()

        timerJob = viewModelScope.launch {
            while (true) {
                delay(100) // check every 100ms for precision
                val now = System.currentTimeMillis()
                val elapsed = now - lastTickTime
                lastTickTime = now

                val state = _uiState.value.gameState
                if (state.isGameOver || !_uiState.value.isTimerRunning || _uiState.value.isTimerPaused) {
                    continue
                }

                if (state.turn == PieceColor.WHITE) {
                    val remaining = _uiState.value.whiteTimeMillis - elapsed
                    if (remaining <= 0) {
                        _uiState.update {
                            it.copy(
                                whiteTimeMillis = 0,
                                gameState = state.copy(
                                    isCheckmate = true,
                                    drawReason = "Waktu Putih Habis"
                                )
                            )
                        }
                        soundAndHaptics.playWinSound()
                        break
                    } else {
                        _uiState.update { it.copy(whiteTimeMillis = remaining) }
                    }
                } else {
                    val remaining = _uiState.value.blackTimeMillis - elapsed
                    if (remaining <= 0) {
                        _uiState.update {
                            it.copy(
                                blackTimeMillis = 0,
                                gameState = state.copy(
                                    isCheckmate = true,
                                    drawReason = "Waktu Hitam Habis"
                                )
                            )
                        }
                        soundAndHaptics.playWinSound()
                        break
                    } else {
                        _uiState.update { it.copy(blackTimeMillis = remaining) }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundAndHaptics.release()
    }
}
