package com.example.game.gameMechanic

import com.example.game.GameConstants

/**
 * Quản lý animation cho các đối tượng có thể đẩy
 */
class PushableObjectAnimator {
    
    // Class để lưu trữ thông tin animation
    data class AnimationInfo(
        val tileId: Int,
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val duration: Float,
        var elapsed: Float = 0f
    ) {
        val isComplete: Boolean get() = elapsed >= duration
        val progress: Float get() = (elapsed / duration).coerceIn(0f, 1f)
        
        fun getCurrentX(): Float = lerp(startX, endX, progress)
        fun getCurrentY(): Float = lerp(startY, endY, progress)
        
        private fun lerp(start: Float, end: Float, t: Float): Float = start + (end - start) * t
    }
    
    // Map lưu trữ các animation đang diễn ra
    private val activeAnimations = mutableMapOf<String, AnimationInfo>()
    
    companion object {
        private const val PUSH_ANIMATION_DURATION = 0.3f // 300ms
    }
    
    /**
     * Bắt đầu animation đẩy đá
     */
    fun startPushAnimation(
        tileId: Int,
        fromX: Int, 
        fromY: Int, 
        toX: Int, 
        toY: Int
    ) {
        val key = "${fromX}_${fromY}_${toX}_${toY}"
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        
        val animation = AnimationInfo(
            tileId = tileId,
            startX = fromX * tileSize,
            startY = fromY * tileSize,
            endX = toX * tileSize,
            endY = toY * tileSize,
            duration = PUSH_ANIMATION_DURATION
        )
        
        activeAnimations[key] = animation
        println("🎬 Started push animation for tile $tileId from ($fromX, $fromY) to ($toX, $toY)")
    }
    
    /**
     * Update tất cả animations
     */
    fun update(deltaTime: Float): List<AnimationInfo> {
        val completedAnimations = mutableListOf<AnimationInfo>()
        val completedKeys = mutableListOf<String>()
        
        activeAnimations.forEach { (key, animation) ->
            animation.elapsed += deltaTime
            
            if (animation.isComplete) {
                completedAnimations.add(animation.copy()) // Copy to avoid reference issues
                completedKeys.add(key)
                println("🎬 Animation completed for key: $key")
            }
        }
        
        // Remove completed animations
        completedKeys.forEach { key ->
            activeAnimations.remove(key)
        }
        
        return completedAnimations
    }
    
    /**
     * Lấy vị trí hiện tại của một tile đang được animate
     */
    fun getAnimatedPosition(tileX: Int, tileY: Int): Pair<Float, Float>? {
        // Tìm animation có destination là (tileX, tileY)
        val tileSize = GameConstants.TILE_SIZE.toFloat()
        val targetX = tileX * tileSize
        val targetY = tileY * tileSize
        
        activeAnimations.values.forEach { animation ->
            if (animation.endX == targetX && animation.endY == targetY) {
                return Pair(animation.getCurrentX(), animation.getCurrentY())
            }
        }
        
        return null // Không có animation nào
    }
    
    /**
     * Check xem có animation nào đang diễn ra không
     */
    fun hasActiveAnimations(): Boolean = activeAnimations.isNotEmpty()
    
    /**
     * Lấy tất cả animated positions để render
     */
    fun getAllAnimatedObjects(): List<AnimatedObject> {
        return activeAnimations.values.map { animation ->
            AnimatedObject(
                tileId = animation.tileId,
                x = animation.getCurrentX(),
                y = animation.getCurrentY()
            )
        }
    }
    
    /**
     * Dừng tất cả animations (dùng khi cần reset)
     */
    fun clearAllAnimations() {
        activeAnimations.clear()
        println("🎬 All animations cleared")
    }
    
    data class AnimatedObject(
        val tileId: Int,
        val x: Float,
        val y: Float
    )
}
