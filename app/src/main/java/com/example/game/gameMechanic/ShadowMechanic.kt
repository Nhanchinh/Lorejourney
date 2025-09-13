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
        checkShadowTriggers()
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
        val currentTile = gameMap.getTile(playerTileX, playerTileY)
        
        if (TileConstants.isShadowSpawn(currentTile)) {
            playerEntity?.let { pe ->
                shadowEntity = ShadowEntity(player.x, player.y, context, pe)
                println("🌟 Shadow spawned at tile ($playerTileX, $playerTileY)")
            }
        }
    }
    
    private fun checkShadowTriggers() {
        shadowEntity?.let { shadow ->
            if (shadow.isOnShadowTrigger(gameMap)) {
                openShadowDoors()
            }
        }
    }
    
    private fun openShadowDoors() {
        var doorsOpened = 0
        for (y in 0 until gameMap.height) {
            for (x in 0 until gameMap.width) {
                if (gameMap.getTile(x, y) == TileConstants.TILE_SHADOW_DOOR) {
                    gameMap.setTile(x, y, TileConstants.TILE_FLOOR)
                    doorsOpened++
                }
            }
        }
        if (doorsOpened > 0) {
            println("🚪 Opened $doorsOpened shadow door(s)")
        }
    }
    
    fun isPuzzleComplete(): Boolean {
        // Puzzle complete khi không còn shadow door nào
        for (y in 0 until gameMap.height) {
            for (x in 0 until gameMap.width) {
                if (gameMap.getTile(x, y) == TileConstants.TILE_SHADOW_DOOR) {
                    return false
                }
            }
        }
        return true
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
    
    data class ShadowInfo(
        val directionChangeCount: Int,
        val isFollowing: Boolean,
        val pathSize: Int
    )
}
