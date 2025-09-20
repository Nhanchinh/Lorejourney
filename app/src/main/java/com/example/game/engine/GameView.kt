package com.example.game.engine

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import com.example.game.GameConstants
import com.example.game.core.GameStateManager

/**
 * Main game view với Canvas rendering
 */
class GameView(
    context: Context,
    private val containerLayout: FrameLayout
) : SurfaceView(context), SurfaceHolder.Callback {
    
    private var gameThread: GameThread? = null
    private var gameStateManager: GameStateManager? = null
    private val gameContext = context
    
    init {
        holder.addCallback(this)
        isFocusable = true
        isClickable = true
    }
    
    override fun surfaceCreated(holder: SurfaceHolder) {
        gameStateManager = GameStateManager(gameContext, containerLayout)
        gameThread = GameThread(holder, this)
        gameThread?.isRunning = true
        gameThread?.start()
    }
    
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        GameConstants.SCREEN_WIDTH = width
        GameConstants.SCREEN_HEIGHT = height
        gameStateManager?.onScreenSizeChanged(width, height)
    }
    
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread?.isRunning = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                // Retry
            }
        }
    }
    
    fun update(deltaTime: Long) {
        gameStateManager?.update(deltaTime)
    }
    
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        gameStateManager?.render(canvas)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val handled = gameStateManager?.handleTouch(event) ?: false
        performClick()
        return handled || super.onTouchEvent(event)
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
