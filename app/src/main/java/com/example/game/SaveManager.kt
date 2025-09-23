package com.example.game

import android.content.Context
import android.content.SharedPreferences

/**
 * Quản lý việc lưu/tải progress game
 */
object SaveManager {
    private const val PREFS_NAME = "TopDownGamePrefs"
    private const val KEY_MAX_UNLOCKED_LEVEL = "max_unlocked_level"
    private const val KEY_VIEWED_STORIES = "viewed_stories"
    
    private lateinit var prefs: SharedPreferences
    private val viewedStories = mutableSetOf<Int>()
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProgress()
        loadViewedStories()
    }
    
    fun saveProgress() {
        prefs.edit()
            .putInt(KEY_MAX_UNLOCKED_LEVEL, GameConstants.MAX_UNLOCKED_LEVEL)
            .putStringSet(KEY_VIEWED_STORIES, viewedStories.map { it.toString() }.toSet())
            .apply()
        
        println("💾 Progress saved: Max unlocked level = ${GameConstants.MAX_UNLOCKED_LEVEL}")
    }
    
    private fun loadProgress() {
        GameConstants.MAX_UNLOCKED_LEVEL = prefs.getInt(KEY_MAX_UNLOCKED_LEVEL, 1)
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
}
