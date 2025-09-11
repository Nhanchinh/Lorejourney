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

/**
 * Quản lý các màn hình game
 */
class GameStateManager(private val context: Context) {
    
    private var currentScreen: Screen? = null
    private var currentState = GameConstants.STATE_MENU
    private var nextState = -1
    private var nextLevelId = 1
    private val animationManager = AnimationManager()
    
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
    
    // THÊM METHOD NÀY
    fun startLevel(levelId: Int) {
        nextLevelId = levelId
        nextState = GameConstants.STATE_PLAYING
    }
    
    private fun performStateChange() {
        currentState = nextState
        currentScreen = when (currentState) {
            GameConstants.STATE_MENU -> MainMenuScreen(this, context, animationManager)
            GameConstants.STATE_LEVEL_SELECT -> LevelSelectScreen(this, context, animationManager)
            GameConstants.STATE_PLAYING -> GameScreen(this, nextLevelId, context)
            GameConstants.STATE_SETTINGS -> SettingsScreen(this, context, animationManager)
            else -> MainMenuScreen(this, context, animationManager)
        }
        nextState = -1
    }
    
    fun onScreenSizeChanged(width: Int, height: Int) {
        GameConstants.SCREEN_WIDTH = width
        GameConstants.SCREEN_HEIGHT = height
        currentScreen?.onScreenSizeChanged(width, height)
    }
}
