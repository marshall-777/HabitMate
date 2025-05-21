package com.example.habitmate

import android.content.Context
import android.media.MediaPlayer

object AudioPlayer {

    private var backgroundPlayer: MediaPlayer? = null
    private var effectPlayer: MediaPlayer? = null

    fun startBackgroundMusic(context: Context, resId: Int) {
        stopBackgroundMusic()
        backgroundPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            start()
        }
    }

    fun stopBackgroundMusic() {
        backgroundPlayer?.stop()
        backgroundPlayer?.release()
        backgroundPlayer = null
    }

    fun playEffect(context: Context, resId: Int) {
        effectPlayer?.release()
        effectPlayer = MediaPlayer.create(context, resId).apply {
            setOnCompletionListener {
                it.release()
            }
            start()
        }
    }
}