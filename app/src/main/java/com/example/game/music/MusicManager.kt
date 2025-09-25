package com.example.game.music

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import com.example.game.R
import com.example.game.SaveManager


object MusicManager {
	private var waitingHallPlayer: MediaPlayer? = null
	private var inGamePlayer: MediaPlayer? = null
	private var soundPool: SoundPool? = null
	private val soundMap = mutableMapOf<String, Int>()
	
	private var _isMusicEnabled = true
	private var _isSoundEnabled = true
	
	var isMusicEnabled: Boolean
		get() = _isMusicEnabled
		set(value) {
			_isMusicEnabled = value
			SaveManager.saveMusicSettings()
		}
	
	var isSoundEnabled: Boolean
		get() = _isSoundEnabled
		set(value) {
			_isSoundEnabled = value
			SaveManager.saveMusicSettings()
		}

	fun setMusicSettings(music: Boolean, sound: Boolean) {
		_isMusicEnabled = music
		_isSoundEnabled = sound
	}

	private fun initSoundPool() {
		if (soundPool == null) {
			soundPool = SoundPool.Builder().setMaxStreams(6).build()
		}
	}

	fun preloadSounds(context: Context) {
		if (soundPool == null) initSoundPool()
        // Danh sách tên âm thanh cần preload (thay thế bằng tên thực tế từ res/raw/)
        val soundNames = listOf("torch", "winner", "footsteps", "ghost", "pushstone", "ding")  // Ví dụ: thay bằng tên file như "click", "jump", v.v.
        for (name in soundNames) {
            val soundId = loadSound(context, name)
            if (soundId != 0) {
                soundMap[name] = soundId
            }
        }
    }

	private fun loadSound(context: Context, name: String): Int {
		val resId = context.resources.getIdentifier(name, "raw", context.packageName)
		if (resId == 0) return 0
		return soundPool?.load(context, resId, 1) ?: 0
	}

	fun playSound(context: Context, name: String) {
		if (!isSoundEnabled) return
		if (soundPool == null) initSoundPool()
		val soundId = soundMap[name] ?: loadSound(context, name).also { soundMap[name] = it }
		if (soundId != 0) {
			soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
		}
	}

	fun playWaitingHallMusic(context: Context) {
		if (!isMusicEnabled) return
		inGamePlayer?.let {
			it.stop()
			it.release()
			inGamePlayer = null
		}
		if (waitingHallPlayer == null) {
			waitingHallPlayer = MediaPlayer.create(context, R.raw.wattinghallmusic)
			waitingHallPlayer?.setVolume(0.6f, 0.6f)
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
			inGamePlayer?.setVolume(0.6f, 0.6f)
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
		soundPool?.release()
		soundPool = null
		soundMap.clear()
	}
}
