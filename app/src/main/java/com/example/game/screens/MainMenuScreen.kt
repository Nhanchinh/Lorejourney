package com.example.game.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.view.MotionEvent
import com.example.game.GameConstants
import com.example.game.R
import com.example.game.animation.AnimationManager
import com.example.game.core.GameStateManager
import kotlin.random.Random

class MainMenuScreen(
    private val gameStateManager: GameStateManager,
    private val context: Context,
    private val animationManager: AnimationManager
) : Screen() {
    
    private var backgroundBitmap: Bitmap? = null
    private var settingsIconBitmap: Bitmap? = null
    
    // UI Elements - GIỮ NGUYÊN HẾT
    private val startButton = RectF()
    private val settingsButton = RectF()
    
    // ĐOM ĐÓMP VÀ HẠT BỤI
    private data class Firefly(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var brightness: Float,
        var pulseSpeed: Float,
        var size: Float
    )
    
    private data class Dust(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var alpha: Float,
        var size: Float,
        var life: Float
    )
    
    private val fireflies = mutableListOf<Firefly>()
    private val dustParticles = mutableListOf<Dust>()
    private var animationTime = 0L
    
    // Paint objects - GIỮ NGUYÊN HẾT
    private val titlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 80f
        isFakeBoldText = true
        setShadowLayer(5f, 0f, 0f, Color.parseColor("#00FFFF"))
    }
    
    private val subtitlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 28f
        alpha = 220
    }
    
    private val startButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#00BFFF")
    }
    
    private val startButtonTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 42f
        isFakeBoldText = true
    }
    
    private val startButtonBorderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.WHITE
    }
    
    private val settingsButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#5A5A5A")
        alpha = 180
    }
    
    private val settingsButtonBorderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.WHITE
        alpha = 150
    }
    
    // PAINT CHO ĐOM ĐÓMP VÀ HẠT BỤI
    private val fireflyPaint = Paint().apply {
        isAntiAlias = true
    }
    
    private val dustPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }
    
    init {
        loadAssets()
        initParticles()
        updateButtonPositions()
    }
    
    private fun loadAssets() {
        try {
            backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.background_img)
            settingsIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.settings_icon)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    // KHỞI TẠO NHIỀU PARTICLES HƠN - RÕ RÀNG
    private fun initParticles() {
        fireflies.clear()
        dustParticles.clear()
        
        // NHIỀU ĐOM ĐÓMP HƠN - sáng hơn
        repeat(18) { // Tăng từ 12 lên 18
            fireflies.add(
                Firefly(
                    x = Random.nextFloat() * GameConstants.SCREEN_WIDTH,
                    y = Random.nextFloat() * GameConstants.SCREEN_HEIGHT,
                    vx = (Random.nextFloat() - 0.5f) * 22f,
                    vy = (Random.nextFloat() - 0.5f) * 22f,
                    brightness = Random.nextFloat() * 0.7f + 0.3f, // Sáng hơn
                    pulseSpeed = Random.nextFloat() * 0.025f + 0.01f,
                    size = Random.nextFloat() * 2.5f + 1.2f // To hơn
                )
            )
        }
        
        // NHIỀU HẠT BỤI HƠN - rõ ràng
        repeat(55) { // Tăng từ 40 lên 55
            dustParticles.add(
                Dust(
                    x = Random.nextFloat() * GameConstants.SCREEN_WIDTH,
                    y = Random.nextFloat() * GameConstants.SCREEN_HEIGHT,
                    vx = (Random.nextFloat() - 0.5f) * 8f,
                    vy = (Random.nextFloat() - 0.5f) * 8f,
                    alpha = Random.nextFloat() * 0.9f + 0.4f, // RẤT RÕ
                    size = Random.nextFloat() * 1.8f + 1f, // TO HƠN
                    life = Random.nextFloat() * 12000f + 8000f
                )
            )
        }
    }
    
    override fun update(deltaTime: Long) {
        animationTime += deltaTime
        updateParticles(deltaTime)
        updateButtonPositions()
    }
    
    // CẬP NHẬT PARTICLES - GIỮ NGUYÊN LOGIC
    private fun updateParticles(deltaTime: Long) {
        // Cập nhật đom đóm
        for (firefly in fireflies) {
            firefly.x += firefly.vx * deltaTime * 0.001f
            firefly.y += firefly.vy * deltaTime * 0.001f
            
            // WRAP AROUND
            if (firefly.x < -30f) firefly.x = GameConstants.SCREEN_WIDTH + 30f
            if (firefly.x > GameConstants.SCREEN_WIDTH + 30f) firefly.x = -30f
            if (firefly.y < -30f) firefly.y = GameConstants.SCREEN_HEIGHT + 30f
            if (firefly.y > GameConstants.SCREEN_HEIGHT + 30f) firefly.y = -30f
            
            // Pulse brightness - RÕ HƠN
            firefly.brightness = ((Math.sin((animationTime * firefly.pulseSpeed).toDouble()) + 1f).toFloat() * 0.6f + 0.4f)
            
            // Random direction change
            if (Random.nextFloat() < 0.002f) {
                firefly.vx += (Random.nextFloat() - 0.5f) * 10f
                firefly.vy += (Random.nextFloat() - 0.5f) * 10f
                firefly.vx = firefly.vx.coerceIn(-25f, 25f)
                firefly.vy = firefly.vy.coerceIn(-25f, 25f)
            }
            
            // Gentle floating drift
            firefly.vy += Math.sin((animationTime * 0.001 + firefly.x * 0.008).toDouble()).toFloat() * 0.4f
            firefly.vx += Math.cos((animationTime * 0.0008 + firefly.y * 0.008).toDouble()).toFloat() * 0.3f
        }
        
        // Cập nhật hạt bụi
        for (dust in dustParticles) {
            dust.x += dust.vx * deltaTime * 0.001f
            dust.y += dust.vy * deltaTime * 0.001f
            dust.life -= deltaTime
            
            // WRAP AROUND
            if (dust.x < -15f) dust.x = GameConstants.SCREEN_WIDTH + 15f
            if (dust.x > GameConstants.SCREEN_WIDTH + 15f) dust.x = -15f
            if (dust.y < -15f) dust.y = GameConstants.SCREEN_HEIGHT + 15f
            if (dust.y > GameConstants.SCREEN_HEIGHT + 15f) dust.y = -15f
            
            // Gentle floating
            dust.vy += Math.sin((animationTime * 0.0006 + dust.x * 0.012).toDouble()).toFloat() * 0.2f
            dust.vx += Math.cos((animationTime * 0.0005 + dust.y * 0.012).toDouble()).toFloat() * 0.15f
            
            // ALPHA RÕ HƠN NHIỀU
            dust.alpha = (dust.life / 8000f).coerceIn(0f, 1f) * (Math.sin((animationTime * 0.004).toDouble()) + 1f).toFloat() * 0.5f + 0.5f
            
            // Respawn khi hết life
            if (dust.life <= 0) {
                dust.x = Random.nextFloat() * GameConstants.SCREEN_WIDTH
                dust.y = Random.nextFloat() * GameConstants.SCREEN_HEIGHT
                dust.life = Random.nextFloat() * 12000f + 8000f
                dust.alpha = Random.nextFloat() * 0.9f + 0.4f
            }
        }
        
        // SPAWN ĐOM ĐÓMP TỪ TÁN LÁ - hơi nhiều hơn
        if (Random.nextFloat() < 0.008f && fireflies.size < 30) { // Tăng chance và max
            val spawnArea = Random.nextInt(6)
            val (x, y) = when (spawnArea) {
                0 -> Pair(Random.nextFloat() * 120f, Random.nextFloat() * 180f) // Top-left trees
                1 -> Pair(GameConstants.SCREEN_WIDTH - 120f + Random.nextFloat() * 120f, Random.nextFloat() * 180f) // Top-right
                2 -> Pair(Random.nextFloat() * 180f, GameConstants.SCREEN_HEIGHT - 120f + Random.nextFloat() * 120f) // Bottom-left
                3 -> Pair(GameConstants.SCREEN_WIDTH - 180f + Random.nextFloat() * 180f, GameConstants.SCREEN_HEIGHT - 120f + Random.nextFloat() * 120f) // Bottom-right
                4 -> Pair(Random.nextFloat() * GameConstants.SCREEN_WIDTH, -25f) // From top
                else -> Pair(Random.nextFloat() * GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT + 25f) // From bottom
            }
            
            fireflies.add(
                Firefly(
                    x = x,
                    y = y,
                    vx = (Random.nextFloat() - 0.5f) * 20f,
                    vy = (Random.nextFloat() - 0.5f) * 20f,
                    brightness = Random.nextFloat() * 0.7f + 0.3f, // Sáng hơn
                    pulseSpeed = Random.nextFloat() * 0.025f + 0.01f,
                    size = Random.nextFloat() * 2.5f + 1.2f // To hơn
                )
            )
        }
    }
    
    override fun draw(canvas: Canvas) {
        // Draw background - GIỮ NGUYÊN
        backgroundBitmap?.let { bg ->
            val scaleX = canvas.width.toFloat() / bg.width
            val scaleY = canvas.height.toFloat() / bg.height
            val scale = maxOf(scaleX, scaleY)
            
            canvas.save()
            canvas.scale(scale, scale)
            canvas.drawBitmap(bg, 0f, 0f, null)
            canvas.restore()
        }
        
        // VẼ HẠT BỤI TRƯỚC (dưới cùng)
        drawDustParticles(canvas)
        
        // VẼ ĐOM ĐÓMP
        drawFireflies(canvas)
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        
        // UI Elements - GIỮ NGUYÊN HẾT
        canvas.drawText("LORE JOURNEY", centerX, GameConstants.SCREEN_HEIGHT * 0.25f, titlePaint)
        
        canvas.drawRoundRect(startButton, 30f, 30f, startButtonPaint)
        canvas.drawRoundRect(startButton, 30f, 30f, startButtonBorderPaint)
        canvas.drawText("START", startButton.centerX(), startButton.centerY() + 15f, startButtonTextPaint)
        
        canvas.drawText("press start to start your journey", centerX, GameConstants.SCREEN_HEIGHT * 0.78f, subtitlePaint)
        
        canvas.drawRoundRect(settingsButton, 15f, 15f, settingsButtonPaint)
        canvas.drawRoundRect(settingsButton, 15f, 15f, settingsButtonBorderPaint)
        
        settingsIconBitmap?.let { icon ->
            val iconPadding = 15f
            val iconRect = RectF(
                settingsButton.left + iconPadding,
                settingsButton.top + iconPadding,
                settingsButton.right - iconPadding,
                settingsButton.bottom - iconPadding
            )
            canvas.drawBitmap(icon, null, iconRect, null)
        }
        
        // KHÔNG VẼ TRANSITION Ở ĐÂY - GameStateManager sẽ làm
    }
    
    // VẼ ĐOM ĐÓMP RÕ HƠN NHIỀU
    private fun drawFireflies(canvas: Canvas) {
        for (firefly in fireflies) {
            val alpha = (firefly.brightness * 200 + 80).toInt().coerceIn(0, 255) // RÕ HƠN NHIỀU
            
            // Glow effect lớn - rõ hơn
            fireflyPaint.color = Color.argb(alpha/4, 255, 255, 120)
            fireflyPaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            canvas.drawCircle(firefly.x, firefly.y, firefly.size * 5f, fireflyPaint)
            
            // Glow effect vừa - rõ hơn
            fireflyPaint.color = Color.argb(alpha/2, 255, 210, 80)
            canvas.drawCircle(firefly.x, firefly.y, firefly.size * 3f, fireflyPaint)
            
            // Core đom đóm - RÕ VÀ SÁNG
            fireflyPaint.color = Color.argb(alpha, 255, 180, 50) // Vàng sáng hơn
            fireflyPaint.setShadowLayer(firefly.size * 3f, 0f, 0f, Color.argb(alpha/2, 255, 200, 60))
            canvas.drawCircle(firefly.x, firefly.y, firefly.size, fireflyPaint)
            
            // Sparkle effect - rõ hơn
            if (firefly.brightness > 0.6f) {
                fireflyPaint.color = Color.argb((alpha * 0.9f).toInt(), 255, 255, 180)
                canvas.drawCircle(firefly.x, firefly.y, firefly.size * 0.6f, fireflyPaint)
            }
        }
    }
    
    // VẼ HẠT BỤI RẤT RÕ RÀNG
    private fun drawDustParticles(canvas: Canvas) {
        for (dust in dustParticles) {
            val alpha = (dust.alpha * 220).toInt().coerceIn(0, 220) // RẤT RÕ RÀNG
            
            // Glow effect cho hạt bụi - rõ hơn
            dustPaint.color = Color.argb(alpha/3, 255, 255, 255)
            dustPaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            canvas.drawCircle(dust.x, dust.y, dust.size * 2.5f, dustPaint)
            
            // Core hạt bụi - RẤT RÕ RÀNG
            dustPaint.color = Color.argb(alpha, 255, 255, 255) // Trắng tinh
            dustPaint.setShadowLayer(dust.size * 1.8f, 0f, 0f, Color.argb(alpha/2, 240, 240, 255))
            canvas.drawCircle(dust.x, dust.y, dust.size, dustPaint)
            
            // Thêm sparkle cho hạt bụi
            if (dust.alpha > 0.7f) {
                dustPaint.color = Color.argb((alpha * 0.8f).toInt(), 255, 255, 255)
                canvas.drawCircle(dust.x, dust.y, dust.size * 0.4f, dustPaint)
            }
        }
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                
                if (startButton.contains(x, y)) {
                    // SLIDE ANIMATION
                    animationManager.startTransition(com.example.game.animation.AnimationManager.TransitionType.SLIDE_LEFT)
                    gameStateManager.changeState(GameConstants.STATE_WORLD_SELECT)
                    return true
                }
                
                if (settingsButton.contains(x, y)) {
                    // FADE ANIMATION  
                    animationManager.startTransition(com.example.game.animation.AnimationManager.TransitionType.FADE)
                    gameStateManager.changeState(GameConstants.STATE_SETTINGS)
                    return true
                }
            }
        }
        return false
    }
    
    private fun updateButtonPositions() {
        // GIỮ NGUYÊN HẾT
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        
        val buttonWidth = 450f
        val buttonHeight = 100f
        startButton.set(
            centerX - buttonWidth / 2,
            GameConstants.SCREEN_HEIGHT * 0.68f - buttonHeight / 2,
            centerX + buttonWidth / 2,
            GameConstants.SCREEN_HEIGHT * 0.68f + buttonHeight / 2
        )
        
        val settingsSize = 100f
        settingsButton.set(
            GameConstants.SCREEN_WIDTH - settingsSize - 30f,
            30f,
            GameConstants.SCREEN_WIDTH - 30f,
            30f + settingsSize
        )
        
        if (fireflies.isEmpty() || dustParticles.isEmpty()) {
            initParticles()
        }
    }
    
    override fun onScreenSizeChanged(width: Int, height: Int) {
        updateButtonPositions()
    }
}