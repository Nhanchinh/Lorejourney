package com.example.game.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Test utility để kiểm tra map6 sprite system
 */
object Map6TestUtils {
    
    /**
     * Test vẽ một vài tile đầu tiên của sprite sheet
     */
    fun drawTestSprites(canvas: Canvas, context: Context, startX: Float, startY: Float, tileSize: Float) {
        val spriteManager = TileSpriteManager(context)
        val testPaint = Paint().apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = tileSize * 0.15f
            isAntiAlias = true
        }
        
        // Vẽ 10 sprite đầu tiên để test
        for (i in 1..10) {
            val x = startX + ((i - 1) % 5) * (tileSize + 10)
            val y = startY + ((i - 1) / 5) * (tileSize + 30)
            
            // Vẽ sprite
            spriteManager.drawSprite(canvas, i, x, y, tileSize)
            
            // Vẽ ID bên dưới
            canvas.drawText("ID:$i", x + tileSize/2, y + tileSize + 20, testPaint)
        }
        
        spriteManager.cleanup()
    }
    
    /**
     * Vẽ map6 demo
     */
    fun drawMap6Demo(canvas: Canvas, context: Context) {
        try {
            val mapData = MapLoader.loadFromAssets(context, "level6.txt")
            if (mapData != null) {
                val spriteManager = TileSpriteManager(context)
                val tileSize = 32f
                
                // Vẽ một phần nhỏ của map để test (10x10 tiles đầu tiên)
                val maxCols = minOf(10, mapData.width)
                val maxRows = minOf(10, mapData.height)
                
                for (y in 0 until maxRows) {
                    for (x in 0 until maxCols) {
                        val tileId = mapData.tiles[y][x]
                        val drawX = x * tileSize + 50f
                        val drawY = y * tileSize + 100f
                        
                        spriteManager.drawSprite(canvas, tileId, drawX, drawY, tileSize)
                    }
                }
                
                spriteManager.cleanup()
                
                // Vẽ thông tin
                val infoPaint = Paint().apply {
                    color = Color.WHITE
                    textAlign = Paint.Align.LEFT
                    textSize = 24f
                    isAntiAlias = true
                }
                canvas.drawText("Map6 Demo - ${mapData.width}x${mapData.height}", 50f, 50f, infoPaint)
                canvas.drawText("Player spawn: (${mapData.playerSpawnX}, ${mapData.playerSpawnY})", 50f, 80f, infoPaint)
            }
        } catch (e: Exception) {
            val errorPaint = Paint().apply {
                color = Color.RED
                textAlign = Paint.Align.LEFT
                textSize = 24f
                isAntiAlias = true
            }
            canvas.drawText("Error loading map6: ${e.message}", 50f, 50f, errorPaint)
        }
    }
}