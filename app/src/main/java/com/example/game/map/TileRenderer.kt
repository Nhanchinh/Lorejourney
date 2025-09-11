package com.example.game.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * Renderer để vẽ các tile khác nhau
 */
class TileRenderer {
    
    // Paints cho các loại tile
    private val wallPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#424242")
    }
    
    private val floorPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#ECEFF1")
    }
    
    private val grassPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4CAF50")
    }
    
    private val waterPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#2196F3")
    }
    
    private val stonePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#757575")
    }
    
    private val dirtPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#8D6E63")
    }
    
    private val keyPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
    }
    
    private val buttonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FF5722")
    }
    
    private val doorPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#795548")
    }
    
    private val chestPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FF9800")
    }
    
    private val treePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#2E7D32")
    }
    
    private val rockPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#616161")
    }
    
    private val flowerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#E91E63")
    }
    
    private val bushPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#388E3C")
    }
    
    private val houseWallPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#6D4C41")
    }
    
    private val houseFloorPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#BCAAA4")
    }
    
    private val roofPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#D32F2F")
    }
    
    private val windowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#81D4FA")
    }
    
    private val lavaPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FF3D00")
    }
    
    private val icePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#E1F5FE")
    }
    
    private val sandPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFF3E0")
    }
    
    private val borderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#90A4AE")
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    
    // Thêm các Paint cho màu cần thiết
    private val grayPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#808080")
    }
    
    private val yellowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFFF00")
    }
    
    // Thêm paint cho end tile
    private val endPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#00FF00") // Màu xanh lá sáng
    }

    private val endBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#008000") // Màu xanh lá đậm
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    // RectF để tái sử dụng cho drawRoundRect
    private val tempRect = RectF()
    
    /**
     * Vẽ một tile tại vị trí (x, y) với size cho trước
     */
    fun drawTile(canvas: Canvas, tileId: Int, x: Float, y: Float, size: Float) {
        when (tileId) {
            TileConstants.TILE_EMPTY -> {
                // Không vẽ gì
            }
            
            TileConstants.TILE_WALL -> {
                canvas.drawRect(x, y, x + size, y + size, wallPaint)
                canvas.drawRect(x, y, x + size, y + size, borderPaint)
            }
            
            TileConstants.TILE_FLOOR -> {
                canvas.drawRect(x, y, x + size, y + size, floorPaint)
                // Grid pattern
                canvas.drawLine(x, y, x + size, y, borderPaint)
                canvas.drawLine(x, y, x, y + size, borderPaint)
            }
            
            TileConstants.TILE_GRASS -> {
                canvas.drawRect(x, y, x + size, y + size, grassPaint)
                // Grass texture
                canvas.drawLine(x + size/4, y, x + size/4, y + size, borderPaint)
                canvas.drawLine(x + 3*size/4, y, x + 3*size/4, y + size, borderPaint)
            }
            
            TileConstants.TILE_WATER -> {
                canvas.drawRect(x, y, x + size, y + size, waterPaint)
                // Water waves
                canvas.drawLine(x, y + size/3, x + size, y + size/3, borderPaint)
                canvas.drawLine(x, y + 2*size/3, x + size, y + 2*size/3, borderPaint)
            }
            
            TileConstants.TILE_STONE -> {
                canvas.drawRect(x, y, x + size, y + size, stonePaint)
                canvas.drawRect(x, y, x + size, y + size, borderPaint)
            }
            
            TileConstants.TILE_DIRT -> {
                canvas.drawRect(x, y, x + size, y + size, dirtPaint)
            }
            
            TileConstants.TILE_KEY -> {
                canvas.drawRect(x, y, x + size, y + size, floorPaint) // Background
                val centerX = x + size / 2
                val centerY = y + size / 2
                val keySize = size * 0.6f
                
                // Sử dụng RectF để vẽ key
                tempRect.set(
                    centerX - keySize/2,
                    centerY - keySize/2,
                    centerX + keySize/2,
                    centerY + keySize/2
                )
                canvas.drawRoundRect(tempRect, keySize * 0.2f, keySize * 0.2f, keyPaint)
            }
            
            TileConstants.TILE_BUTTON -> {
                canvas.drawRect(x, y, x + size, y + size, floorPaint) // Background
                val centerX = x + size / 2
                val centerY = y + size / 2
                canvas.drawCircle(centerX, centerY, size * 0.3f, buttonPaint)
            }
            
            TileConstants.TILE_DOOR -> {
                canvas.drawRect(x, y, x + size, y + size, doorPaint)
                canvas.drawCircle(x + size*0.8f, y + size/2, size*0.08f, keyPaint) // Handle
            }
            
            TileConstants.TILE_CHEST -> {
                canvas.drawRect(x, y, x + size, y + size, floorPaint) // Background
                canvas.drawRect(x + size*0.2f, y + size*0.4f, x + size*0.8f, y + size*0.9f, chestPaint)
                canvas.drawRect(x + size*0.2f, y + size*0.4f, x + size*0.8f, y + size*0.9f, borderPaint)
            }
            
            TileConstants.TILE_TREE -> {
                canvas.drawRect(x, y, x + size, y + size, grassPaint) // Background
                // Trunk
                canvas.drawRect(x + size*0.4f, y + size*0.6f, x + size*0.6f, y + size, dirtPaint)
                // Leaves
                canvas.drawCircle(x + size/2, y + size*0.4f, size*0.3f, treePaint)
            }
            
            TileConstants.TILE_ROCK -> {
                canvas.drawRect(x, y, x + size, y + size, grassPaint) // Background
                canvas.drawCircle(x + size/2, y + size/2, size*0.3f, rockPaint)
            }
            
            TileConstants.TILE_FLOWER -> {
                canvas.drawRect(x, y, x + size, y + size, grassPaint) // Background
                canvas.drawCircle(x + size/2, y + size/2, size*0.2f, flowerPaint)
            }
            
            TileConstants.TILE_BUSH -> {
                canvas.drawRect(x, y, x + size, y + size, grassPaint) // Background
                canvas.drawCircle(x + size/2, y + size/2, size*0.25f, bushPaint)
            }
            
            TileConstants.TILE_HOUSE_WALL -> {
                canvas.drawRect(x, y, x + size, y + size, houseWallPaint)
                canvas.drawRect(x, y, x + size, y + size, borderPaint)
            }
            
            TileConstants.TILE_HOUSE_FLOOR -> {
                canvas.drawRect(x, y, x + size, y + size, houseFloorPaint)
                canvas.drawLine(x, y, x + size, y, borderPaint)
                canvas.drawLine(x, y, x, y + size, borderPaint)
            }
            
            TileConstants.TILE_ROOF -> {
                canvas.drawRect(x, y, x + size, y + size, roofPaint)
                // Roof lines
                for (i in 1..3) {
                    canvas.drawLine(x, y + i*size/4, x + size, y + i*size/4, borderPaint)
                }
            }
            
            TileConstants.TILE_WINDOW -> {
                canvas.drawRect(x, y, x + size, y + size, windowPaint)
                canvas.drawLine(x, y + size/2, x + size, y + size/2, borderPaint)
                canvas.drawLine(x + size/2, y, x + size/2, y + size, borderPaint)
            }
            
            TileConstants.TILE_LAVA -> {
                canvas.drawRect(x, y, x + size, y + size, lavaPaint)
                // Bubble effect - sử dụng yellowPaint thay vì Color.YELLOW
                canvas.drawCircle(x + size*0.3f, y + size*0.3f, size*0.1f, yellowPaint)
                canvas.drawCircle(x + size*0.7f, y + size*0.7f, size*0.08f, yellowPaint)
            }
            
            TileConstants.TILE_ICE -> {
                canvas.drawRect(x, y, x + size, y + size, icePaint)
                // Ice pattern
                canvas.drawLine(x, y, x + size, y + size, borderPaint)
                canvas.drawLine(x + size, y, x, y + size, borderPaint)
            }
            
            TileConstants.TILE_SAND -> {
                canvas.drawRect(x, y, x + size, y + size, sandPaint)
                // Sand dots
                canvas.drawCircle(x + size*0.2f, y + size*0.3f, 2f, dirtPaint)
                canvas.drawCircle(x + size*0.6f, y + size*0.7f, 2f, dirtPaint)
                canvas.drawCircle(x + size*0.8f, y + size*0.2f, 2f, dirtPaint)
            }
            
            TileConstants.TILE_END -> {
                canvas.drawRect(x, y, x + size, y + size, floorPaint) // Background
                // Vẽ END tile như một ngôi sao hoặc cổng
                val centerX = x + size / 2
                val centerY = y + size / 2
                val starSize = size * 0.4f
                
                // Vẽ hình tròn lớn
                canvas.drawCircle(centerX, centerY, starSize, endPaint)
                canvas.drawCircle(centerX, centerY, starSize, endBorderPaint)
                
                // Vẽ chữ "E" ở giữa
                val textPaint = Paint().apply {
                    isAntiAlias = true
                    color = Color.WHITE
                    textAlign = Paint.Align.CENTER
                    textSize = size * 0.3f
                    isFakeBoldText = true
                }
                canvas.drawText("E", centerX, centerY + textPaint.textSize/3, textPaint)
            }
            
            else -> {
                // Unknown tile - sử dụng grayPaint thay vì Color.GRAY
                canvas.drawRect(x, y, x + size, y + size, grayPaint)
                canvas.drawRect(x, y, x + size, y + size, borderPaint)
            }
        }
    }
}
