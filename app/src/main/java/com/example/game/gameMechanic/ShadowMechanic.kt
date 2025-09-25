package com.example.game.gameMechanic

import android.content.Context
import android.graphics.Canvas
import com.example.game.GameMap
import com.example.game.SpritePlayer
import com.example.game.entity.EntityManager
import com.example.game.entity.PlayerEntity
import com.example.game.entity.ShadowEntity
import com.example.game.map.TileConstants
import com.example.game.music.MusicManager

/**
 * Shadow mechanic cho level 3
 * Quản lý toàn bộ logic shadow system - hỗ trợ nhiều shadows
 */
class ShadowMechanic(
    private val context: Context,
    private val gameMap: GameMap,
    private val player: SpritePlayer
) {
    
    private val entityManager = EntityManager()
    private var playerEntity: PlayerEntity? = null
    private val shadowEntities = mutableListOf<ShadowEntity>()
    private val spawnedTiles = mutableSetOf<Pair<Int, Int>>() // Track tiles đã spawn shadow
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
        
        // Update all shadows
        shadowEntities.forEach { shadow ->
            if (shadow.isActive) {
                shadow.update(deltaTime)
            }
        }
        
        // Remove inactive shadows
        shadowEntities.removeAll { !it.isActive }
        
        // Check mechanics
        checkShadowSpawn()
    }
    
    fun draw(canvas: Canvas) {
        shadowEntities.forEach { shadow ->
            if (shadow.isVisible) {
                shadow.draw(canvas)
            }
        }
    }
    
    private fun checkShadowSpawn() {
        val playerTileX = player.getCurrentTileX()
        val playerTileY = player.getCurrentTileY()
        val currentTile = gameMap.getTile(playerTileX, playerTileY, 2) // Check active layer
        
        if (TileConstants.isShadowSpawn(currentTile)) {
            val tilePosition = Pair(playerTileX, playerTileY)
            
            // Chỉ tạo shadow mới nếu tile này chưa spawn shadow
            if (!spawnedTiles.contains(tilePosition)) {
                playerEntity?.let { pe ->
                    val newShadow = ShadowEntity(
                        player.x, 
                        player.y, 
                        context, 
                        pe, 
                        gameMap,
                        playerTileX, // spawn tile X
                        playerTileY  // spawn tile Y
                    )
                    shadowEntities.add(newShadow)
                    spawnedTiles.add(tilePosition)
                    MusicManager.playSound(context, "ghost")
                    println("🌟 Shadow spawned at tile ($playerTileX, $playerTileY) - Total shadows: ${shadowEntities.size}")
                }
            }
        }
    }
    
    fun getShadowInfo(): ShadowInfo? {
        // Trả về thông tin của shadow đầu tiên (hoặc có thể mở rộng để trả về list)
        return shadowEntities.firstOrNull()?.let { shadow ->
            ShadowInfo(
                directionChangeCount = shadow.getDirectionChangeCount(),
                isFollowing = shadow.isStillFollowing(),
                pathSize = shadow.getPathHistorySize()
            )
        }
    }
    
    /**
     * Get all shadows info
     */
    fun getAllShadowsInfo(): List<ShadowInfo> {
        return shadowEntities.map { shadow ->
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
        // Trả về vị trí của shadow đầu tiên (backward compatibility)
        return shadowEntities.firstOrNull()?.let { shadow ->
            Pair(shadow.getCurrentTileX(), shadow.getCurrentTileY())
        }
    }
    
    /**
     * Get all shadows positions
     */
    fun getAllShadowTilePositions(): List<Pair<Int, Int>> {
        return shadowEntities.map { shadow ->
            Pair(shadow.getCurrentTileX(), shadow.getCurrentTileY())
        }
    }    /**
     * Get player position in tile coordinates
     */
    fun getPlayerTilePosition(): Pair<Int, Int> {
        return Pair(player.getCurrentTileX(), player.getCurrentTileY())
    }
    
    /**
     * Check if shadow exists and is active
     */
    fun hasShadow(): Boolean {
        return shadowEntities.any { it.isActive }
    }
    
    /**
     * Get number of active shadows
     */
    fun getShadowCount(): Int {
        return shadowEntities.count { it.isActive }
    }
    
    data class ShadowInfo(
        val directionChangeCount: Int,
        val isFollowing: Boolean,
        val pathSize: Int
    )
}
