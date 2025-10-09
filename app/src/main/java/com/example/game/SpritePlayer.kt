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
import com.example.game.music.MusicManager

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
    private val moveSpeed = GameConstants.PLAYER_SPEED.toFloat() * 5f // Kết nối với GameConstants
    private val maxVelocity = GameConstants.PLAYER_SPEED.toFloat() * 8f // Tỷ lệ với PLAYER_SPEED
    
    // Input state
    private var isMovingUp = false
    private var isMovingDown = false
    private var isMovingLeft = false
    private var isMovingRight = false
    
    // Animation - CHỈ DECLARE 1 LẦN
    private var currentDirection = Direction.IDLE
    private var currentFrame = 0
    private var frameTime = 0L
    private val frameInterval = 150L
    
    // Snap state - ĐƠN GIẢN
    private var shouldSnap = false
    private var snapCooldown = 0L
    private var wasBlockedByCollision = false // Track if stopped due to collision
    
    // Sprite sheet properties
    private var spriteSheet: Bitmap? = null
    private val totalFrames = 4 // 4 frames per direction
    private var frameWidth = 0
    private var frameHeight = 0
    
    // Direction enum theo layout sprite sheet mới (horizontal layout)
    enum class Direction(val startFrame: Int) {
        IDLE(0),   // Frames 0-3: đứng yên
        LEFT(4),   // Frames 4-7: đi trái
        RIGHT(8),  // Frames 8-11: đi phải
        UP(12),    // Frames 12-15: đi lên
        DOWN(16)   // Frames 16-19: đi xuống
    }
    
    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }
    
    private val srcRect = Rect()
    private val destRect = RectF()
    
    private val size = GameConstants.TILE_SIZE.toFloat() * 1.0f // 96 * 1.0 = 96 pixels - giữ nguyên kích thước tile
    
    // Push logic reference
    private var pushLogic: PushLogic? = null
    
    // Thêm variables để track push attempts
    private var lastPushTime = 0L
    private var pushCooldown = 500L // 500ms cooldown between pushes
    
    // Ice sliding variables
    private var isOnIce = false
    private var iceSlideDirection = Direction.IDLE
    private var iceSlideSpeed = 0f
    private val maxIceSlideSpeed = moveSpeed * 1.5f
    private var lastIceCheck = 0L
    
    init {
        loadSpriteSheet()
    }
    
    private fun loadSpriteSheet() {
        try {
            spriteSheet = BitmapFactory.decodeResource(
                context.resources, 
                R.drawable.player_sprite_sheet
            )
            
            spriteSheet?.let { bitmap ->
                // Calculate frame dimensions (horizontal layout: 20 frames x 1 row)
                frameWidth = bitmap.width / 44  // 20 frames total (5 animations × 4 frames each)
                frameHeight = bitmap.height     // 1 row only
                println("✅ Loaded sprite sheet with frame size: ${frameWidth}x${frameHeight}")
            }
        } catch (e: Exception) {
            println("❌ Failed to load sprite sheet: ${e.message}")
        }
    }
    
    fun setPushLogic(pushLogic: PushLogic) {
        this.pushLogic = pushLogic
        println("🔄 PushLogic has been set for SpritePlayer!")
    }
    
    // Footstep sound variables
    private var lastFootstepTime = 0L
    private val footstepInterval = 450L // Phát âm thanh mỗi 450ms khi di chuyển
    
    // Update method để handle push cooldown
    fun update(deltaTime: Long) {
        val deltaSeconds = deltaTime / 1000f
        
        // Update push logic animations
        pushLogic?.update(deltaSeconds)
        
        // Handle snap cooldown
        if (snapCooldown > 0) {
            snapCooldown -= deltaTime
        }
        
        // Check if currently on ice
        checkIceStatus()
        
        // Update movement (includes push logic and ice sliding)
        if (isOnIce) {
            updateIceSliding(deltaSeconds)
        } else {
            updateSimpleMovement(deltaSeconds)
        }
        
        // Update animation
        updateAnimation(deltaTime)
        
        // Handle snapping (only if not on ice)
        if (!isOnIce) {
            handleSnapping(deltaTime)
        }
        
        // Handle footstep sounds
        handleFootstepSounds(deltaTime)
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
            
            // Reset snap trigger and collision flag when starting to move
            shouldSnap = false
            wasBlockedByCollision = false
            
            // If on ice and trying to move, check if we should start sliding
            if (isOnIce && iceSlideSpeed <= 0f) {
                // Player is on ice and trying to move - start sliding
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    iceSlideDirection = if (velocityX > 0) Direction.RIGHT else Direction.LEFT
                } else {
                    iceSlideDirection = if (velocityY > 0) Direction.DOWN else Direction.UP
                }
                iceSlideSpeed = moveSpeed * 0.8f
                println("🧊 Starting ice slide from input: direction $iceSlideDirection")
            }
        } else {
            // Not moving - decelerate
            velocityX *= 0.6f
            velocityY *= 0.6f
            
            // Trigger snap when velocity gets low AND not blocked by collision
            if (!shouldSnap && Math.abs(velocityX) < 20f && Math.abs(velocityY) < 20f && !wasBlockedByCollision) {
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
            println("tryPushStone")
            if (tryPushStone(newX, newY)) {
                // Push succeeded - allow movement
                x = newX
                y = newY
                wasBlockedByCollision = false // Push succeeded, not blocked
            } else {
                // Normal collision - stop movement and mark as blocked
                velocityX = 0f
                velocityY = 0f
                wasBlockedByCollision = true // Blocked by collision, don't snap
                shouldSnap = false // Explicitly prevent snapping
            }
        } else {
            // Normal movement
            x = newX
            y = newY
            wasBlockedByCollision = false // Clear collision flag during normal movement
        }
    }
    
    private fun checkIceStatus() {
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        val centerX = x + tileSize / 2
        val centerY = y + tileSize / 2
        val tileX = (centerX / tileSize).toInt()
        val tileY = (centerY / tileSize).toInt()
        
        // Check main layer for ice tiles
        val currentTile = gameMap.getTile(tileX, tileY, 1) // Main layer
        val wasOnIce = isOnIce
        isOnIce = TileConstants.isIce(currentTile)
        
        // If just stepped onto ice, start sliding in current movement direction
        if (isOnIce && !wasOnIce) {
            println("🧊 Player stepped onto ice tile $currentTile at ($tileX, $tileY)")
            // Determine slide direction from current movement
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                iceSlideDirection = if (velocityX > 0) Direction.RIGHT else Direction.LEFT
            } else {
                iceSlideDirection = if (velocityY > 0) Direction.DOWN else Direction.UP
            }
            
            // Set initial ice slide speed based on current velocity
            iceSlideSpeed = Math.max(moveSpeed * 0.8f, 
                Math.sqrt((velocityX * velocityX + velocityY * velocityY).toDouble()).toFloat())
            
            println("🧊 Starting ice slide in direction: $iceSlideDirection with speed: $iceSlideSpeed")
        }
        
        // If left ice, stop sliding
        if (!isOnIce && wasOnIce) {
            println("🧊 Player left ice, stopping slide")
            iceSlideSpeed = 0f
        }
    }
    
    private fun updateIceSliding(deltaSeconds: Float) {
        if (iceSlideSpeed <= 0f) {
            // No sliding happening, allow normal movement but check for ice exit
            updateSimpleMovement(deltaSeconds)
            return
        }
        
        // Calculate slide movement
        val slideVelX = when (iceSlideDirection) {
            Direction.LEFT -> -iceSlideSpeed
            Direction.RIGHT -> iceSlideSpeed
            else -> 0f
        }
        
        val slideVelY = when (iceSlideDirection) {
            Direction.UP -> -iceSlideSpeed
            Direction.DOWN -> iceSlideSpeed
            else -> 0f
        }
        
        // Calculate new position
        val newX = x + slideVelX * deltaSeconds
        val newY = y + slideVelY * deltaSeconds
        
        // Check if new position is valid
        if (isPositionValidWithDirection(newX, newY, iceSlideDirection)) {
            x = newX
            y = newY
            
            // Update velocity for animation
            velocityX = slideVelX
            velocityY = slideVelY
            currentDirection = iceSlideDirection
            
            // Slightly increase slide speed (acceleration on ice)
            iceSlideSpeed = Math.min(iceSlideSpeed * 1.02f, maxIceSlideSpeed)
            
        } else {
            // Hit obstacle - stop sliding
            println("🧊 Ice slide stopped by obstacle")
            iceSlideSpeed = 0f
            velocityX = 0f
            velocityY = 0f
            wasBlockedByCollision = true // Ice slide blocked by obstacle
            shouldSnap = false // Don't snap when blocked on ice
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
            } else if (totalDist < 47f) {
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
        
        // Kiểm tra va chạm theo hướng di chuyển hiện tại
        return isPositionValidWithDirection(testX, testY, currentDirection)
    }
    
    private fun isPositionValidWithDirection(testX: Float, testY: Float, direction: Direction): Boolean {
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        val playerCollisionRadius = tileSize * 0.35f // Bán kính người chơi (40% tile size)
        
        val centerX = testX + tileSize / 2
        val centerY = testY + tileSize / 2
        
        // Tính toán 2 điểm kiểm tra dựa trên hướng di chuyển
        val (checkPoint1, checkPoint2) = when (direction) {
            Direction.UP -> {
                // Kiểm tra 2 điểm phía trên: trái và phải
                Pair(
                    Pair(centerX - playerCollisionRadius, centerY - playerCollisionRadius), // Top-left
                    Pair(centerX + playerCollisionRadius, centerY - playerCollisionRadius)  // Top-right
                )
            }
            Direction.DOWN -> {
                // Kiểm tra 2 điểm phía dưới: trái và phải
                Pair(
                    Pair(centerX - playerCollisionRadius, centerY + playerCollisionRadius), // Bottom-left
                    Pair(centerX + playerCollisionRadius, centerY + playerCollisionRadius)  // Bottom-right
                )
            }
            Direction.LEFT -> {
                // Kiểm tra 2 điểm phía trái: trên và dưới
                Pair(
                    Pair(centerX - playerCollisionRadius, centerY - playerCollisionRadius), // Top-left
                    Pair(centerX - playerCollisionRadius, centerY + playerCollisionRadius)  // Bottom-left
                )
            }
            Direction.RIGHT -> {
                // Kiểm tra 2 điểm phía phải: trên và dưới
                Pair(
                    Pair(centerX + playerCollisionRadius, centerY - playerCollisionRadius), // Top-right
                    Pair(centerX + playerCollisionRadius, centerY + playerCollisionRadius)  // Bottom-right
                )
            }
            Direction.IDLE -> {
                // Khi đứng yên, chỉ kiểm tra điểm trung tâm
                val tileX = (centerX / tileSize).toInt()
                val tileY = (centerY / tileSize).toInt()
                
                // Check active layer for pushable stones
                val activeTile = gameMap.getTile(tileX, tileY, 2)
                if (TileConstants.isPushable(activeTile)) {
                    return false
                }
                
                return gameMap.isWalkable(tileX, tileY)
            }
        }
        
        // Kiểm tra cả 2 điểm
        val checkPoints = arrayOf(checkPoint1, checkPoint2)
        
        for ((checkX, checkY) in checkPoints) {
            val tileX = (checkX / tileSize).toInt()
            val tileY = (checkY / tileSize).toInt()
            
            // Check active layer for pushable stones
            val activeTile = gameMap.getTile(tileX, tileY, 2)
            if (TileConstants.isPushable(activeTile)) {
                println("🔍 Found pushable stone on active layer at ($tileX, $tileY): $activeTile")
                if (tryPushStone(checkX, checkY)) {
                    return true
                } else {
                    return false
                }
            }
            
            // Check walkability (main layer terrain)
            if (!gameMap.isWalkable(tileX, tileY)) {
                return false
            }
        }
        
        return true
    }
    
    private fun updateDirection() {
        currentDirection = when {
            Math.abs(velocityX) > Math.abs(velocityY) -> {
                if (velocityX > 0) Direction.RIGHT else Direction.LEFT
            }
            velocityY > 0 -> Direction.DOWN
            velocityY < 0 -> Direction.UP
            else -> Direction.IDLE
        }
    }
    
    private fun updateAnimation(deltaTime: Long) {
        if (Math.abs(velocityX) > 15f || Math.abs(velocityY) > 15f) {
            // Moving - animate
            frameTime += deltaTime
            if (frameTime >= frameInterval) {
                currentFrame = (currentFrame + 1) % totalFrames
                frameTime = 0
            }
        } else {
            // Not moving - use idle animation
            currentDirection = Direction.IDLE
            frameTime += deltaTime
            if (frameTime >= frameInterval) {
                currentFrame = (currentFrame + 1) % totalFrames
                frameTime = 0
            }
        }
    }
    
    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + (b - a) * t.coerceIn(0f, 1f)
    }
    
    private fun handleFootstepSounds(deltaTime: Long) {
        val currentTime = System.currentTimeMillis()
        if (isCurrentlyMoving() && currentTime - lastFootstepTime >= footstepInterval) {
            MusicManager.playSound(context, "footsteps")
            lastFootstepTime = currentTime
        }
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
        
        // DON'T stop ice sliding - let player slide until they hit obstacle or leave ice
        // Removed: if (isOnIce) { iceSlideSpeed = 0f }
    }
    
    fun isCurrentlyMoving(): Boolean {
        return Math.abs(velocityX) > 10f || Math.abs(velocityY) > 10f || iceSlideSpeed > 0f
    }
    
    // Existing methods remain the same
    fun move(dx: Int, dy: Int, gameMap: GameMap): Boolean {
        val currentTileX = getCurrentTileX()
        val currentTileY = getCurrentTileY()
        
        val newTileX = currentTileX + dx
        val newTileY = currentTileY + dy
        
        println("🚶 SpritePlayer trying to move from ($currentTileX, $currentTileY) to ($newTileX, $newTileY)")
        
        // Check normal movement first
        if (gameMap.isWalkable(newTileX, newTileY)) {
            println("✅ Normal movement allowed")
            x = newTileX * GameConstants.TILE_SIZE.toFloat()
            y = newTileY * GameConstants.TILE_SIZE.toFloat()
            return true
        }
        
        // Check push logic - pushable objects are on active layer (layer 2)
        val mainTile = gameMap.getTile(newTileX, newTileY, 1) // Main layer
        val activeTile = gameMap.getTile(newTileX, newTileY, 2) // Active layer
        println("🔍 Main layer tile at ($newTileX, $newTileY): $mainTile")
        println("🔍 Active layer tile at ($newTileX, $newTileY): $activeTile")
        
        if (TileConstants.isPushable(activeTile)) {
            println("🔄 Found pushable tile $activeTile, checking push logic...")
            println("🔄 PushLogic available: ${pushLogic != null}")
            
            if (pushLogic?.tryPush(currentTileX, currentTileY, dx, dy) == true) {
                println("✅ Push successful, moving player")
                x = newTileX * GameConstants.TILE_SIZE.toFloat()
                y = newTileY * GameConstants.TILE_SIZE.toFloat()
                return true
            } else {
                println("❌ Push failed")
            }
        } else {
            println("❌ Tile $activeTile is not pushable")
        }
        
        return false
    }
    
    fun draw(canvas: Canvas) {
        val sprite = spriteSheet
        
        if (sprite != null && frameWidth > 0 && frameHeight > 0) {
            // Calculate source rectangle trong sprite sheet (horizontal layout)
            val frameIndex = currentDirection.startFrame + (currentFrame % 4) // 4 frames per animation
            val srcX = frameIndex * frameWidth
            val srcY = 0 // Chỉ có 1 row
            
            srcRect.set(srcX, srcY, srcX + frameWidth, srcY + frameHeight)
            
            // Calculate destination rectangle on screen với offset lên trên
            val centerX = x + GameConstants.TILE_SIZE / 2f
            val centerY = y + GameConstants.TILE_SIZE / 2f
            
            // Offset nhân vật lên trên một chút (khoảng 8-12 pixels)
            val yOffset = -10f // Số âm để đẩy lên trên
            val adjustedCenterY = centerY + yOffset
            
            // Giữ nguyên tỷ lệ aspect ratio của sprite gốc (24x24 = 1:1)
            val spriteAspectRatio = frameWidth.toFloat() / frameHeight.toFloat()
            val displaySize = size
            
            val displayWidth = displaySize
            val displayHeight = displaySize / spriteAspectRatio
            
            destRect.set(
                centerX - displayWidth / 2,
                adjustedCenterY - displayHeight / 2,
                centerX + displayWidth / 2,
                adjustedCenterY + displayHeight / 2
            )
            
            canvas.drawBitmap(sprite, srcRect, destRect, paint)
        } else {
            // Fallback drawing với offset
            drawFallbackPlayer(canvas)
        }
    }
    
    private fun drawFallbackPlayer(canvas: Canvas) {
        val centerX = x + GameConstants.TILE_SIZE / 2f
        val centerY = y + GameConstants.TILE_SIZE / 2f
        
        // Áp dụng cùng offset như sprite drawing
        val yOffset = -10f
        val adjustedCenterY = centerY + yOffset
        
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
        
        canvas.drawCircle(centerX, adjustedCenterY, radius, bodyPaint)
        canvas.drawCircle(centerX, adjustedCenterY, radius, borderPaint)
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

    // Method để thử đẩy đá sử dụng hướng di chuyển hiện tại
    private fun tryPushStone(newX: Float, newY: Float): Boolean {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastPushTime < pushCooldown) {
            return false // Still in cooldown
        }
        
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        
        // Tính hướng đẩy từ currentDirection thay vì từ vị trí
        val (dx, dy) = when (currentDirection) {
            Direction.LEFT -> Pair(-1, 0)
            Direction.RIGHT -> Pair(1, 0)
            Direction.UP -> Pair(0, -1)
            Direction.DOWN -> Pair(0, 1)
            Direction.IDLE -> return false // Không đẩy khi đứng yên
        }
        
        // Get current tile position
        val currentTileX = getCurrentTileX()
        val currentTileY = getCurrentTileY()
        
        // Tính vị trí đá dựa trên hướng di chuyển
        val stoneTileX = currentTileX + dx
        val stoneTileY = currentTileY + dy
        
        // Check if target tile has pushable stone - CHECK ACTIVE LAYER (layer 2)
        val targetTile = gameMap.getTile(stoneTileX, stoneTileY, 2) // Active layer where pushable objects are
        if (!TileConstants.isPushable(targetTile)) {
            return false // Không có đá để đẩy
        }
        
        println("🔄 Found pushable stone at ($stoneTileX, $stoneTileY), attempting push in direction ($dx, $dy)")
        
        // Try to push the stone
        if (pushLogic?.tryPush(currentTileX, currentTileY, dx, dy) == true) {
            lastPushTime = currentTime
            println("🪨 Successfully pushed stone!")
            return true // Đẩy thành công
        } else {
            println("❌ PushLogic.tryPush() returned false")
            return false // Đẩy thất bại
        }
    }
}