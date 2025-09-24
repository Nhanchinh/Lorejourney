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
import com.example.game.music.MusicManager

class SettingsScreen(
    private val gameStateManager: GameStateManager,
    private val context: Context,
    private val animationManager: AnimationManager
) : Screen() {
    
    private var backgroundBitmap: Bitmap? = null
    
    // Settings options
    private var soundEnabled = true
    // Sử dụng biến chung từ MusicManager
    private var vibrationEnabled = true
    
    // Popup confirmation
    private var showConfirmationPopup = false
    private var confirmationType = "" // "UNLOCK_ALL" hoặc "RESET_PROGRESS"
    private var confirmationMessage = ""
    
    // UI Paints
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
    
    // Paint cho unlock all levels button
    private val unlockButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4CAF50") // Màu xanh lá
        style = Paint.Style.FILL
    }
    
    // Paint cho reset progress button
    private val resetButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#F44336") // Màu đỏ cảnh báo
        style = Paint.Style.FILL
    }

    private val buttonTextPaint = Paint().apply {
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
    
    // Popup paints
    private val popupBackgroundPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#E0000000") // Semi-transparent black
        style = Paint.Style.FILL
    }
    
    private val popupBoxPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#2C3E50") // Dark blue-gray
        style = Paint.Style.FILL
    }
    
    private val popupTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 36f
        isFakeBoldText = true
    }
    
    private val popupTitlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#E74C3C") // Red for warning
        textAlign = Paint.Align.CENTER
        textSize = 42f
        isFakeBoldText = true
    }
    
    private val confirmButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#E74C3C") // Red
        style = Paint.Style.FILL
    }
    
    private val cancelButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#95A5A6") // Gray
        style = Paint.Style.FILL
    }
    
    // UI Elements
    private val soundToggle = RectF()
    private val musicToggle = RectF()
    private val vibrationToggle = RectF()
    private val unlockAllButton = RectF()
    private val resetProgressButton = RectF()
    private val backButton = RectF()
    
    // Popup elements
    private val popupBox = RectF()
    private val confirmButton = RectF()
    private val cancelButton = RectF()
    
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
        
        // Apply entrance animation
        animationManager.applyMenuAnimation(canvas)
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val startY = GameConstants.SCREEN_HEIGHT * 0.3f
        val optionSpacing = 100f
        
        // Title
        canvas.drawText("CÀI ĐẶT", centerX, 150f, titlePaint)
        
        // Sound option
        canvas.drawText("Hiệu ứng âm thanh", centerX - 250f, startY + 15f, optionPaint)
        val soundPaint = if (soundEnabled) toggleOnPaint else toggleOffPaint
        canvas.drawRoundRect(soundToggle, 15f, 15f, soundPaint)
        canvas.drawRoundRect(soundToggle, 15f, 15f, toggleBorderPaint)
        canvas.drawText(if (soundEnabled) "BẬT" else "TẮT", soundToggle.centerX(), soundToggle.centerY() + 8f, toggleTextPaint)
        
        // Music option
        canvas.drawText("Nhạc nền", centerX - 250f, startY + optionSpacing + 15f, optionPaint)
        val musicPaint = if (MusicManager.isMusicEnabled) toggleOnPaint else toggleOffPaint
        canvas.drawRoundRect(musicToggle, 15f, 15f, musicPaint)
        canvas.drawRoundRect(musicToggle, 15f, 15f, toggleBorderPaint)
        canvas.drawText(if (MusicManager.isMusicEnabled) "BẬT" else "TẮT", musicToggle.centerX(), musicToggle.centerY() + 8f, toggleTextPaint)

        // Vibration option
        canvas.drawText("Rung", centerX - 250f, startY + optionSpacing * 2 + 15f, optionPaint)
        val vibrationPaint = if (vibrationEnabled) toggleOnPaint else toggleOffPaint
        canvas.drawRoundRect(vibrationToggle, 15f, 15f, vibrationPaint)
        canvas.drawRoundRect(vibrationToggle, 15f, 15f, toggleBorderPaint)
        canvas.drawText(if (vibrationEnabled) "BẬT" else "TẮT", vibrationToggle.centerX(), vibrationToggle.centerY() + 8f, toggleTextPaint)
        
        // Unlock All Levels button
        canvas.drawRoundRect(unlockAllButton, 15f, 15f, unlockButtonPaint)
        canvas.drawRoundRect(unlockAllButton, 15f, 15f, borderPaint)
        canvas.drawText("MỞ KHÓA TẤT CẢ", unlockAllButton.centerX(), unlockAllButton.centerY() + 10f, buttonTextPaint)
        
        // Reset Progress button
        canvas.drawRoundRect(resetProgressButton, 15f, 15f, resetButtonPaint)
        canvas.drawRoundRect(resetProgressButton, 15f, 15f, borderPaint)
        canvas.drawText("ĐẶT LẠI TIẾN TRÌNH", resetProgressButton.centerX(), resetProgressButton.centerY() + 10f, buttonTextPaint)
        
        // Back button
        canvas.drawRoundRect(backButton, 15f, 15f, backButtonPaint)
        canvas.drawRoundRect(backButton, 15f, 15f, toggleBorderPaint)
        canvas.drawText("← QUAY LẠI", backButton.centerX(), backButton.centerY() + 10f, toggleTextPaint)
        
        // Draw confirmation popup if active
        if (showConfirmationPopup) {
            drawConfirmationPopup(canvas)
        }
    }
    
    private fun drawConfirmationPopup(canvas: Canvas) {
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val centerY = GameConstants.SCREEN_HEIGHT / 2f
        
        // Draw overlay background
        canvas.drawRect(0f, 0f, GameConstants.SCREEN_WIDTH.toFloat(), GameConstants.SCREEN_HEIGHT.toFloat(), popupBackgroundPaint)
        
        // Draw popup box
        canvas.drawRoundRect(popupBox, 20f, 20f, popupBoxPaint)
        canvas.drawRoundRect(popupBox, 20f, 20f, borderPaint)
        
        // Draw title
        canvas.drawText("XÁC NHẬN", centerX, popupBox.top + 60f, popupTitlePaint)
        
        // Draw message with proper line breaks
        drawMultilineText(canvas, confirmationMessage, centerX, popupBox.top + 120f, popupTextPaint)
        
        // Draw buttons
        canvas.drawRoundRect(confirmButton, 15f, 15f, confirmButtonPaint)
        canvas.drawRoundRect(confirmButton, 15f, 15f, borderPaint)
        canvas.drawText("CÓ", confirmButton.centerX(), confirmButton.centerY() + 10f, buttonTextPaint)
        
        canvas.drawRoundRect(cancelButton, 15f, 15f, cancelButtonPaint)
        canvas.drawRoundRect(cancelButton, 15f, 15f, borderPaint)
        canvas.drawText("KHÔNG", cancelButton.centerX(), cancelButton.centerY() + 10f, buttonTextPaint)
    }

    /**
     * Draw multiline text with proper line breaks
     */
    private fun drawMultilineText(canvas: Canvas, text: String, centerX: Float, startY: Float, paint: Paint) {
        val lines = text.split("\n")
        val lineHeight = paint.textSize * 1.2f // Spacing between lines
        
        lines.forEachIndexed { index, line ->
            val y = startY + (index * lineHeight)
            canvas.drawText(line, centerX, y, paint)
        }
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                
                // Handle popup touches first
                if (showConfirmationPopup) {
                    if (confirmButton.contains(x, y)) {
                        // User confirmed
                        when (confirmationType) {
                            "UNLOCK_ALL" -> {
                                unlockAllLevels()
                            }
                            "RESET_PROGRESS" -> {
                                resetAllProgress()
                            }
                        }
                        showConfirmationPopup = false
                        return true
                    } else if (cancelButton.contains(x, y)) {
                        // User cancelled
                        showConfirmationPopup = false
                        return true
                    }
                    // If touch is outside popup, ignore it
                    return true
                }
                
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
                    val newState = !MusicManager.isMusicEnabled
                    MusicManager.isMusicEnabled = newState
                    if (!newState) {
                        MusicManager.stopMusic()
                    } else {
                        MusicManager.playWaitingHallMusic(context)
                    }
                    return true
                }
                
                // Check vibration toggle
                if (vibrationToggle.contains(x, y)) {
                    vibrationEnabled = !vibrationEnabled
                    return true
                }
                
                // Check unlock all levels button
                if (unlockAllButton.contains(x, y)) {
                    showConfirmation("UNLOCK_ALL", "Mở khóa tất cả màn chơi?\nĐiều này sẽ làm cho tất cả\nmàn chơi có thể chơi được.")
                    return true
                }
                
                // Check reset progress button
                if (resetProgressButton.contains(x, y)) {
                    showConfirmation("RESET_PROGRESS", "Đặt lại tất cả tiến trình?\nĐiều này sẽ khóa tất cả\nmàn chơi trừ Màn 1.")
                    return true
                }
            }
        }
        return false
    }
    
    /**
     * Show confirmation popup
     */
    private fun showConfirmation(type: String, message: String) {
        confirmationType = type
        confirmationMessage = message
        showConfirmationPopup = true
        updatePopupPositions()
    }
    
    /**
     * Unlock tất cả levels
     */
    private fun unlockAllLevels() {
        println(" Unlocking all levels...")
        SaveManager.unlockAllLevels()
        println("✅ All levels unlocked!")
    }
    
    /**
     * Reset toàn bộ progress
     */
    private fun resetAllProgress() {
        println("🔄 Resetting all progress...")
        SaveManager.resetProgress()
        SaveManager.resetViewedStories() // Reset trạng thái đã xem story
        println("✅ All progress reset!")
    }
    
    private fun updateUIPositions() {
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val startY = GameConstants.SCREEN_HEIGHT * 0.3f
        val optionSpacing = 100f
        val toggleWidth = 120f
        val toggleHeight = 60f
        val buttonWidth = 400f
        val buttonHeight = 80f
        
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
        
        // Unlock All Levels button
        unlockAllButton.set(
            centerX - buttonWidth/2,
            startY + optionSpacing * 3 + 20f,
            centerX + buttonWidth/2,
            startY + optionSpacing * 3 + 20f + buttonHeight
        )
        
        // Reset Progress button
        resetProgressButton.set(
            centerX - buttonWidth/2,
            startY + optionSpacing * 4 + 40f,
            centerX + buttonWidth/2,
            startY + optionSpacing * 4 + 40f + buttonHeight
        )
        
        // Back button
        backButton.set(50f, 50f, 250f, 130f)
        
        // Update popup positions
        updatePopupPositions()
    }
    
    private fun updatePopupPositions() {
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val centerY = GameConstants.SCREEN_HEIGHT / 2f
        val popupWidth = 500f
        val popupHeight = 300f
        val buttonWidth = 120f
        val buttonHeight = 60f
        
        // Popup box
        popupBox.set(
            centerX - popupWidth/2,
            centerY - popupHeight/2,
            centerX + popupWidth/2,
            centerY + popupHeight/2
        )
        
        // Confirm button (YES)
        confirmButton.set(
            centerX - 80f - buttonWidth/2,
            popupBox.bottom - 80f,
            centerX - 80f + buttonWidth/2,
            popupBox.bottom - 80f + buttonHeight
        )
        
        // Cancel button (NO)
        cancelButton.set(
            centerX + 80f - buttonWidth/2,
            popupBox.bottom - 80f,
            centerX + 80f + buttonWidth/2,
            popupBox.bottom - 80f + buttonHeight
        )
    }
    
    override fun onScreenSizeChanged(width: Int, height: Int) {
        updateUIPositions()
    }
}
