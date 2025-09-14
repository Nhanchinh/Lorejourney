package com.example.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.game.map.SimpleGameMap
import com.example.game.map.MapLoader
import android.content.Context

class GameMap(private val simpleMap: SimpleGameMap) {
    val width = simpleMap.width
    val height = simpleMap.height
    val playerSpawnX = simpleMap.playerSpawnX
    val playerSpawnY = simpleMap.playerSpawnY
    
    fun getTile(x: Int, y: Int): Int {
        return simpleMap.getTile(x, y)
    }
    
    fun getTile(x: Int, y: Int, layer: Int): Int {
        return simpleMap.getTile(x, y, layer)
    }
    
    fun isWalkable(x: Int, y: Int): Boolean {
        return simpleMap.isWalkable(x, y)
    }
    
    fun setTile(x: Int, y: Int, tileId: Int) {
        simpleMap.setTile(x, y, tileId)
    }
    
    fun setTile(x: Int, y: Int, tileId: Int, layer: Int) {
        simpleMap.setTile(x, y, tileId, layer)
    }
    
    fun draw(canvas: Canvas, camera: Camera) {
        simpleMap.draw(canvas, camera)
    }
    
    companion object {
        /**
         * Centralized method để load level theo ID
         */
        fun loadLevel(context: Context, levelId: Int): GameMap {
            // Xử lý đặc biệt cho map6 (sprite-based)
            if (levelId == 6) {
                return loadMap6(context)
            }
            
            // Thử load từ assets trước cho map 1-5 (code-based)
            return try {
                val loadedMap = loadFromAssets(context, "level$levelId.txt")
                println("Loaded map from assets: level$levelId.txt")
                loadedMap
            } catch (e: Exception) {
                println("Failed to load from assets: ${e.message}")
                // Fallback về test map
                createTestMap()
            }
        }
        
        /**
         * Load map6 sử dụng sprite system
         */
        fun loadMap6(context: Context): GameMap {
            return try {
                println("Loading map6 with sprite system...")
                val mapData = MapLoader.loadFromAssets(context, "level6.txt")
                val simpleMap = if (mapData != null) {
                    println("Successfully loaded map6 data: ${mapData.width}x${mapData.height}")
                    SimpleGameMap.createSpriteMap(context, mapData)
                } else {
                    println("Failed to load map6 data, using default")
                    SimpleGameMap.createDefaultMap()
                }
                GameMap(simpleMap)
            } catch (e: Exception) {
                println("Failed to load map6: ${e.message}")
                e.printStackTrace()
                // Fallback
                createTestMap()
            }
        }
        
        fun createLevel1(): GameMap {
            val simpleMap = SimpleGameMap.createDefaultMap()
            return GameMap(simpleMap)
        }
        
        fun createTestMap(): GameMap = createLevel1()
        
        /**
         * Load map từ assets
         */
        fun loadFromAssets(context: Context, fileName: String): GameMap {
            println("Attempting to load: $fileName") // Debug log
            val mapData = MapLoader.loadFromAssets(context, fileName)
            val simpleMap = if (mapData != null) {
                println("Successfully loaded map data: ${mapData.width}x${mapData.height}") // Debug log
                SimpleGameMap(mapData)
            } else {
                println("Failed to load map data, using default") // Debug log
                SimpleGameMap.createDefaultMap()
            }
            return GameMap(simpleMap)
        }
        
        /**
         * Load map từ string
         */
        fun loadFromString(mapString: String): GameMap {
            val mapData = MapLoader.loadFromString(mapString)
            val simpleMap = if (mapData != null) {
                SimpleGameMap(mapData)
            } else {
                SimpleGameMap.createDefaultMap()
            }
            return GameMap(simpleMap)
        }
        
        /**
         * Cleanup resources cho GameMap
         */
        fun cleanup(gameMap: GameMap) {
            gameMap.simpleMap.cleanup()
        }
    }
}
