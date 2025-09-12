package com.example.game.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.example.game.Camera
import com.example.game.GameConstants
import com.example.game.GameMap
import com.example.game.Player
import com.example.game.core.GameStateManager
import com.example.game.map.TileConstants
import com.example.game.SaveManager
import com.example.game.gameMechanic.PushLogic
import com.example.game.SpritePlayer

class GameScreen(
    private val gameStateManager: GameStateManager,
    private val levelId: Int,
    private val context: Context
) : Screen() {
    
    private lateinit var gameMap: GameMap
    private lateinit var player: SpritePlayer
    private lateinit var camera: Camera
    private var pushLogic: PushLogic? = null
    
    // UI Elements
    private val pauseButton = RectF()
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
    }
    
    private fun initLevel() {
        gameMap = GameMap.loadLevel(context, levelId)
        
        // Initialize push logic chỉ cho level 1
        if (levelId == 1) {
            pushLogic = PushLogic(gameMap)
        }
        
        // Debug: In thông tin map
        println("Map loaded - Width: ${gameMap.width}, Height: ${gameMap.height}, SpawnX: ${gameMap.playerSpawnX}, SpawnY: ${gameMap.playerSpawnY}")
        
        // Sử dụng SpritePlayer với gameMap
        player = SpritePlayer(
            (gameMap.playerSpawnX * GameConstants.TILE_SIZE).toFloat(),
            (gameMap.playerSpawnY * GameConstants.TILE_SIZE).toFloat(),
            context,
            gameMap  // ←── THÊM LẠI gameMap VÀO ĐÂY
        )
        
        // Connect push logic cho level 1
        if (levelId == 1 && pushLogic != null) {
            player.setPushLogic(pushLogic!!)
        }
        
        camera = Camera()
    }
    
    override fun update(deltaTime: Long) {
        if (levelCompleted) {
            completionTimer += deltaTime
            if (completionTimer >= completionDelay) {
                // Sửa từ setState() thành changeState()
                gameStateManager.changeState(GameConstants.STATE_LEVEL_SELECT)
            }
            return
        }
        
        player.update(deltaTime)
        camera.update(player.getCenterX(), player.getCenterY(), gameMap.width, gameMap.height)
        updateUIPositions()
        
        // Check completion based on level type
        val isComplete = if (levelId == 1) {
            // Level 1: Puzzle mechanics
            player.checkPuzzleComplete()
        } else {
            // Other levels: Traditional reach-end
            player.checkLevelComplete(gameMap)
        }

        if (isComplete) {
            completeLevel()
        }
        
        // Continuous movement handling
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
            completionTimer = 0
            
            println("🎉 LEVEL $levelId COMPLETED! 🎉")
            
            // Unlock level tiếp theo và save
            if (levelId >= GameConstants.MAX_UNLOCKED_LEVEL && levelId < GameConstants.TOTAL_LEVELS) {
                SaveManager.unlockLevel(levelId + 1)
            }
            
            // TODO: Có thể thêm effect, sound, animation ở đây
        }
    }
    
    private fun updateUIPositions() {
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        val screenW = GameConstants.SCREEN_WIDTH
        val screenH = GameConstants.SCREEN_HEIGHT
        
        // Pause button
        pauseButton.set(screenW - 150f, 20f, screenW - 20f, 100f)
        
        // HUGE touchpad
        val touchpadSize = minOf(screenW, screenH) * 0.4f
        val buttonSize = touchpadSize * 0.3f
        val margin = 40f
        
        val touchpadCenterX = margin + touchpadSize / 2
        val touchpadCenterY = screenH - margin - touchpadSize / 2
        
        touchpadBase.set(
            touchpadCenterX - touchpadSize / 2,
            touchpadCenterY - touchpadSize / 2,
            touchpadCenterX + touchpadSize / 2,
            touchpadCenterY + touchpadSize / 2
        )
        
        val buttonOffset = touchpadSize * 0.25f
        
        upButton.set(
            touchpadCenterX - buttonSize/2,
            touchpadCenterY - buttonOffset - buttonSize/2,
            touchpadCenterX + buttonSize/2,
            touchpadCenterY - buttonOffset + buttonSize/2
        )
        
        downButton.set(
            touchpadCenterX - buttonSize/2,
            touchpadCenterY + buttonOffset - buttonSize/2,
            touchpadCenterX + buttonSize/2,
            touchpadCenterY + buttonOffset + buttonSize/2
        )
        
        leftButton.set(
            touchpadCenterX - buttonOffset - buttonSize/2,
            touchpadCenterY - buttonSize/2,
            touchpadCenterX - buttonOffset + buttonSize/2,
            touchpadCenterY + buttonSize/2
        )
        
        rightButton.set(
            touchpadCenterX + buttonOffset - buttonSize/2,
            touchpadCenterY - buttonSize/2,
            touchpadCenterX + buttonOffset + buttonSize/2,
            touchpadCenterY + buttonSize/2
        )
    }
    
    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.parseColor("#222222"))
        
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        canvas.save()
        camera.apply(canvas)
        
        gameMap.draw(canvas, camera)
        player.draw(canvas)
        
        canvas.restore()
        drawUI(canvas)
        
        // Vẽ thông báo hoàn thành level
        if (levelCompleted) {
            drawCompletionMessage(canvas)
        }
    }
    
    private fun drawUI(canvas: Canvas) {
        // Touchpad
        canvas.drawOval(touchpadBase, touchpadBasePaint)
        canvas.drawOval(touchpadBase, controlBorderPaint)
        
        // Direction buttons
        val upPaint = if (currentDirection == "UP" && isButtonPressed) controlPressedPaint else controlPaint
        val downPaint = if (currentDirection == "DOWN" && isButtonPressed) controlPressedPaint else controlPaint
        val leftPaint = if (currentDirection == "LEFT" && isButtonPressed) controlPressedPaint else controlPaint
        val rightPaint = if (currentDirection == "RIGHT" && isButtonPressed) controlPressedPaint else controlPaint
        
        canvas.drawOval(upButton, upPaint)
        canvas.drawOval(upButton, controlBorderPaint)
        canvas.drawText("↑", upButton.centerX(), upButton.centerY() + 15, arrowPaint)
        
        canvas.drawOval(downButton, downPaint)
        canvas.drawOval(downButton, controlBorderPaint)
        canvas.drawText("↓", downButton.centerX(), downButton.centerY() + 15, arrowPaint)
        
        canvas.drawOval(leftButton, leftPaint)
        canvas.drawOval(leftButton, controlBorderPaint)
        canvas.drawText("←", leftButton.centerX(), leftButton.centerY() + 15, arrowPaint)
        
        canvas.drawOval(rightButton, rightPaint)
        canvas.drawOval(rightButton, controlBorderPaint)
        canvas.drawText("→", rightButton.centerX(), rightButton.centerY() + 15, arrowPaint)
        
        // Pause button
        canvas.drawRoundRect(pauseButton, 15f, 15f, pauseButtonPaint)
        canvas.drawRoundRect(pauseButton, 15f, 15f, controlBorderPaint)
        canvas.drawText("PAUSE", pauseButton.centerX(), pauseButton.centerY() + 8, pauseTextPaint)
        
        // Info
        pauseTextPaint.textAlign = Paint.Align.LEFT
        pauseTextPaint.textSize = 20f
        canvas.drawText("Level $levelId", 20f, 40f, pauseTextPaint)
        canvas.drawText("Tile: (${player.getCurrentTileX()}, ${player.getCurrentTileY()})", 20f, 70f, pauseTextPaint)
        
        // Show puzzle progress chỉ cho level 1
        if (levelId == 1 && pushLogic != null) {
            val (completed, total) = pushLogic!!.getProgress()
            canvas.drawText("Progress: $completed/$total", 20f, 100f, pauseTextPaint)
        }
        
        pauseTextPaint.textAlign = Paint.Align.CENTER
        pauseTextPaint.textSize = 24f
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
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (pauseButton.contains(event.x, event.y)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        gameStateManager.changeState(GameConstants.STATE_LEVEL_SELECT)
                    }
                    return true
                }
                
                when {
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
                    touchpadBase.contains(event.x, event.y) -> {
                        val centerX = touchpadBase.centerX()
                        val centerY = touchpadBase.centerY()
                        val deltaX = event.x - centerX
                        val deltaY = event.y - centerY
                        
                        currentDirection = if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            if (deltaX > 0) "RIGHT" else "LEFT"
                        } else {
                            if (deltaY > 0) "DOWN" else "UP"
                        }
                        
                        isButtonPressed = true
                        return true
                    }
                    else -> {
                        isButtonPressed = false
                        currentDirection = ""
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
}
