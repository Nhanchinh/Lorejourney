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
import com.example.game.map.MapLoader
import com.example.game.map.TileRenderer
import com.example.game.map.SimpleGameMap
import com.example.game.SaveManager

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
    
    // Thêm biến để tracking level completion
    private var levelCompleted = false
    private var completionTimer = 0L
    private val completionDelay = 1000L // 1 giây delay trước khi chuyển
    
    init {
        initLevel()
    }
    
    private fun initLevel() {
        // Load map theo levelId từ assets hoặc fallback
        gameMap = try {
            // Thử load từ assets trước
            val loadedMap = GameMap.loadFromAssets(context, "level$levelId.txt")
            println("Loaded map from assets: level$levelId.txt") // Debug log
            loadedMap
        } catch (e: Exception) {
            println("Failed to load from assets: ${e.message}") // Debug log
            // Nếu không có file, dùng map mặc định theo level
            when (levelId) {
                1 -> {
                    println("Using hardcoded level 1 map") // Debug log
                    createLevel1Map()
                }
                2 -> {
                    println("Using hardcoded level 2 map") // Debug log
                    createLevel2Map()
                }
                3 -> {
                    println("Using hardcoded level 3 map") // Debug log
                    createLevel3Map()
                }
                4 -> {
                    println("Using hardcoded level 4 map") // Debug log
                    createLevel4Map()
                }
                5 -> {
                    println("Using hardcoded level 5 map") // Debug log
                    createLevel5Map()
                }
                else -> {
                    println("Using test map for level $levelId") // Debug log
                    GameMap.createTestMap()
                }
            }
        }
        
        // Debug: In thông tin map
        println("Map loaded - Width: ${gameMap.width}, Height: ${gameMap.height}, SpawnX: ${gameMap.playerSpawnX}, SpawnY: ${gameMap.playerSpawnY}")
        
        // Sử dụng spawn position từ map
        player = Player(
            (gameMap.playerSpawnX * GameConstants.TILE_SIZE).toFloat(),
            (gameMap.playerSpawnY * GameConstants.TILE_SIZE).toFloat()
        )
        
        camera = Camera()
    }

    private fun createLevel1Map(): GameMap {
        val mapString = """
12
10
1
1
1,1,1,1,1,1,1,1,1,1,1,1
1,2,2,2,2,2,2,2,2,2,2,1
1,2,3,20,3,3,21,3,3,22,2,1
1,2,3,3,3,3,3,3,3,3,2,1
1,2,2,12,2,2,2,2,12,2,2,1
1,2,31,31,10,31,31,11,31,2,1
1,2,31,31,31,31,31,31,31,2,1
1,2,2,2,2,13,2,2,2,14,2,1
1,2,4,4,4,4,4,4,4,4,2,1
1,1,1,1,1,1,1,1,1,1,1,1
        """.trimIndent()
        return GameMap.loadFromString(mapString)
    }

    private fun createLevel2Map(): GameMap {
        val mapString = """
15
12
2
2
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,3,3,20,3,3,1,3,3,21,3,3,2,1
1,2,3,3,3,3,3,1,3,3,3,3,3,2,1
1,2,20,3,3,22,3,1,3,21,3,3,20,2,1
1,1,1,1,1,12,1,1,1,12,1,1,1,1,1
1,2,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,10,31,31,31,31,31,31,11,31,2,1
1,2,31,31,31,13,31,31,13,31,31,31,2,1
1,2,2,2,2,2,2,2,2,2,2,14,2,2,1
1,2,4,4,4,4,4,40,40,4,4,4,4,4,1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
        """.trimIndent()
        return GameMap.loadFromString(mapString)
    }

    private fun createLevel3Map(): GameMap {
        val mapString = """
18
14
1
1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,3,20,3,3,21,3,3,1,3,3,22,3,3,20,2,1
1,2,3,3,3,3,3,3,3,1,3,3,3,3,3,3,2,1
1,2,20,3,3,22,3,3,3,12,3,3,21,3,3,20,2,1
1,1,1,1,1,1,1,1,1,12,1,1,1,1,1,1,1,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,10,31,31,31,13,31,31,13,31,31,31,11,31,2,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,31,31,13,31,31,31,31,31,31,13,31,31,31,2,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,14,2,1
1,2,4,4,4,40,40,4,4,12,4,4,40,40,4,4,2,1
1,2,42,42,42,42,42,42,42,2,42,42,42,42,42,42,2,1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
    """.trimIndent()
        return GameMap.loadFromString(mapString)
    }

    private fun createLevel4Map(): GameMap {
        val mapString = """
20
16
2
2
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,3,20,3,3,21,3,3,3,3,3,3,22,3,3,20,3,2,1
1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1
1,2,20,3,3,22,3,3,3,3,3,3,3,3,21,3,3,20,2,1
1,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,1
1,1,1,12,1,1,1,1,1,1,1,1,1,1,1,1,12,1,1,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,10,31,31,31,31,31,31,31,31,31,31,31,31,11,31,2,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,31,31,31,13,31,31,31,31,31,31,31,31,13,31,31,31,2,1
1,2,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,31,2,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,14,2,1
1,2,40,40,40,4,4,4,41,41,41,41,41,4,4,4,40,40,2,1
1,2,42,42,42,42,42,42,42,42,42,42,42,42,42,42,42,42,2,1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
    """.trimIndent()
        return GameMap.loadFromString(mapString)
    }

    private fun createLevel5Map(): GameMap {
        val mapString = """
22
18
1
1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,2,1
1,2,1,20,1,2,1,21,1,2,1,22,1,2,1,20,1,2,1,21,2,1
1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,2,1
1,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1
1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,2,1
1,2,1,10,1,2,1,11,1,2,1,12,1,2,1,13,1,2,1,31,2,1
1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,2,1
1,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,1,2,2,2,2,1
1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1,1,2,1,1
1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1
1,2,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,14,2,1
1,2,40,40,41,41,40,40,41,41,40,40,41,41,40,40,41,41,40,40,2,1
1,2,42,42,42,42,42,42,42,42,42,42,42,42,42,42,42,42,42,42,2,1
1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
    """.trimIndent()
        return GameMap.loadFromString(mapString)
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
        
        // Kiểm tra hoàn thành level
        if (player.checkLevelComplete(gameMap)) {
            completeLevel()
        }
        
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
