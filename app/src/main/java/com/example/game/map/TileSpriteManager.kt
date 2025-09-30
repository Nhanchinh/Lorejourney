package com.example.game.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.Paint
import android.graphics.RectF

/**
 * Manager để xử lý sprite sheet cho map tiles
 * File map17x16.png có 17 hàng và 16 cột
 * Đánh số từ 1, từ trái sang phải, từ trên xuống dưới
 */
class TileSpriteManager(context: Context) {
    
    companion object {
        const val SPRITE_COLS = 16  // 16 cột
        const val SPRITE_ROWS = 17  // 17 hàng
        const val TOTAL_SPRITES = SPRITE_COLS * SPRITE_ROWS  // 272 sprites total
    }
    
    private var spriteSheet: Bitmap? = null
    private var tileWidth: Int = 0
    private var tileHeight: Int = 0
    private var isInitialized = false
    private val paintNoFilter: Paint = Paint().apply {
        isFilterBitmap = false
        isAntiAlias = false
        isDither = false
    }
    
    init {
        loadSpriteSheet(context)
    }
    
    /**
     * Load sprite sheet từ drawable
     */
    private fun loadSpriteSheet(context: Context) {
        try {
            // Load sprite sheet từ drawable
            val resourceId = context.resources.getIdentifier("map17x16", "drawable", context.packageName)
            if (resourceId != 0) {
                spriteSheet = BitmapFactory.decodeResource(context.resources, resourceId)
                spriteSheet?.let { sheet ->
                    tileWidth = sheet.width / SPRITE_COLS
                    tileHeight = sheet.height / SPRITE_ROWS
                    isInitialized = true
                    println("TileSpriteManager: Loaded sprite sheet ${sheet.width}x${sheet.height}, tile size: ${tileWidth}x${tileHeight}")
                }
            } else {
                println("TileSpriteManager: Could not find map17x16 drawable resource")
            }
        } catch (e: Exception) {
            println("TileSpriteManager: Error loading sprite sheet: ${e.message}")
            e.printStackTrace()
        }
    }
    
    /**
     * Vẽ tile từ sprite sheet theo sprite ID
     * @param canvas Canvas để vẽ
     * @param spriteId ID của sprite (1-272), tính từ trái sang phải, trên xuống dưới
     * @param destX Vị trí X đích trên canvas
     * @param destY Vị trí Y đích trên canvas  
     * @param destSize Kích thước tile đích
     */
    fun drawSprite(canvas: Canvas, spriteId: Int, destX: Float, destY: Float, destSize: Float) {
        if (!isInitialized || spriteSheet == null) {
            // Fallback: vẽ hình chữ nhật màu xám nếu không load được sprite
            drawFallbackTile(canvas, destX, destY, destSize, spriteId)
            return
        }
        
        // Validate spriteId (1-based)
        if (spriteId < 1 || spriteId > TOTAL_SPRITES) {
            drawFallbackTile(canvas, destX, destY, destSize, spriteId)
            return
        }
        
        // Convert 1-based spriteId to 0-based index
        val index = spriteId - 1
        
        // Calculate sprite position in sheet (0-based)
        val col = index % SPRITE_COLS
        val row = index / SPRITE_COLS
        
        // Source rectangle trong sprite sheet (inset 1px để tránh bleeding cạnh)
        val srcLeft = col * tileWidth
        val srcTop = row * tileHeight
        val srcRight = srcLeft + tileWidth
        val srcBottom = srcTop + tileHeight
        val inset = 1
        val srcRect = Rect(srcLeft + inset, srcTop + inset, srcRight - inset, srcBottom - inset)
        
        // Destination rectangle trên canvas (snap to pixel grid)
        val dx = kotlin.math.floor(destX).toFloat()
        val dy = kotlin.math.floor(destY).toFloat()
        val ds = kotlin.math.floor(destSize).toFloat()
        val destRect = RectF(dx, dy, dx + ds, dy + ds)
        
        // Vẽ sprite
        spriteSheet?.let { sheet ->
            canvas.drawBitmap(sheet, srcRect, destRect, paintNoFilter)
        }
    }
    
