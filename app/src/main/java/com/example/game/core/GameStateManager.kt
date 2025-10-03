package com.example.game.core

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.game.GameConstants
import com.example.game.SaveManager
import com.example.game.animation.AnimationManager
import com.example.game.music.MusicManager
import com.example.game.screens.GameScreen
import com.example.game.screens.LevelSelectScreen
import com.example.game.screens.MainMenuScreen
import com.example.game.screens.Screen
import com.example.game.screens.SettingsScreen
import com.example.game.screens.PauseScreen
import com.example.game.screens.WorldSelectScreen

/**
 * Quản lý các màn hình game
 */
class GameStateManager(
    private val context: Context,
    private val containerLayout: FrameLayout
) {
    
    private var currentScreen: Screen? = null
    private var currentState = GameConstants.STATE_MENU
    private var nextState = -1
    private var nextLevelId = 1
    private val animationManager = AnimationManager()
    
    // THÊM: Để track world selection
    var selectedWorld = 1 // Default to world 1
    
    // THÊM: Để track last completed level for auto-selection
    var lastCompletedLevel = -1
    
    // THÊM: Để track game screen cho pause/resume
    private var currentGameScreen: GameScreen? = null
    
    init {
        // Kiểm tra xem có saved game state không
        val savedState = SaveManager.loadGameState()
        if (savedState != null && savedState.gameState == GameConstants.STATE_PLAYING) {
            // Khôi phục game đang chơi
            currentState = GameConstants.STATE_PLAYING
            nextLevelId = savedState.levelId
            currentGameScreen = GameScreen(this, savedState.levelId, context, containerLayout)
            currentScreen = currentGameScreen
            println("🔄 Restored game: Level ${savedState.levelId}")
        } else {
            // Bắt đầu từ menu
            currentScreen = MainMenuScreen(this, context, animationManager)
        }
    }
    
    fun update(deltaTime: Long) {
        animationManager.update(deltaTime)
        currentScreen?.update(deltaTime)
        
        if (nextState != -1 && !animationManager.isTransitionActive()) {
            performStateChange()
        }
    }
    
    fun render(canvas: Canvas) {
        currentScreen?.draw(canvas)
        animationManager.drawTransition(canvas, canvas.width, canvas.height)
    }
    
    fun handleTouch(event: MotionEvent): Boolean {
        return currentScreen?.handleTouch(event) ?: false
    }
    
    fun changeState(newState: Int) {
        if (newState != currentState) {
            nextState = newState
        }
    }
    
    fun startLevel(levelId: Int) {
        nextLevelId = levelId
        nextState = GameConstants.STATE_PLAYING
    }
    
    // THÊM: Method để pause game
    fun pauseGame() {
        if (currentState == GameConstants.STATE_PLAYING) {
            nextState = GameConstants.STATE_PAUSED
        }
    }
    
    // THÊM: Method để resume game
    fun resumeGame() {
        if (currentState == GameConstants.STATE_PAUSED) {
            nextState = GameConstants.STATE_PLAYING
        }
    }
    
    fun onScreenSizeChanged(width: Int, height: Int) {
        // Handle screen size changes if needed
    }
    
    private fun performStateChange() {
        when (nextState) {
            GameConstants.STATE_MENU -> {
                currentScreen = MainMenuScreen(this, context, animationManager)
                currentGameScreen = null // Clear game screen
            }
            
            GameConstants.STATE_WORLD_SELECT -> {
                currentScreen = WorldSelectScreen(this, context, animationManager)
                currentGameScreen = null // Clear game screen
            }
            
            GameConstants.STATE_LEVEL_SELECT -> {
                currentScreen = LevelSelectScreen(this, context, animationManager)
                currentGameScreen = null // Clear game screen
            }
            
            GameConstants.STATE_PLAYING -> {
                if (currentState == GameConstants.STATE_PAUSED && currentGameScreen != null) {
                    // Resume từ pause - dùng lại GameScreen cũ
                    currentScreen = currentGameScreen
                } else {
                    // Start level mới
                    currentGameScreen = GameScreen(this, nextLevelId, context, containerLayout)
                    currentScreen = currentGameScreen
                }
            }
            
            GameConstants.STATE_SETTINGS -> {
                currentScreen = SettingsScreen(this, context, animationManager)
            }
            
            GameConstants.STATE_PAUSED -> {
                if (currentScreen is GameScreen) {
                    val gameScreen = currentScreen as GameScreen
                    val progress = gameScreen.getCurrentProgress()
                    currentScreen = PauseScreen(this, nextLevelId, context, progress)
                }
            }
        }
        
        currentState = nextState
        nextState = -1
    }

    // THÊM: Method để restart level hiện tại
    fun restartCurrentLevel(levelId: Int) {
        println("🔄 Restarting level $levelId...")
        
        // Tạo GameScreen mới cho level này
        currentGameScreen = GameScreen(this, levelId, context, containerLayout)
        currentScreen = currentGameScreen
        
        // Chuyển về trạng thái playing
        currentState = GameConstants.STATE_PLAYING
        nextState = GameConstants.STATE_PLAYING
        
        println("✅ Level $levelId restarted successfully!")
    }
    
    /**
     * Lưu trạng thái game hiện tại khi pause
     */
    fun saveCurrentState() {
        when (currentState) {
            GameConstants.STATE_PLAYING -> {
                // Lưu trạng thái khi đang chơi
                currentGameScreen?.let { gameScreen ->
                    val progress = gameScreen.getCurrentProgress()
                    SaveManager.saveGameState(
                        gameState = currentState,
                        levelId = nextLevelId,
                        playerX = progress.playerX,
                        playerY = progress.playerY
                    )
                    println("💾 Game state saved during gameplay")
                }
            }
            GameConstants.STATE_PAUSED -> {
                // Đã lưu rồi, không cần lưu lại
                println("⏸️ Game is paused, state already saved")
            }
            else -> {
                // Xóa saved state nếu không đang chơi game
                SaveManager.clearGameState()
                println("🗑️ Cleared game state (not in gameplay)")
            }
        }
    }
    
    /**
     * Khôi phục trạng thái game khi resume
     */
    fun restoreState() {
        // Logic khôi phục đã được xử lý trong init()
        // Method này có thể để trống hoặc dùng để refresh music
        when (currentState) {
            GameConstants.STATE_PLAYING -> {
                MusicManager.playInGameMusic(context)
                println("🎵 Resumed in-game music")
            }
            GameConstants.STATE_MENU, GameConstants.STATE_LEVEL_SELECT, 
            GameConstants.STATE_WORLD_SELECT, GameConstants.STATE_SETTINGS -> {
                MusicManager.playWaitingHallMusic(context)
                println("🎵 Resumed menu music")
            }
        }
    }
}
