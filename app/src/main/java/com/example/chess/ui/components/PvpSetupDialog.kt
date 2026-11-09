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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.chess.model.TimeSetting

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PvpSetupDialog(
    initialP1Name: String,
    initialP2Name: String,
    initialTime: TimeSetting,
    initialAutoRotate: Boolean = false,
    onStartGame: (p1Name: String, p2Name: String, time: TimeSetting, customMin: Long, customInc: Long, autoRotate: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var p1Name by remember { mutableStateOf(initialP1Name) }
    var p2Name by remember { mutableStateOf(initialP2Name) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var customMinutes by remember { mutableFloatStateOf(5f) }
    var customIncrement by remember { mutableFloatStateOf(0f) }
    var autoRotate by remember { mutableStateOf(initialAutoRotate) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Player 1 vs Player 2",
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
                Text(
                    text = "Atur nama pemain, kontrol waktu, dan orientasi papan catur:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // PLAYER 1 NAME INPUT
                OutlinedTextField(
                    value = p1Name,
                    onValueChange = { p1Name = it },
                    label = { Text("Nama Player 1 (Bidak Putih ♔)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_player_1_name")
                )

                // PLAYER 2 NAME INPUT
                OutlinedTextField(
                    value = p2Name,
                    onValueChange = { p2Name = it },
                    label = { Text("Nama Player 2 (Bidak Hitam ♚)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_player_2_name")
                )

                // BOARD ORIENTATION OPTIONS
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ScreenRotation, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Orientasi Papan Catur",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Option 1: Papan Tetap (Meja Berhadapan)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (!autoRotate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (!autoRotate) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                            ) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { autoRotate = false }
                                .padding(vertical = 2.dp)
                                .testTag("pvp_mode_fixed_board")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Papan Tetap",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (!autoRotate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Putih di bawah, Hitam di atas (HP di meja)",
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (!autoRotate) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Option 2: Putar Bergantian (Oper HP)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (autoRotate) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (autoRotate) CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                            ) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { autoRotate = true }
                                .padding(vertical = 2.dp)
                                .testTag("pvp_mode_rotate_board")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Putar Bergantian",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (autoRotate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Papan berputar 180° otomatis tiap giliran",
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (autoRotate) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // TIMER PRESETS
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
                                modifier = Modifier.testTag("pvp_time_${time.name.lowercase()}")
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
                        p1Name.ifEmpty { "Player 1" },
                        p2Name.ifEmpty { "Player 2" },
                        selectedTime,
                        customMinutes.toLong(),
                        customIncrement.toLong(),
                        autoRotate
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("pvp_confirm_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mulai Main")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
