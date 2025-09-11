package com.example.game

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Player(startX: Float, startY: Float) {
    var x = startX
    var y = startY
    private var targetX = x
    private var targetY = y
    
    private var isMoving = false
    private var animationTime = 0L
    private val moveDuration = 250L
    private var startX = x
    private var startY = y
    
    // Player visuals - TO HƠN VÀ CHI TIẾT HƠN
    private val playerBodyPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#4285F4") // Blue đẹp hơn
    }
    
    private val playerBorderPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#1565C0") // Border đậm hơn
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    
    private val playerEyePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }
    
    private val playerPupilPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }
    
    private val size = GameConstants.TILE_SIZE.toFloat() * 0.85f  // Lớn hơn (85% thay vì 80%)
    
    fun update(deltaTime: Long) {
        if (isMoving) {
            animationTime += deltaTime
            
            if (animationTime >= moveDuration) {
                x = targetX
                y = targetY
                isMoving = false
                animationTime = 0
            } else {
                val progress = animationTime.toFloat() / moveDuration.toFloat()
                val easedProgress = easeOut(progress)
                x = startX + (targetX - startX) * easedProgress
                y = startY + (targetY - startY) * easedProgress
            }
        }
    }
    
    private fun easeOut(t: Float): Float {
        return 1f - (1f - t) * (1f - t) * (1f - t)
    }
    
    fun move(dx: Int, dy: Int, gameMap: GameMap): Boolean {
        if (isMoving) return false
        
        val currentTileX = (x / GameConstants.TILE_SIZE).toInt()
        val currentTileY = (y / GameConstants.TILE_SIZE).toInt()
        
        val newTileX = currentTileX + dx
        val newTileY = currentTileY + dy
        
        if (gameMap.isWalkable(newTileX, newTileY)) {
            startX = x
            startY = y
            targetX = newTileX * GameConstants.TILE_SIZE.toFloat()
            targetY = newTileY * GameConstants.TILE_SIZE.toFloat()
            
            isMoving = true
            animationTime = 0
            return true
        }
        return false
    }
    
    fun draw(canvas: Canvas) {
        val centerX = x + GameConstants.TILE_SIZE / 2f
        val centerY = y + GameConstants.TILE_SIZE / 2f
        val radius = size / 2f
        
        // Vẽ body - HÌNH TRÒN TO HƠN
        canvas.drawCircle(centerX, centerY, radius, playerBodyPaint)
        canvas.drawCircle(centerX, centerY, radius, playerBorderPaint)
        
        // Vẽ mắt - CHI TIẾT HƠN
        val eyeRadius = radius * 0.15f
        val eyeOffsetX = radius * 0.3f
        val eyeOffsetY = radius * 0.2f
        
        // Mắt trái
        canvas.drawCircle(centerX - eyeOffsetX, centerY - eyeOffsetY, eyeRadius, playerEyePaint)
        canvas.drawCircle(centerX - eyeOffsetX, centerY - eyeOffsetY, eyeRadius * 0.6f, playerPupilPaint)
        
        // Mắt phải
        canvas.drawCircle(centerX + eyeOffsetX, centerY - eyeOffsetY, eyeRadius, playerEyePaint)
        canvas.drawCircle(centerX + eyeOffsetX, centerY - eyeOffsetY, eyeRadius * 0.6f, playerPupilPaint)
    }
    
    fun getCenterX(): Float = x + GameConstants.TILE_SIZE / 2f
    fun getCenterY(): Float = y + GameConstants.TILE_SIZE / 2f
    
    fun getCurrentTileX(): Int = (x / GameConstants.TILE_SIZE).toInt()
    fun getCurrentTileY(): Int = (y / GameConstants.TILE_SIZE).toInt()
    
    fun isCurrentlyMoving(): Boolean = isMoving

    fun checkLevelComplete(gameMap: GameMap): Boolean {
        val tileX = (x / GameConstants.TILE_SIZE).toInt()
        val tileY = (y / GameConstants.TILE_SIZE).toInt()
        val currentTile = gameMap.getTile(tileX, tileY)
        
        return currentTile == com.example.game.map.TileConstants.TILE_END
    }
}
