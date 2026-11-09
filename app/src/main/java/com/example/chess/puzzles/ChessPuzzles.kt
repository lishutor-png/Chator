package com.example.chess.puzzles

import com.example.chess.model.PieceColor
import java.util.Calendar

data class Puzzle(
    val id: Int,
    val title: String,
    val theme: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val fen: String,
    val turn: PieceColor,
    val targetMoves: List<String>,
    val hint: String,
    val explanation: String
)

object ChessPuzzleRepository {
    val puzzles: List<Puzzle> = listOf(
        // EASY PUZZLES
        Puzzle(
            id = 1,
            title = "Back-Rank Mate",
            theme = "Skakmat Koridor",
            difficulty = "Easy",
            fen = "6k1/5ppp/8/8/8/8/5PPP/R5K1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Ra8#"),
            hint = "Benteng putih dapat meluncur langsung ke baris 8.",
            explanation = "Raja hitam terperangkap di baris 8 oleh bidaknya sendiri (Back-Rank Mate)."
        ),
        Puzzle(
            id = 2,
            title = "Scholar's Attack",
            theme = "Skakmat Serangan Cepat",
            difficulty = "Easy",
            fen = "r1bqkb1r/pppp1ppp/2n5/4p3/2B1n3/5Q2/PPPP1PPP/RNB1K1NR w KQkq - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Qxf7#"),
            hint = "Petak f7 hitam adalah titik terlemah yang hanya dijaga oleh Raja.",
            explanation = "Menteri memakan bidak di f7 dengan perlindungan Luncur di c4."
        ),
        Puzzle(
            id = 3,
            title = "Smothered Mate (Tercekik)",
            theme = "Kuncian Kuda",
            difficulty = "Easy",
            fen = "6rk/6pp/7N/8/8/8/8/7K w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Nf7#"),
            hint = "Hanya Kuda yang bisa melompati bidak untuk menyerang Raja.",
            explanation = "Raja hitam tidak bisa bergerak karena terkurung oleh bidak dan bentengnya sendiri."
        ),
        Puzzle(
            id = 4,
            title = "Boden's Mate Cross",
            theme = "Persilangan Luncur",
            difficulty = "Easy",
            fen = "2kr4/ppp2ppp/8/8/1B6/8/PPP2PPP/3B2K1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Bg4#"),
            hint = "Gunakan Luncur petak terang untuk memotong jalur pelarian Raja.",
            explanation = "Luncur memberikan skak fatal di diagonal g4-c8 sementara petak lain tertutup."
        ),
        Puzzle(
            id = 5,
            title = "Royal Fork Kuda",
            theme = "Garpu Taktis",
            difficulty = "Easy",
            fen = "r3k2r/ppp2ppp/8/3n4/8/2N5/PPP2PPP/R3K2R w KQkq - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Nxd5"),
            hint = "Kuda putih dapat mengambil kuda hitam yang tidak terjaga.",
            explanation = "Kuda mengambil bidak bebas dan menguasai sentral."
        ),
        Puzzle(
            id = 6,
            title = "Lawnmower Mate (Dua Benteng)",
            theme = "Dua Menara",
            difficulty = "Easy",
            fen = "k7/1R6/1K6/8/8/8/8/7R w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Rh8#"),
            hint = "Satu benteng mengurung baris 7, benteng lainnya menghabisi di baris 8.",
            explanation = "Kombinasi klasik dua benteng bertingkat (Lawnmower mate)."
        ),
        Puzzle(
            id = 7,
            title = "Back-Rank Counter",
            theme = "Serangan Balik Hitam",
            difficulty = "Easy",
            fen = "4r1k1/5ppp/8/8/8/8/5PPP/4R1K1 b - - 0 1",
            turn = PieceColor.BLACK,
            targetMoves = listOf("Rxe1#"),
            hint = "Benteng hitam mengambil benteng putih di e1.",
            explanation = "Skakmat baris belakang oleh benteng hitam."
        ),
        Puzzle(
            id = 8,
            title = "Queen Infiltration",
            theme = "Kemenangan Menteri Bebas",
            difficulty = "Easy",
            fen = "r4rk1/ppp2ppp/2n5/3q4/3P4/2PB1Q2/P4PPP/R3R1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Qxd5"),
            hint = "Menteri hitam tidak dijaga di d5.",
            explanation = "Putih memenangkan menteri hitam secara cuma-cuma."
        ),

        // MEDIUM PUZZLES
        Puzzle(
            id = 9,
            title = "Anastasia's Mate",
            theme = "Kombinasi Kuda & Benteng",
            difficulty = "Medium",
            fen = "5rk1/1p3ppp/7N/8/8/8/8/1R4K1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Rb8#"),
            hint = "Kuda mengunci petak pelarian Raja hitam di g8 dan h7.",
            explanation = "Benteng mengeksekusi skakmat di baris terakhir."
        ),
        Puzzle(
            id = 10,
            title = "Arabian Mate",
            theme = "Kunci Sudut Arab",
            difficulty = "Medium",
            fen = "7k/R7/5N2/8/8/8/8/7K w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Rh7#"),
            hint = "Benteng menyerang di samping Raja dengan perlindungan Kuda dari f6.",
            explanation = "Kuda mengontrol petak g8 dan melindungi Benteng di h7."
        ),
        Puzzle(
            id = 11,
            title = "Queen & King Skewer",
            theme = "Tusukan Menembus",
            difficulty = "Medium",
            fen = "4k3/8/8/8/8/8/1q6/R3K3 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Ra8+"),
            hint = "Skak Raja dari jarak jauh agar dapat memenangkan menteri di langkah berikutnya.",
            explanation = "Benteng melakukan skewer tajam terhadap Raja dan Menteri hitam."
        ),
        Puzzle(
            id = 12,
            title = "Hook Mate",
            theme = "Kait Bidak & Kuda",
            difficulty = "Medium",
            fen = "5rk1/5p1p/5Np1/8/8/1R6/8/7K w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Rh3#"),
            hint = "Benteng memanfaatkan sayap h yang terbuka.",
            explanation = "Kombinasi Kuda, Bidak, dan Benteng membentuk kuncian Hook Mate."
        ),
        Puzzle(
            id = 13,
            title = "Bishop Battery",
            theme = "Baterai Ratu dan Luncur",
            difficulty = "Medium",
            fen = "r1b2rk1/pp3ppp/1qn5/3B4/8/5Q2/PP3PPP/R1B1R1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Qxf7+"),
            hint = "Korbankan menteri untuk menghancurkan pertahanan sayap raja.",
            explanation = "Pertahanan sayap raja hancur oleh tusukan tajam menteri dan luncur."
        ),
        Puzzle(
            id = 14,
            title = "Corner Lockdown",
            theme = "Dominasi Diagonal",
            difficulty = "Medium",
            fen = "6k1/5p1p/6p1/8/8/8/1B5P/7K w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Bf6"),
            hint = "Kunci raja lawan di sudut papan.",
            explanation = "Luncur mendominasi petak f6 dan mengurung raja lawan."
        ),

        // HARD PUZZLES
        Puzzle(
            id = 15,
            title = "Opera House Decoy",
            theme = "Pengorbanan Taktis Morphy",
            difficulty = "Hard",
            fen = "4kb1r/p2rqppp/5n2/1B2p1B1/4P3/1Q6/PPP2PPP/R3K2R w KQk - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Bxf6"),
            hint = "Hilangkan bek pelindung sebelum membongkar pin di sayap raja.",
            explanation = "Menghancurkan koordinasi pertahanan lawan dengan eliminasi bek utama."
        ),
        Puzzle(
            id = 16,
            title = "Blackburne's Mate",
            theme = "Dua Luncur & Kuda Maut",
            difficulty = "Hard",
            fen = "r4rk1/pb3ppp/1p6/8/8/2N2N2/PP3PPP/R1B1RBK1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Re7"),
            hint = "Infiltrasi baris ke-7 untuk menekan perwira hitam yang belum berkembang.",
            explanation = "Benteng di baris ke-7 memberikan dominasi mutlak atas posisi lawan."
        ),
        Puzzle(
            id = 17,
            title = "Queen Deflection Sacrifice",
            theme = "Pengalihan Bek",
            difficulty = "Hard",
            fen = "2r3k1/5ppp/8/3Q4/8/8/5PPP/4R1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            targetMoves = listOf("Qd7"),
            hint = "Ancam benteng c8 dan ancam skakmat di d8 secara simultan.",
            explanation = "Menteri menekan benteng dan baris terakhir secara bersamaan (Double Attack)."
        ),
        Puzzle(
            id = 18,
            title = "Knight Smother Setup",
            theme = "Kombinasi Skak Ganda",
            difficulty = "Hard",
            fen = "r1b1k2r/pppp1Npp/8/4p3/2Bn3q/8/PPPP2PP/RNBQ2KR b kq - 0 1",
            turn = PieceColor.BLACK,
            targetMoves = listOf("Nf3+"),
            hint = "Hitam memiliki inisiatif serangan balik yang mematikan ke g1.",
            explanation = "Skak balik mendadak membongkar diagonal sayap raja putih."
        )
    )

    fun getPuzzlesByDifficulty(difficulty: String): List<Puzzle> {
        return if (difficulty == "All") puzzles else puzzles.filter { it.difficulty.equals(difficulty, ignoreCase = true) }
    }

    fun getDailyPuzzle(): Puzzle {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return puzzles[dayOfYear % puzzles.size]
    }
}
