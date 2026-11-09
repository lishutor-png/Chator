package com.example.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.BoardTheme
import com.example.chess.ui.GameViewModel

@Composable
fun SettingsScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var soundEnabled by remember { mutableStateOf(viewModel.soundAndHaptics.isSoundEnabled) }
    var hapticsEnabled by remember { mutableStateOf(viewModel.soundAndHaptics.isHapticsEnabled) }
    var autoRotate by remember { mutableStateOf(viewModel.preferences.autoRotateBoard) }
    var themeMode by remember { mutableStateOf(viewModel.preferences.themeMode) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .widthIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        // THEME MODE (LIGHT / DARK / SYSTEM)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mode Tampilan",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "DARK" to "Dark Mode",
                        "LIGHT" to "Light Mode",
                        "SYSTEM" to "Sistem"
                    ).forEach { (mode, label) ->
                        FilterChip(
                            selected = themeMode == mode,
                            onClick = {
                                themeMode = mode
                                viewModel.preferences.themeMode = mode
                            },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // BOARD THEME SELECTION
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tema Papan Catur",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BoardTheme.values().forEach { theme ->
                        val isSelected = uiState.boardTheme == theme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                                .clickable { viewModel.setBoardTheme(theme) }
                                .padding(12.dp)
                                .testTag("theme_${theme.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = theme.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )

                            // Swatch preview of light & dark squares
                            Row(
                                modifier = Modifier
                                    .size(width = 44.dp, height = 22.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0x33000000), RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(theme.lightSquare)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(theme.darkSquare)
                                )
                            }
                        }
                    }
                }
            }
        }

        // AUDIO & FEEDBACK SETTINGS
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Suara & Getaran",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Efek Suara", fontWeight = FontWeight.SemiBold)
                            Text("Nada melangkah, skak, dan menang", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            viewModel.setSoundEnabled(it)
                        },
                        modifier = Modifier.testTag("switch_sound")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Vibration, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Getaran Haptik", fontWeight = FontWeight.SemiBold)
                            Text("Umpan balik getar saat menggerakkan bidak", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = {
                            hapticsEnabled = it
                            viewModel.setHapticsEnabled(it)
                        },
                        modifier = Modifier.testTag("switch_haptics")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ScreenRotation, contentDescription = null)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Rotasi Otomatis (2 Pemain)", fontWeight = FontWeight.SemiBold)
                            Text(
                                if (autoRotate) "Putar papan 180° otomatis tiap giliran (Oper HP)" else "Papan Tetap: Putih di bawah, Hitam di atas (HP di meja)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = autoRotate,
                        onCheckedChange = {
                            autoRotate = it
                            viewModel.setAutoRotateBoard(it)
                        },
                        modifier = Modifier.testTag("switch_auto_rotate")
                    )
                }
            }
        }

        // ABOUT APP CARD
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tentang CHATOR",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CHATOR adalah aplikasi catur Android modern dengan desain minimalis, flat UI, dan aksen emerald. Bekerja 100% full offline tanpa memerlukan koneksi internet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fitur: Player vs Computer (AI Easy/Medium/Hard), Player vs Player (1 perangkat), Puzzle Catur (Easy/Medium/Hard/Daily), Training Interaktif (Bidak, Aturan Khusus, Taktik, Endgame), Timer Profesional (1+0, 3+2, 5+0, 10+0, 15+10, Kustom), dan Ekspor PGN.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Versi: 1.0.0 (Modern Flat UI • Emerald Accent)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
