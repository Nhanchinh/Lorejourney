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
    private val player: PlayerEntity,
    private val gameMap: GameMap,
    private val spawnTileX: Int = -1, // Tile X nơi shadow được spawn
    private val spawnTileY: Int = -1  // Tile Y nơi shadow được spawn
) : Entity(startX, startY, context) {
    
    // Shadow properties
    private var isFollowing = true
    private var followDistance = 3 // 3 tiles distance
    
    // Path tracking để follow player
    private val pathHistory = mutableListOf<Pair<Float, Float>>()
    private var lastPlayerTileX = player.getCurrentTileX()
    private var lastPlayerTileY = player.getCurrentTileY()
    
    override fun update(deltaTime: Long) {
        if (!isFollowing) {
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
            // Check if player is on ice
            val playerTileId = gameMap.getTile(currentPlayerTileX, currentPlayerTileY, 1)
            val isPlayerOnIce = com.example.game.map.TileConstants.isIce(playerTileId)
            
            if (isPlayerOnIce) {
                followDistance++
                println("🧊 Player on ice - Shadow follow distance increased to: $followDistance")
            }
            
            // Add player's current position to path history
            pathHistory.add(Pair(
                lastPlayerTileX * GameConstants.TILE_SIZE.toFloat(), 
                lastPlayerTileY * GameConstants.TILE_SIZE.toFloat()
            ))
            
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
    
    override fun draw(canvas: Canvas) {
        if (!isVisible) return
        
        // Draw shadow as tile ID 47 instead of circle
        val tileRenderer = com.example.game.map.TileRenderer()
        tileRenderer.initSpriteMode(context)
        tileRenderer.drawTile(canvas, 47, x, y, GameConstants.TILE_SIZE.toFloat())
    }
    
    // Shadow specific methods
    fun isOnShadowTrigger(): Boolean {
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
        pathHistory.clear()
        println("🌟 Shadow: Reset to initial state")
    }
    
    // Getters
    fun getDirectionChangeCount(): Int = 0 // Always return 0 since we removed direction counting
    fun isStillFollowing(): Boolean = isFollowing // Simply return isFollowing status
    fun getPathHistorySize(): Int = pathHistory.size
    fun getSpawnTilePosition(): Pair<Int, Int> = Pair(spawnTileX, spawnTileY)
}
