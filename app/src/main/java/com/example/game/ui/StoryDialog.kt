package com.example.game.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.view.MotionEvent
import com.example.game.GameConstants

class StoryDialog {
    private var isVisible = false
    private var currentStoryIndex = 0
    private var storySegments = listOf<String>()
    private var onDismiss: (() -> Unit)? = null
    
    // Animation
    private var animationProgress = 0f
    private var targetProgress = 0f
    private val animationSpeed = 0.2f
    
    // UI Elements
    private val dialogBox = RectF()
    private val continueButton = RectF()
    private val skipButton = RectF()
    
    // Paint objects
    private val overlayPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#88000000")
        style = Paint.Style.FILL
    }
    
    private val dialogBackgroundPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#1A237E")
        style = Paint.Style.FILL
    }
    
    private val dialogBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
        style = Paint.Style.STROKE
        strokeWidth = 4f
        setShadowLayer(8f, 0f, 0f, Color.parseColor("#FFD700"))
    }
    
    private val titlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
        textAlign = Paint.Align.CENTER
        textSize = 44f
        isFakeBoldText = true
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    
    private val storyTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        textSize = 32f
        alpha = 240
    }
    
    private val buttonPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    
    private val buttonTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 28f
        isFakeBoldText = true
    }
    
    private val characterNamePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#00E5FF")
        textAlign = Paint.Align.LEFT
        textSize = 36f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    /**
     * Hiển thị story dialog với nhiều đoạn text
     */
    fun show(segments: List<String>, onComplete: () -> Unit) {
        this.storySegments = segments.map { segment ->
            if (segment.startsWith("ELARA:")) {
                segment
            } else {
                "NGƯỜI KỂ CHUYỆN: $segment"
            }
        }
        this.currentStoryIndex = 0
        this.onDismiss = onComplete
        this.isVisible = true
        this.targetProgress = 1f
        updateDialogLayout()
    }
    
    /**
     * Ẩn dialog
     */
    fun hide() {
        targetProgress = 0f
        if (animationProgress <= 0.1f) {
            isVisible = false
            onDismiss?.invoke()
        }
    }
    
    /**
     * Update animation và logic
     */
    fun update() {
        if (!isVisible) return
        
        // Smooth animation
        if (animationProgress < targetProgress) {
            animationProgress = minOf(targetProgress, animationProgress + animationSpeed)
        } else if (animationProgress > targetProgress) {
            animationProgress = maxOf(targetProgress, animationProgress - animationSpeed)
            if (targetProgress == 0f && animationProgress <= 0.1f) {
                isVisible = false
                onDismiss?.invoke()
            }
        }
    }
    
    /**
     * Vẽ dialog lên canvas
     */
    fun draw(canvas: Canvas) {
        if (!isVisible || animationProgress <= 0f) return
        
        val alpha = (255 * animationProgress).toInt()
        
        // Vẽ overlay
        overlayPaint.alpha = alpha / 2
        canvas.drawRect(0f, 0f, GameConstants.SCREEN_WIDTH.toFloat(), GameConstants.SCREEN_HEIGHT.toFloat(), overlayPaint)
        
        // Vẽ dialog box với hiệu ứng scale
        val scale = 0.8f + (0.2f * animationProgress)
        canvas.save()
        canvas.scale(scale, scale, dialogBox.centerX(), dialogBox.centerY())
        
        // Background gradient
        val gradient = LinearGradient(
            dialogBox.left, dialogBox.top, dialogBox.left, dialogBox.bottom,
            intArrayOf(
                Color.parseColor("#283593"),
                Color.parseColor("#1A237E"),
                Color.parseColor("#0D1B2A")
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        dialogBackgroundPaint.shader = gradient
        dialogBackgroundPaint.alpha = alpha
        
        canvas.drawRoundRect(dialogBox, 20f, 20f, dialogBackgroundPaint)
        
        dialogBorderPaint.alpha = alpha
        canvas.drawRoundRect(dialogBox, 20f, 20f, dialogBorderPaint)
        
        // Vẽ nội dung nếu có story segments
        if (storySegments.isNotEmpty() && currentStoryIndex < storySegments.size) {
            drawStoryContent(canvas, alpha)
        }
        
        // Vẽ buttons
        drawButtons(canvas, alpha)
        
        canvas.restore()
    }
    
    private fun drawStoryContent(canvas: Canvas, alpha: Int) {
        val currentSegment = storySegments[currentStoryIndex]
        
        // Phân tích đoạn text
        val parts = currentSegment.split(": ", limit = 2)
        val characterName = parts[0]
        val dialogue = if (parts.size > 1) parts[1] else currentSegment
        
        val contentY = dialogBox.top + 80f
        
        // Vẽ tên nhân vật
        characterNamePaint.alpha = alpha
        canvas.drawText(characterName, dialogBox.left + 40f, contentY, characterNamePaint)
        
        // Vẽ đoạn dialogue với word wrapping
        storyTextPaint.alpha = (alpha * 0.9f).toInt()
        drawWrappedText(
            canvas, 
            dialogue, 
            dialogBox.left + 40f, 
            contentY + 50f, 
            dialogBox.width() - 80f, 
            storyTextPaint
        )
        
        // Hiển thị tiến độ
        val progressText = "${currentStoryIndex + 1}/${storySegments.size}"
        val progressPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FFD700")
            textAlign = Paint.Align.RIGHT
            textSize = 24f
            this.alpha = (alpha * 0.7f).toInt()
        }
        canvas.drawText(progressText, dialogBox.right - 40f, dialogBox.top + 50f, progressPaint)
    }
    
    private fun drawButtons(canvas: Canvas, alpha: Int) {
        // Continue button
        val continueGradient = LinearGradient(
            continueButton.left, continueButton.top, continueButton.left, continueButton.bottom,
            intArrayOf(
                Color.parseColor("#43A047"),
                Color.parseColor("#388E3C")
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        buttonPaint.shader = continueGradient
        buttonPaint.alpha = alpha
        canvas.drawRoundRect(continueButton, 12f, 12f, buttonPaint)
        
        val continueText = if (currentStoryIndex < storySegments.size - 1) "TIẾP TỤC" else "HOÀN THÀNH"
        buttonTextPaint.alpha = alpha
        canvas.drawText(continueText, continueButton.centerX(), continueButton.centerY() + 10f, buttonTextPaint)
        
        // Skip button
        val skipGradient = LinearGradient(
            skipButton.left, skipButton.top, skipButton.left, skipButton.bottom,
            intArrayOf(
                Color.parseColor("#F57C00"),
                Color.parseColor("#E65100")
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        buttonPaint.shader = skipGradient
        buttonPaint.alpha = alpha
        canvas.drawRoundRect(skipButton, 12f, 12f, buttonPaint)
        
        canvas.drawText("BỎ QUA", skipButton.centerX(), skipButton.centerY() + 10f, buttonTextPaint)
    }
    
    private fun drawWrappedText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
        val words = text.split(" ")
        var line = ""
        var currentY = y
        val lineHeight = paint.textSize + 8f
        
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val textWidth = paint.measureText(testLine)
            
            if (textWidth <= maxWidth) {
                line = testLine
            } else {
                if (line.isNotEmpty()) {
                    canvas.drawText(line, x, currentY, paint)
                    currentY += lineHeight
                }
                line = word
            }
        }
        
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, currentY, paint)
        }
    }
    
    private fun updateDialogLayout() {
        val margin = 60f
        val buttonHeight = 80f
        val dialogHeight = GameConstants.SCREEN_HEIGHT * 0.6f
        val dialogWidth = GameConstants.SCREEN_WIDTH - (margin * 2)
        
        dialogBox.set(
            margin,
            GameConstants.SCREEN_HEIGHT * 0.2f,
            margin + dialogWidth,
            GameConstants.SCREEN_HEIGHT * 0.2f + dialogHeight
        )
        
        // Buttons ở dưới cùng dialog
        val buttonY = dialogBox.bottom - buttonHeight - 20f
        val buttonWidth = 180f
        val buttonSpacing = 40f
        
        continueButton.set(
            dialogBox.centerX() - buttonWidth - buttonSpacing/2,
            buttonY,
            dialogBox.centerX() - buttonSpacing/2,
            buttonY + buttonHeight
        )
        
        skipButton.set(
            dialogBox.centerX() + buttonSpacing/2,
            buttonY,
            dialogBox.centerX() + buttonWidth + buttonSpacing/2,
            buttonY + buttonHeight
        )
    }
    
    /**
     * Xử lý touch events
     */
    fun handleTouch(event: MotionEvent): Boolean {
        if (!isVisible || animationProgress < 0.9f) return false
        
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            
            if (continueButton.contains(x, y)) {
                if (currentStoryIndex < storySegments.size - 1) {
                    currentStoryIndex++
                    return true
                } else {
                    hide()
                    return true
                }
            }
            
            if (skipButton.contains(x, y)) {
                hide()
                return true
            }
        }
        
        return true // Block touches to underlying elements
    }
    
    fun isActive(): Boolean = isVisible
}