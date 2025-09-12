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
    private val context: Context,
    private val gameMap: GameMap
) {
    
    // Position
    var x = startX
    var y = startY
    
    // Movement state - ĐƠN GIẢN HÓA
    private var velocityX = 0f
    private var velocityY = 0f
    private val moveSpeed = 150f // Giảm tốc độ
    private val maxVelocity = 200f
    
    // Input state
    private var isMovingUp = false
    private var isMovingDown = false
    private var isMovingLeft = false
    private var isMovingRight = false
    
    // Animation - CHỈ DECLARE 1 LẦN
    private var currentDirection = Direction.DOWN
    private var currentFrame = 0
    private var frameTime = 0L
    private val frameInterval = 150L
    
    // Snap state - ĐƠN GIẢN
    private var shouldSnap = false
    private var snapCooldown = 0L
    
    // Sprite sheet properties
    private var spriteSheet: Bitmap? = null
    private val totalFrames = 4 // 4 frames per direction
    private var frameWidth = 0
    private var frameHeight = 0
    
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
    
    private val size = GameConstants.TILE_SIZE.toFloat() * 2.5f
    
    // Push logic reference
    private var pushLogic: PushLogic? = null
    
    // Thêm variables để track push attempts
    private var lastPushTime = 0L
    private var pushCooldown = 500L // 500ms cooldown between pushes
    
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
    
    // Update method để handle push cooldown
    fun update(deltaTime: Long) {
        val deltaSeconds = deltaTime / 1000f
        
        // Handle snap cooldown
        if (snapCooldown > 0) {
            snapCooldown -= deltaTime
        }
        
        // Update movement (includes push logic)
        updateSimpleMovement(deltaSeconds)
        
        // Update animation
        updateAnimation(deltaTime)
        
        // Handle snapping
        handleSnapping(deltaTime)
    }
    
    private fun updateSimpleMovement(deltaSeconds: Float) {
        // Calculate target velocity từ input
        var targetVelX = 0f
        var targetVelY = 0f
        
        if (isMovingRight) targetVelX = moveSpeed
        if (isMovingLeft) targetVelX = -moveSpeed
        if (isMovingDown) targetVelY = moveSpeed
        if (isMovingUp) targetVelY = -moveSpeed
        
        // Smooth velocity change
        if (targetVelX != 0f || targetVelY != 0f) {
            // Moving - accelerate toward target
            velocityX = lerp(velocityX, targetVelX, 0.4f)
            velocityY = lerp(velocityY, targetVelY, 0.4f)
            
            // Update direction
            updateDirection()
            
            // Reset snap trigger
            shouldSnap = false
        } else {
            // Not moving - decelerate
            velocityX *= 0.6f
            velocityY *= 0.6f
            
            // Trigger snap when velocity gets low
            if (!shouldSnap && Math.abs(velocityX) < 20f && Math.abs(velocityY) < 20f) {
                shouldSnap = true
            }
        }
        
        // Clamp velocity
        velocityX = velocityX.coerceIn(-maxVelocity, maxVelocity)
        velocityY = velocityY.coerceIn(-maxVelocity, maxVelocity)
        
        // Calculate new position
        val newX = x + velocityX * deltaSeconds
        val newY = y + velocityY * deltaSeconds
        
        // Check for push mechanics TRƯỚC KHI check collision
        if (!isPositionValid(newX, newY)) {
            // Position invalid - check if it's because of pushable stone
            if (tryPushStone(newX, newY)) {
                // Push succeeded - allow movement
                x = newX
                y = newY
            } else {
                // Normal collision - stop movement
                velocityX = 0f
                velocityY = 0f
                shouldSnap = true
            }
        } else {
            // Normal movement
            x = newX
            y = newY
        }
    }
    
    private fun handleSnapping(deltaTime: Long) {
        if (shouldSnap && snapCooldown <= 0) {
            val tileSize = GameConstants.TILE_SIZE.toFloat()
            
            // Find nearest tile position
            val nearestTileX = Math.round(x / tileSize)
            val nearestTileY = Math.round(y / tileSize)
            
            val targetX = nearestTileX * tileSize
            val targetY = nearestTileY * tileSize
            
            // Quick snap if close enough
            val distX = targetX - x
            val distY = targetY - y
            val totalDist = Math.sqrt((distX * distX + distY * distY).toDouble()).toFloat()
            
            if (totalDist < 5f) {
                // Very close - instant snap
                x = targetX
                y = targetY
                velocityX = 0f
                velocityY = 0f
                shouldSnap = false
                snapCooldown = 100L // Short cooldown
                currentFrame = 0
            } else if (totalDist < 32f) {
                // Close - smooth snap
                val snapSpeed = 5f
                x = lerp(x, targetX, snapSpeed * deltaTime / 1000f)
                y = lerp(y, targetY, snapSpeed * deltaTime / 1000f)
                velocityX = 0f
                velocityY = 0f
            }
        }
    }
    
    private fun isPositionValid(testX: Float, testY: Float): Boolean {
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        
        // Bounds check
        if (testX < 0 || testY < 0) return false
        if (testX >= gameMap.width * tileSize - tileSize) return false
        if (testY >= gameMap.height * tileSize - tileSize) return false
        
        // Tile collision check
        val centerX = testX + tileSize / 2
        val centerY = testY + tileSize / 2
        val tileX = (centerX / tileSize).toInt()
        val tileY = (centerY / tileSize).toInt()
        
        val tileId = gameMap.getTile(tileX, tileY)
        
        // Allow movement to pushable stones (will be handled by tryPushStone)
        if (TileConstants.isPushable(tileId)) {
            return false // This will trigger push check
        }
        
        return gameMap.isWalkable(tileX, tileY)
    }
    
    private fun updateDirection() {
        currentDirection = when {
            Math.abs(velocityX) > Math.abs(velocityY) -> {
                if (velocityX > 0) Direction.RIGHT else Direction.LEFT
            }
            else -> {
                if (velocityY > 0) Direction.DOWN else Direction.UP
            }
        }
    }
    
    private fun updateAnimation(deltaTime: Long) {
        if (Math.abs(velocityX) > 15f || Math.abs(velocityY) > 15f) {
            frameTime += deltaTime
            if (frameTime >= frameInterval) {
                currentFrame = (currentFrame + 1) % totalFrames
                frameTime = 0
            }
        } else {
            currentFrame = 0 // Idle frame
        }
    }
    
    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t.coerceIn(0f, 1f)
    }
    
    // Input methods
    fun startMoving(direction: String) {
        when (direction) {
            "UP" -> isMovingUp = true
            "DOWN" -> isMovingDown = true
            "LEFT" -> isMovingLeft = true
            "RIGHT" -> isMovingRight = true
        }
    }
    
    fun stopMoving(direction: String) {
        when (direction) {
            "UP" -> isMovingUp = false
            "DOWN" -> isMovingDown = false
            "LEFT" -> isMovingLeft = false
            "RIGHT" -> isMovingRight = false
        }
    }
    
    fun stopAllMovement() {
        isMovingUp = false
        isMovingDown = false
        isMovingLeft = false
        isMovingRight = false
    }
    
    fun isCurrentlyMoving(): Boolean {
        return Math.abs(velocityX) > 10f || Math.abs(velocityY) > 10f
    }
    
    // Existing methods remain the same
    fun move(dx: Int, dy: Int, gameMap: GameMap): Boolean {
        // For push mechanics in level 1
        val currentTileX = getCurrentTileX()
        val currentTileY = getCurrentTileY()
        
        val newTileX = currentTileX + dx
        val newTileY = currentTileY + dy
        
        // Check push logic first (if available)
        val nextTile = gameMap.getTile(newTileX, newTileY)
        if (TileConstants.isPushable(nextTile)) {
            if (pushLogic?.tryPush(currentTileX, currentTileY, dx, dy) == true) {
                // Move to new tile position
                x = newTileX * GameConstants.TILE_SIZE.toFloat()
                y = newTileY * GameConstants.TILE_SIZE.toFloat()
                return true
            }
        }
        
        return false
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
    
    fun getCurrentTileX(): Int = ((x + GameConstants.TILE_SIZE / 2) / GameConstants.TILE_SIZE).toInt()
    fun getCurrentTileY(): Int = ((y + GameConstants.TILE_SIZE / 2) / GameConstants.TILE_SIZE).toInt()
    
    fun checkLevelComplete(gameMap: GameMap): Boolean {
        val tileX = getCurrentTileX()
        val tileY = getCurrentTileY()
        val currentTile = gameMap.getTile(tileX, tileY)
        
        return currentTile == TileConstants.TILE_END
    }
    
    fun checkPuzzleComplete(): Boolean {
        return pushLogic?.isPuzzleComplete() ?: false
    }
    
    fun dispose() {
        spriteSheet?.recycle()
    }

    // THÊM METHOD MỚI: Try push stone
    private fun tryPushStone(newX: Float, newY: Float): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPushTime < pushCooldown) {
            return false // Still in cooldown
        }
        
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        
        // Get current tile position
        val currentTileX = getCurrentTileX()
        val currentTileY = getCurrentTileY()
        
        // Get target tile position (where we're trying to move)
        val centerX = newX + tileSize / 2
        val centerY = newY + tileSize / 2
        val targetTileX = (centerX / tileSize).toInt()
        val targetTileY = (centerY / tileSize).toInt()
        
        // Calculate push direction
        val dx = targetTileX - currentTileX
        val dy = targetTileY - currentTileY
        
        // Only allow pushing in cardinal directions
        if ((dx != 0 && dy != 0) || (dx == 0 && dy == 0)) {
            return false
        }
        
        // Check if target tile has pushable stone
        val targetTile = gameMap.getTile(targetTileX, targetTileY)
        if (!TileConstants.isPushable(targetTile)) {
            return false
        }
        
        // Try to push the stone
        if (pushLogic?.tryPush(currentTileX, currentTileY, dx, dy) == true) {
            lastPushTime = currentTime
            println("🪨 Successfully pushed stone from ($currentTileX, $currentTileY) direction ($dx, $dy)")
            
            // Move player to the stone's previous position
            x = targetTileX * tileSize
            y = targetTileY * tileSize
            
            // Stop movement after push
            velocityX = 0f
            velocityY = 0f
            shouldSnap = true
            
            return true
        }
        
        return false
    }
}