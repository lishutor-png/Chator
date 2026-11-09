package com.example

import com.example.chess.ai.ChessAI
import com.example.chess.engine.ChessEngine
import com.example.chess.model.AiDifficulty
import com.example.chess.model.PieceColor
import com.example.chess.model.Square
import com.example.chess.puzzles.ChessPuzzleRepository
import com.example.chess.training.TrainingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testInitialBoardSetup() {
    val state = ChessEngine.newGame()
    assertEquals(32, state.pieces.size)
    assertEquals(PieceColor.WHITE, state.turn)
    assertFalse(state.isCheck)
    assertFalse(state.isCheckmate)
    assertFalse(state.isDraw)
  }

  @Test
  fun testInitialLegalMovesCount() {
    val state = ChessEngine.newGame()
    val legalMoves = ChessEngine.generateLegalMoves(state)
    // 16 pawn moves (8 single + 8 double) + 4 knight moves = 20 legal opening moves
    assertEquals(20, legalMoves.size)
  }

  @Test
  fun testPawnMoveExecution() {
    val state = ChessEngine.newGame()
    val e2 = Square.fromAlgebraic("e2")
    val e4 = Square.fromAlgebraic("e4")
    val e2e4Move = ChessEngine.generateLegalMoves(state).first { it.from == e2 && it.to == e4 }

    val nextState = ChessEngine.makeMove(state, e2e4Move)
    assertEquals(PieceColor.BLACK, nextState.turn)
    assertEquals(Square.fromAlgebraic("e3"), nextState.enPassantTarget)
    assertEquals("e4", nextState.lastMove?.san)
  }

  @Test
  fun testChessAiGeneratesValidMoveForAllDifficulties() {
    val state = ChessEngine.newGame()
    listOf(AiDifficulty.EASY, AiDifficulty.MEDIUM, AiDifficulty.HARD).forEach { diff ->
      val bestMove = ChessAI.getBestMove(state, diff)
      assertNotNull("Move should not be null for $diff", bestMove)
      val legalMoves = ChessEngine.generateLegalMoves(state)
      assertTrue(legalMoves.contains(bestMove))
    }
  }

  @Test
  fun testFenLoadingAndCheckmateDetection() {
    // Scholar's Mate position after Qxf7#
    val fen = "r1bqkb1r/pppp1Qpp/2n5/4p3/2B1n3/8/PPPP1PPP/RNB1K1NR b KQkq - 0 1"
    val state = ChessEngine.fromFen(fen)
    assertTrue(state.isCheck)
    assertTrue(state.isCheckmate)
  }

  @Test
  fun testPgnGeneration() {
    val state = ChessEngine.newGame()
    val e2 = Square.fromAlgebraic("e2")
    val e4 = Square.fromAlgebraic("e4")
    val e2e4Move = ChessEngine.generateLegalMoves(state).first { it.from == e2 && it.to == e4 }
    val nextState = ChessEngine.makeMove(state, e2e4Move)

    val pgn = ChessEngine.generatePgn(nextState, "Player 1", "Chator AI", "*")
    assertTrue(pgn.contains("[Event \"Chator Mobile Match\"]"))
    assertTrue(pgn.contains("1. e4"))
  }

  @Test
  fun testTrainingLessonsAvailable() {
    val lessons = TrainingRepository.lessons
    assertTrue(lessons.size >= 15)
    val categories = lessons.map { it.category }.distinct()
    assertTrue(categories.contains("Pergerakan Bidak"))
    assertTrue(categories.contains("Check & Checkmate"))
    assertTrue(categories.contains("Aturan Khusus"))
    assertTrue(categories.contains("Taktik Catur"))
  }

  @Test
  fun testDailyPuzzleAvailable() {
    val daily = ChessPuzzleRepository.getDailyPuzzle()
    assertNotNull(daily)
    assertTrue(daily.targetMoves.isNotEmpty())
  }
}
