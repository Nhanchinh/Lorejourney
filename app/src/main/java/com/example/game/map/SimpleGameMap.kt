package com.example.game.map

import android.content.Context
import android.graphics.Canvas
import com.example.game.Camera
import com.example.game.GameConstants
import com.example.game.gameMechanic.PushLogic

/**
 * Simple GameMap sử dụng hệ thống tile mới
 * Hỗ trợ cả code-based tiles và sprite-based tiles
 */
class SimpleGameMap(
    private val mapData: MapLoader.MapData,
    private val context: Context? = null,
    private val useSpriteMode: Boolean = false
) {
    val width = mapData.width
    val height = mapData.height
    val playerSpawnX = mapData.playerSpawnX
    val playerSpawnY = mapData.playerSpawnY
    
    private val tileRenderer = TileRenderer()
    
    init {
        // Khởi tạo sprite mode nếu cần
        if (useSpriteMode && context != null) {
            tileRenderer.initSpriteMode(context)
            tileRenderer.setSpriteMode(true)
            println("SimpleGameMap: Initialized with sprite mode")
        } else {
            println("SimpleGameMap: Initialized with code-based mode")
        }
    }
    
    fun getTile(x: Int, y: Int, layer: Int = 1): Int {
        if (x < 0 || x >= width || y < 0 || y >= height) return TileConstants.TILE_WALL
        return when (layer) {
            0 -> mapData.bottomLayer[y][x]
            1 -> mapData.mainLayer[y][x]
            2 -> mapData.activeLayer[y][x]
            else -> mapData.mainLayer[y][x] // Default to main layer
        }
    }
    
    // Compatibility method for old code
    fun getTile(x: Int, y: Int): Int {
        return getTile(x, y, 1) // Default to main layer
    }
    
    fun isWalkable(x: Int, y: Int): Boolean {
        // Check collision on main layer (terrain) và active layer (game objects)
        val mainTileId = getTile(x, y, 1)
        val activeTileId = getTile(x, y, 2)
        
        // Main layer walkability (terrain)
        val mainWalkable = TileConstants.isWalkable(mainTileId)
        
        // Active layer walkability (game objects)
        val activeWalkable = when (activeTileId) {
            TileConstants.TILE_EMPTY,
            TileConstants.TILE_TARGET,
            TileConstants.TILE_KEY,
            TileConstants.TILE_BUTTON,
            TileConstants.TILE_END,
            TileConstants.TILE_SHADOW_SPAWN,
            TileConstants.TILE_SHADOW_TRIGGER -> true
            
            TileConstants.TILE_PUSHABLE_STONE,
            TileConstants.TILE_STONE_ON_TARGET,
            TileConstants.TILE_SHADOW_DOOR -> false
            
            else -> true // Default to walkable for unknown tiles
        }
        
        return mainWalkable && activeWalkable
    }
    
    fun setTile(x: Int, y: Int, tileId: Int, layer: Int = 1) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            when (layer) {
                0 -> mapData.bottomLayer[y][x] = tileId
                1 -> mapData.mainLayer[y][x] = tileId
                2 -> mapData.activeLayer[y][x] = tileId
            }
        }
    }
    
    // Compatibility method for old code
    fun setTile(x: Int, y: Int, tileId: Int) {
        setTile(x, y, tileId, 1) // Default to main layer
    }
    
    fun draw(canvas: Canvas, camera: Camera, pushLogic: PushLogic? = null) {
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        
        val padding = 2
        val startX = ((camera.x / tileSize).toInt() - padding).coerceAtLeast(0)
        val endX = (((camera.x + GameConstants.SCREEN_WIDTH) / tileSize).toInt() + padding).coerceAtMost(width - 1)
        val startY = ((camera.y / tileSize).toInt() - padding).coerceAtLeast(0)
        val endY = (((camera.y + GameConstants.SCREEN_HEIGHT) / tileSize).toInt() + padding).coerceAtMost(height - 1)
        
        // Get animated objects if available
        val animatedObjects = pushLogic?.animator?.getAllAnimatedObjects() ?: emptyList()
        
        // Render layers in order: bottom -> main -> active
        // Bottom layer (background elements)
        for (y in startY..endY) {
            for (x in startX..endX) {
                val tileId = getTile(x, y, 0) // Bottom layer
                if (tileId != TileConstants.TILE_EMPTY) {
                    val drawX = x * tileSize
                    val drawY = y * tileSize
                    tileRenderer.drawTile(canvas, tileId, drawX, drawY, tileSize)
                }
            }
        }
        
        // Main layer (primary game elements)
        for (y in startY..endY) {
            for (x in startX..endX) {
                val tileId = getTile(x, y, 1) // Main layer
                if (tileId != TileConstants.TILE_EMPTY) {
                    val drawX = x * tileSize
                    val drawY = y * tileSize
                    tileRenderer.drawTile(canvas, tileId, drawX, drawY, tileSize)
                }
            }
        }
        
        // Active layer (foreground elements, effects)
        for (y in startY..endY) {
            for (x in startX..endX) {
                val tileId = getTile(x, y, 2) // Active layer
                if (tileId != TileConstants.TILE_EMPTY) {
                    // Always draw since we now manage map state properly
                    val drawX = x * tileSize
                    val drawY = y * tileSize
                    tileRenderer.drawTile(canvas, tileId, drawX, drawY, tileSize)
                }
            }
        }
        
        // Draw animated objects on top
        animatedObjects.forEach { animated ->
            tileRenderer.drawTile(canvas, animated.tileId, animated.x, animated.y, tileSize)
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        tileRenderer.cleanup()
    }
    
    companion object {
        /**
         * Tạo map mặc định để test với sprite mode disabled (để compatibility)
         */
        fun createDefaultMap(): SimpleGameMap {
            val mapData = createDefaultMapData()
            return SimpleGameMap(mapData)
        }
        
        /**
         * Tạo map6 sử dụng sprite mode
         */
        fun createSpriteMap(context: Context, mapData: MapLoader.MapData): SimpleGameMap {
            return SimpleGameMap(mapData, context, useSpriteMode = true)
        }
        
        /**
         * Tạo default map với sprite mode
         */
        fun createDefaultSpriteMap(context: Context): SimpleGameMap {
            val mapData = createDefaultMapData()
            return SimpleGameMap(mapData, context, useSpriteMode = true)
        }
        
        /**
         * Tạo map data mặc định
         */
        private fun createDefaultMapData(): MapLoader.MapData {
            val mapString = """
20
15
1
1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1
1,2,3,20,3,3,21,3,3,3,3,3,22,3,3,3,20,3,2,1
1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1
1,2,2,2,2,12,2,2,2,2,2,2,2,2,12,2,2,2,2,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,10,31,31,31,31,31,31,31,31,31,11,31,31,31,31,2,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,31,31,13,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,1
1,2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,2,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
            """.trimIndent()
            
            return MapLoader.loadFromString(mapString) ?: createFallbackMapData()
        }
        
        private fun createFallbackMapData(): MapLoader.MapData {
            val width = 10
            val height = 10
            val bottomLayer = Array(height) { IntArray(width) }
            val mainLayer = Array(height) { IntArray(width) }
            val activeLayer = Array(height) { IntArray(width) }
            
            // Tạo map đơn giản: wall border, floor inside
            for (y in 0 until height) {
                for (x in 0 until width) {
                    // Bottom layer: empty
                    bottomLayer[y][x] = TileConstants.TILE_EMPTY
                    
                    // Main layer: walls and floors
                    mainLayer[y][x] = if (x == 0 || x == width-1 || y == 0 || y == height-1) {
                        TileConstants.TILE_WALL
                    } else {
                        TileConstants.TILE_FLOOR
                    }
                    
                    // Active layer: empty
                    activeLayer[y][x] = TileConstants.TILE_EMPTY
                }
            }
            
            return MapLoader.MapData(width, height, 1, 1, bottomLayer, mainLayer, activeLayer)
        }
    }
}
