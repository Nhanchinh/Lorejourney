package com.example.game.map

import android.graphics.Canvas
import com.example.game.Camera
import com.example.game.GameConstants

/**
 * Simple GameMap sử dụng hệ thống tile mới
 */
class SimpleGameMap(private val mapData: MapLoader.MapData) {
    val width = mapData.width
    val height = mapData.height
    val playerSpawnX = mapData.playerSpawnX
    val playerSpawnY = mapData.playerSpawnY
    
    private val tileRenderer = TileRenderer()
    
    fun getTile(x: Int, y: Int): Int {
        if (x < 0 || x >= width || y < 0 || y >= height) return TileConstants.TILE_WALL
        return mapData.tiles[y][x]
    }
    
    fun isWalkable(x: Int, y: Int): Boolean {
        val tileId = getTile(x, y)
        return TileConstants.isWalkable(tileId)
    }
    
    fun setTile(x: Int, y: Int, tileId: Int) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            mapData.tiles[y][x] = tileId
        }
    }
    
    fun draw(canvas: Canvas, camera: Camera) {
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        
        val padding = 2
        val startX = ((camera.x / tileSize).toInt() - padding).coerceAtLeast(0)
        val endX = (((camera.x + GameConstants.SCREEN_WIDTH) / tileSize).toInt() + padding).coerceAtMost(width - 1)
        val startY = ((camera.y / tileSize).toInt() - padding).coerceAtLeast(0)
        val endY = (((camera.y + GameConstants.SCREEN_HEIGHT) / tileSize).toInt() + padding).coerceAtMost(height - 1)
        
        for (y in startY..endY) {
            for (x in startX..endX) {
                val tileId = getTile(x, y)
                val drawX = x * tileSize
                val drawY = y * tileSize
                
                tileRenderer.drawTile(canvas, tileId, drawX, drawY, tileSize)
            }
        }
    }
    
    companion object {
        /**
         * Tạo map mặc định để test
         */
        fun createDefaultMap(): SimpleGameMap {
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
            
            val mapData = MapLoader.loadFromString(mapString)
            return if (mapData != null) {
                SimpleGameMap(mapData)
            } else {
                // Fallback map nếu parse lỗi
                createFallbackMap()
            }
        }
        
        private fun createFallbackMap(): SimpleGameMap {
            val width = 10
            val height = 10
            val tiles = Array(height) { IntArray(width) }
            
            // Tạo map đơn giản: wall border, floor inside
            for (y in 0 until height) {
                for (x in 0 until width) {
                    tiles[y][x] = if (x == 0 || x == width-1 || y == 0 || y == height-1) {
                        TileConstants.TILE_WALL
                    } else {
                        TileConstants.TILE_FLOOR
                    }
                }
            }
            
            val mapData = MapLoader.MapData(width, height, 1, 1, tiles)
            return SimpleGameMap(mapData)
        }
    }
}
