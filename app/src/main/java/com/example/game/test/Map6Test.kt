package com.example.game.test

import android.content.Context
import com.example.game.GameMap

/**
 * Test class để verify hệ thống map6 mới
 */
object Map6Test {
    
    /**
     * Test load map6
     */
    fun testLoadMap6(context: Context): Boolean {
        return try {
            val map6 = GameMap.loadMap6(context)
            println("Map6Test: Successfully loaded map6 - size: ${map6.width}x${map6.height}")
            println("Map6Test: Player spawn at (${map6.playerSpawnX}, ${map6.playerSpawnY})")
            true
        } catch (e: Exception) {
            println("Map6Test: Failed to load map6: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Test sprite system
     */
    fun testSpriteSystem(context: Context): Boolean {
        return try {
            val tileSpriteManager = com.example.game.map.TileSpriteManager(context)
            val isReady = tileSpriteManager.isReady()
            println("Map6Test: Sprite system ready: $isReady")
            
            if (isReady) {
                val (tileWidth, tileHeight) = tileSpriteManager.getTileSize()
                println("Map6Test: Tile size: ${tileWidth}x${tileHeight}")
            }
            
            tileSpriteManager.cleanup()
            true
        } catch (e: Exception) {
            println("Map6Test: Sprite system error: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Test toàn bộ hệ thống
     */
    fun runAllTests(context: Context): Boolean {
        println("=== Map6 System Tests ===")
        
        val spriteTest = testSpriteSystem(context)
        val map6Test = testLoadMap6(context)
        
        val allPassed = spriteTest && map6Test
        
        println("=== Test Results ===")
        println("Sprite System: ${if (spriteTest) "PASS" else "FAIL"}")
        println("Map6 Loading: ${if (map6Test) "PASS" else "FAIL"}")
        println("Overall: ${if (allPassed) "PASS" else "FAIL"}")
        
        return allPassed
    }
}