    /**
     * Vẽ sprite với alpha (độ mờ)
     */
    fun drawSpriteWithAlpha(canvas: Canvas, spriteId: Int, destX: Float, destY: Float, destSize: Float, alpha: Float) {
        if (!isInitialized || spriteSheet == null) {
            drawFallbackTile(canvas, destX, destY, destSize, spriteId)
            return
        }
        
        // Validate spriteId và alpha
        if (spriteId < 1 || spriteId > TOTAL_SPRITES) {
            drawFallbackTile(canvas, destX, destY, destSize, spriteId)
            return
        }
        
        // Convert 1-based spriteId to 0-based index
        val index = spriteId - 1
        val col = index % SPRITE_COLS
        val row = index / SPRITE_COLS
        
        // Source rectangle trong sprite sheet (inset 1px để tránh bleeding cạnh)
        val srcLeft = col * tileWidth
        val srcTop = row * tileHeight
        val srcRight = srcLeft + tileWidth
        val srcBottom = srcTop + tileHeight
        val inset = 1
        val srcRect = Rect(srcLeft + inset, srcTop + inset, srcRight - inset, srcBottom - inset)
        
        // Destination rectangle trên canvas (snap to pixel grid)
        val dx = kotlin.math.floor(destX).toFloat()
        val dy = kotlin.math.floor(destY).toFloat()
        val ds = kotlin.math.floor(destSize).toFloat()
        val destRect = RectF(dx, dy, dx + ds, dy + ds)
        
        // Tạo paint với alpha
        val alphaPaint = android.graphics.Paint().apply {
            this.alpha = (alpha * 255).toInt().coerceIn(0, 255)
            isFilterBitmap = false
            isAntiAlias = false
            isDither = false
        }
        
        // Vẽ sprite với alpha
        spriteSheet?.let { sheet ->
            canvas.drawBitmap(sheet, srcRect, destRect, alphaPaint)
        }
    }
    
    /**
     * Vẽ tile fallback khi không có sprite
     */
    private fun drawFallbackTile(canvas: Canvas, destX: Float, destY: Float, destSize: Float, spriteId: Int) {
        // Tạo paint fallback
        val fallbackPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = when (spriteId % 6) {
                1 -> android.graphics.Color.parseColor("#424242") // Gray
                2 -> android.graphics.Color.parseColor("#ECEFF1") // Light gray
                3 -> android.graphics.Color.parseColor("#4CAF50") // Green
                4 -> android.graphics.Color.parseColor("#2196F3") // Blue
                5 -> android.graphics.Color.parseColor("#FF5722") // Red
                else -> android.graphics.Color.parseColor("#9E9E9E") // Default gray
            }
        }
        
        val borderPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#90A4AE")
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 1f
        }
        
        // Vẽ tile fallback
        canvas.drawRect(destX, destY, destX + destSize, destY + destSize, fallbackPaint)
        canvas.drawRect(destX, destY, destX + destSize, destY + destSize, borderPaint)
        
        // Vẽ số sprite ID ở giữa tile (cho debug)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = destSize * 0.2f
            isFakeBoldText = true
        }
        val centerX = destX + destSize / 2
        val centerY = destY + destSize / 2 + textPaint.textSize / 3
        canvas.drawText(spriteId.toString(), centerX, centerY, textPaint)
    }
    
    /**
     * Check xem sprite manager đã sẵn sàng chưa
     */
    fun isReady(): Boolean = isInitialized && spriteSheet != null
    
    /**
     * Get thông tin kích thước tile
     */
    fun getTileSize(): Pair<Int, Int> = Pair(tileWidth, tileHeight)
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        spriteSheet?.recycle()
        spriteSheet = null
        isInitialized = false
    }
}