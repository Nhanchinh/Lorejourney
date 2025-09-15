package com.example.game.entity

import android.content.Context
import android.graphics.Canvas
import com.example.game.GameMap
import com.example.game.SpritePlayer
import com.example.game.gameMechanic.PushLogic

/**
 * Player entity - wrapper cho SpritePlayer hiện tại
 * Không thay đổi logic, chỉ adapt vào Entity system
 */
class PlayerEntity(
    startX: Float,
    startY: Float,
    context: Context,
    private val gameMap: GameMap
) : Entity(startX, startY, context) {
    
    // Composition: sử dụng SpritePlayer hiện tại
    private val spritePlayer = SpritePlayer(startX, startY, context, gameMap)
    
    // Delegate properties đến SpritePlayer
    override var x: Float
        get() = spritePlayer.x
        set(value) { 
            spritePlayer.x = value
        }
    
    override var y: Float
        get() = spritePlayer.y
        set(value) { 
            spritePlayer.y = value
        }
    
    override fun update(deltaTime: Long) {
        spritePlayer.update(deltaTime)
        // Sync base entity position với sprite player
        super.x = spritePlayer.x
        super.y = spritePlayer.y
    }
    
    override fun draw(canvas: Canvas) {
        spritePlayer.draw(canvas)
    }
    
    // Delegate methods từ SpritePlayer
    fun startMoving(direction: String) = spritePlayer.startMoving(direction)
    fun stopMoving(direction: String) = spritePlayer.stopMoving(direction)
    fun stopAllMovement() = spritePlayer.stopAllMovement()
    fun isCurrentlyMoving(): Boolean = spritePlayer.isCurrentlyMoving()
    fun checkLevelComplete(gameMap: GameMap): Boolean = spritePlayer.checkLevelComplete(gameMap)
    fun checkPuzzleComplete(): Boolean = spritePlayer.checkPuzzleComplete()
    fun setPushLogic(pushLogic: PushLogic) = spritePlayer.setPushLogic(pushLogic)
    
    override fun getCenterX(): Float = spritePlayer.getCenterX()
    override fun getCenterY(): Float = spritePlayer.getCenterY()
    override fun getCurrentTileX(): Int = spritePlayer.getCurrentTileX()
    override fun getCurrentTileY(): Int = spritePlayer.getCurrentTileY()
    
    override fun dispose() {
        spritePlayer.dispose()
        super.dispose()
    }
    
    // Additional methods for entity system
    fun getLastDirection(): String {
        // TODO: Có thể thêm tracking direction trong SpritePlayer
        return "DOWN" // Placeholder
    }
    
    fun getTilePosition(): Pair<Int, Int> {
        return Pair(getCurrentTileX(), getCurrentTileY())
    }
}
