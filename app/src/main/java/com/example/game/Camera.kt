package com.example.game

import android.graphics.Canvas

/**
 * Camera để follow player và vẽ world full screen
 */
class Camera {
    var x = 0f
    var y = 0f
    
    private var targetX = 0f
    private var targetY = 0f
    
    // Smooth following
    private val followSpeed = 0.15f
    
    fun update(playerX: Float, playerY: Float, mapWidth: Int, mapHeight: Int) {
        val screenW = GameConstants.SCREEN_WIDTH.toFloat()
        val screenH = GameConstants.SCREEN_HEIGHT.toFloat()
        
        // Target camera position (center player on screen)
        targetX = playerX - screenW / 2f
        targetY = playerY - screenH / 2f
        
        // Calculate map bounds in pixels
        val mapWidthPixels = mapWidth * GameConstants.TILE_SIZE.toFloat()
        val mapHeightPixels = mapHeight * GameConstants.TILE_SIZE.toFloat()
        
        // Clamp camera to keep map filling screen
        val maxX = mapWidthPixels - screenW
        val maxY = mapHeightPixels - screenH
        
        // If map is smaller than screen, center it
        if (mapWidthPixels < screenW) {
            targetX = -(screenW - mapWidthPixels) / 2f
        } else {
            targetX = targetX.coerceIn(0f, maxX)
        }
        
        if (mapHeightPixels < screenH) {
            targetY = -(screenH - mapHeightPixels) / 2f
        } else {
            targetY = targetY.coerceIn(0f, maxY)
        }
        
        // Smooth movement
        x += (targetX - x) * followSpeed
        y += (targetY - y) * followSpeed
    }
    
    fun apply(canvas: Canvas) {
        // Snap translation to integer pixels to avoid tile seams
        val snapX = kotlin.math.floor(x).toFloat()
        val snapY = kotlin.math.floor(y).toFloat()
        canvas.translate(-snapX, -snapY)
    }
    
    fun worldToScreen(worldX: Float, worldY: Float): Pair<Float, Float> {
        return Pair(worldX - x, worldY - y)
    }
    
    fun screenToWorld(screenX: Float, screenY: Float): Pair<Float, Float> {
        return Pair(screenX + x, screenY + y)
    }
}
