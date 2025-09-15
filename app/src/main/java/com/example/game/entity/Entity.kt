package com.example.game.entity

import android.content.Context
import android.graphics.Canvas
import com.example.game.GameConstants

/**
 * Base class cho tất cả entities trong game
 */
abstract class Entity(
    startX: Float,
    startY: Float,
    protected val context: Context
) {
    // Entity state
    var isActive = true
    var isVisible = true
    
    // Position properties - OPEN để có thể override
    open var x: Float = startX
    open var y: Float = startY
    
    // Common methods mà tất cả entities cần implement
    abstract fun update(deltaTime: Long)
    abstract fun draw(canvas: Canvas)
    
    // Utility methods - OPEN để có thể override
    open fun getCurrentTileX(): Int = ((x + GameConstants.TILE_SIZE / 2) / GameConstants.TILE_SIZE).toInt()
    open fun getCurrentTileY(): Int = ((y + GameConstants.TILE_SIZE / 2) / GameConstants.TILE_SIZE).toInt()
    
    open fun getCenterX(): Float = x + GameConstants.TILE_SIZE / 2f
    open fun getCenterY(): Float = y + GameConstants.TILE_SIZE / 2f
    
    // Collision detection
    fun distanceTo(other: Entity): Float {
        val dx = getCenterX() - other.getCenterX()
        val dy = getCenterY() - other.getCenterY()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
    
    // Check if entity is within screen bounds
    fun isOnScreen(cameraX: Float, cameraY: Float, screenWidth: Int, screenHeight: Int): Boolean {
        val entityScreenX = x - cameraX
        val entityScreenY = y - cameraY
        val entitySize = GameConstants.TILE_SIZE.toFloat()
        
        return entityScreenX + entitySize >= 0 && 
               entityScreenX <= screenWidth &&
               entityScreenY + entitySize >= 0 && 
               entityScreenY <= screenHeight
    }
    
    // Lifecycle
    open fun dispose() {
        // Override nếu cần cleanup
    }
}