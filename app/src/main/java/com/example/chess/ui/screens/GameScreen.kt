package com.example.chess.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.GameMode
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.TimeSetting
import com.example.chess.ui.ChessBoardView
import com.example.chess.ui.GameViewModel
import com.example.chess.ui.components.GameOverDialog
import com.example.chess.ui.components.NewGameDialog
import com.example.chess.ui.components.PromotionDialog

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showNewGameDialog by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showGameOverDialog by remember { mutableStateOf(false) }

    if (uiState.gameState.isGameOver && !showGameOverDialog) {
        showGameOverDialog = true
    }

    if (showNewGameDialog) {
        NewGameDialog(
            initialMode = uiState.gameMode,
            initialDifficulty = uiState.aiDifficulty,
            initialColor = uiState.playerColorVsAi,
            initialTime = uiState.timeSetting,
            initialAutoRotate = uiState.autoRotateBoard,
            onStartGame = { mode, color, diff, time, customMin, customInc, autoRotate ->
                viewModel.startNewGame(
                    mode = mode,
                    playerColor = color,
                    difficulty = diff,
                    time = time,
                    customMinutes = customMin,
                    customIncrement = customInc,
                    autoRotate = autoRotate
                )
                showNewGameDialog = false
                showGameOverDialog = false
            },
            onDismiss = { showNewGameDialog = false }
        )
    }

    if (uiState.showPromotionDialog) {
        PromotionDialog(
            playerColor = uiState.gameState.pendingPromotion?.piece?.color ?: PieceColor.WHITE,
            onSelectPiece = { promoType ->
                viewModel.selectPromotion(promoType)
            }
        )
    }

    if (showGameOverDialog && uiState.gameState.isGameOver) {
        GameOverDialog(
            gameState = uiState.gameState,
            onPlayAgain = {
                showGameOverDialog = false
                viewModel.startNewGame()
            },
            onDismiss = { showGameOverDialog = false }
        )
    }

    // Top player info (Player 2 / Black sitting across the table)
    val isTopBlack = !uiState.isBoardFlipped
    val topColor = if (isTopBlack) PieceColor.BLACK else PieceColor.WHITE
    val topName = when (uiState.gameMode) {
        GameMode.VS_AI -> "Chator AI (${uiState.aiDifficulty.title})"
        GameMode.PASS_AND_PLAY -> if (isTopBlack) uiState.player2Name else uiState.player1Name
        GameMode.PUZZLES -> "Puzzle Mode"
        GameMode.TRAINING -> "Training Mode"
        GameMode.ANALYSIS -> "Analisis"
    }
    val topCaptured = if (isTopBlack) uiState.gameState.capturedByBlack else uiState.gameState.capturedByWhite
    val topIsTurn = uiState.gameState.turn == topColor && !uiState.gameState.isGameOver
    val topTime = if (isTopBlack) uiState.blackTimeMillis else uiState.whiteTimeMillis

    // Bottom player info (Player 1 / White sitting on this side)
    val bottomColor = topColor.opponent
    val bottomName = when (uiState.gameMode) {
        GameMode.VS_AI -> "${uiState.player1Name} (${bottomColor.displayName})"
        GameMode.PASS_AND_PLAY -> if (bottomColor == PieceColor.WHITE) uiState.player1Name else uiState.player2Name
        GameMode.PUZZLES -> uiState.player1Name
        GameMode.TRAINING -> uiState.player1Name
        GameMode.ANALYSIS -> "Giliran ${bottomColor.displayName}"
    }
    val bottomCaptured = if (bottomColor == PieceColor.WHITE) uiState.gameState.capturedByWhite else uiState.gameState.capturedByBlack
    val bottomIsTurn = uiState.gameState.turn == bottomColor && !uiState.gameState.isGameOver
    val bottomTime = if (bottomColor == PieceColor.WHITE) uiState.whiteTimeMillis else uiState.blackTimeMillis

    val hasTimer = uiState.timeSetting != TimeSetting.UNLIMITED

    // Tabletop mode: In 2-Player Fixed Board, two players sit across each other.
    // Flip Black pieces and top player's clock UI so they face directly toward Player 2!
    val isTabletopMode = uiState.gameMode == GameMode.PASS_AND_PLAY && !uiState.autoRotateBoard

    // Full Screen Clean Layout
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP FLOATING HEADER: Back button, Match status pill & Quick actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Floating Back Button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E293B).copy(alpha = 0.85f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0x33FFFFFF))
                    ),
                    modifier = Modifier.size(38.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Menu",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Match Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        uiState.gameState.isCheck -> Color(0xFFEF4444)
                        uiState.isTimerPaused -> Color(0xFFF59E0B)
                        else -> Color(0xFF1E293B).copy(alpha = 0.9f)
                    },
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (uiState.gameState.isCheck) Color.White else Color(0xFF10B981)
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val statusText = when {
                            uiState.gameState.isCheckmate -> "SKAKMAT!"
                            uiState.gameState.isDraw -> "REMIS"
                            uiState.gameState.isCheck -> "SKAK!"
                            uiState.isTimerPaused -> "PAUSE"
                            uiState.gameState.turn == PieceColor.WHITE -> "Giliran Putih"
                            else -> "Giliran Hitam"
                        }
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // More Menu Button (Top Right)
                Box {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF1E293B).copy(alpha = 0.85f),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0x33FFFFFF))
                        ),
                        modifier = Modifier.size(38.dp)
                    ) {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier.testTag("action_more_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu Opsi",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Game Baru / Reset") },
                            leadingIcon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                showNewGameDialog = true
                            }
                        )
                        if (uiState.gameMode == GameMode.PASS_AND_PLAY) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (uiState.autoRotateBoard) "Mode: Papan Tetap (Meja)" else "Mode: Putar Bergantian (Oper HP)"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (uiState.autoRotateBoard) Icons.Default.StayCurrentPortrait else Icons.Default.ScreenRotation,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.toggleAutoRotateBoard()
                                    val msg = if (uiState.autoRotateBoard) "Mode: Papan Tetap (Putih di bawah, Hitam di atas)" else "Mode: Putar Bergantian tiap giliran"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Tawaran Remis") },
                            leadingIcon = { Icon(Icons.Default.Handshake, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                viewModel.offerDraw()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Menyerah (Resign)") },
                            leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                viewModel.resignGame()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Salin PGN") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                val pgn = com.example.chess.engine.ChessEngine.generatePgn(uiState.gameState)
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Chator PGN", pgn))
                                Toast.makeText(context, "PGN berhasil disalin ke papan klip!", Toast.LENGTH_SHORT).show()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Putar Papan Manual") },
                            leadingIcon = { Icon(Icons.Default.ScreenRotation, contentDescription = null) },
                            onClick = {
                                showMoreMenu = false
                                viewModel.toggleBoardFlip()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // TOP PLAYER FLOATING CLOCK / TIMER BUTTON (Player 2 / Black Area)
            // In Tabletop Mode, rotated 180° so text, time, and badges face upright towards Player 2 sitting across the table!
            FloatingPlayerClockButton(
                name = topName,
                color = topColor,
                isCurrentTurn = topIsTurn,
                isThinking = uiState.isThinking && uiState.gameMode == GameMode.VS_AI,
                isCheck = uiState.gameState.isCheck && topIsTurn,
                capturedPieces = topCaptured,
                timeMillis = topTime,
                hasTimer = hasTimer,
                onClockClicked = {
                    if (!uiState.gameState.isGameOver && !uiState.isTimerPaused) {
                        viewModel.pressPlayerClock(topColor)
                    }
                },
                rotate180 = isTabletopMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("top_player_clock")
            )

            Spacer(modifier = Modifier.height(2.dp))

            // CENTER: FULL-SCREEN CHESS BOARD (Maximum width & clear square view)
            // In Tabletop Mode, Black pieces are rotated 180° so their heads/silhouettes point toward Player 2!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                ChessBoardView(
                    gameState = uiState.gameState,
                    boardTheme = uiState.boardTheme,
                    isFlipped = uiState.isBoardFlipped,
                    hintMove = uiState.hintMove,
                    onSquareClicked = { square ->
                        viewModel.onSquareClicked(square)
                    },
                    rotateBlackPieces = isTabletopMode,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // BOTTOM PLAYER FLOATING CLOCK / TIMER BUTTON (Player 1 / White Area)
            FloatingPlayerClockButton(
                name = bottomName,
                color = bottomColor,
                isCurrentTurn = bottomIsTurn,
                isThinking = false,
                isCheck = uiState.gameState.isCheck && bottomIsTurn,
                capturedPieces = bottomCaptured,
                timeMillis = bottomTime,
                hasTimer = hasTimer,
                onClockClicked = {
                    if (!uiState.gameState.isGameOver && !uiState.isTimerPaused) {
                        viewModel.pressPlayerClock(bottomColor)
                    }
                },
                rotate180 = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_player_clock")
            )

            // Hint Explanation Tooltip Banner (if active)
            AnimatedVisibility(
                visible = uiState.hintExplanation != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.95f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.hintExplanation ?: "",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // MINIMALIST FLOATING ACTION CAPSULE (Unobtrusive & Clean)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.9f),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0x44FFFFFF))
                ),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .height(48.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // New Game
                    IconButton(
                        onClick = { showNewGameDialog = true },
                        modifier = Modifier.testTag("action_new_game")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Game Baru",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // Pause / Resume Timer
                    if (hasTimer && !uiState.gameState.isGameOver) {
                        IconButton(
                            onClick = { viewModel.togglePauseTimer() },
                            modifier = Modifier.testTag("action_pause_timer")
                        ) {
                            Icon(
                                imageVector = if (uiState.isTimerPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (uiState.isTimerPaused) "Resume" else "Pause",
                                tint = if (uiState.isTimerPaused) Color(0xFFF59E0B) else Color.White
                            )
                        }
                    }

                    // Undo
                    IconButton(
                        onClick = { viewModel.undoMove() },
                        enabled = uiState.canUndo,
                        modifier = Modifier.testTag("action_undo")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Urungkan",
                            tint = if (uiState.canUndo) Color.White else Color(0x55FFFFFF)
                        )
                    }

                    // Redo
                    IconButton(
                        onClick = { viewModel.redoMove() },
                        enabled = uiState.canRedo,
                        modifier = Modifier.testTag("action_redo")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Ulangi",
                            tint = if (uiState.canRedo) Color.White else Color(0x55FFFFFF)
                        )
                    }

                    // Hint
                    IconButton(
                        onClick = { viewModel.requestHint() },
                        enabled = !uiState.gameState.isGameOver && !uiState.isThinking,
                        modifier = Modifier.testTag("action_hint")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Petunjuk",
                            tint = if (uiState.hintMove != null) Color(0xFF10B981) else Color.White
                        )
                    }

                    // Board Orientation / Rotation Toggle
                    IconButton(
                        onClick = {
                            if (uiState.gameMode == GameMode.PASS_AND_PLAY) {
                                viewModel.toggleAutoRotateBoard()
                                val msg = if (uiState.autoRotateBoard) "Mode: Papan Tetap (Putih di bawah, Hitam di atas)" else "Mode: Putar Bergantian tiap giliran"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.toggleBoardFlip()
                            }
                        },
                        modifier = Modifier.testTag("action_flip_board")
                    ) {
                        Icon(
                            imageVector = if (uiState.gameMode == GameMode.PASS_AND_PLAY && !uiState.autoRotateBoard) {
                                Icons.Default.StayCurrentPortrait
                            } else {
                                Icons.Default.ScreenRotation
                            },
                            contentDescription = "Orientasi Papan",
                            tint = if (uiState.gameMode == GameMode.PASS_AND_PLAY && !uiState.autoRotateBoard) {
                                Color(0xFF10B981)
                            } else {
                                Color.White
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern floating chess clock button component.
 * Placed cleanly above and below the board area for each player.
 * Supports 180° rotation for tabletop mode so text and timer face Player 2 across the table.
 */
@Composable
private fun FloatingPlayerClockButton(
    name: String,
    color: PieceColor,
    isCurrentTurn: Boolean,
    isThinking: Boolean,
    isCheck: Boolean = false,
    capturedPieces: List<Piece>,
    timeMillis: Long,
    hasTimer: Boolean,
    onClockClicked: () -> Unit,
    rotate180: Boolean = false,
    modifier: Modifier = Modifier
) {
    val totalSecs = (timeMillis / 1000).coerceAtLeast(0)
    val mins = totalSecs / 60
    val secs = totalSecs % 60
    val isUrgent = totalSecs < 30 && hasTimer

    val timeString = if (hasTimer) {
        if (totalSecs < 10) {
            val tenths = ((timeMillis % 1000) / 100).coerceAtLeast(0)
            "%d.%d".format(secs, tenths)
        } else {
            "%02d:%02d".format(mins, secs)
        }
    } else {
        "∞"
    }

    val targetBgColor = when {
        isUrgent && isCurrentTurn -> Color(0xFFB91C1C)
        isCurrentTurn -> Color(0xFF065F46) // Emerald active container
        else -> Color(0xFF1E293B) // Dark inactive card
    }
    val animatedBgColor by animateColorAsState(targetValue = targetBgColor, label = "clockBg")

    val targetBorderColor = when {
        isUrgent && isCurrentTurn -> Color(0xFFEF4444)
        isCurrentTurn -> Color(0xFF10B981) // Glowing emerald outline
        else -> Color(0x33FFFFFF)
    }
    val animatedBorderColor by animateColorAsState(targetValue = targetBorderColor, label = "clockBorder")

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = animatedBgColor,
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(animatedBorderColor)
        ),
        shadowElevation = if (isCurrentTurn) 8.dp else 2.dp,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .then(if (rotate180) Modifier.graphicsLayer { rotationZ = 180f } else Modifier)
            .clickable(onClick = onClockClicked)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Player identity & captured pieces
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Piece badge (White or Black King icon)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            if (color == PieceColor.WHITE) Color(0xFFF8FAFC) else Color(0xFF0F172A),
                            CircleShape
                        )
                        .border(
                            1.5.dp,
                            if (color == PieceColor.WHITE) Color(0xFFCBD5E1) else Color(0xFF475569),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "♚",
                        fontSize = 18.sp,
                        color = if (color == PieceColor.WHITE) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        if (isThinking) {
                            Spacer(modifier = Modifier.width(6.dp))
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFF10B981),
                                strokeWidth = 2.dp
                            )
                        }
                        if (isCurrentTurn) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (isCheck) Color(0xFFEF4444) else Color(0xFF10B981).copy(alpha = 0.35f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isCheck) "SKAK!" else "GILIRAN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isCheck) Color.White else Color(0xFF6EE7B7),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    // Captured Pieces row
                    if (capturedPieces.isNotEmpty()) {
                        Row(modifier = Modifier.padding(top = 2.dp)) {
                            val grouped = capturedPieces.groupBy { it.type }
                            grouped.entries.forEach { entry ->
                                val sym = when (entry.key) {
                                    PieceType.QUEEN -> "♛"
                                    PieceType.ROOK -> "♜"
                                    PieceType.BISHOP -> "♝"
                                    PieceType.KNIGHT -> "♞"
                                    PieceType.PAWN -> "♟"
                                    PieceType.KING -> ""
                                }
                                Text(
                                    text = "$sym${if (entry.value.size > 1) entry.value.size else ""}",
                                    fontSize = 11.sp,
                                    color = Color(0xCCFFFFFF),
                                    modifier = Modifier.padding(end = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Big Modern Digital Chess Clock with active pulse dot
            Surface(
                color = when {
                    isUrgent && isCurrentTurn -> Color(0xFFEF4444)
                    isCurrentTurn -> Color(0xFF047857)
                    else -> Color(0xFF0F172A).copy(alpha = 0.8f)
                },
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (isCurrentTurn) Color(0xFF34D399) else Color(0x22FFFFFF)
                    )
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (isCurrentTurn) {
                        Box(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(7.dp)
                                .background(Color(0xFF34D399), CircleShape)
                        )
                    }
                    Text(
                        text = timeString,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp,
                        color = when {
                            isUrgent && isCurrentTurn -> Color.White
                            isCurrentTurn -> Color(0xFF6EE7B7)
                            else -> Color(0xFFE2E8F0)
                        }
                    )
                }
            }
        }
    }
}
