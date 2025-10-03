package com.example.game

import android.content.Context
import android.content.SharedPreferences
import com.example.game.music.MusicManager

/**
 * Quản lý việc lưu/tải progress game
 */
object SaveManager {
    private const val PREFS_NAME = "TopDownGamePrefs"
    private const val KEY_MAX_UNLOCKED_LEVEL = "max_unlocked_level"
    private const val KEY_VIEWED_STORIES = "viewed_stories"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    
    // Keys for game state
    private const val KEY_CURRENT_STATE = "current_state"
    private const val KEY_CURRENT_LEVEL = "current_level"
    private const val KEY_PLAYER_X = "player_x"
    private const val KEY_PLAYER_Y = "player_y"
    private const val KEY_HAS_ACTIVE_GAME = "has_active_game"
    
    private lateinit var prefs: SharedPreferences
    private val viewedStories = mutableSetOf<Int>()
    
    // Current game state
    private var hasActiveGame = false
    private var savedGameState = -1
    private var savedLevelId = -1
    private var savedPlayerX = 0f
    private var savedPlayerY = 0f
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProgress()
        loadViewedStories()
    }
    
    fun saveProgress() {
        prefs.edit()
            .putInt(KEY_MAX_UNLOCKED_LEVEL, GameConstants.MAX_UNLOCKED_LEVEL)
            .putStringSet(KEY_VIEWED_STORIES, viewedStories.map { it.toString() }.toSet())
            .putBoolean(KEY_MUSIC_ENABLED, MusicManager.isMusicEnabled)
            .putBoolean(KEY_SOUND_ENABLED, MusicManager.isSoundEnabled)
            .apply()
        
        println("💾 Progress saved: Max unlocked level = ${GameConstants.MAX_UNLOCKED_LEVEL}")
    }
    
    fun saveMusicSettings() {
        prefs.edit()
            .putBoolean(KEY_MUSIC_ENABLED, MusicManager.isMusicEnabled)
            .putBoolean(KEY_SOUND_ENABLED, MusicManager.isSoundEnabled)
            .apply()
        
        println("🎵 Music settings saved: Music=${MusicManager.isMusicEnabled}, Sound=${MusicManager.isSoundEnabled}")
    }
    
    private fun loadProgress() {
        GameConstants.MAX_UNLOCKED_LEVEL = prefs.getInt(KEY_MAX_UNLOCKED_LEVEL, 1)
        val musicEnabled = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        val soundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        MusicManager.setMusicSettings(musicEnabled, soundEnabled)
        println("📁 Progress loaded: Max unlocked level = ${GameConstants.MAX_UNLOCKED_LEVEL}")
    }
    
    private fun loadViewedStories() {
        val viewedSet = prefs.getStringSet(KEY_VIEWED_STORIES, emptySet()) ?: emptySet()
        viewedStories.clear()
        viewedStories.addAll(viewedSet.mapNotNull { it.toIntOrNull() })
        println("📚 Viewed stories loaded: ${viewedStories.size} stories")
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
        println("🔓 All levels unlocked!")
    }
    
    /**
     * Kiểm tra xem story của level đã được xem chưa
     */
    fun hasViewedStory(levelId: Int): Boolean {
        return viewedStories.contains(levelId)
    }
    
    /**
     * Đánh dấu story của level đã được xem
     */
    fun markStoryAsViewed(levelId: Int) {
        if (viewedStories.add(levelId)) {
            saveProgress()
            println("📖 Story for level $levelId marked as viewed")
        }
    }
    
    /**
     * Reset trạng thái đã xem story
     */
    fun resetViewedStories() {
        viewedStories.clear()
        saveProgress()
        println("🔄 Viewed stories reset!")
    }
    
    /**
     * Lưu trạng thái game hiện tại
     */
    fun saveGameState(gameState: Int, levelId: Int, playerX: Float, playerY: Float) {
        hasActiveGame = true
        savedGameState = gameState
        savedLevelId = levelId
        savedPlayerX = playerX
        savedPlayerY = playerY
        
        prefs.edit()
            .putBoolean(KEY_HAS_ACTIVE_GAME, hasActiveGame)
            .putInt(KEY_CURRENT_STATE, savedGameState)
            .putInt(KEY_CURRENT_LEVEL, savedLevelId)
            .putFloat(KEY_PLAYER_X, savedPlayerX)
            .putFloat(KEY_PLAYER_Y, savedPlayerY)
            .apply()
        
        println("💾 Game state saved: State=$gameState, Level=$levelId, PlayerPos=($playerX, $playerY)")
    }
    
    /**
     * Tải trạng thái game đã lưu
     */
    fun loadGameState(): GameState? {
        hasActiveGame = prefs.getBoolean(KEY_HAS_ACTIVE_GAME, false)
        
        if (!hasActiveGame) {
            println("📁 No saved game state found")
            return null
        }
        
        savedGameState = prefs.getInt(KEY_CURRENT_STATE, -1)
        savedLevelId = prefs.getInt(KEY_CURRENT_LEVEL, -1)
        savedPlayerX = prefs.getFloat(KEY_PLAYER_X, 0f)
        savedPlayerY = prefs.getFloat(KEY_PLAYER_Y, 0f)
        
        println("📁 Game state loaded: State=$savedGameState, Level=$savedLevelId, PlayerPos=($savedPlayerX, $savedPlayerY)")
        
        return GameState(
            gameState = savedGameState,
            levelId = savedLevelId,
            playerX = savedPlayerX,
            playerY = savedPlayerY
        )
    }
    
    /**
     * Xóa trạng thái game đã lưu
     */
    fun clearGameState() {
        hasActiveGame = false
        savedGameState = -1
        savedLevelId = -1
        savedPlayerX = 0f
        savedPlayerY = 0f
        
        prefs.edit()
            .putBoolean(KEY_HAS_ACTIVE_GAME, false)
            .apply()
        
        println("🗑️ Game state cleared")
    }
    
    /**
     * Kiểm tra có game đang chơi dở không
     */
    fun hasActiveGame(): Boolean {
        return prefs.getBoolean(KEY_HAS_ACTIVE_GAME, false)
    }
    
    /**
     * Data class để lưu trạng thái game
     */
    data class GameState(
        val gameState: Int,
        val levelId: Int,
        val playerX: Float,
        val playerY: Float
    )
}
