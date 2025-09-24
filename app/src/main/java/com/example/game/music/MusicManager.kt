package com.example.game.music

import android.content.Context
import android.media.MediaPlayer
import com.example.game.R


object MusicManager {
	private var waitingHallPlayer: MediaPlayer? = null
	private var inGamePlayer: MediaPlayer? = null
    var isMusicEnabled: Boolean = true

	fun playWaitingHallMusic(context: Context) {
		if (!isMusicEnabled) return
		inGamePlayer?.let {
			it.stop()
			it.release()
			inGamePlayer = null
		}
		if (waitingHallPlayer == null) {
			waitingHallPlayer = MediaPlayer.create(context, R.raw.wattinghallmusic)
			waitingHallPlayer?.isLooping = true
			waitingHallPlayer?.start()
		} else if (waitingHallPlayer?.isPlaying == false) {
			waitingHallPlayer?.seekTo(0)
			waitingHallPlayer?.start()
		}
		// Nếu đã đang phát thì không làm gì
	}

	fun playInGameMusic(context: Context) {
		if (!isMusicEnabled) return
		waitingHallPlayer?.let {
			it.stop()
			it.release()
			waitingHallPlayer = null
		}
		if (inGamePlayer == null) {
			inGamePlayer = MediaPlayer.create(context, R.raw.ingamemusic)
			inGamePlayer?.isLooping = true
			inGamePlayer?.start()
		} else if (inGamePlayer?.isPlaying == false) {
			inGamePlayer?.seekTo(0)
			inGamePlayer?.start()
		}
		// Nếu đã đang phát thì không làm gì
	}

	fun stopMusic() {
        println("Stopping music")
		waitingHallPlayer?.stop()
		waitingHallPlayer?.release()
		waitingHallPlayer = null
		inGamePlayer?.stop()
		inGamePlayer?.release()
		inGamePlayer = null
	}
}
