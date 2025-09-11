package com.example.game.engine

import android.graphics.Canvas
import android.view.SurfaceHolder

/**
 * Thread chính cho game loop
 */
class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {
    
    var isRunning = false
    private val targetFPS = 60
    private val targetTime = 1000 / targetFPS
    
    override fun run() {
        var lastTime = System.currentTimeMillis()
        
        while (isRunning) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - lastTime
            lastTime = currentTime
            
            // Update
            gameView.update(deltaTime)
            
            // Render
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        gameView.draw(canvas)
                    }
                }
            } finally {
                canvas?.let { surfaceHolder.unlockCanvasAndPost(it) }
            }
            
            // FPS control
            val frameTime = System.currentTimeMillis() - currentTime
            if (frameTime < targetTime) {
                try {
                    sleep(targetTime - frameTime)
                } catch (e: InterruptedException) {
                    // Handle interruption
                }
            }
        }
    }
}
