package com.example.chess.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.GameMode
import com.example.chess.model.PieceColor
import com.example.chess.puzzles.ChessPuzzleRepository
import com.example.chess.ui.ChessBoardView
import com.example.chess.ui.GameViewModel

@Composable
fun PuzzlesScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val puzzles = ChessPuzzleRepository.puzzles
    val currentPuzzle = puzzles.getOrNull(uiState.currentPuzzleIndex) ?: puzzles.first()

    var selectedDifficulty by remember { mutableStateOf("All") }
    var showHint by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (uiState.gameMode != GameMode.PUZZLES) {
            viewModel.loadPuzzle(0)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // FILTER & DAILY PUZZLE BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("All" to "Semua", "Easy" to "Easy", "Medium" to "Medium", "Hard" to "Hard").forEach { (key, label) ->
                FilterChip(
                    selected = selectedDifficulty == key,
                    onClick = {
                        selectedDifficulty = key
                        val list = ChessPuzzleRepository.getPuzzlesByDifficulty(key)
                        val target = list.firstOrNull() ?: puzzles.first()
                        viewModel.loadPuzzle(puzzles.indexOf(target))
                    },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }

            FilterChip(
                selected = false,
                onClick = {
                    viewModel.loadDailyPuzzle()
                },
                leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(14.dp))
                },
                label = { Text("Puzzle Harian", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("daily_puzzle_chip")
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // PUZZLE HEADER CARD
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Puzzle #${currentPuzzle.id}: ${currentPuzzle.title}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = currentPuzzle.theme,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val diffColor = when (currentPuzzle.difficulty) {
                        "Easy" -> Color(0xFF10B981)
                        "Medium" -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    }

                    SuggestionChip(
                        onClick = {},
                        label = {
                            Text(
                                text = currentPuzzle.difficulty,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = diffColor
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val turnText = if (currentPuzzle.turn == PieceColor.WHITE) "Giliran Putih Melangkah" else "Giliran Hitam Melangkah"
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (currentPuzzle.turn == PieceColor.WHITE) Color.White else Color.Black,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = turnText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // CHESS BOARD
        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth(),
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
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // PUZZLE MESSAGE BANNER
        if (uiState.puzzleMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isPuzzleSolved) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.isPuzzleSolved) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (uiState.isPuzzleSolved) Color(0xFF16A34A) else Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.puzzleMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (uiState.isPuzzleSolved) Color(0xFF166534) else Color(0xFF991B1B)
                    )
                }
            }
        }

        // HINT TEXT
        AnimatedVisibility(visible = showHint) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Petunjuk: ${currentPuzzle.hint}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // NAVIGATION & ACTION CONTROLS
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        showHint = false
                        viewModel.prevPuzzle()
                    },
                    modifier = Modifier.testTag("puzzle_prev")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sebelumnya")
                }

                OutlinedButton(
                    onClick = {
                        showHint = false
                        viewModel.loadPuzzle(uiState.currentPuzzleIndex)
                    },
                    modifier = Modifier.testTag("puzzle_reset")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ulangi", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showHint = !showHint },
                    modifier = Modifier.testTag("puzzle_hint")
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Petunjuk", fontSize = 12.sp)
                }

                IconButton(
                    onClick = {
                        showHint = false
                        viewModel.nextPuzzle()
                    },
                    modifier = Modifier.testTag("puzzle_next")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Berikutnya")
                }
            }
        }
    }
}
