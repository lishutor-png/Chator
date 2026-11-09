package com.example.chess.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.AiDifficulty
import com.example.chess.model.PieceColor
import com.example.chess.model.TimeSetting

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PveSetupDialog(
    initialPlayerName: String,
    initialDifficulty: AiDifficulty,
    initialColor: PieceColor,
    initialTime: TimeSetting,
    onStartGame: (playerName: String, color: PieceColor, diff: AiDifficulty, time: TimeSetting, customMin: Long, customInc: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var playerName by remember { mutableStateOf(initialPlayerName) }
    var selectedDifficulty by remember { mutableStateOf(initialDifficulty) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var customMinutes by remember { mutableFloatStateOf(5f) }
    var customIncrement by remember { mutableFloatStateOf(0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SmartToy, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Player vs Komputer",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // PLAYER NAME INPUT
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    label = { Text("Nama Anda") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_player_ai_name")
                )

                // AI DIFFICULTY
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
                                label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f).testTag("ai_diff_${diff.name.lowercase()}")
                            )
                        }
                    }
                }

                // PIECE COLOR
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
                            modifier = Modifier.weight(1f).testTag("ai_color_white")
                        )
                        FilterChip(
                            selected = selectedColor == PieceColor.BLACK,
                            onClick = { selectedColor = PieceColor.BLACK },
                            label = { Text("Hitam ♚", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("ai_color_black")
                        )
                    }
                }

                // MATCH TIMER
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pengatur Waktu (Timer)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
                                modifier = Modifier.testTag("ai_time_${time.name.lowercase()}")
                            )
                        }
                    }

                    if (selectedTime == TimeSetting.CUSTOM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Waktu: ${customMinutes.toInt()} Menit",
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
                        playerName.ifEmpty { "Player 1" },
                        selectedColor,
                        selectedDifficulty,
                        selectedTime,
                        customMinutes.toLong(),
                        customIncrement.toLong()
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("ai_confirm_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
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
