package com.example.chess.data

import android.content.Context
import android.content.SharedPreferences
import com.example.chess.model.AiDifficulty
import com.example.chess.model.BoardTheme
import com.example.chess.model.MatchRecord
import com.example.chess.model.TimeSetting
import org.json.JSONArray
import org.json.JSONObject

class ChessPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("chator_prefs", Context.MODE_PRIVATE)

    var boardTheme: BoardTheme
        get() {
            val name = prefs.getString("board_theme", BoardTheme.WOOD.name) ?: BoardTheme.WOOD.name
            return try { BoardTheme.valueOf(name) } catch (_: Exception) { BoardTheme.WOOD }
        }
        set(value) = prefs.edit().putString("board_theme", value.name).apply()

    var aiDifficulty: AiDifficulty
        get() {
            val name = prefs.getString("ai_difficulty", AiDifficulty.MEDIUM.name) ?: AiDifficulty.MEDIUM.name
            return try { AiDifficulty.valueOf(name) } catch (_: Exception) { AiDifficulty.MEDIUM }
        }
        set(value) = prefs.edit().putString("ai_difficulty", value.name).apply()

    var timeSetting: TimeSetting
        get() {
            val name = prefs.getString("time_setting", TimeSetting.UNLIMITED.name) ?: TimeSetting.UNLIMITED.name
            return try { TimeSetting.valueOf(name) } catch (_: Exception) { TimeSetting.UNLIMITED }
        }
        set(value) = prefs.edit().putString("time_setting", value.name).apply()

    var themeMode: String // "SYSTEM", "LIGHT", "DARK"
        get() = prefs.getString("theme_mode", "DARK") ?: "DARK"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    var player1Name: String
        get() = prefs.getString("player_1_name", "Player 1") ?: "Player 1"
        set(value) = prefs.edit().putString("player_1_name", value).apply()

    var player2Name: String
        get() = prefs.getString("player_2_name", "Player 2") ?: "Player 2"
        set(value) = prefs.edit().putString("player_2_name", value).apply()

    var autoRotateBoard: Boolean
        get() = prefs.getBoolean("auto_rotate_board", true)
        set(value) = prefs.edit().putBoolean("auto_rotate_board", value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean("haptics_enabled", true)
        set(value) = prefs.edit().putBoolean("haptics_enabled", value).apply()

    var gamesPlayed: Int
        get() = prefs.getInt("games_played", 0)
        set(value) = prefs.edit().putInt("games_played", value).apply()

    var winsVsAi: Int
        get() = prefs.getInt("wins_vs_ai", 0)
        set(value) = prefs.edit().putInt("wins_vs_ai", value).apply()

    var lossesVsAi: Int
        get() = prefs.getInt("losses_vs_ai", 0)
        set(value) = prefs.edit().putInt("losses_vs_ai", value).apply()

    var drawsVsAi: Int
        get() = prefs.getInt("draws_vs_ai", 0)
        set(value) = prefs.edit().putInt("draws_vs_ai", value).apply()

    var puzzlesSolved: Int
        get() = prefs.getInt("puzzles_solved", 0)
        set(value) = prefs.edit().putInt("puzzles_solved", value).apply()

    fun recordGameResult(result: String) { // "WIN", "LOSS", "DRAW"
        gamesPlayed++
        when (result) {
            "WIN" -> winsVsAi++
            "LOSS" -> lossesVsAi++
            "DRAW" -> drawsVsAi++
        }
    }

    fun incrementPuzzleSolved() {
        puzzlesSolved++
    }

    fun saveMatchRecord(record: MatchRecord) {
        val existing = getMatchHistory().toMutableList()
        existing.add(0, record) // newest first
        val limited = existing.take(30) // Keep latest 30 matches

        val array = JSONArray()
        for (m in limited) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("timestamp", m.timestamp)
                put("mode", m.mode)
                put("playerColor", m.playerColor)
                put("opponentName", m.opponentName)
                put("result", m.result)
                put("resultDescription", m.resultDescription)
                put("totalMoves", m.totalMoves)
                put("pgn", m.pgn)
            }
            array.put(obj)
        }
        prefs.edit().putString("match_history", array.toString()).apply()
    }

    fun getMatchHistory(): List<MatchRecord> {
        val jsonStr = prefs.getString("match_history", null) ?: return emptyList()
        val list = mutableListOf<MatchRecord>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    MatchRecord(
                        id = obj.optString("id", i.toString()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        mode = obj.optString("mode", ""),
                        playerColor = obj.optString("playerColor", "Putih"),
                        opponentName = obj.optString("opponentName", "AI"),
                        result = obj.optString("result", "*"),
                        resultDescription = obj.optString("resultDescription", ""),
                        totalMoves = obj.optInt("totalMoves", 0),
                        pgn = obj.optString("pgn", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun resetStats() {
        prefs.edit()
            .putInt("games_played", 0)
            .putInt("wins_vs_ai", 0)
            .putInt("losses_vs_ai", 0)
            .putInt("draws_vs_ai", 0)
            .putInt("puzzles_solved", 0)
            .remove("match_history")
            .apply()
    }
}
