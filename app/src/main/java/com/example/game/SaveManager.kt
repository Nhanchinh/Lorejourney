package com.example.game

import android.content.Context
import android.content.SharedPreferences

/**
 * Quản lý việc lưu/tải progress game
 */
object SaveManager {
    private const val PREFS_NAME = "TopDownGamePrefs"
    private const val KEY_MAX_UNLOCKED_LEVEL = "max_unlocked_level"
    
    private lateinit var prefs: SharedPreferences
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProgress()
    }
    
    fun saveProgress() {
        prefs.edit()
            .putInt(KEY_MAX_UNLOCKED_LEVEL, GameConstants.MAX_UNLOCKED_LEVEL)
            .apply()
        
        println("💾 Progress saved: Max unlocked level = ${GameConstants.MAX_UNLOCKED_LEVEL}")
    }
    
    private fun loadProgress() {
        GameConstants.MAX_UNLOCKED_LEVEL = prefs.getInt(KEY_MAX_UNLOCKED_LEVEL, 1)
        println("📁 Progress loaded: Max unlocked level = ${GameConstants.MAX_UNLOCKED_LEVEL}")
    }
    
    fun unlockLevel(level: Int) {
        if (level > GameConstants.MAX_UNLOCKED_LEVEL && level <= GameConstants.TOTAL_LEVELS) {
            GameConstants.MAX_UNLOCKED_LEVEL = level
            saveProgress()
            println("🔓 Level $level unlocked and saved!")
        }
    }
    
    fun resetProgress() {
        GameConstants.MAX_UNLOCKED_LEVEL = 1
        saveProgress()
        println("🔄 Progress reset!")
    }
    
    /**
     * Unlock tất cả levels
     */
    fun unlockAllLevels() {
        GameConstants.MAX_UNLOCKED_LEVEL = GameConstants.TOTAL_LEVELS
        saveProgress()
        println("🔓 All levels unlocked! (${GameConstants.TOTAL_LEVELS} levels)")
    }
}
