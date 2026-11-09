package com.example.chess.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType

@Composable
fun ChessPieceView(
    piece: Piece,
    modifier: Modifier = Modifier,
    rotate180: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (rotate180) Modifier.graphicsLayer { rotationZ = 180f } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        val symbol = when (piece.type) {
            PieceType.KING -> "♚"
            PieceType.QUEEN -> "♛"
            PieceType.ROOK -> "♜"
            PieceType.BISHOP -> "♝"
            PieceType.KNIGHT -> "♞"
            PieceType.PAWN -> "♟"
        }

        val isWhite = piece.color == PieceColor.WHITE

        if (isWhite) {
            // White piece: High-contrast solid ivory/white with multi-directional dark outline
            val strokeColor = Color(0xFF0F172A)
            val strokeOffsets = listOf(
                Offset(-2f, -2f),
                Offset(2f, -2f),
                Offset(-2f, 2f),
                Offset(2f, 2f),
                Offset(-2.5f, 0f),
                Offset(2.5f, 0f),
                Offset(0f, -2.5f),
                Offset(0f, 2.5f)
            )

            // Outline layers for maximum visibility against cream/light squares
            for (off in strokeOffsets) {
                Text(
                    text = symbol,
                    style = TextStyle(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = strokeColor,
                        shadow = Shadow(
                            color = strokeColor,
                            offset = off,
                            blurRadius = 1.5f
                        )
                    )
                )
            }

            // Foreground pure brilliant white piece
            Text(
                text = symbol,
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFFFFF),
                    shadow = Shadow(
                        color = Color(0x66000000),
                        offset = Offset(2f, 3f),
                        blurRadius = 4f
                    )
                )
            )
        } else {
            // Black piece: Solid obsidian/charcoal with sleek light rim and shadow
            val rimColor = Color(0x77CBD5E1)
            val rimOffsets = listOf(
                Offset(-1.2f, -1.2f),
                Offset(1.2f, -1.2f),
                Offset(-1.2f, 1.2f),
                Offset(1.2f, 1.2f)
            )

            for (off in rimOffsets) {
                Text(
                    text = symbol,
                    style = TextStyle(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A),
                        shadow = Shadow(
                            color = rimColor,
                            offset = off,
                            blurRadius = 2f
                        )
                    )
                )
            }

            // Foreground deep black piece
            Text(
                text = symbol,
                style = TextStyle(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF18181B),
                    shadow = Shadow(
                        color = Color(0x88000000),
                        offset = Offset(2f, 3f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}
