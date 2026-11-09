package com.example.chess.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.BoardTheme
import com.example.chess.model.GameState
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.Square

@Composable
fun ChessBoardView(
    gameState: GameState,
    boardTheme: BoardTheme,
    isFlipped: Boolean,
    hintMove: Move?,
    onSquareClicked: (Square) -> Unit,
    modifier: Modifier = Modifier,
    rotateBlackPieces: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(boardTheme.darkSquare)
            .padding(4.dp)
            .testTag("chess_board")
    ) {
        val squareSize = maxWidth / 8

        Box(modifier = Modifier.fillMaxSize()) {
            for (displayRank in 0..7) {
                for (displayFile in 0..7) {
                    val file = if (isFlipped) 7 - displayFile else displayFile
                    val rank = if (isFlipped) displayRank else 7 - displayRank
                    val square = Square(file, rank)

                    val isLight = square.isLight
                    val isSelected = gameState.selectedSquare == square
                    val isLastMoveFrom = gameState.lastMove?.from == square
                    val isLastMoveTo = gameState.lastMove?.to == square
                    val isKingInCheck = gameState.isCheck && gameState.currentKingSquare == square
                    val isHintFrom = hintMove?.from == square
                    val isHintTo = hintMove?.to == square

                    val baseColor = if (isLight) boardTheme.lightSquare else boardTheme.darkSquare
                    val highlightColor = if (isLight) boardTheme.lightSquareHighlight else boardTheme.darkSquareHighlight

                    val targetColor = when {
                        isKingInCheck -> Color(0xFFEF4444)
                        isSelected -> Color(0xFFFACC15)
                        isHintTo || isHintFrom -> Color(0xFF10B981)
                        isLastMoveFrom || isLastMoveTo -> highlightColor
                        else -> baseColor
                    }

                    val animatedBg by animateColorAsState(targetValue = targetColor, label = "sqBg")

                    val xOffset = squareSize * displayFile
                    val yOffset = squareSize * displayRank

                    val moveForSquare = gameState.legalMovesForSelected.firstOrNull { it.to == square }
                    val isValidTarget = moveForSquare != null
                    val isCaptureTarget = isValidTarget && (moveForSquare?.isCapture == true || moveForSquare?.isEnPassant == true)

                    val piece = gameState.pieces[square]

                    Box(
                        modifier = Modifier
                            .offset(x = xOffset, y = yOffset)
                            .size(squareSize)
                            .background(animatedBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSquareClicked(square)
                            }
                            .testTag("square_${square.algebraic}"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Coordinates labels
                        if (displayRank == 7) {
                            Text(
                                text = ('a' + file).toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) boardTheme.darkSquare else boardTheme.lightSquare,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 2.dp, bottom = 1.dp)
                            )
                        }
                        if (displayFile == 0) {
                            Text(
                                text = (rank + 1).toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLight) boardTheme.darkSquare else boardTheme.lightSquare,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 2.dp, top = 1.dp)
                            )
                        }

                        // Piece
                        if (piece != null) {
                            val shouldRotate = rotateBlackPieces && piece.color == PieceColor.BLACK && !isFlipped
                            ChessPieceView(
                                piece = piece,
                                rotate180 = shouldRotate
                            )
                        }

                        // Valid move indicators
                        if (isValidTarget) {
                            if (isCaptureTarget) {
                                // Red/Dark Ring for capture
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(0.85f)
                                        .border(3.5.dp, Color(0x99000000), CircleShape)
                                )
                            } else {
                                // Dot for quiet move
                                Box(
                                    modifier = Modifier
                                        .size(squareSize * 0.32f)
                                        .background(Color(0x55000000), CircleShape)
                                )
                            }
                        }

                        // Selected border
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(2.dp, Color(0xFFEAB308), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}
