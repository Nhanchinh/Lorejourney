package com.example.game.gameMechanic

import android.content.Context
import android.graphics.Canvas
import com.example.game.GameMap
import com.example.game.SpritePlayer
import com.example.game.entity.EntityManager
import com.example.game.entity.PlayerEntity
import com.example.game.entity.ShadowEntity
import com.example.game.map.TileConstants

/**
 * Shadow mechanic cho level 3
 * Quản lý toàn bộ logic shadow system
 */
class ShadowMechanic(
    private val context: Context,
    private val gameMap: GameMap,
    private val player: SpritePlayer
) {
    
    private val entityManager = EntityManager()
    private var playerEntity: PlayerEntity? = null
    private var shadowEntity: ShadowEntity? = null
    private var isInitialized = false
    
    fun initialize() {
        if (isInitialized) return
        
        playerEntity = PlayerEntity(player.x, player.y, context, gameMap)
        isInitialized = true
        println("🌟 Shadow mechanic initialized")
    }
    
    fun update(deltaTime: Long) {
        if (!isInitialized) return
        
        // Sync PlayerEntity position
        playerEntity?.let { pe ->
            pe.x = player.x
            pe.y = player.y
        }
        
        // Update shadow
        shadowEntity?.let { shadow ->
            if (shadow.isActive) {
                shadow.update(deltaTime)
            }
        }
        
        // Check mechanics
        checkShadowSpawn()
    }
    
    fun draw(canvas: Canvas) {
        shadowEntity?.let { shadow ->
            if (shadow.isVisible) {
                shadow.draw(canvas)
            }
        }
    }
    
    private fun checkShadowSpawn() {
        if (shadowEntity != null) return
        
        val playerTileX = player.getCurrentTileX()
        val playerTileY = player.getCurrentTileY()
        val currentTile = gameMap.getTile(playerTileX, playerTileY, 2) // Check active layer
        
        if (TileConstants.isShadowSpawn(currentTile)) {
            playerEntity?.let { pe ->
                shadowEntity = ShadowEntity(player.x, player.y, context, pe, gameMap)
                println("🌟 Shadow spawned at tile ($playerTileX, $playerTileY) on active layer")
            }
        }
    }
    
    fun getShadowInfo(): ShadowInfo? {
        return shadowEntity?.let { shadow ->
            ShadowInfo(
                directionChangeCount = shadow.getDirectionChangeCount(),
                isFollowing = shadow.isStillFollowing(),
                pathSize = shadow.getPathHistorySize()
            )
        }
    }
    
    /**
     * Get shadow position in tile coordinates
     */
    fun getShadowTilePosition(): Pair<Int, Int>? {
        return shadowEntity?.let { shadow ->
            Pair(shadow.getCurrentTileX(), shadow.getCurrentTileY())
        }
    }
    
    /**
     * Get player position in tile coordinates
     */
    fun getPlayerTilePosition(): Pair<Int, Int> {
        return Pair(player.getCurrentTileX(), player.getCurrentTileY())
    }
    
    /**
     * Check if shadow exists and is active
     */
    fun hasShadow(): Boolean {
        return shadowEntity != null && shadowEntity?.isActive == true
    }
    
    data class ShadowInfo(
        val directionChangeCount: Int,
        val isFollowing: Boolean,
        val pathSize: Int
    )
}
