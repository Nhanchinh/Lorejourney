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
         * Tất cả các level đều sử dụng sprite-based rendering
         */
        fun loadLevel(context: Context, levelId: Int): GameMap {
            // Sử dụng sprite mode cho tất cả các level
            return try {
                val mapData = MapLoader.loadFromAssets(context, "level$levelId.txt")
                val simpleMap = if (mapData != null) {
                    println("Successfully loaded level$levelId data: ${mapData.width}x${mapData.height}")
                    SimpleGameMap.createSpriteMap(context, mapData)
                } else {
                    println("Failed to load level$levelId data, using default")
                    SimpleGameMap.createDefaultSpriteMap(context)
                }
                GameMap(simpleMap)
            } catch (e: Exception) {
                println("Failed to load level$levelId: ${e.message}")
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
         * Load map từ assets với sprite mode
         */
        fun loadFromAssets(context: Context, fileName: String): GameMap {
            println("Attempting to load: $fileName") // Debug log
            val mapData = MapLoader.loadFromAssets(context, fileName)
            val simpleMap = if (mapData != null) {
                println("Successfully loaded map data: ${mapData.width}x${mapData.height}") // Debug log
                SimpleGameMap.createSpriteMap(context, mapData)
            } else {
                println("Failed to load map data, using default") // Debug log
                SimpleGameMap.createDefaultSpriteMap(context)
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
