package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.ui.GameViewModel
import com.example.chess.ui.screens.GameScreen
import com.example.chess.ui.screens.HomeScreen
import com.example.chess.ui.screens.PuzzlesScreen
import com.example.chess.ui.screens.SettingsScreen
import com.example.chess.ui.screens.SplashScreen
import com.example.chess.ui.screens.StatsScreen
import com.example.chess.ui.screens.TrainingScreen
import com.example.chess.ui.theme.ChatorTheme

enum class ScreenDestination {
    SPLASH,
    HOME,
    GAME,
    PUZZLE,
    TRAINING,
    STATS,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode = viewModel.preferences.themeMode
            ChatorTheme(themeMode = themeMode) {
                var currentScreen by rememberSaveable { mutableStateOf(ScreenDestination.SPLASH) }

                // Hardware Back button handling: return to HOME from any sub-screen
                BackHandler(enabled = currentScreen != ScreenDestination.HOME && currentScreen != ScreenDestination.SPLASH) {
                    currentScreen = ScreenDestination.HOME
                }

                val isHomeScreen = currentScreen == ScreenDestination.HOME
                val isGameScreen = currentScreen == ScreenDestination.GAME

                if (currentScreen == ScreenDestination.SPLASH) {
                    SplashScreen(
                        onSplashFinished = {
                            currentScreen = ScreenDestination.HOME
                        }
                    )
                } else {
                    Scaffold(
                        topBar = {
                            if (!isGameScreen && !isHomeScreen) {
                                CenterAlignedTopAppBar(
                                    navigationIcon = {
                                        if (currentScreen != ScreenDestination.HOME) {
                                            IconButton(
                                                onClick = { currentScreen = ScreenDestination.HOME },
                                                modifier = Modifier.testTag("top_bar_back_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Kembali ke Menu Utama"
                                                )
                                            }
                                        }
                                    },
                                    title = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(26.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(6.dp))
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.img_chator_logo),
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "CHATOR",
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 2.sp,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.WifiOff,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(10.dp),
                                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "Offline",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    },
                                    actions = {
                                        IconButton(
                                            onClick = { currentScreen = ScreenDestination.SETTINGS },
                                            modifier = Modifier.testTag("top_bar_settings")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Settings,
                                                contentDescription = "Pengaturan",
                                                tint = if (currentScreen == ScreenDestination.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        },
                        bottomBar = {
                            if (!isGameScreen && !isHomeScreen) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 6.dp,
                                    modifier = Modifier.testTag("bottom_nav_bar")
                                ) {
                                    NavigationBarItem(
                                        selected = currentScreen == ScreenDestination.HOME,
                                        onClick = { currentScreen = ScreenDestination.HOME },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                        modifier = Modifier.testTag("tab_home")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == ScreenDestination.GAME,
                                        onClick = { currentScreen = ScreenDestination.GAME },
                                        icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Play") },
                                        label = { Text("Play", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                        modifier = Modifier.testTag("tab_play")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == ScreenDestination.PUZZLE,
                                        onClick = { currentScreen = ScreenDestination.PUZZLE },
                                        icon = { Icon(Icons.Default.Psychology, contentDescription = "Puzzle") },
                                        label = { Text("Puzzle", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                        modifier = Modifier.testTag("tab_puzzle")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == ScreenDestination.TRAINING,
                                        onClick = { currentScreen = ScreenDestination.TRAINING },
                                        icon = { Icon(Icons.Default.School, contentDescription = "Training") },
                                        label = { Text("Training", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                        modifier = Modifier.testTag("tab_training")
                                    )
                                    NavigationBarItem(
                                        selected = currentScreen == ScreenDestination.STATS,
                                        onClick = { currentScreen = ScreenDestination.STATS },
                                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                                        label = { Text("Stats", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                        modifier = Modifier.testTag("tab_stats")
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(if (isGameScreen || isHomeScreen) Modifier else Modifier.padding(innerPadding))
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "ScreenTransition"
                            ) { screen ->
                                when (screen) {
                                    ScreenDestination.SPLASH -> {}
                                    ScreenDestination.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToGame = { currentScreen = ScreenDestination.GAME },
                                        onNavigateToPuzzles = { currentScreen = ScreenDestination.PUZZLE },
                                        onNavigateToTraining = { currentScreen = ScreenDestination.TRAINING },
                                        onNavigateToStats = { currentScreen = ScreenDestination.STATS },
                                        onNavigateToSettings = { currentScreen = ScreenDestination.SETTINGS }
                                    )
                                    ScreenDestination.GAME -> GameScreen(
                                        viewModel = viewModel,
                                        onNavigateBack = { currentScreen = ScreenDestination.HOME }
                                    )
                                    ScreenDestination.PUZZLE -> PuzzlesScreen(viewModel = viewModel)
                                    ScreenDestination.TRAINING -> TrainingScreen(viewModel = viewModel)
                                    ScreenDestination.STATS -> StatsScreen(viewModel = viewModel)
                                    ScreenDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
