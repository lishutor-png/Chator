package com.example.chess.training

import com.example.chess.model.PieceColor

data class TrainingLesson(
    val id: Int,
    val category: String,
    val title: String,
    val shortDesc: String,
    val explanation: String,
    val keyRule: String,
    val initialFen: String,
    val turn: PieceColor,
    val goalMove: String?,
    val practicePrompt: String
)

object TrainingRepository {
    val lessons: List<TrainingLesson> = listOf(
        // 1. PERGERAKAN BIDAK
        TrainingLesson(
            id = 1,
            category = "Pergerakan Bidak",
            title = "Bidak (Pawn)",
            shortDesc = "Prajurit barisan depan catur",
            explanation = "Bidak melangkah maju satu petak ke depan. Pada langkah pertamanya, bidak memiliki opsi melangkah dua petak ke depan. Bidak hanya dapat memakan perwira musuh satu petak menyilang ke depan.",
            keyRule = "Maju lurus 1 petak (opsi 2 di awal), makan menyilang 1 petak ke depan.",
            initialFen = "8/8/8/8/8/8/4P3/8 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "e4",
            practicePrompt = "Coba langkahkan bidak putih dari e2 maju dua petak ke e4!"
        ),
        TrainingLesson(
            id = 2,
            category = "Pergerakan Bidak",
            title = "Kuda (Knight)",
            shortDesc = "Satu-satunya perwira yang dapat melompati perwira lain",
            explanation = "Kuda bergerak dengan pola berbentuk huruf 'L': dua petak lurus lalu satu petak tegak lurus (atau sebaliknya). Kuda adalah satu-satunya perwira catur yang bisa melompati bidak atau perwira lain.",
            keyRule = "Pola gerakan 'L' (2 petak lurus + 1 ke samping). Mampu melompati perwira.",
            initialFen = "8/8/8/8/8/8/8/1N6 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Nc3",
            practicePrompt = "Langkahkan Kuda dari petak b1 menuju petak sentral c3!"
        ),
        TrainingLesson(
            id = 3,
            category = "Pergerakan Bidak",
            title = "Gajah / Luncur (Bishop)",
            shortDesc = "Penguasa garis diagonal",
            explanation = "Luncur bergerak menyilang (diagonal) sejauh mungkin selama jalurnya tidak terhalang. Setiap pemain memiliki satu Luncur petak terang dan satu Luncur petak gelap.",
            keyRule = "Bergerak diagonal tanpa batas selama jalur tidak terhalang.",
            initialFen = "8/8/8/8/8/8/8/2B5 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Bc4",
            practicePrompt = "Gerakkan Luncur putih dari c1 meluncur ke diagonal c4!"
        ),
        TrainingLesson(
            id = 4,
            category = "Pergerakan Bidak",
            title = "Benteng (Rook)",
            shortDesc = "Kekuatan garis lurus horizontal dan vertikal",
            explanation = "Benteng bergerak lurus ke depan, belakang, kiri, atau kanan sejauh mungkin di sepanjang baris atau lajur yang kosong.",
            keyRule = "Bergerak lurus vertikal & horizontal tanpa batas selama jalur kosong.",
            initialFen = "8/8/8/8/8/8/8/R7 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Ra4",
            practicePrompt = "Luncurkan Benteng dari a1 maju ke petak a4!"
        ),
        TrainingLesson(
            id = 5,
            category = "Pergerakan Bidak",
            title = "Menteri / Ratu (Queen)",
            shortDesc = "Perwira paling kuat di atas papan",
            explanation = "Menteri menggabungkan kekuatan Benteng dan Luncur: dapat bergerak ke segala arah (lurus horizontal, vertikal, maupun diagonal) sejauh mungkin.",
            keyRule = "Menggabungkan gerakan Benteng dan Luncur ke segala penjuru.",
            initialFen = "8/8/8/8/8/8/8/3Q4 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Qd4",
            practicePrompt = "Tempatkan Menteri dari d1 langsung ke pusat papan di d4!"
        ),
        TrainingLesson(
            id = 6,
            category = "Pergerakan Bidak",
            title = "Raja (King)",
            shortDesc = "Jiwa dari permainan catur",
            explanation = "Raja dapat bergerak satu petak ke arah mana pun (horizontal, vertikal, atau diagonal). Raja tidak boleh melangkah ke petak yang sedang diserang oleh lawan.",
            keyRule = "Satu petak ke segala arah. Tidak boleh melangkah ke petak skak.",
            initialFen = "8/8/8/8/8/8/8/4K3 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Ke2",
            practicePrompt = "Gerakkan Raja putih dari e1 satu langkah maju ke e2!"
        ),

        // 2. CHECK & CHECKMATE
        TrainingLesson(
            id = 7,
            category = "Check & Checkmate",
            title = "Mengenal Skak (Check)",
            shortDesc = "Ancaman langsung terhadap keselamatan Raja",
            explanation = "Skak terjadi ketika Raja sedang diserang oleh perwira lawan. Pemain yang terkena skak WAJIB segera menyelamatkan rajanya dengan salah satu dari 3 cara: Lari, Tutup (halangi), atau Makan perwira penyerang.",
            keyRule = "Cara keluar dari skak: Lari, Tutup dengan perwira lain, atau Makan penyerang.",
            initialFen = "8/8/8/4k3/8/8/8/4R1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Re4+",
            practicePrompt = "Gerakkan benteng putih ke e4 untuk memberikan skak langsung ke Raja lawan!"
        ),
        TrainingLesson(
            id = 8,
            category = "Check & Checkmate",
            title = "Skakmat (Checkmate)",
            shortDesc = "Kemenangan mutlak saat Raja tak lagi berkutik",
            explanation = "Skakmat adalah kondisi di mana Raja sedang dalam keadaan skak dan TIDAK ADA satu pun langkah legal untuk melarikan diri, menutup, atau memakan penyerang. Pertandingan langsung selesai!",
            keyRule = "Skak tanpa ada jalan keluar mengakhiri pertandingan seketika.",
            initialFen = "6k1/5ppp/8/8/8/8/5PPP/R5K1 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Ra8#",
            practicePrompt = "Jalankan Benteng putih ke baris 8 (Ra8#) untuk mengunci skakmat!"
        ),

        // 3. CASTLING & EN PASSANT
        TrainingLesson(
            id = 9,
            category = "Aturan Khusus",
            title = "Rokade (Castling)",
            shortDesc = "Langkah ganda untuk mengamankan Raja dan mengaktifkan Benteng",
            explanation = "Rokade adalah satu-satunya gerakan di mana dua perwira (Raja dan Benteng) berpindah secara bersamaan. Syaratnya: Raja dan Benteng belum pernah bergerak, tidak ada perwira di antara keduanya, Raja tidak dalam skak, dan petak yang dilewati tidak sedang diserang.",
            keyRule = "Raja bergeser 2 petak ke arah Benteng, dan Benteng melompati Raja.",
            initialFen = "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "O-O",
            practicePrompt = "Lakukan Rokade Sayap Raja (Kingside O-O) dengan memindahkan Raja ke g1!"
        ),
        TrainingLesson(
            id = 10,
            category = "Aturan Khusus",
            title = "En Passant (Memakan Sambil Lalu)",
            shortDesc = "Aturan unik penangkapan bidak yang melesat dua petak",
            explanation = "Jika bidak musuh maju dua petak dari posisi awal dan mendarat persis di samping bidak Anda, Anda dapat memakannya secara menyilang di petak yang dilewatinya. Aturan ini hanya berlaku tepat pada giliran berikutnya!",
            keyRule = "Hanya bisa dilakukan seketika setelah bidak musuh maju 2 petak.",
            initialFen = "8/8/8/4Pp2/8/8/8/8 w - f6 0 1",
            turn = PieceColor.WHITE,
            goalMove = "exf6",
            practicePrompt = "Makan bidak hitam di f5 secara En Passant dengan melangkah ke f6!"
        ),

        // 4. OPENING PRINCIPLES
        TrainingLesson(
            id = 11,
            category = "Prinsip Pembukaan",
            title = "Kuasai Pusat Papan",
            shortDesc = "Kunci mengendalikan jalannya pertandingan",
            explanation = "Petak e4, d4, e5, dan d5 adalah medan pertempuran paling strategis di papan catur. Menguasai petak pusat memberi kebebasan bergerak bagi perwira Anda dan membatasi manuver lawan.",
            keyRule = "Majukan bidak tengah (e4 atau d4) di langkah pertama.",
            initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "e4",
            practicePrompt = "Langkahkan bidak raja e2 maju dua petak ke e4 untuk membuka pertarungan!"
        ),
        TrainingLesson(
            id = 12,
            category = "Prinsip Pembukaan",
            title = "Kembangkan Kuda & Luncur",
            shortDesc = "Prioritas perwira ringan sebelum Menteri",
            explanation = "Kuda dan Luncur harus segera diaktifkan ke petak yang menekan pusat. Hindari menggerakkan bidak yang sama berulang kali di fase pembukaan sebelum perwira lain aktif.",
            keyRule = "Kuda ke c3/f3 dan Luncur ke c4/b5 di langkah awal.",
            initialFen = "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Nf3",
            practicePrompt = "Kembangkan Kuda putih dari g1 ke f3 untuk menekan bidak e5 lawan!"
        ),

        // 5. TACTICS (FORK, PIN, SKEWER)
        TrainingLesson(
            id = 13,
            category = "Taktik Catur",
            title = "Garpu Kuda (Fork)",
            shortDesc = "Menyerang dua sasaran berharga secara simultan",
            explanation = "Garpu (*Fork*) adalah taktik di mana satu perwira menyerang dua atau lebih perwira lawan sekaligus. Kuda adalah ahli garpu terbaik karena lompatannya tidak bisa dihalangi.",
            keyRule = "Satu serangan mengancam dua target tak berdaya sekaligus.",
            initialFen = "r3k3/8/8/8/4N3/8/8/4K3 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Nd6+",
            practicePrompt = "Gerakkan Kuda ke d6 (Nd6+) untuk melakukan garpu skak terhadap Raja dan Benteng!"
        ),
        TrainingLesson(
            id = 14,
            category = "Taktik Catur",
            title = "Kancingan (Pin)",
            shortDesc = "Mengunci perwira lawan agar tidak bisa berkutik",
            explanation = "Kancingan (*Pin*) terjadi saat perwira lawan tidak bisa bergerak karena jika ia bergerak, perwira yang lebih berharga (atau Raja) di belakangnya akan tertangkap.",
            keyRule = "Perwira yang terkena pin absolut terhadap Raja dilarang bergerak.",
            initialFen = "4k3/8/8/8/1B6/8/4r3/4R1K1 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Rxe2",
            practicePrompt = "Manfaatkan kancingan Luncur untuk memakan benteng hitam di e2!"
        ),
        TrainingLesson(
            id = 15,
            category = "Taktik Catur",
            title = "Tusukan (Skewer)",
            shortDesc = "Menyerang perwira depan untuk menangkap yang di belakang",
            explanation = "Tusukan (*Skewer*) adalah kebalikan dari Pin: perwira yang lebih berharga berada di depan dan terpaksa melarikan diri, sehingga perwira di belakangnya dapat dimakan.",
            keyRule = "Ancam perwira utama di depan untuk memenangkan materi di belakangnya.",
            initialFen = "4k3/8/8/8/8/8/1q6/R3K3 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Ra8+",
            practicePrompt = "Lakukan skewer dengan meluncurkan Benteng putih ke baris a8!"
        ),

        // 6. ENDGAME
        TrainingLesson(
            id = 16,
            category = "Babak Akhir",
            title = "Raja Aktif di Babak Akhir",
            shortDesc = "Saat bahaya skakmat mereda, Raja menjadi perwira penyerang",
            explanation = "Pada babak akhir (*endgame*), ketika menteri dan sebagian besar perwira sudah bertukar, Raja harus maju aktif ke pusat papan untuk mendukung laju bidak bebas.",
            keyRule = "Raja yang pasif kalah dari Raja yang aktif di babak akhir.",
            initialFen = "8/8/4k3/8/8/4K3/4P3/8 w - - 0 1",
            turn = PieceColor.WHITE,
            goalMove = "Kd4",
            practicePrompt = "Majukan Raja putih ke petak d4 untuk merebut posisi oposisi!"
        )
    )

    fun getLessonsByCategory(category: String): List<TrainingLesson> {
        return if (category == "All") lessons else lessons.filter { it.category.equals(category, ignoreCase = true) }
    }
}
