package com.example.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.example.game.map.TileConstants
import com.example.game.gameMechanic.PushLogic

/**
 * Player với sprite animation từ Basic_Charakter_Spritesheet.png
 */
class SpritePlayer(
    startX: Float, 
    startY: Float,
    private val context: Context
) {
    
    // Position và movement
    var x = startX
    var y = startY
    private var targetX = x
    private var targetY = y
    
    private var isMoving = false
    private var animationTime = 0L
    private val moveDuration = 250L
    private var startX = x
    private var startY = y
    
    // Sprite sheet properties
    private var spriteSheet: Bitmap? = null
    private val totalFrames = 4 // 4 frames per direction
    private var frameWidth = 0
    private var frameHeight = 0
    
    // Animation state
    private var currentDirection = Direction.DOWN
    private var currentFrame = 0
    private var frameTime = 0L
    private val frameInterval = 200L // 200ms per frame
    
    // Direction enum theo layout sprite sheet
    enum class Direction(val row: Int) {
        DOWN(0),   // Row 0: đi xuống
        UP(1),     // Row 1: đi lên
        LEFT(2),   // Row 2: đi trái
        RIGHT(3)   // Row 3: đi phải
    }
    
    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }
    
    private val srcRect = Rect()
    private val destRect = RectF()
    
    private val size = GameConstants.TILE_SIZE.toFloat() * 2.5f  // ←── SIÊU TO (250% tile size)
    
    // Push logic reference
    private var pushLogic: PushLogic? = null
    
    init {
        loadSpriteSheet()
    }
    
    private fun loadSpriteSheet() {
        try {
            spriteSheet = BitmapFactory.decodeResource(
                context.resources, 
                R.drawable.basic_charakter_spritesheet
            )
            
            spriteSheet?.let { bitmap ->
                // Calculate frame dimensions (4x4 grid)
                frameWidth = bitmap.width / 4  // 4 columns
                frameHeight = bitmap.height / 4 // 4 rows
                
                println("✅ Sprite sheet loaded: ${bitmap.width}x${bitmap.height}")
                println("✅ Frame size: ${frameWidth}x${frameHeight}")
            }
        } catch (e: Exception) {
            println("❌ Failed to load sprite sheet: ${e.message}")
        }
    }
    
    fun setPushLogic(pushLogic: PushLogic) {
        this.pushLogic = pushLogic
    }
    
    fun update(deltaTime: Long) {
        if (isMoving) {
            animationTime += deltaTime
            frameTime += deltaTime
            
            // Update animation frame khi moving
            if (frameTime >= frameInterval) {
                currentFrame = (currentFrame + 1) % totalFrames
                frameTime = 0
            }
            
            if (animationTime >= moveDuration) {
                x = targetX
                y = targetY
                isMoving = false
                animationTime = 0
                currentFrame = 0 // Reset về frame đầu khi dừng
            } else {
                val progress = animationTime.toFloat() / moveDuration.toFloat()
                val easedProgress = easeOut(progress)
                x = startX + (targetX - startX) * easedProgress
                y = startY + (targetY - startY) * easedProgress
            }
        }
    }
    
    private fun easeOut(t: Float): Float {
        return 1f - (1f - t) * (1f - t) * (1f - t)
    }
    
    fun move(dx: Int, dy: Int, gameMap: GameMap): Boolean {
        if (isMoving) return false
        
        // Update direction based on movement
        currentDirection = when {
            dx > 0 -> Direction.RIGHT
            dx < 0 -> Direction.LEFT
            dy > 0 -> Direction.DOWN
            dy < 0 -> Direction.UP
            else -> currentDirection
        }
        
        val currentTileX = (x / GameConstants.TILE_SIZE).toInt()
        val currentTileY = (y / GameConstants.TILE_SIZE).toInt()
        
        val newTileX = currentTileX + dx
        val newTileY = currentTileY + dy
        
        // Check push logic first (if available)
        val nextTile = gameMap.getTile(newTileX, newTileY)
        if (TileConstants.isPushable(nextTile)) {
            if (pushLogic?.tryPush(currentTileX, currentTileY, dx, dy) == true) {
                startMovement(newTileX, newTileY)
                return true
            }
        }
        
        // Check normal movement
        if (gameMap.isWalkable(newTileX, newTileY)) {
            startMovement(newTileX, newTileY)
            return true
        }
        
        return false
    }
    
    private fun startMovement(tileX: Int, tileY: Int) {
        startX = x
        startY = y
        targetX = tileX * GameConstants.TILE_SIZE.toFloat()
        targetY = tileY * GameConstants.TILE_SIZE.toFloat()
        
        isMoving = true
        animationTime = 0
        frameTime = 0
        currentFrame = 0
    }
    
    fun draw(canvas: Canvas) {
        val sprite = spriteSheet
        
        if (sprite != null && frameWidth > 0 && frameHeight > 0) {
            // Calculate source rectangle trong sprite sheet
            val srcX = currentFrame * frameWidth
            val srcY = currentDirection.row * frameHeight
            
            srcRect.set(srcX, srcY, srcX + frameWidth, srcY + frameHeight)
            
            // Calculate destination rectangle on screen
            val centerX = x + GameConstants.TILE_SIZE / 2f
            val centerY = y + GameConstants.TILE_SIZE / 2f
            val halfSize = size / 2
            
            destRect.set(
                centerX - halfSize,
                centerY - halfSize,
                centerX + halfSize,
                centerY + halfSize
            )
            
            canvas.drawBitmap(sprite, srcRect, destRect, paint)
        } else {
            // Fallback drawing
            drawFallbackPlayer(canvas)
        }
    }
    
    private fun drawFallbackPlayer(canvas: Canvas) {
        val centerX = x + GameConstants.TILE_SIZE / 2f
        val centerY = y + GameConstants.TILE_SIZE / 2f
        val radius = size / 2f
        
        val bodyPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#4285F4")
        }
        
        val borderPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#1565C0")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        
        canvas.drawCircle(centerX, centerY, radius, bodyPaint)
        canvas.drawCircle(centerX, centerY, radius, borderPaint)
    }
    
    fun getCenterX(): Float = x + GameConstants.TILE_SIZE / 2f
    fun getCenterY(): Float = y + GameConstants.TILE_SIZE / 2f
    
    fun getCurrentTileX(): Int = (x / GameConstants.TILE_SIZE).toInt()
    fun getCurrentTileY(): Int = (y / GameConstants.TILE_SIZE).toInt()
    
    fun isCurrentlyMoving(): Boolean = isMoving
    
    fun checkLevelComplete(gameMap: GameMap): Boolean {
        val tileX = (x / GameConstants.TILE_SIZE).toInt()
        val tileY = (y / GameConstants.TILE_SIZE).toInt()
        val currentTile = gameMap.getTile(tileX, tileY)
        
        return currentTile == TileConstants.TILE_END
    }
    
    fun checkPuzzleComplete(): Boolean {
        return pushLogic?.isPuzzleComplete() ?: false
    }
    
    fun dispose() {
        spriteSheet?.recycle()
    }
}