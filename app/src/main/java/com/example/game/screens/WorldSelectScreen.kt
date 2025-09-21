package com.example.game.screens

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.example.game.GameConstants
import com.example.game.R
import com.example.game.animation.AnimationManager
import com.example.game.core.GameStateManager
import com.example.game.SaveManager
import android.graphics.LinearGradient
import android.graphics.Shader

class WorldSelectScreen(
    private val gameStateManager: GameStateManager,
    private val context: Context,
    private val animationManager: AnimationManager
) : Screen() {
    
    private var backgroundBitmap: Bitmap? = null
    
    // World data
    private data class World(
        val id: Int,
        val name: String,
        val title: String,
        val description: String,
        val difficulty: String,
        val levelRange: IntRange,
        val isUnlocked: Boolean,
        val primaryColor: Int,
        val secondaryColor: Int
    )
    
    // All worlds data - organized by worlds  
    private val allWorldsData = listOf(
        World(
            id = 1,
            name = "World 1",
            title = "Mystical Forest",
            description = "Begin your journey in the enchanted forest where ancient magic still flows through the trees. Learn the basic mechanics and discover the secrets hidden in nature's embrace.",
            difficulty = "Beginner",
            levelRange = 1..4,
            isUnlocked = true,
            primaryColor = Color.parseColor("#66BB6A"),
            secondaryColor = Color.parseColor("#4CAF50")
        ),
        World(
            id = 2,
            name = "World 2",
            title = "Sky Temple",
            description = "Ascend to the floating temples among the clouds. Master advanced puzzle mechanics and navigate through mystical aerial challenges that will test your wisdom.",
            difficulty = "Intermediate", 
            levelRange = 5..7,
            isUnlocked = false,
            primaryColor = Color.parseColor("#42A5F5"),
            secondaryColor = Color.parseColor("#2196F3")
        ),
        World(
            id = 3,
            name = "World 3",
            title = "Cosmic Realm",
            description = "Journey through the infinite cosmos where reality bends and time flows differently. Only the most skilled adventurers can unlock the ultimate secrets of creation.",
            difficulty = "Expert",
            levelRange = 8..10,
            isUnlocked = false,
            primaryColor = Color.parseColor("#AB47BC"),
            secondaryColor = Color.parseColor("#9C27B0")
        )
    )
    
    // Dynamic worlds list based on unlock status
    private val worlds: List<World> get() {
        val maxUnlocked = GameConstants.MAX_UNLOCKED_LEVEL
        return allWorldsData.map { world ->
            when (world.id) {
                1 -> world // World 1 always unlocked
                2 -> world.copy(isUnlocked = maxUnlocked >= 5) // World 2 unlocks when level 5 is available
                3 -> world.copy(isUnlocked = maxUnlocked >= 8) // World 3 unlocks when level 8 is available
                else -> world
            }
        }
    }
    
    private var selectedWorld = 0
    private val worldButtons = mutableListOf<RectF>()
    private val backButton = RectF()
    private val continueButton = RectF()
    private val descriptionRect = RectF()
    
    // Paint objects
    private val titlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 80f
        isFakeBoldText = true
        setShadowLayer(5f, 0f, 0f, Color.parseColor("#00FFFF"))
    }
    
    private val worldButtonPaint = Paint().apply {
        isAntiAlias = true
    }
    
    private val worldTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 48f
        isFakeBoldText = true
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    
    private val backButtonPaint = Paint().apply {
        isAntiAlias = true
    }
    
    private val backTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 34f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    private val buttonBorderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.WHITE
        alpha = 120
    }
    
    private val descriptionBgPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#0D1B2A")
        alpha = 180
    }
    
    private val worldTitlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
        textAlign = Paint.Align.LEFT
        textSize = 52f
        isFakeBoldText = true
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    
    private val difficultyPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFB300")
        textAlign = Paint.Align.LEFT
        textSize = 32f
        isFakeBoldText = true
    }
    
    private val descriptionTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        textSize = 28f
        alpha = 220
    }
    
    private val levelInfoPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#00CAFF")
        textAlign = Paint.Align.LEFT
        textSize = 30f
        isFakeBoldText = true
    }
    
    private val continueButtonPaint = Paint().apply {
        isAntiAlias = true
    }
    
    private val continueTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 38f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    private val lockedTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#BDBDBD")
        textAlign = Paint.Align.CENTER
        textSize = 44f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    init {
        loadBackground()
        updateUIPositions()
    }
    
    private fun loadBackground() {
        try {
            backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.background_img)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun update(deltaTime: Long) {
        // No need for checkWorldUnlock() anymore since worlds is computed dynamically
    }
    
    override fun draw(canvas: Canvas) {
        // Draw background
        backgroundBitmap?.let { bg ->
            val scaleX = canvas.width.toFloat() / bg.width
            val scaleY = canvas.height.toFloat() / bg.height
            val scale = maxOf(scaleX, scaleY)
            
            canvas.save()
            canvas.scale(scale, scale)
            canvas.drawBitmap(bg, 0f, 0f, null)
            canvas.restore()
        }
        
        // Title
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        canvas.drawText("SELECT WORLD", centerX, 68f, titlePaint)
        
        // Back button
        drawBackButton(canvas)
        
        // World buttons
        drawWorldButtons(canvas)
        
        // Description panel
        drawDescriptionPanel(canvas)
        
        // Continue button
        drawContinueButton(canvas)
    }
    
    private fun drawBackButton(canvas: Canvas) {
        val gradient = LinearGradient(
            backButton.left, backButton.top, backButton.left, backButton.bottom,
            intArrayOf(
                Color.parseColor("#546E7A"),
                Color.parseColor("#37474F"),
                Color.parseColor("#263238")
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        backButtonPaint.shader = gradient
        
        canvas.drawRoundRect(backButton, 8f, 8f, backButtonPaint)
        canvas.drawRoundRect(backButton, 8f, 8f, buttonBorderPaint)
        
        canvas.drawText("← BACK", backButton.centerX(), backButton.centerY() + 8f, backTextPaint)
    }
    
    private fun drawWorldButtons(canvas: Canvas) {
        for (i in worldButtons.indices) {
            val world = worlds[i]
            val button = worldButtons[i]
            val isSelected = i == selectedWorld
            val isUnlocked = world.isUnlocked
            
            // Gradient for button background
            val buttonGradient = if (isUnlocked) {
                if (isSelected) {
                    LinearGradient(
                        button.left, button.top, button.left, button.bottom,
                        intArrayOf(
                            world.primaryColor,
                            world.secondaryColor,
                            adjustBrightness(world.secondaryColor, 0.8f)
                        ),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                } else {
                    LinearGradient(
                        button.left, button.top, button.left, button.bottom,
                        intArrayOf(
                            adjustBrightness(world.primaryColor, 0.7f),
                            adjustBrightness(world.secondaryColor, 0.7f)
                        ),
                        floatArrayOf(0f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
            } else {
                LinearGradient(
                    button.left, button.top, button.left, button.bottom,
                    intArrayOf(
                        Color.parseColor("#616161"),
                        Color.parseColor("#424242")
                    ),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            
            worldButtonPaint.shader = buttonGradient
            
            // Draw button
            canvas.drawRoundRect(button, 25f, 25f, worldButtonPaint)
            
            // Draw border
            if (isSelected && isUnlocked) {
                val selectedBorderPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    color = Color.WHITE
                    alpha = 140
                    setShadowLayer(6f, 0f, 0f, Color.WHITE)
                }
                canvas.drawRoundRect(button, 25f, 25f, selectedBorderPaint)
            } else {
                canvas.drawRoundRect(button, 25f, 25f, buttonBorderPaint)
            }
            
            // Draw text
            val text = if (isUnlocked) {
                world.name
            } else {
                "🔒 ${world.name}"
            }
            
            val textPaint = if (isUnlocked) worldTextPaint else lockedTextPaint
            
            canvas.drawText(
                text,
                button.centerX(),
                button.centerY() + 8f,
                textPaint
            )
        }
    }
    
    private fun drawDescriptionPanel(canvas: Canvas) {
        val selectedWorldData = worlds[selectedWorld]
        
        // Background
        val bgGradient = LinearGradient(
            descriptionRect.left, descriptionRect.top,
            descriptionRect.left, descriptionRect.bottom,
            intArrayOf(
                Color.parseColor("#263238"),
                Color.parseColor("#37474F")
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        descriptionBgPaint.shader = bgGradient
        
        canvas.drawRoundRect(descriptionRect, 15f, 15f, descriptionBgPaint)
        canvas.drawRoundRect(descriptionRect, 15f, 15f, buttonBorderPaint)
        
        // World title
        canvas.drawText(
            selectedWorldData.title,
            descriptionRect.centerX(),
            descriptionRect.top + 60f,
            worldTitlePaint
        )
        
        // Difficulty
        canvas.drawText(
            "Difficulty: ${selectedWorldData.difficulty}",
            descriptionRect.left + 30f,
            descriptionRect.top + 110f,
            difficultyPaint
        )
        
        // Level range
        val levelRangeText = if (selectedWorldData.isUnlocked) {
            "Levels ${selectedWorldData.levelRange.first}-${selectedWorldData.levelRange.last}"
        } else {
            "Locked - Complete previous world to unlock"
        }
        
        canvas.drawText(
            levelRangeText,
            descriptionRect.left + 30f,
            descriptionRect.top + 150f,
            levelInfoPaint
        )
        
        // Description
        val description = if (selectedWorldData.isUnlocked) {
            selectedWorldData.description
        } else {
            "This mysterious realm remains sealed. Complete the previous world to unlock its secrets and continue your journey."
        }
        
        drawWrappedText(
            canvas,
            description,
            descriptionRect.left + 30f,
            descriptionRect.top + 190f,
            descriptionRect.width() - 60f,
            descriptionTextPaint
        )
    }
    
    private fun drawContinueButton(canvas: Canvas) {
        val selectedWorldData = worlds[selectedWorld]
        val isPlayable = selectedWorldData.isUnlocked
        
        // Gradient for continue button
        val gradient = if (isPlayable) {
            LinearGradient(
                continueButton.left, continueButton.top, continueButton.left, continueButton.bottom,
                intArrayOf(
                    Color.parseColor("#66BB6A"),
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#388E3C")
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        } else {
            LinearGradient(
                continueButton.left, continueButton.top, continueButton.left, continueButton.bottom,
                intArrayOf(
                    Color.parseColor("#616161"),
                    Color.parseColor("#424242")
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        
        continueButtonPaint.shader = gradient
        
        canvas.drawRoundRect(continueButton, 8f, 8f, continueButtonPaint)
        canvas.drawRoundRect(continueButton, 8f, 8f, buttonBorderPaint)
        
        val continueText = if (isPlayable) "CONTINUE" else "LOCKED"
        continueTextPaint.alpha = if (isPlayable) 255 else 150
        canvas.drawText(continueText, continueButton.centerX(), continueButton.centerY() + 15f, continueTextPaint)
    }
    
    private fun drawWrappedText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
        val words = text.split(" ")
        var line = ""
        var currentY = y
        
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val testWidth = paint.measureText(testLine)
            
            if (testWidth > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += paint.textSize + 15f
            } else {
                line = testLine
            }
        }
        
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, currentY, paint)
        }
    }
    
    private fun adjustBrightness(color: Int, factor: Float): Int {
        val r = ((color shr 16) and 0xFF)
        val g = ((color shr 8) and 0xFF)
        val b = (color and 0xFF)
        
        val newR = (r * factor).toInt().coerceIn(0, 255)
        val newG = (g * factor).toInt().coerceIn(0, 255)
        val newB = (b * factor).toInt().coerceIn(0, 255)
        
        return Color.rgb(newR, newG, newB)
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            
            if (backButton.contains(x, y)) {
                gameStateManager.changeState(GameConstants.STATE_MENU)
                return true
            }
            
            // Check world button clicks
            for (i in worldButtons.indices) {
                val button = worldButtons[i]
                if (button.contains(x, y)) {
                    selectedWorld = i
                    return true
                }
            }
            
            // Continue button
            if (continueButton.contains(x, y)) {
                val selectedWorldData = worlds[selectedWorld]
                if (selectedWorldData.isUnlocked) {
                    // Pass selected world to level selection
                    gameStateManager.selectedWorld = selectedWorldData.id
                    gameStateManager.changeState(GameConstants.STATE_LEVEL_SELECT)
                }
                return true
            }
        }
        
        return false
    }
    
    private fun updateUIPositions() {
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        val screenW = GameConstants.SCREEN_WIDTH.toFloat()
        val screenH = GameConstants.SCREEN_HEIGHT.toFloat()
        
        worldButtons.clear()
        
        // Back button
        val backButtonHeight = 50f
        val backButtonY = 68f - backButtonHeight / 2f
        backButton.set(15f, backButtonY, 170f, backButtonY + backButtonHeight)
        
        // World buttons - 3 buttons arranged vertically
        val buttonWidth = screenW * 0.35f
        val buttonHeight = 120f
        val startY = 160f
        val margin = 30f
        val leftMargin = 30f
        
        for (i in worlds.indices) {
            val y = startY + i * (buttonHeight + margin)
            worldButtons.add(RectF(leftMargin, y, leftMargin + buttonWidth, y + buttonHeight))
        }
        
        // Description panel
        val descX = screenW * 0.4f
        val descY = 160f
        val descWidth = screenW * 0.55f
        val descHeight = screenH * 0.5f
        
        descriptionRect.set(descX, descY, descX + descWidth, descY + descHeight)
        
        // Continue button
        val continueButtonWidth = 260f
        val continueButtonHeight = 90f
        val continueX = (screenW - continueButtonWidth) / 2f
        val continueY = screenH - continueButtonHeight - 40f
        
        continueButton.set(continueX, continueY, continueX + continueButtonWidth, continueY + continueButtonHeight)
    }
    
    override fun onScreenSizeChanged(width: Int, height: Int) {
        updateUIPositions()
    }
}