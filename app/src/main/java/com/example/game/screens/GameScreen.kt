package com.example.game.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.game.Camera
import com.example.game.GameConstants
import com.example.game.GameMap
import com.example.game.core.GameStateManager
import com.example.game.map.TileConstants
import com.example.game.SaveManager
import com.example.game.gameMechanic.PushLogic
import com.example.game.SpritePlayer
import com.example.game.gameMechanic.ShadowMechanic

class GameScreen(
    private val gameStateManager: GameStateManager,
    private val levelId: Int,
    private val context: Context,
    private val containerLayout: FrameLayout
) : Screen() {
    
    private lateinit var gameMap: GameMap
    private lateinit var player: SpritePlayer
    private lateinit var camera: Camera
    private var pushLogic: PushLogic? = null
    
    // CHỈ CÓ shadowMechanic
    private var shadowMechanic: ShadowMechanic? = null
    
    // UI Elements
    private val pauseButton = RectF()
    private val helpButton = RectF() // THÊM help button
    
    private val pauseButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#88000000")
        style = Paint.Style.FILL
    }
    
    private val pauseTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 24f
        isFakeBoldText = true
    }
    
    // Help button paint
    private val helpTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 28f
        isFakeBoldText = true
    }
    
    // Video popup system
    private val videoPopup = VideoPopup(context)
    
    // HUGE touchpad
    private val touchpadBase = RectF()
    private val upButton = RectF()
    private val downButton = RectF()
    private val leftButton = RectF()
    private val rightButton = RectF()
    
    private val touchpadBasePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#22FFFFFF")
        style = Paint.Style.FILL
    }
    
    private val controlPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#77FFFFFF")
        style = Paint.Style.FILL
    }
    
    private val controlPressedPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#CCFFFFFF")
        style = Paint.Style.FILL
    }
    
    private val controlBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    private val arrowPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        textAlign = Paint.Align.CENTER
        textSize = 48f
        isFakeBoldText = true
    }
    
    // Touch handling
    private var currentDirection = ""
    private var isButtonPressed = false
    private var moveTimer = 0L
    private val moveInterval = 280L
    
    // Thêm biến để tracking level completion
    private var levelCompleted = false
    private var completionTimer = 0L
    private val completionDelay = 1000L // 1 giây delay trước khi chuyển
    
    init {
        initLevel()
        updateUIElements()
    }
    
    private fun initLevel() {
        gameMap = GameMap.loadLevel(context, levelId)
        
        // Initialize PushLogic for levels that have pushable objects
        // For now, initialize for all levels to be safe
        pushLogic = PushLogic(gameMap)
        println("🔄 PushLogic initialized for level $levelId")
        
        // Log thông tin đặc biệt cho map6
        if (levelId == 6) {
            println("Map6 (Sprite-based) loaded - Width: ${gameMap.width}, Height: ${gameMap.height}, SpawnX: ${gameMap.playerSpawnX}, SpawnY: ${gameMap.playerSpawnY}")
        } else {
            println("Map loaded - Width: ${gameMap.width}, Height: ${gameMap.height}, SpawnX: ${gameMap.playerSpawnX}, SpawnY: ${gameMap.playerSpawnY}")
        }
        
        player = SpritePlayer(
            (gameMap.playerSpawnX * GameConstants.TILE_SIZE).toFloat(),
            (gameMap.playerSpawnY * GameConstants.TILE_SIZE).toFloat(),
            context,
            gameMap
        )
        
        // Set PushLogic for levels that need it
        if (pushLogic != null) {
            player.setPushLogic(pushLogic!!)
            println("🔄 PushLogic assigned to player for level $levelId")
        } else {
            println("⚠️ PushLogic is null for level $levelId")
        }
        
        
         // Áp dụng shadow mechanic cho TẤT CẢ level
        shadowMechanic = ShadowMechanic(context, gameMap, player)
        shadowMechanic!!.initialize()
        println(" Shadow mechanic initialized for level $levelId")
        
        camera = Camera()
    }
    
    override fun update(deltaTime: Long) {
        if (levelCompleted) {
            completionTimer += deltaTime
            if (completionTimer >= completionDelay) {
                gameStateManager.changeState(GameConstants.STATE_LEVEL_SELECT)
            }
            return
        }
        
        player.update(deltaTime)
        camera.update(player.getCenterX(), player.getCenterY(), gameMap.width, gameMap.height)
        updateUIPositions()
        
        // CHỈ CÓ 1 dòng này
        shadowMechanic?.update(deltaTime)
        
        val isComplete = if (levelId == 1) {
            player.checkLevelComplete(gameMap)
        } else if (levelId == 3) {
            // CHỈ CÓ dòng này
            player.checkLevelComplete(gameMap) && (shadowMechanic?.isPuzzleComplete() ?: true)
        } else {
            player.checkLevelComplete(gameMap)
        }

        if (isComplete) {
            completeLevel()
        }
        
        if (isButtonPressed) {
            when (currentDirection) {
                "UP" -> player.startMoving("UP")
                "DOWN" -> player.startMoving("DOWN")
                "LEFT" -> player.startMoving("LEFT")
                "RIGHT" -> player.startMoving("RIGHT")
            }
        } else {
            player.stopAllMovement()
        }
    }

    private fun completeLevel() {
        if (!levelCompleted) {
            levelCompleted = true
            SaveManager.unlockLevel(levelId + 1)
        }
    }
    
    private fun updateUIPositions() {
        val screenWidth = GameConstants.SCREEN_WIDTH.toFloat()
        val screenHeight = GameConstants.SCREEN_HEIGHT.toFloat()
        
        // TĂNG KÍCH THƯỚC: Pause button bên phải (to hơn)
        pauseButton.set(screenWidth - 180f, 20f, screenWidth - 20f, 120f)
        
        // TĂNG KÍCH THƯỚC: Touchpad bên trái (to hơn)
        val touchpadSize = minOf(screenWidth, screenHeight) * 0.5f // Tăng từ 0.4f lên 0.5f
        val margin = 30f
        val touchpadLeft = margin
        val touchpadBottom = screenHeight - margin
        val touchpadRight = touchpadLeft + touchpadSize
        val touchpadTop = touchpadBottom - touchpadSize
        
        touchpadBase.set(touchpadLeft, touchpadTop, touchpadRight, touchpadBottom)
        
        // TĂNG KÍCH THƯỚC: Directional buttons to hơn
        val buttonSize = touchpadSize * 0.35f // Tăng từ 0.25f lên 0.35f
        val centerX = touchpadBase.centerX()
        val centerY = touchpadBase.centerY()
        val buttonOffset = touchpadSize * 0.25f // Khoảng cách từ center
        
        // Up button (to hơn)
        upButton.set(
            centerX - buttonSize/2, 
            centerY - buttonOffset - buttonSize/2,
            centerX + buttonSize/2, 
            centerY - buttonOffset + buttonSize/2
        )
        
        // Down button (to hơn)
        downButton.set(
            centerX - buttonSize/2, 
            centerY + buttonOffset - buttonSize/2,
            centerX + buttonSize/2, 
            centerY + buttonOffset + buttonSize/2
        )
        
        // Left button (to hơn)
        leftButton.set(
            centerX - buttonOffset - buttonSize/2, 
            centerY - buttonSize/2,
            centerX - buttonOffset + buttonSize/2, 
            centerY + buttonSize/2
        )
        
        // Right button (to hơn)
        rightButton.set(
            centerX + buttonOffset - buttonSize/2, 
            centerY - buttonSize/2,
            centerX + buttonOffset + buttonSize/2, 
            centerY + buttonSize/2
        )
    }

    private fun updateUIElements() {
        val screenWidth = GameConstants.SCREEN_WIDTH
        val screenHeight = GameConstants.SCREEN_HEIGHT
        
        // Pause button (góc phải trên)
        val pauseButtonSize = 60f
        pauseButton.set(
            screenWidth - pauseButtonSize - 20f,
            20f,
            screenWidth - 20f,
            20f + pauseButtonSize
        )
        
        // Help button (góc trái trên, cách edge 2cm = 80px)
        val helpButtonSize = 60f
        helpButton.set(
            80f, // 2cm từ trái
            20f,
            80f + helpButtonSize,
            20f + helpButtonSize
        )
    }
    
    override fun draw(canvas: Canvas) {
        canvas.save()
        camera.apply(canvas)
        
        gameMap.draw(canvas, camera, pushLogic)
        player.draw(canvas)
        
        // CHỈ CÓ 1 dòng này
        shadowMechanic?.draw(canvas)
        
        canvas.restore()
        drawUI(canvas)
        
        // Draw video popup if active
        if (videoPopup.isActive) {
            videoPopup.draw(canvas)
        }
        
        // THÊM: Vẽ completion message nếu level completed
        if (levelCompleted) {
            drawCompletionMessage(canvas)
        }
    }
    
    private fun drawUI(canvas: Canvas) {
        // Draw pause button đẹp hơn
        val pauseBackgroundPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#44000000") // Semi-transparent background
        }
        
        val pauseIconPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        
        val pauseBorderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        
        // Draw pause button background
        canvas.drawOval(pauseButton, pauseBackgroundPaint)
        canvas.drawOval(pauseButton, pauseBorderPaint)
        
        // Draw pause icon (2 vertical bars thay vì ⏸ symbol)
        val centerX = pauseButton.centerX()
        val centerY = pauseButton.centerY()
        val barWidth = 8f
        val barHeight = 24f
        val barSpacing = 6f
        
        // Left bar
        canvas.drawRoundRect(
            centerX - barSpacing - barWidth, centerY - barHeight/2,
            centerX - barSpacing, centerY + barHeight/2,
            4f, 4f, pauseIconPaint
        )
        
        // Right bar  
        canvas.drawRoundRect(
            centerX + barSpacing, centerY - barHeight/2,
            centerX + barSpacing + barWidth, centerY + barHeight/2,
            4f, 4f, pauseIconPaint
        )
        
        // Draw help button
        drawHelpButton(canvas)
        
        // Draw touchpad background
        canvas.drawOval(touchpadBase, touchpadBasePaint)
        canvas.drawOval(touchpadBase, controlBorderPaint)
        
        // Draw directional buttons with press states
        val upPressed = currentDirection == "UP" && isButtonPressed
        val downPressed = currentDirection == "DOWN" && isButtonPressed
        val leftPressed = currentDirection == "LEFT" && isButtonPressed
        val rightPressed = currentDirection == "RIGHT" && isButtonPressed
        
        // Up button
        canvas.drawOval(upButton, if (upPressed) controlPressedPaint else controlPaint)
        canvas.drawOval(upButton, controlBorderPaint)
        
        // Down button
        canvas.drawOval(downButton, if (downPressed) controlPressedPaint else controlPaint)
        canvas.drawOval(downButton, controlBorderPaint)
        
        // Left button
        canvas.drawOval(leftButton, if (leftPressed) controlPressedPaint else controlPaint)
        canvas.drawOval(leftButton, controlBorderPaint)
        
        // Right button
        canvas.drawOval(rightButton, if (rightPressed) controlPressedPaint else controlPaint)
        canvas.drawOval(rightButton, controlBorderPaint)
        
        // Tăng kích thước arrows
        val arrowPaintLarge = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            textSize = 64f // Tăng từ 48f lên 64f
            isFakeBoldText = true
        }
        
        canvas.drawText("▲", upButton.centerX(), upButton.centerY() + 20f, arrowPaintLarge)
        canvas.drawText("▼", downButton.centerX(), downButton.centerY() + 20f, arrowPaintLarge)
        canvas.drawText("◀", leftButton.centerX(), leftButton.centerY() + 20f, arrowPaintLarge)
        canvas.drawText("▶", rightButton.centerX(), rightButton.centerY() + 20f, arrowPaintLarge)
        
        // Shadow info display cho level 3
        if (levelId == 3) {
            shadowMechanic?.getShadowInfo()?.let { info ->
                val infoPaint = Paint().apply {
                    color = Color.WHITE
                    textSize = 30f
                    isAntiAlias = true
                    setShadowLayer(2f, 1f, 1f, Color.BLACK)
                }
                canvas.drawText("Shadow: ${info.directionChangeCount}/4 turns", 
                               50f, 150f, infoPaint)
                canvas.drawText("Following: ${info.isFollowing}", 
                               50f, 190f, infoPaint)
            }
        }
    }

    private fun drawHelpButton(canvas: Canvas) {
        val helpBackgroundPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#44000000")
        }
        
        val helpBorderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FFFFFF")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        
        // Draw help button background
        canvas.drawOval(helpButton, helpBackgroundPaint)
        canvas.drawOval(helpButton, helpBorderPaint)
        
        // Draw "?" symbol
        canvas.drawText("?", helpButton.centerX(), helpButton.centerY() + 10f, helpTextPaint)
    }

    private fun drawCompletionMessage(canvas: Canvas) {
        // Vẽ overlay
        val overlayPaint = Paint().apply {
            color = Color.parseColor("#80000000") // Semi-transparent black
        }
        canvas.drawRect(0f, 0f, GameConstants.SCREEN_WIDTH.toFloat(), GameConstants.SCREEN_HEIGHT.toFloat(), overlayPaint)
        
        // Vẽ text "Level Complete!"
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = 60f
            isFakeBoldText = true
        }
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val centerY = GameConstants.SCREEN_HEIGHT / 2f
        
        canvas.drawText("LEVEL $levelId COMPLETE!", centerX, centerY - 50, textPaint)
        
        if (levelId < GameConstants.TOTAL_LEVELS) {
            val subTextPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#FFD700")
                textAlign = Paint.Align.CENTER
                textSize = 40f
            }
            canvas.drawText("Level ${levelId + 1} Unlocked!", centerX, centerY + 20, subTextPaint)
        } else {
            val subTextPaint = Paint().apply {
                isAntiAlias = true
                color = Color.parseColor("#FFD700")
                textAlign = Paint.Align.CENTER
                textSize = 40f
            }
            canvas.drawText("All Levels Complete!", centerX, centerY + 20, subTextPaint)
        }
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        // Handle video popup touches first
        if (videoPopup.isActive) {
            if (videoPopup.handleTouch(event)) {
                return true
            }
            return true // Block other touches when popup is open
        }
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                
                // Check help button
                if (helpButton.contains(x, y)) {
                    // Show different videos based on level
                    val videoName = when (levelId) {
                        1 -> "mechanic1" // Basic controls
                        2 -> "mechanic2" // Push mechanics
                        3 -> "mechanic3" // Shadow mechanics
                        4 -> "mechanic4" // Advanced puzzles
                        5 -> "mechanic5" // More advanced
                        6 -> "mechanic6" // Final mechanics
                        else -> "mechanic1" // Default
                    }
                    videoPopup.show(videoName, containerLayout)
                    return true
                }
                
                when {
                    pauseButton.contains(event.x, event.y) -> {
                        gameStateManager.pauseGame()
                        return true
                    }
                    upButton.contains(event.x, event.y) -> {
                        currentDirection = "UP"
                        isButtonPressed = true
                        return true
                    }
                    downButton.contains(event.x, event.y) -> {
                        currentDirection = "DOWN"
                        isButtonPressed = true
                        return true
                    }
                    leftButton.contains(event.x, event.y) -> {
                        currentDirection = "LEFT"
                        isButtonPressed = true
                        return true
                    }
                    rightButton.contains(event.x, event.y) -> {
                        currentDirection = "RIGHT"
                        isButtonPressed = true
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                isButtonPressed = false
                currentDirection = ""
                return true
            }
        }
        return false
    }

    fun getCurrentProgress(): PauseScreen.GameProgress {
        return PauseScreen.GameProgress(
            playerX = player.x,
            playerY = player.y,
            shadowSpawned = shadowMechanic != null,
            shadowX = 0f,
            shadowY = 0f,
            shadowDirectionChanges = 0,
            doorsOpened = emptyList()
        )
    }

    private fun getOpenedDoors(): List<Pair<Int, Int>> {
        // Return list of opened door positions
        return emptyList() // Implement based on your needs
    }
}
