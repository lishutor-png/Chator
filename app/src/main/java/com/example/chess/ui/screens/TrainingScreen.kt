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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.chess.training.TrainingRepository
import com.example.chess.ui.ChessBoardView
import com.example.chess.ui.GameViewModel

@Composable
fun TrainingScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val lessons = TrainingRepository.lessons
    val currentLesson = lessons.getOrNull(uiState.currentTrainingIndex) ?: lessons.first()

    var selectedCategory by remember { mutableStateOf("All") }
    var showHint by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (uiState.gameMode != GameMode.TRAINING) {
            viewModel.loadTrainingLesson(0)
        }
    }

    val categories = listOf(
        "All" to "Semua",
        "Pergerakan Bidak" to "Bidak",
        "Check & Checkmate" to "Skak/Skakmat",
        "Aturan Khusus" to "Rokade & En Passant",
        "Prinsip Pembukaan" to "Pembukaan",
        "Taktik Catur" to "Taktik",
        "Babak Akhir" to "Endgame"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .widthIn(max = 600.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // CATEGORY FILTER BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { (catKey, catLabel) ->
                FilterChip(
                    selected = selectedCategory == catKey,
                    onClick = {
                        selectedCategory = catKey
                        val matchingLesson = if (catKey == "All") lessons.first() else lessons.firstOrNull { it.category == catKey } ?: lessons.first()
                        viewModel.loadTrainingLesson(lessons.indexOf(matchingLesson))
                    },
                    label = { Text(catLabel, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // LESSON INFO CARD
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
                            text = "${currentLesson.category}: ${currentLesson.title}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = currentLesson.shortDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    SuggestionChip(
                        onClick = {},
                        label = { Text("#${currentLesson.id}", fontSize = 11.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentLesson.explanation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Interactive practice prompt
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentLesson.practicePrompt,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // INTERACTIVE CHESS BOARD
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

        // FEEDBACK BANNER
        if (uiState.trainingMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isTrainingCompleted) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.isTrainingCompleted) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (uiState.isTrainingCompleted) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.trainingMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (uiState.isTrainingCompleted) Color(0xFF166534) else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // HINT TEXT
        AnimatedVisibility(visible = showHint) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
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
                        text = "Kunci Aturan: ${currentLesson.keyRule}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }

        // NAVIGATION CONTROLS
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
                        viewModel.prevTrainingLesson()
                    },
                    modifier = Modifier.testTag("training_prev")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Sebelumnya")
                }

                OutlinedButton(
                    onClick = {
                        showHint = false
                        viewModel.loadTrainingLesson(uiState.currentTrainingIndex)
                    },
                    modifier = Modifier.testTag("training_reset")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ulangi", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showHint = !showHint },
                    modifier = Modifier.testTag("training_hint")
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kunci", fontSize = 12.sp)
                }

                IconButton(
                    onClick = {
                        showHint = false
                        viewModel.nextTrainingLesson()
                    },
                    modifier = Modifier.testTag("training_next")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Berikutnya")
                }
            }
        }
    }
}
