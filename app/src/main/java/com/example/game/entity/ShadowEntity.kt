package com.example.game.entity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.game.GameConstants
import com.example.game.GameMap
import com.example.game.map.TileConstants
import kotlin.math.*

/**
 * Shadow entity - bóng theo sau player theo yêu cầu
 */
class ShadowEntity(
    startX: Float,
    startY: Float,
    context: Context,
    private val player: PlayerEntity
) : Entity(startX, startY, context) {
    
    // Shadow properties
    private var isFollowing = true
    private val followDistance = 2 // 2 tiles distance
    private var directionChangeCount = 0
    private val maxDirectionChanges = 4
    
    // Path tracking để follow player
    private val pathHistory = mutableListOf<Pair<Float, Float>>()
    private var lastPlayerDirection = ""
    private var lastPlayerTileX = player.getCurrentTileX()
    private var lastPlayerTileY = player.getCurrentTileY()
    
    // Visual properties
    private val shadowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#88000000") // Semi-transparent black
    }
    
    private val shadowBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#AA000000")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        textAlign = Paint.Align.CENTER
        textSize = 20f
        isFakeBoldText = true
    }
    
    private val size = GameConstants.TILE_SIZE.toFloat() * 0.8f
    
    override fun update(deltaTime: Long) {
        if (!isFollowing || directionChangeCount >= maxDirectionChanges) {
            return
        }
        
        trackPlayerMovement()
        updateFollowPosition()
    }
    
    private fun trackPlayerMovement() {
        val currentPlayerTileX = player.getCurrentTileX()
        val currentPlayerTileY = player.getCurrentTileY()
        
        // Check if player moved to new tile
        if (currentPlayerTileX != lastPlayerTileX || currentPlayerTileY != lastPlayerTileY) {
            // Add player's current position to path history
            pathHistory.add(Pair(
                lastPlayerTileX * GameConstants.TILE_SIZE.toFloat(), 
                lastPlayerTileY * GameConstants.TILE_SIZE.toFloat()
            ))
            
            // Track direction changes
            val currentDirection = getMovementDirection(
                lastPlayerTileX, lastPlayerTileY,
                currentPlayerTileX, currentPlayerTileY
            )
            
            if (currentDirection != lastPlayerDirection && lastPlayerDirection.isNotEmpty()) {
                directionChangeCount++
                println("🌟 Shadow: Direction change #$directionChangeCount ($lastPlayerDirection -> $currentDirection)")
                
                if (directionChangeCount >= maxDirectionChanges) {
                    isFollowing = false
                    println("🌟 Shadow: Stopped following after $maxDirectionChanges direction changes")
                }
            }
            
            lastPlayerDirection = currentDirection
            lastPlayerTileX = currentPlayerTileX
            lastPlayerTileY = currentPlayerTileY
            
            // Keep only necessary path history
            while (pathHistory.size > followDistance + 2) {
                pathHistory.removeAt(0)
            }
        }
    }
    
    private fun updateFollowPosition() {
        if (pathHistory.size >= followDistance) {
            val targetPos = pathHistory[0] // Follow 4 tiles behind
            x = targetPos.first
            y = targetPos.second
            pathHistory.removeAt(0)
        }
    }
    
    private fun getMovementDirection(fromX: Int, fromY: Int, toX: Int, toY: Int): String {
        return when {
            toX > fromX -> "RIGHT"
            toX < fromX -> "LEFT"
            toY > fromY -> "DOWN"
            toY < fromY -> "UP"
            else -> ""
        }
    }
    
    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        
        // Draw shadow as tile ID 47 instead of circle
        val tileRenderer = com.example.game.map.TileRenderer()
        tileRenderer.initSpriteMode(context)
        tileRenderer.drawTile(canvas, 47, x, y, GameConstants.TILE_SIZE.toFloat())
        
        // Draw status indicator ở GIỮA tile thay vì phía trên
        val centerX = x + GameConstants.TILE_SIZE / 2f
        val centerY = y + GameConstants.TILE_SIZE *2f / 3f  // Đặt ở giữa tile
        
        if (!isFollowing || directionChangeCount >= maxDirectionChanges) {
            canvas.drawText("STOP", centerX, centerY, textPaint)
        } else {
            // Draw following indicator với text TO HƠN
            val followPaint = Paint().apply {
                color = Color.BLACK
                textAlign = Paint.Align.CENTER
                textSize = 20f  // Tăng từ 16f lên 24f (to hơn)
                isFakeBoldText = true  // Làm chữ đậm hơn
            }
            canvas.drawText("${directionChangeCount}/$maxDirectionChanges", 
                          centerX, centerY, followPaint)
        }
    }
    
    // Shadow specific methods
    fun isOnShadowTrigger(gameMap: GameMap): Boolean {
        val tileX = getCurrentTileX()
        val tileY = getCurrentTileY()
        val tileId = gameMap.getTile(tileX, tileY, 2) // Check active layer
        return TileConstants.isShadowTrigger(tileId)
    }
    
    fun stopFollowing() {
        isFollowing = false
        println("🌟 Shadow: Manually stopped following")
    }
    
    fun resetShadow() {
        isFollowing = true
        directionChangeCount = 0
        pathHistory.clear()
        lastPlayerDirection = ""
        println("🌟 Shadow: Reset to initial state")
    }
    
    // Getters
    fun getDirectionChangeCount(): Int = directionChangeCount
    fun isStillFollowing(): Boolean = isFollowing && directionChangeCount < maxDirectionChanges
    fun getPathHistorySize(): Int = pathHistory.size
}
