package com.example.chess.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.chess.model.GameMode
import com.example.chess.model.TimeSetting
import com.example.chess.ui.GameViewModel
import com.example.chess.ui.components.PveSetupDialog
import com.example.chess.ui.components.PvpSetupDialog

@Composable
fun HomeScreen(
    viewModel: GameViewModel,
    onNavigateToGame: () -> Unit,
    onNavigateToPuzzles: () -> Unit,
    onNavigateToTraining: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = remember { viewModel.getStats() }

    var showPvpDialog by remember { mutableStateOf(false) }
    var showPveDialog by remember { mutableStateOf(false) }

    val gamesPlayed = stats["gamesPlayed"] ?: 0
    val wins = stats["winsVsAi"] ?: 0
    val winRate = if (gamesPlayed > 0) (wins * 100) / gamesPlayed else 0
    val puzzlesSolved = stats["puzzlesSolved"] ?: 0

    if (showPvpDialog) {
        PvpSetupDialog(
            initialP1Name = uiState.player1Name,
            initialP2Name = uiState.player2Name,
            initialTime = uiState.timeSetting,
            initialAutoRotate = uiState.autoRotateBoard,
            onStartGame = { p1, p2, time, customMin, customInc, autoRotate ->
                viewModel.startNewGame(
                    mode = GameMode.PASS_AND_PLAY,
                    time = time,
                    customMinutes = customMin,
                    customIncrement = customInc,
                    p1Name = p1,
                    p2Name = p2,
                    autoRotate = autoRotate
                )
                showPvpDialog = false
                onNavigateToGame()
            },
            onDismiss = { showPvpDialog = false }
        )
    }

    if (showPveDialog) {
        PveSetupDialog(
            initialPlayerName = uiState.player1Name,
            initialDifficulty = uiState.aiDifficulty,
            initialColor = uiState.playerColorVsAi,
            initialTime = uiState.timeSetting,
            onStartGame = { pName, color, diff, time, customMin, customInc ->
                viewModel.startNewGame(
                    mode = GameMode.VS_AI,
                    playerColor = color,
                    difficulty = diff,
                    time = time,
                    customMinutes = customMin,
                    customIncrement = customInc,
                    p1Name = pName
                )
                showPveDialog = false
                onNavigateToGame()
            },
            onDismiss = { showPveDialog = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF090D16),
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 600.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // INTEGRATED SPORTY TOP HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp), spotColor = Color(0xFF10B981))
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_chator_logo),
                            contentDescription = "Chator Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CHATOR",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF10B981))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(Color(0xFF10B981), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OFFLINE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF6EE7B7)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Professional Modern Chess",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                // Settings circular button
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E293B).copy(alpha = 0.85f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0x33FFFFFF))
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    IconButton(
                        onClick = onNavigateToSettings,
                        modifier = Modifier.testTag("home_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // STATS PROGRESS STRIP (Sleek horizontal badge pills)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0x22FFFFFF))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Match stat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "$gamesPlayed", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            Text(text = "Tanding", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0x33FFFFFF)))

                    // Win rate stat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "$winRate%", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF34D399))
                            Text(text = "Win Rate", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0x33FFFFFF)))

                    // Puzzle stat
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Extension,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = "$puzzlesSolved", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFFCD34D))
                            Text(text = "Puzzle", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }

            // SECTION 1: MODE PERMAINAN UTAMA (HERO ACTION CARDS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MODE PERMAINAN UTAMA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF10B981)
                )
            }

            // 1. HERO CARD: PLAYER 1 VS PLAYER 2 (Mabar 1 HP)
            SportyHeroPlayCard(
                title = "Player 1 vs Player 2",
                subtitle = "Tanding 2 pemain di satu ponsel • Papan tetap di meja atau putar per giliran",
                buttonLabel = "Main 2 Pemain",
                accentColor = Color(0xFF10B981),
                gradientColors = listOf(Color(0xFF064E3B), Color(0xFF022C22)),
                badges = listOf("⚔️ Mabar 1 HP", "⏱️ Jam Catur Digital", "🔄 Tabletop Mode"),
                icon = Icons.Default.Group,
                watermark = "♚ ♔",
                onClick = { showPvpDialog = true },
                testTag = "menu_card_pvp"
            )

            // 2. HERO CARD: PLAYER VS KOMPUTER (AI)
            SportyHeroPlayCard(
                title = "Player vs Komputer",
                subtitle = "Tantang engine AI pintar Chator dengan 3 tingkat kecerdasan strategi",
                buttonLabel = "Tantang AI",
                accentColor = Color(0xFF38BDF8),
                gradientColors = listOf(Color(0xFF0C4A6E), Color(0xFF082F49)),
                badges = listOf("🟢 Easy", "🟡 Medium", "🔴 Hard", "⚡ 100% Offline"),
                icon = Icons.Default.SmartToy,
                watermark = "🤖 ♚",
                onClick = { showPveDialog = true },
                testTag = "menu_card_pve"
            )

            // QUICK START PRESETS (Akses Cepat Efisien)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AKSES CEPAT (QUICK START)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    title = "Blitz 3+2",
                    icon = Icons.Default.Bolt,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.startNewGame(
                            mode = GameMode.PASS_AND_PLAY,
                            time = TimeSetting.BLITZ_3_2,
                            autoRotate = false
                        )
                        onNavigateToGame()
                    }
                )
                QuickActionChip(
                    title = "Rapid 10+0",
                    icon = Icons.Default.Timer,
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.startNewGame(
                            mode = GameMode.PASS_AND_PLAY,
                            time = TimeSetting.RAPID_10_0,
                            autoRotate = false
                        )
                        onNavigateToGame()
                    }
                )
                QuickActionChip(
                    title = "Santai ∞",
                    icon = Icons.Default.SportsEsports,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.startNewGame(
                            mode = GameMode.PASS_AND_PLAY,
                            time = TimeSetting.UNLIMITED,
                            autoRotate = false
                        )
                        onNavigateToGame()
                    }
                )
            }

            // SECTION 2: ASAH KEMAMPUAN & TAKTIK
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ASAH KEMAMPUAN & TAKTIK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            // DUAL CARDS: PUZZLE & TRAINING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Puzzle Card
                SportyFeatureCard(
                    title = "Teka-teki Catur",
                    subtitle = "Latihan taktik & skakmat harian",
                    badge = "Taktik & Puzzle",
                    icon = Icons.Default.Psychology,
                    accentColor = Color(0xFFF59E0B),
                    gradientColors = listOf(Color(0xFF78350F), Color(0xFF451A03)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPuzzles,
                    testTag = "menu_card_puzzles"
                )

                // Training Card
                SportyFeatureCard(
                    title = "Latihan Interaktif",
                    subtitle = "16 modul dari dasar hingga taktik",
                    badge = "Modul Belajar",
                    icon = Icons.Default.School,
                    accentColor = Color(0xFFA855F7),
                    gradientColors = listOf(Color(0xFF581C87), Color(0xFF3B0764)),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTraining,
                    testTag = "menu_card_training"
                )
            }

            // SECTION 3: STATISTIK & RIWAYAT PGN
            SportyWideMenuCard(
                title = "Statistik & Riwayat PGN",
                subtitle = "Rekor kemenangan, win rate, analisis pertandingan, dan ekspor PGN catur",
                badge = "Rekor & PGN Viewer",
                icon = Icons.Default.BarChart,
                accentColor = Color(0xFF14B8A6),
                onClick = onNavigateToStats,
                testTag = "menu_card_stats"
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * High-energy, sporty hero card for primary play modes (PvP & PvE).
 */
@Composable
private fun SportyHeroPlayCard(
    title: String,
    subtitle: String,
    buttonLabel: String,
    accentColor: Color,
    gradientColors: List<Color>,
    badges: List<String>,
    icon: ImageVector,
    watermark: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF0F172A),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(accentColor, accentColor.copy(alpha = 0.3f)))
        ),
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(18.dp)
        ) {
            // Subtle watermark
            Text(
                text = watermark,
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.06f),
                modifier = Modifier.align(Alignment.BottomEnd)
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                                .border(1.5.dp, accentColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = subtitle,
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                maxLines = 2,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Feature pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    badges.take(3).forEach { badge ->
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(6.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0x22FFFFFF))
                            )
                        ) {
                            Text(
                                text = badge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Tactile Action Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buttonLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact feature card (2-column layout for Puzzle & Training).
 */
@Composable
private fun SportyFeatureCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E293B),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(accentColor.copy(alpha = 0.5f))
        ),
        shadowElevation = 6.dp,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradientColors))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                            .border(1.2.dp, accentColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0x66FFFFFF),
                        modifier = Modifier.size(13.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Wide streamlined menu card (e.g. for Stats & PGN).
 */
@Composable
private fun SportyWideMenuCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.7f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(Color(0x33FFFFFF))
        ),
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.2.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0x66FFFFFF),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Modern quick-launch preset chip.
 */
@Composable
private fun QuickActionChip(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.8f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(color.copy(alpha = 0.35f))
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
