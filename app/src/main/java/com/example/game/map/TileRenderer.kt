package com.example.game.map

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

/**
 * Renderer để vẽ các tile khác nhau
 * Hỗ trợ cả vẽ bằng code (legacy) và vẽ từ sprite sheet (default mode)
 */
class TileRenderer {
    
    // Sprite manager cho tất cả các map (nullable)
    private var tileSpriteManager: TileSpriteManager? = null
    private var useSpriteMode = false
    
    /**
     * Khởi tạo sprite mode cho tất cả các map
     */
    fun initSpriteMode(context: Context) {
        tileSpriteManager = TileSpriteManager(context)
        useSpriteMode = tileSpriteManager?.isReady() == true
        println("TileRenderer: Sprite mode ${if (useSpriteMode) "enabled" else "disabled"}")
    }
    
    /**
     * Bật/tắt sprite mode
     */
    fun setSpriteMode(enabled: Boolean) {
        useSpriteMode = enabled && tileSpriteManager?.isReady() == true
        println("TileRenderer: Sprite mode ${if (useSpriteMode) "enabled" else "disabled"}")
    }
    
    /**
     * Check xem có đang ở sprite mode không
     */
    fun isSpriteMode(): Boolean = useSpriteMode
    
    /**
     * Vẽ một tile tại vị trí (x, y) với size cho trước
     * Hỗ trợ cả sprite mode và code-based mode
     */
    fun drawTile(canvas: Canvas, tileId: Int, x: Float, y: Float, size: Float) {
        // Nếu đang ở sprite mode và có sprite manager
        if (useSpriteMode && tileSpriteManager != null) {
            // Snap to integer pixels to avoid seams
            val sx = kotlin.math.floor(x).toFloat()
            val sy = kotlin.math.floor(y).toFloat()
            val ss = kotlin.math.floor(size).toFloat()
            drawTileSprite(canvas, tileId, sx, sy, ss)
            return
        }
    }
    
    /**
     * Vẽ một tile với alpha (độ mờ) tùy chỉnh
     */
    fun drawTileWithAlpha(canvas: Canvas, tileId: Int, x: Float, y: Float, size: Float, alpha: Float) {
        // Nếu đang ở sprite mode và có sprite manager
        if (useSpriteMode && tileSpriteManager != null) {
            val sx = kotlin.math.floor(x).toFloat()
            val sy = kotlin.math.floor(y).toFloat()
            val ss = kotlin.math.floor(size).toFloat()
            tileSpriteManager?.drawSpriteWithAlpha(canvas, tileId, sx, sy, ss, alpha)
            return
        }
    }
    
    /**
     * Vẽ tile từ sprite sheet (default mode)
     */
    private fun drawTileSprite(canvas: Canvas, tileId: Int, x: Float, y: Float, size: Float) {
        tileSpriteManager?.drawSprite(canvas, tileId, x, y, size)
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        tileSpriteManager?.cleanup()
        tileSpriteManager = null
        useSpriteMode = false
    }
}
