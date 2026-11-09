package com.example.chess.model

import androidx.compose.ui.graphics.Color

enum class BoardTheme(
    val title: String,
    val lightSquare: Color,
    val darkSquare: Color,
    val lightSquareHighlight: Color,
    val darkSquareHighlight: Color
) {
    WOOD(
        title = "Krem & Cokelat Klasik",
        lightSquare = Color(0xFFF0D9B5),
        darkSquare = Color(0xFFB58863),
        lightSquareHighlight = Color(0xFFCED674),
        darkSquareHighlight = Color(0xFFAAA23B)
    ),
    EMERALD(
        title = "Zamrud Modern",
        lightSquare = Color(0xFFEEEED2),
        darkSquare = Color(0xFF769656),
        lightSquareHighlight = Color(0xFFF7F785),
        darkSquareHighlight = Color(0xFFBACA44)
    ),
    OCEAN(
        title = "Samudra Biru",
        lightSquare = Color(0xFFE2E8F0),
        darkSquare = Color(0xFF4A7398),
        lightSquareHighlight = Color(0xFF90CDF4),
        darkSquareHighlight = Color(0xFF3182CE)
    ),
    CHARCOAL(
        title = "Modern Obsidian",
        lightSquare = Color(0xFFE2E8F0),
        darkSquare = Color(0xFF475569),
        lightSquareHighlight = Color(0xFF6EE7B7),
        darkSquareHighlight = Color(0xFF059669)
    )
}

enum class AiDifficulty(val title: String, val subtitle: String, val depth: Int) {
    EASY("Easy", "Tingkat pemula, langkah kasual & santai", 1),
    MEDIUM("Medium", "Tingkat menengah, perhitungan taktis posisional", 2),
    HARD("Hard", "Tingkat ahli, Minimax Alpha-Beta & evaluasi agresif", 3)
}

enum class GameMode(val title: String, val subtitle: String) {
    VS_AI("Player vs Computer", "Bermain melawan AI dengan 3 tingkat kesulitan"),
    PASS_AND_PLAY("Player vs Player", "Bermain berdua bergantian di satu perangkat"),
    PUZZLES("Puzzle Catur", "Teka-teki taktik skakmat & kombinasi legal"),
    TRAINING("Latihan Interaktif", "Panduan interaktif pembukaan, aturan khusus, dan taktik"),
    ANALYSIS("Papan Analisis", "Papan bebas untuk mencoba berbagai posisi & evaluasi")
}

enum class TimeSetting(
    val title: String,
    val initialSeconds: Long,
    val incrementSeconds: Long
) {
    UNLIMITED("Tanpa Batas", 0L, 0L),
    BULLET_1_0("1 + 0", 60L, 0L),
    BLITZ_3_2("3 + 2", 180L, 2L),
    BLITZ_5_0("5 + 0", 300L, 0L),
    RAPID_10_0("10 + 0", 600L, 0L),
    RAPID_15_10("15 + 10", 900L, 10L),
    CUSTOM("Kustom", 300L, 5L)
}
