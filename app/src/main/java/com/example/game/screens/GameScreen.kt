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

class GameScreen(
    private val gameStateManager: GameStateManager,
    private val levelId: Int,
    private val context: Context
) : Screen() {
    
    private lateinit var gameMap: GameMap
    private lateinit var player: Player
    private lateinit var camera: Camera
    
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
    
    init {
        initLevel()
    }
    
    private fun initLevel() {
        // Bây giờ có thể dùng context để load map từ file nếu cần
        gameMap = when (levelId) {
            1 -> GameMap.createTestMap() // Tạm thời dùng test map
            else -> GameMap.createTestMap()
        }
        
        player = Player(
            GameConstants.TILE_SIZE.toFloat(),
            GameConstants.TILE_SIZE.toFloat()
        )
        
        camera = Camera()
    }
    
    override fun update(deltaTime: Long) {
        player.update(deltaTime)
        camera.update(player.getCenterX(), player.getCenterY(), gameMap.width, gameMap.height)
        updateUIPositions()
        
        if (isButtonPressed) {
            moveTimer += deltaTime
            
            if (moveTimer >= moveInterval && !player.isCurrentlyMoving()) {
                moveTimer = 0
                when (currentDirection) {
                    "UP" -> player.move(0, -1, gameMap)
                    "DOWN" -> player.move(0, 1, gameMap)
                    "LEFT" -> player.move(-1, 0, gameMap)
                    "RIGHT" -> player.move(1, 0, gameMap)
                }
            }
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
        pauseTextPaint.textAlign = Paint.Align.CENTER
        pauseTextPaint.textSize = 24f
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
                        if (currentDirection != "UP") {
                            currentDirection = "UP"
                            isButtonPressed = true
                            moveTimer = moveInterval
                        }
                        return true
                    }
                    downButton.contains(event.x, event.y) -> {
                        if (currentDirection != "DOWN") {
                            currentDirection = "DOWN"
                            isButtonPressed = true
                            moveTimer = moveInterval
                        }
                        return true
                    }
                    leftButton.contains(event.x, event.y) -> {
                        if (currentDirection != "LEFT") {
                            currentDirection = "LEFT"
                            isButtonPressed = true
                            moveTimer = moveInterval
                        }
                        return true
                    }
                    rightButton.contains(event.x, event.y) -> {
                        if (currentDirection != "RIGHT") {
                            currentDirection = "RIGHT"
                            isButtonPressed = true
                            moveTimer = moveInterval
                        }
                        return true
                    }
                    touchpadBase.contains(event.x, event.y) -> {
                        val centerX = touchpadBase.centerX()
                        val centerY = touchpadBase.centerY()
                        val deltaX = event.x - centerX
                        val deltaY = event.y - centerY
                        
                        val newDirection = if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            if (deltaX > 0) "RIGHT" else "LEFT"
                        } else {
                            if (deltaY > 0) "DOWN" else "UP"
                        }
                        
                        if (currentDirection != newDirection) {
                            currentDirection = newDirection
                            isButtonPressed = true
                            moveTimer = moveInterval
                        }
                        return true
                    }
                    else -> {
                        isButtonPressed = false
                        currentDirection = ""
                        moveTimer = 0
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                isButtonPressed = false
                currentDirection = ""
                moveTimer = 0
                return true
            }
        }
        return false
    }
}
