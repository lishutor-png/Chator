package com.example.chess.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundAndHaptics(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playMoveSound() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        } catch (_: Exception) {}
    }

    fun playCaptureSound() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
        } catch (_: Exception) {}
    }

    fun playCheckSound() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 120)
        } catch (_: Exception) {}
    }

    fun playWinSound() {
        if (!isSoundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_NETWORK_CALLWAITING, 200)
        } catch (_: Exception) {}
    }

    fun vibrateMove() {
        if (!isHapticsEnabled) return
        vibrateMillis(25)
    }

    fun vibrateCapture() {
        if (!isHapticsEnabled) return
        vibrateMillis(50)
    }

    fun vibrateCheck() {
        if (!isHapticsEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 50, 40, 70)
            val amplitudes = intArrayOf(0, 180, 0, 255)
            try {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } catch (_: Exception) {
                vibrateMillis(90)
            }
        } else {
            vibrateMillis(90)
        }
    }

    private fun vibrateMillis(ms: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(ms)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
