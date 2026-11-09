package com.example.chess.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.AiDifficulty
import com.example.chess.model.GameMode
import com.example.chess.model.PieceColor
import com.example.chess.model.TimeSetting

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewGameDialog(
    initialMode: GameMode,
    initialDifficulty: AiDifficulty,
    initialColor: PieceColor,
    initialTime: TimeSetting,
    initialAutoRotate: Boolean = false,
    onStartGame: (GameMode, PieceColor, AiDifficulty, TimeSetting, Long, Long, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMode by remember { mutableStateOf(if (initialMode == GameMode.PASS_AND_PLAY) GameMode.PASS_AND_PLAY else GameMode.VS_AI) }
    var selectedDifficulty by remember { mutableStateOf(initialDifficulty) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var customMinutes by remember { mutableFloatStateOf(5f) }
    var customIncrement by remember { mutableFloatStateOf(0f) }
    var autoRotate by remember { mutableStateOf(initialAutoRotate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Mulai Permainan Baru",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Mode
                Column {
                    Text(
                        text = "Mode Permainan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedMode == GameMode.VS_AI,
                            onClick = { selectedMode = GameMode.VS_AI },
                            label = { Text("Player vs Computer", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("mode_vs_ai")
                        )
                        FilterChip(
                            selected = selectedMode == GameMode.PASS_AND_PLAY,
                            onClick = { selectedMode = GameMode.PASS_AND_PLAY },
                            label = { Text("Player vs Player", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("mode_2_player")
                        )
                    }
                }

                // If PvP: Orientation options
                if (selectedMode == GameMode.PASS_AND_PLAY) {
                    Column {
                        Text(
                            text = "Orientasi Papan (PvP)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (!autoRotate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (!autoRotate) CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                                ) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { autoRotate = false }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Papan Tetap", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Putih di bawah, Hitam di atas", fontSize = 9.sp, textAlign = TextAlign.Center)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (autoRotate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (autoRotate) CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                                ) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { autoRotate = true }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Putar Bergantian", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Berputar 180° tiap giliran", fontSize = 9.sp, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }

                // AI Difficulty (only if VS_AI)
                if (selectedMode == GameMode.VS_AI) {
                    Column {
                        Text(
                            text = "Tingkat Kesulitan AI",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                AiDifficulty.EASY to "Easy",
                                AiDifficulty.MEDIUM to "Medium",
                                AiDifficulty.HARD to "Hard"
                            ).forEach { (diff, label) ->
                                FilterChip(
                                    selected = selectedDifficulty == diff,
                                    onClick = { selectedDifficulty = diff },
                                    label = { Text(label, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f).testTag("diff_${diff.name.lowercase()}")
                                )
                            }
                        }
                    }

                    // Choose Color (White, Black, Random)
                    Column {
                        Text(
                            text = "Pilih Warna Bidak Anda",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = selectedColor == PieceColor.WHITE,
                                onClick = { selectedColor = PieceColor.WHITE },
                                label = { Text("Putih ♔", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("color_white")
                            )
                            FilterChip(
                                selected = selectedColor == PieceColor.BLACK,
                                onClick = { selectedColor = PieceColor.BLACK },
                                label = { Text("Hitam ♚", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f).testTag("color_black")
                            )
                        }
                    }
                }

                // Match Clock (1+0, 3+2, 5+0, 10+0, 15+10, Custom, Unlimited)
                Column {
                    Text(
                        text = "Kontrol Waktu (Timer)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            TimeSetting.BULLET_1_0 to "1+0",
                            TimeSetting.BLITZ_3_2 to "3+2",
                            TimeSetting.BLITZ_5_0 to "5+0",
                            TimeSetting.RAPID_10_0 to "10+0",
                            TimeSetting.RAPID_15_10 to "15+10",
                            TimeSetting.CUSTOM to "Kustom",
                            TimeSetting.UNLIMITED to "Bebas"
                        ).forEach { (time, label) ->
                            FilterChip(
                                selected = selectedTime == time,
                                onClick = { selectedTime = time },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.testTag("time_${time.name.lowercase()}")
                            )
                        }
                    }

                    // Custom slider if CUSTOM selected
                    if (selectedTime == TimeSetting.CUSTOM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Waktu: ${customMinutes.toInt()} Menit | Increment: ${customIncrement.toInt()} Detik",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Slider(
                            value = customMinutes,
                            onValueChange = { customMinutes = it },
                            valueRange = 1f..60f,
                            steps = 59
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStartGame(
                        selectedMode,
                        selectedColor,
                        selectedDifficulty,
                        selectedTime,
                        customMinutes.toLong(),
                        customIncrement.toLong(),
                        autoRotate
                    )
                },
                modifier = Modifier.testTag("confirm_start_game_button")
            ) {
                Text("Mulai Tanding")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
