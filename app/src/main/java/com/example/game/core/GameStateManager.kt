package com.example.game.core

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import com.example.game.GameConstants
import com.example.game.animation.AnimationManager
import com.example.game.screens.GameScreen
import com.example.game.screens.LevelSelectScreen
import com.example.game.screens.MainMenuScreen
import com.example.game.screens.Screen
import com.example.game.screens.SettingsScreen
import com.example.game.screens.PauseScreen

/**
 * Quản lý các màn hình game
 */
class GameStateManager(private val context: Context) {
    
    private var currentScreen: Screen? = null
    private var currentState = GameConstants.STATE_MENU
    private var nextState = -1
    private var nextLevelId = 1
    private val animationManager = AnimationManager()
    
    // THÊM: Để track game screen cho pause/resume
    private var currentGameScreen: GameScreen? = null
    
    init {
        currentScreen = MainMenuScreen(this, context, animationManager)
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
                    currentGameScreen = GameScreen(this, nextLevelId, context)
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
}
