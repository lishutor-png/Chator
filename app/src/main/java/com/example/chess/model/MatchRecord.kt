package com.example.chess.model

data class MatchRecord(
    val id: String,
    val timestamp: Long,
    val mode: String,
    val playerColor: String,
    val opponentName: String,
    val result: String, // "1-0", "0-1", "1/2-1/2"
    val resultDescription: String,
    val totalMoves: Int,
    val pgn: String
)
