package com.example.game.animation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.pow

/**
 * Quản lý animation cho transitions và UI effects
 */
class AnimationManager {
    
    // Screen transition
    private var isTransitioning = false
    private var transitionProgress = 0f
    private var transitionDuration = 250f // GIẢM XUỐNG 250ms cho mượt hơn
    private var transitionType = TransitionType.FADE
    private var lastTime = 0L // Thêm để tính deltaTime chính xác hơn
    private var easeProgress = 0f // Thêm để làm mượt transition
    
    // Menu animations
    private var menuAnimationTime = 0L
    private var pulseAnimation = 0f
    private var floatAnimation = 0f
    private val buttonHoverScale = mutableMapOf<String, Float>()
    
    enum class TransitionType {
        FADE, SLIDE_LEFT, SLIDE_RIGHT, ZOOM
    }
    
    fun startTransition(type: TransitionType = TransitionType.FADE) {
        isTransitioning = true
        transitionProgress = 0f
        transitionType = type
    }
    
    fun update(deltaTime: Long) {
        updateTransition(deltaTime)
        updateMenuAnimations(deltaTime)
    }
    
    private fun updateTransition(deltaTime: Long) {
        if (!isTransitioning) return
        
        // Tính progress với easing function
        val dt = deltaTime.coerceAtMost(32L) // Giới hạn deltaTime để tránh lag
        transitionProgress += dt / transitionDuration
        
        // Easing function cho mượt hơn
        easeProgress = if (transitionProgress < 1f) {
            easeInOutQuad(transitionProgress)
        } else {
            1f
        }
        
        if (transitionProgress >= 1f) {
            transitionProgress = 1f
            easeProgress = 1f
            isTransitioning = false
        }
    }
    
    // Easing function cho animation mượt
    private fun easeInOutQuad(x: Float): Float {
        return when {
            x < 0.5f -> 2f * x * x
            else -> 1f - (-2f * x + 2f).pow(2) / 2f
        }
    }
    
    fun isTransitionActive(): Boolean {
        return isTransitioning
    }
    
    fun updateMenuAnimations(deltaTime: Long) {
        menuAnimationTime += deltaTime
        
        // Pulse animation (0.0 to 1.0)
        pulseAnimation = (Math.sin(menuAnimationTime * 0.003) + 1f).toFloat() * 0.5f
        
        // Float animation (-1.0 to 1.0)
        floatAnimation = Math.sin(menuAnimationTime * 0.002).toFloat()
    }
    
    fun updateButtonHover(buttonId: String, isHovered: Boolean, deltaTime: Long) {
        val currentScale = buttonHoverScale[buttonId] ?: 1f
        val targetScale = if (isHovered) 1.1f else 1f
        val lerpSpeed = deltaTime * 0.005f
        
        val newScale = currentScale + (targetScale - currentScale) * lerpSpeed
        buttonHoverScale[buttonId] = newScale.coerceIn(1f, 1.1f)
    }
    
    fun getButtonScale(buttonId: String): Float {
        return buttonHoverScale[buttonId] ?: 1f
    }
    
    fun applyMenuAnimation(canvas: Canvas) {
        // Apply floating animation
        canvas.translate(0f, floatAnimation * 5f)
        
        // Apply pulse effect (subtle scale)
        val scale = 1f + pulseAnimation * 0.01f
        canvas.scale(scale, scale, canvas.width / 2f, canvas.height / 2f)
    }
    
    // Methods cho MainMenuScreen
    fun getFloatOffset(): Float {
        return floatAnimation * 10f
    }
    
    fun getPulseScale(): Float {
        return 1f + pulseAnimation * 0.05f
    }
    
    fun getPulseAlpha(): Float {
        return 0.7f + pulseAnimation * 0.3f
    }
    
    fun drawTransition(canvas: Canvas, screenWidth: Int, screenHeight: Int) {
        if (!isTransitioning) return
        
        val paint = Paint().apply { 
            isAntiAlias = true
            color = Color.BLACK
        }
        
        when (transitionType) {
            TransitionType.FADE -> {
                // Fade mượt hơn với easing
                val alpha = (255 * (1f - easeProgress)).toInt().coerceIn(0, 255)
                paint.alpha = alpha
                canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)
            }
            
            TransitionType.SLIDE_LEFT -> {
                // Slide mượt hơn
                canvas.save()
                val offset = screenWidth * easeProgress
                canvas.translate(-offset, 0f)
                
                // Vẽ màn hình hiện tại
                // currentScreen?.draw(canvas) // Assuming currentScreen is available
                
                // Vẽ overlay cho effect
                paint.color = Color.parseColor("#1A1A2E")
                paint.alpha = (150 * easeProgress).toInt()
                canvas.drawRect(screenWidth - offset, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)
                
                canvas.restore()
            }
            
            TransitionType.SLIDE_RIGHT -> {
                // Slide mượt hơn
                canvas.save()
                val offset = screenWidth * easeProgress
                canvas.translate(offset, 0f)
                
                // Vẽ màn hình hiện tại
                // currentScreen?.draw(canvas) // Assuming currentScreen is available
                
                // Vẽ overlay cho effect
                paint.color = Color.parseColor("#1A1A2E")
                paint.alpha = (150 * easeProgress).toInt()
                canvas.drawRect(0f, 0f, offset, screenHeight.toFloat(), paint)
                
                canvas.restore()
            }
            
            TransitionType.ZOOM -> {
                // Zoom mượt hơn với scale và fade
                canvas.save()
                
                val scale = 1f + easeProgress * 0.15f
                canvas.scale(scale, scale, screenWidth / 2f, screenHeight / 2f)
                
                // Vẽ màn hình hiện tại
                // currentScreen?.draw(canvas) // Assuming currentScreen is available
                
                // Fade overlay
                paint.color = Color.WHITE
                paint.alpha = (180 * easeProgress).toInt()
                canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)
                
                canvas.restore()
            }
        }
    }
    
    fun getPulseAnimation(): Float = pulseAnimation
    fun getFloatAnimation(): Float = floatAnimation
}
