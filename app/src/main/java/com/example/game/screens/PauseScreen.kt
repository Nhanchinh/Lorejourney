package com.example.game.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.example.game.GameConstants
import com.example.game.SaveManager
import com.example.game.core.GameStateManager

class PauseScreen(
    private val gameStateManager: GameStateManager,
    private val levelId: Int,
    private val context: Context,
    private val gameProgress: GameProgress
) : Screen() {
    
    data class GameProgress(
        val playerX: Float,
        val playerY: Float,
        val shadowSpawned: Boolean = false,
        val shadowX: Float = 0f,
        val shadowY: Float = 0f,
        val shadowDirectionChanges: Int = 0,
        val doorsOpened: List<Pair<Int, Int>> = emptyList()
    )
    
    private val continueButton = RectF()
    private val resetMapButton = RectF() // Đổi tên từ saveExitButton
    private val selectMapButton = RectF()
    
    private val buttonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4285F4")
    }
    
    private val buttonPressedPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#1565C0")
    }
    
    private val buttonBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    private val textPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 36f
        isFakeBoldText = true
    }
    
    private val titlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 48f
        isFakeBoldText = true
    }
    
    private var pressedButton = ""
    
    init {
        updateButtonPositions()
    }
    
    override fun update(deltaTime: Long) {
        
    }
    
    override fun draw(canvas: Canvas) {
        // Draw overlay background
        val overlayPaint = Paint().apply {
            color = Color.parseColor("#CC000000") // Semi-transparent black
        }
        canvas.drawRect(0f, 0f, 
                       GameConstants.SCREEN_WIDTH.toFloat(), 
                       GameConstants.SCREEN_HEIGHT.toFloat(), 
                       overlayPaint)
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val startY = GameConstants.SCREEN_HEIGHT / 2f - 150f
        
        // Draw title
        canvas.drawText("GAME PAUSED", centerX, startY, titlePaint)
        
        // Draw buttons
        drawButton(canvas, continueButton, "CONTINUE", pressedButton == "CONTINUE")
        drawButton(canvas, resetMapButton, "RESET MAP", pressedButton == "RESET_MAP") // Đổi text
        drawButton(canvas, selectMapButton, "SELECT MAP", pressedButton == "SELECT_MAP")
        
        // Draw level info
        val infoPaint = Paint().apply {
            color = Color.parseColor("#CCFFFFFF")
            textAlign = Paint.Align.CENTER
            textSize = 24f
            isAntiAlias = true
        }
        canvas.drawText("Level $levelId", centerX, startY + 50f, infoPaint)
    }
    
    private fun drawButton(canvas: Canvas, button: RectF, text: String, isPressed: Boolean) {
        val paint = if (isPressed) buttonPressedPaint else buttonPaint
        
        canvas.drawRoundRect(button, 20f, 20f, paint)
        canvas.drawRoundRect(button, 20f, 20f, buttonBorderPaint)
        canvas.drawText(text, button.centerX(), button.centerY() + 12f, textPaint)
    }
    
    private fun updateButtonPositions() {
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val centerY = GameConstants.SCREEN_HEIGHT / 2f
        val buttonWidth = 300f
        val buttonHeight = 80f
        val buttonSpacing = 100f
        
        continueButton.set(
            centerX - buttonWidth/2, centerY - buttonHeight/2,
            centerX + buttonWidth/2, centerY + buttonHeight/2
        )
        
        resetMapButton.set( // Đổi tên từ saveExitButton
            centerX - buttonWidth/2, centerY + buttonSpacing - buttonHeight/2,
            centerX + buttonWidth/2, centerY + buttonSpacing + buttonHeight/2
        )
        
        selectMapButton.set(
            centerX - buttonWidth/2, centerY + buttonSpacing*2 - buttonHeight/2,
            centerX + buttonWidth/2, centerY + buttonSpacing*2 + buttonHeight/2
        )
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressedButton = when {
                    continueButton.contains(event.x, event.y) -> "CONTINUE"
                    resetMapButton.contains(event.x, event.y) -> "RESET_MAP" // Đổi tên
                    selectMapButton.contains(event.x, event.y) -> "SELECT_MAP"
                    else -> ""
                }
                return true
            }
            
            MotionEvent.ACTION_UP -> {
                when (pressedButton) {
                    "CONTINUE" -> {
                        gameStateManager.resumeGame() // Dùng resumeGame()
                    }
                    "RESET_MAP" -> { // Đổi logic
                        resetCurrentMap()
                    }
                    "SELECT_MAP" -> {
                        gameStateManager.changeState(GameConstants.STATE_LEVEL_SELECT)
                    }
                }
                pressedButton = ""
                return true
            }
        }
        return false
    }
    
    /**
     * Reset map hiện tại về trạng thái ban đầu (KHÔNG reset progress)
     */
    private fun resetCurrentMap() {
        println("🔄 Resetting map $levelId to initial state...")
        
        // KHÔNG gọi SaveManager.resetProgress() - chỉ restart level
        // SaveManager.resetProgress() // ← XÓA dòng này
        
        // Chỉ restart level hiện tại
        gameStateManager.restartCurrentLevel(levelId)
        
        println("✅ Map $levelId reset completed! (Progress preserved)")
    }
    
    private fun saveGameProgress() {
        // Function này không còn cần thiết vì đã thay bằng reset
        // Giữ lại để tránh lỗi compile nếu có code khác gọi
    }
}
