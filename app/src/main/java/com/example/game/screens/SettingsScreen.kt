package com.example.game.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.example.game.GameConstants
import com.example.game.R
import com.example.game.animation.AnimationManager
import com.example.game.core.GameStateManager
import com.example.game.SaveManager

class SettingsScreen(
    private val gameStateManager: GameStateManager,
    private val context: Context,
    private val animationManager: AnimationManager // THÊM PARAMETER NÀY
) : Screen() {
    
    private var backgroundBitmap: Bitmap? = null
    
    // Settings options
    private var soundEnabled = true
    private var musicEnabled = true
    private var vibrationEnabled = true
    
    // UI Paints - TO HƠN
    private val titlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#E1F5FE")
        textAlign = Paint.Align.CENTER
        textSize = 72f
        isFakeBoldText = true
        setShadowLayer(8f, 4f, 4f, Color.parseColor("#263238"))
    }
    
    private val optionPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFFFFF")
        textAlign = Paint.Align.LEFT
        textSize = 40f
        isFakeBoldText = true
    }
    
    private val toggleOnPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4CAF50")
        style = Paint.Style.FILL
    }
    
    private val toggleOffPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#F44336")
        style = Paint.Style.FILL
    }
    
    private val toggleBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    private val toggleTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 28f
        isFakeBoldText = true
    }
    
    private val backButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#607D8B")
        style = Paint.Style.FILL
    }
    
    // Thêm các Paint objects cho reset button
    private val resetButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#F44336") // Màu đỏ cảnh báo
        style = Paint.Style.FILL
    }

    private val resetTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 32f
        isFakeBoldText = true
    }

    private val borderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    // UI Elements
    private val soundToggle = RectF()
    private val musicToggle = RectF()
    private val vibrationToggle = RectF()
    private val backButton = RectF()
    
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
        animationManager.update(deltaTime)
    }
    
    override fun draw(canvas: Canvas) {  // THAY ĐỔI TỪ render THÀNH draw
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
        
        // Apply entrance animation
        animationManager.applyMenuAnimation(canvas)
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val startY = GameConstants.SCREEN_HEIGHT * 0.35f
        val optionSpacing = 120f
        
        // Title
        canvas.drawText("SETTINGS", centerX, 180f, titlePaint)
        
        // Sound option
        canvas.drawText("Sound Effects", centerX - 250f, startY + 15f, optionPaint)
        val soundPaint = if (soundEnabled) toggleOnPaint else toggleOffPaint
        canvas.drawRoundRect(soundToggle, 15f, 15f, soundPaint)
        canvas.drawRoundRect(soundToggle, 15f, 15f, toggleBorderPaint)
        canvas.drawText(if (soundEnabled) "ON" else "OFF", soundToggle.centerX(), soundToggle.centerY() + 8f, toggleTextPaint)
        
        // Music option
        canvas.drawText("Background Music", centerX - 250f, startY + optionSpacing + 15f, optionPaint)
        val musicPaint = if (musicEnabled) toggleOnPaint else toggleOffPaint
        canvas.drawRoundRect(musicToggle, 15f, 15f, musicPaint)
        canvas.drawRoundRect(musicToggle, 15f, 15f, toggleBorderPaint)
        canvas.drawText(if (musicEnabled) "ON" else "OFF", musicToggle.centerX(), musicToggle.centerY() + 8f, toggleTextPaint)
        
        // Vibration option
        canvas.drawText("Vibration", centerX - 250f, startY + optionSpacing * 2 + 15f, optionPaint)
        val vibrationPaint = if (vibrationEnabled) toggleOnPaint else toggleOffPaint
        canvas.drawRoundRect(vibrationToggle, 15f, 15f, vibrationPaint)
        canvas.drawRoundRect(vibrationToggle, 15f, 15f, toggleBorderPaint)
        canvas.drawText(if (vibrationEnabled) "ON" else "OFF", vibrationToggle.centerX(), vibrationToggle.centerY() + 8f, toggleTextPaint)
        
        // Back button
        canvas.drawRoundRect(backButton, 15f, 15f, backButtonPaint)
        canvas.drawRoundRect(backButton, 15f, 15f, toggleBorderPaint)
        canvas.drawText("← BACK", backButton.centerX(), backButton.centerY() + 10f, toggleTextPaint)
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                
                // Check back button
                if (backButton.contains(x, y)) {
                    animationManager.startTransition()
                    gameStateManager.changeState(GameConstants.STATE_MENU)
                    return true
                }
                
                // Check sound toggle
                if (soundToggle.contains(x, y)) {
                    soundEnabled = !soundEnabled
                    return true
                }
                
                // Check music toggle
                if (musicToggle.contains(x, y)) {
                    musicEnabled = !musicEnabled
                    return true
                }
                
                // Check vibration toggle
                if (vibrationToggle.contains(x, y)) {
                    vibrationEnabled = !vibrationEnabled
                    return true
                }
            }
        }
        return false
    }
    
    private fun updateUIPositions() {
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val startY = GameConstants.SCREEN_HEIGHT * 0.35f
        val optionSpacing = 120f
        val toggleWidth = 120f
        val toggleHeight = 60f
        
        // Sound toggle
        soundToggle.set(
            centerX + 100f,
            startY - toggleHeight/2,
            centerX + 100f + toggleWidth,
            startY + toggleHeight/2
        )
        
        // Music toggle
        musicToggle.set(
            centerX + 100f,
            startY + optionSpacing - toggleHeight/2,
            centerX + 100f + toggleWidth,
            startY + optionSpacing + toggleHeight/2
        )
        
        // Vibration toggle
        vibrationToggle.set(
            centerX + 100f,
            startY + optionSpacing * 2 - toggleHeight/2,
            centerX + 100f + toggleWidth,
            startY + optionSpacing * 2 + toggleHeight/2
        )
        
        // Back button
        backButton.set(50f, 50f, 250f, 130f)
    }
    
    override fun onScreenSizeChanged(width: Int, height: Int) {
        updateUIPositions()
    }
}
