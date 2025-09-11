package com.example.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class GameMap(private val mapData: Array<IntArray>) {
    val width = mapData[0].size
    val height = mapData.size
    
    // Map visuals - CHI TIẾT VÀ TO HƠN
    private val wallPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#424242")
    }
    
    private val wallShadowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#212121")
    }
    
    private val floorPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#ECEFF1")
    }
    
    private val floorDetailPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#CFD8DC")
        strokeWidth = 1f
    }
    
    private val keyPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
    }
    
    private val keyBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFA000")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val buttonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4CAF50")
    }
    
    private val buttonBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#2E7D32")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val borderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#90A4AE")
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    
    fun getTile(x: Int, y: Int): Int {
        if (x < 0 || x >= width || y < 0 || y >= height) return GameConstants.TILE_WALL
        return mapData[y][x]
    }
    
    fun isWalkable(x: Int, y: Int): Boolean {
        val tile = getTile(x, y)
        return when (tile) {
            GameConstants.TILE_WALL -> false
            else -> true
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
                val tileId = mapData[y][x]
                val drawX = x * tileSize
                val drawY = y * tileSize
                
                when (tileId) {
                    GameConstants.TILE_WALL -> {
                        // Wall với shadow effect
                        canvas.drawRect(drawX + 2, drawY + 2, drawX + tileSize + 2, drawY + tileSize + 2, wallShadowPaint)
                        canvas.drawRect(drawX, drawY, drawX + tileSize, drawY + tileSize, wallPaint)
                        canvas.drawRect(drawX, drawY, drawX + tileSize, drawY + tileSize, borderPaint)
                    }
                    GameConstants.TILE_FLOOR -> {
                        // Floor với texture
                        canvas.drawRect(drawX, drawY, drawX + tileSize, drawY + tileSize, floorPaint)
                        // Grid pattern
                        canvas.drawLine(drawX, drawY, drawX + tileSize, drawY, floorDetailPaint)
                        canvas.drawLine(drawX, drawY, drawX, drawY + tileSize, floorDetailPaint)
                    }
                    GameConstants.TILE_KEY -> {
                        // Floor background
                        canvas.drawRect(drawX, drawY, drawX + tileSize, drawY + tileSize, floorPaint)
                        // Key - TO VÀ CHI TIẾT
                        val centerX = drawX + tileSize / 2
                        val centerY = drawY + tileSize / 2
                        val keySize = tileSize * 0.6f
                        canvas.drawRoundRect(
                            centerX - keySize/2, centerY - keySize/2,
                            centerX + keySize/2, centerY + keySize/2,
                            keySize * 0.2f, keySize * 0.2f, keyPaint
                        )
                        canvas.drawRoundRect(
                            centerX - keySize/2, centerY - keySize/2,
                            centerX + keySize/2, centerY + keySize/2,
                            keySize * 0.2f, keySize * 0.2f, keyBorderPaint
                        )
                    }
                    GameConstants.TILE_BUTTON -> {
                        // Floor background
                        canvas.drawRect(drawX, drawY, drawX + tileSize, drawY + tileSize, floorPaint)
                        // Button - TO VÀ CHI TIẾT
                        val centerX = drawX + tileSize / 2
                        val centerY = drawY + tileSize / 2
                        val buttonRadius = tileSize * 0.3f
                        canvas.drawCircle(centerX, centerY, buttonRadius, buttonPaint)
                        canvas.drawCircle(centerX, centerY, buttonRadius, buttonBorderPaint)
                    }
                }
            }
        }
    }
    
    companion object {
        fun createLevel1(): GameMap {
            // Map nhỏ hơn vì tiles lớn hơn
            val mapWidth = 50  // Giảm từ 80 xuống 50
            val mapHeight = 30 // Giảm từ 50 xuống 30
            val mapData = Array(mapHeight) { IntArray(mapWidth) }
            
            for (y in 0 until mapHeight) {
                for (x in 0 until mapWidth) {
                    when {
                        x == 0 || x == mapWidth-1 || y == 0 || y == mapHeight-1 -> {
                            mapData[y][x] = GameConstants.TILE_WALL
                        }
                        (x % 8 == 0 && y % 6 == 0) || (x % 12 == 6 && y % 8 == 4) -> {
                            mapData[y][x] = GameConstants.TILE_WALL
                        }
                        (x % 15 == 7 && y % 10 == 5) -> {
                            mapData[y][x] = GameConstants.TILE_KEY
                        }
                        (x % 20 == 10 && y % 12 == 6) -> {
                            mapData[y][x] = GameConstants.TILE_BUTTON
                        }
                        else -> {
                            mapData[y][x] = GameConstants.TILE_FLOOR
                        }
                    }
                }
            }
            return GameMap(mapData)
        }
        
        fun createTestMap(): GameMap = createLevel1()
    }
}
