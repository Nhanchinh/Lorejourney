package com.example.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.game.map.SimpleGameMap
import com.example.game.map.MapLoader
import com.example.game.gameMechanic.PushLogic
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
    
    fun draw(canvas: Canvas, camera: Camera, pushLogic: PushLogic? = null, player: com.example.game.SpritePlayer? = null) {
        simpleMap.draw(canvas, camera, pushLogic, player)
    }
    
    val tileRenderer = simpleMap.tileRenderer
    
    companion object {
        /**
         * Map level ID to world-based file structure
         */
        private fun getLevelFileName(levelId: Int): String {
            return when (levelId) {
                1 -> "worlds/world1/level1-1.txt"
                2 -> "worlds/world1/level1-2.txt"
                3 -> "worlds/world1/level1-3.txt"
                4 -> "worlds/world1/level1-4.txt"
                5 -> "worlds/world2/level2-1.txt"
                6 -> "worlds/world2/level2-2.txt"
                7 -> "worlds/world2/level2-3.txt"
                8 -> "worlds/world3/level3-1.txt"
                9 -> "worlds/world3/level3-2.txt"
                10 -> "worlds/world3/level3-3.txt"
                11 -> "worlds/world3/level3-4.txt"
                12 -> "worlds/world3/level3-5.txt"
                13 -> "worlds/world3/level3-6.txt"
                14 -> "worlds/world3/level3-7.txt"
                15 -> "worlds/world3/level3-8.txt"
                else -> "level$levelId.txt" // Fallback to legacy naming
            }
        }
        
        /**
         * Centralized method để load level theo ID với world-based structure
         * Tất cả các level đều sử dụng sprite-based rendering
         */
        fun loadLevel(context: Context, levelId: Int): GameMap {
            // Sử dụng sprite mode cho tất cả các level
            return try {
                // Map level ID to world-based file structure
                val levelFileName = getLevelFileName(levelId)
                val mapData = MapLoader.loadFromAssets(context, levelFileName)
                val simpleMap = if (mapData != null) {
                    println("Successfully loaded $levelFileName data: ${mapData.width}x${mapData.height}")
                    SimpleGameMap.createSpriteMap(context, mapData)
                } else {
                    println("Failed to load $levelFileName data, trying fallback to legacy level$levelId.txt")
                    // Fallback to legacy naming for backward compatibility
                    val fallbackData = MapLoader.loadFromAssets(context, "level$levelId.txt")
                    if (fallbackData != null) {
                        SimpleGameMap.createSpriteMap(context, fallbackData)
                    } else {
                        println("Both new and legacy formats failed, using default")
                        SimpleGameMap.createDefaultSpriteMap(context)
                    }
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
