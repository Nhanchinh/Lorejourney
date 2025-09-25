package com.example.game.gameMechanic

import com.example.game.GameMap
import com.example.game.GameConstants
import com.example.game.map.TileConstants
import com.example.game.music.MusicManager
import android.content.Context

/**
 * Quản lý logic đẩy đá trong game puzzle
 */
class PushLogic(private val gameMap: GameMap, private val context: Context) {

    // Track vị trí các target gốc (để restore khi đá rời khỏi target)
    private val originalTargets = mutableSetOf<Pair<Int, Int>>()
    
    // Animator cho push animations
    val animator = PushableObjectAnimator()

    // Map để lưu trữ các push actions pending completion
    private val pendingPushActions = mutableMapOf<String, PendingPushAction>()
    
    // Reference to shadow mechanic (optional)
    private var shadowMechanic: ShadowMechanic? = null
    
    data class PendingPushAction(
        val fromX: Int,
        val fromY: Int,
        val toX: Int,
        val toY: Int,
        val stoneTile: Int
    )

    init {
        // Scan map để tìm tất cả targets
        scanOriginalTargets()
        println("🎯 Found ${originalTargets.size} targets in map")
    }

    private fun scanOriginalTargets() {
        for (y in 0 until gameMap.height) {
            for (x in 0 until gameMap.width) {
                val activeTile = gameMap.getTile(x, y, 2) // Check active layer
                val mainTile = gameMap.getTile(x, y, 1) // Check main layer
                
                if (TileConstants.isTarget(activeTile) || TileConstants.isTarget(mainTile)) {
                    originalTargets.add(Pair(x, y))
                    println("🎯 Target found at ($x, $y) - active: $activeTile, main: $mainTile")
                }
            }
        }
    }

    /**
     * Set reference to shadow mechanic for checking shadow position on targets
     */
    fun setShadowMechanic(shadowMechanic: ShadowMechanic?) {
        this.shadowMechanic = shadowMechanic
    }

    /**
     * Thực hiện đẩy đá nếu có thể
     * @return true nếu push thành công, false nếu không thể push
     */
    fun tryPush(playerTileX: Int, playerTileY: Int, dx: Int, dy: Int): Boolean {
        // Không cho phép push khi đang có animation
        if (animator.hasActiveAnimations()) {
            println("🎬 Push blocked - animation in progress")
            return false
        }
        
        println("🔄 Trying to push stone from ($playerTileX, $playerTileY) to (${playerTileX + dx}, ${playerTileY + dy})")
        val stoneTileX = playerTileX + dx
        val stoneTileY = playerTileY + dy
        val pushToX = stoneTileX + dx
        val pushToY = stoneTileY + dy

        // Check xem có đá để đẩy không (trên active layer)
        val stoneTile = gameMap.getTile(stoneTileX, stoneTileY, 2)
        
        if (!TileConstants.isPushable(stoneTile) && stoneTile != TileConstants.TILE_STONE_ON_TARGET) {
            return false // Không có đá để đẩy
        }

        // Check xem vị trí đẩy đến có hợp lệ không (check cả main và active layer)
        val mainDestinationTile = gameMap.getTile(pushToX, pushToY, 1) // Main layer
        val activeDestinationTile = gameMap.getTile(pushToX, pushToY, 2) // Active layer
        if (!canPushTo(mainDestinationTile, activeDestinationTile)) {
            println("❌ Cannot push stone to ($pushToX, $pushToY) - main: $mainDestinationTile, active: $activeDestinationTile")
            return false // Không thể đẩy đến vị trí này
        }

        // Check xem đá có trượt trên băng không
        val finalDestination = calculateIceSlideDestination(pushToX, pushToY, dx, dy)
        
        // Bắt đầu animation và lưu pending action
        val actionKey = "${stoneTileX}_${stoneTileY}_${finalDestination.first}_${finalDestination.second}"
        pendingPushActions[actionKey] = PendingPushAction(stoneTileX, stoneTileY, finalDestination.first, finalDestination.second, stoneTile)

        // Phát âm thanh đẩy đá và thực hiện animation
        MusicManager.playSound(context, "pushstone")
        if (finalDestination.first != pushToX || finalDestination.second != pushToY) {
            // Có trượt trên băng - animation dài hơn
            println("🧊 Stone will slide on ice from ($pushToX, $pushToY) to (${finalDestination.first}, ${finalDestination.second})")
            animator.startIceSlideAnimation(stoneTile, stoneTileX, stoneTileY, finalDestination.first, finalDestination.second)
        } else {
            // Push bình thường
            animator.startPushAnimation(stoneTile, stoneTileX, stoneTileY, pushToX, pushToY)
        }
        
        // XÓA nguồn ngay lập tức để tránh duplicate
        clearSourceTile(stoneTileX, stoneTileY)
        
        println("🔄 Started push animation from ($stoneTileX, $stoneTileY) to (${finalDestination.first}, ${finalDestination.second})")
        return true
    }

    /**
     * Tính toán vị trí cuối cùng của đá sau khi trượt trên băng
     */
    private fun calculateIceSlideDestination(startX: Int, startY: Int, dx: Int, dy: Int): Pair<Int, Int> {
        var currentX = startX
        var currentY = startY
        
        // Kiểm tra xem vị trí bắt đầu có phải băng không
        val startTile = gameMap.getTile(startX, startY, 1) // Main layer
        if (!TileConstants.isIce(startTile)) {
            // Không phải băng, không trượt
            return Pair(startX, startY)
        }
        
        println("🧊 Stone landed on ice at ($startX, $startY), calculating slide destination...")
        
        // Tiếp tục trượt theo hướng cho đến khi gặp chướng ngại hoặc rời khỏi băng
        while (true) {
            val nextX = currentX + dx
            val nextY = currentY + dy
            
            // Check bounds
            if (nextX < 0 || nextX >= gameMap.width || nextY < 0 || nextY >= gameMap.height) {
                println("🧊 Hit boundary, stopping at ($currentX, $currentY)")
                break
            }
            
            // Check xem vị trí tiếp theo có thể đi vào không
            val nextMainTile = gameMap.getTile(nextX, nextY, 1)
            val nextActiveTile = gameMap.getTile(nextX, nextY, 2)
            
            if (!canPushTo(nextMainTile, nextActiveTile)) {
                println("🧊 Hit obstacle, stopping at ($currentX, $currentY)")
                break
            }
            
            // Di chuyển đến vị trí tiếp theo
            currentX = nextX
            currentY = nextY
            
            // Kiểm tra xem vẫn còn trên băng không
            val currentTile = gameMap.getTile(currentX, currentY, 1)
            if (!TileConstants.isIce(currentTile)) {
                println("🧊 Left ice surface, stopping at ($currentX, $currentY)")
                break
            }
        }
        
        println("🧊 Final slide destination: ($currentX, $currentY)")
        return Pair(currentX, currentY)
    }

    /**
     * Logic canPushTo đơn giản
     */
    private fun canPushTo(mainTileId: Int, activeTileId: Int): Boolean {
        // Main layer phải walkable
        val mainWalkable = TileConstants.isWalkable(mainTileId)
        
        // Active layer phải empty hoặc target
        val activeValid = when (activeTileId) {
            TileConstants.TILE_EMPTY -> true
            else -> TileConstants.isTarget(activeTileId)
        }
        
        val result = mainWalkable && activeValid
        println("🔍 canPushTo check - main: $mainTileId (walkable: $mainWalkable), active: $activeTileId (valid: $activeValid), result: $result")
        return result
    }

    /**
     * Clear source tile - đơn giản hóa
     */
    private fun clearSourceTile(x: Int, y: Int) {
        val sourceMainTile = gameMap.getTile(x, y, 1) // Check main layer at source
        
        if (originalTargets.contains(Pair(x, y))) {
            // Có original target ở vị trí này
            if (TileConstants.isTarget(sourceMainTile)) {
                // Nếu main layer có target, thì active layer để empty
                println("🎯 Target on main layer at ($x, $y), setting active layer to empty")
                gameMap.setTile(x, y, TileConstants.TILE_EMPTY, 2)
            } else {
                // Target chỉ có trên active layer, restore lại
                println("🎯 Restored target at ($x, $y) on active layer")
                gameMap.setTile(x, y, TileConstants.TILE_TARGET, 2)
            }
        } else {
            // Không có original target, set empty
            gameMap.setTile(x, y, TileConstants.TILE_EMPTY, 2)
        }
    }

    /**
     * Update animator (gọi từ game loop)
     */
    fun update(deltaTime: Float) {
        val completedAnimations = animator.update(deltaTime)
        
        // Process completed animations
        completedAnimations.forEach { completedAnimation ->
            // Tìm pending action tương ứng
            val actionKey = pendingPushActions.keys.find { key ->
                val action = pendingPushActions[key]!!
                val tileSize = GameConstants.TILE_SIZE.toFloat()
                val expectedEndX = action.toX.toFloat() * tileSize
                val expectedEndY = action.toY.toFloat() * tileSize
                
                completedAnimation.endX == expectedEndX && completedAnimation.endY == expectedEndY
            }
            
            actionKey?.let { key ->
                val action = pendingPushActions[key]!!
                // Thực hiện push logic sau khi animation hoàn thành
                completePushAction(action)
                pendingPushActions.remove(key)
                println("🎬 Completed push action: $key")
            }
        }
    }
    
    /**
     * Logic mới: Đơn giản hóa completePushAction
     */
    private fun completePushAction(action: PendingPushAction) {
        val destinationActiveTile = gameMap.getTile(action.toX, action.toY, 2) // Active layer
        val destinationMainTile = gameMap.getTile(action.toX, action.toY, 1) // Main layer
        
        // LOGIC MỚI: Kiểm tra vị trí đích có phải target không
        val isDestinationTarget = TileConstants.isTarget(destinationActiveTile) || TileConstants.isTarget(destinationMainTile)
        
        val newDestinationTile = when {
            isDestinationTarget -> {
                // Nếu đích là target → đá chuyển thành ID 240 (stone on target)
                println("✅ Stone pushed onto target at (${action.toX}, ${action.toY}) - converting to ID 240")
                MusicManager.playSound(context, "ding")
                TileConstants.TILE_STONE_ON_TARGET
            }
            else -> {
                // Nếu đích KHÔNG phải target → đá chuyển về đá thường
                println("🎯 Stone pushed to non-target at (${action.toX}, ${action.toY}) - converting to normal stone")
                // Chuyển đổi từ ID 240 về đá thường
                when (action.stoneTile) {
                    TileConstants.TILE_STONE_ON_TARGET -> TileConstants.TILE_PUSHABLE_CRATE // ID 42
                    else -> action.stoneTile // Giữ nguyên nếu đã là đá thường
                }
            }
        }
        
        gameMap.setTile(action.toX, action.toY, newDestinationTile, 2) // Set on active layer
        println("🎯 Final tile at (${action.toX}, ${action.toY}): $newDestinationTile")
    }

    /**
     * Check xem đã hoàn thành puzzle chưa
     */
    fun isPuzzleComplete(): Boolean {
        // Kiểm tra tất cả original targets đều có đá HOẶC bóng HOẶC player
        val completed = originalTargets.all { (x, y) ->
            val activeTile = gameMap.getTile(x, y, 2)
            val hasStone = activeTile == TileConstants.TILE_STONE_ON_TARGET
            
            // Check if ANY shadow is on this target
            val hasShadow = shadowMechanic?.getAllShadowTilePositions()?.any { (shadowX, shadowY) ->
                shadowX == x && shadowY == y
            } ?: false
            
            // Check if player is on this target
            val hasPlayer = shadowMechanic?.getPlayerTilePosition()?.let { (playerX, playerY) ->
                playerX == x && playerY == y
            } ?: false
            
            hasStone || hasShadow || hasPlayer
        }

        if (completed) {
            println("🎉 PUZZLE COMPLETED! All targets have stones or shadows!")
        }

        return completed
    }

    /**
     * Get số lượng targets và số targets đã hoàn thành (có đá hoặc bóng hoặc player)
     */
    fun getProgress(): Pair<Int, Int> {
        val totalTargets = originalTargets.size
        val completedTargets = originalTargets.count { (x, y) ->
            val activeTile = gameMap.getTile(x, y, 2)
            val hasStone = activeTile == TileConstants.TILE_STONE_ON_TARGET
            
            // Check if ANY shadow is on this target
            val hasShadow = shadowMechanic?.getAllShadowTilePositions()?.any { (shadowX, shadowY) ->
                shadowX == x && shadowY == y
            } ?: false
            
            // Check if player is on this target
            val hasPlayer = shadowMechanic?.getPlayerTilePosition()?.let { (playerX, playerY) ->
                playerX == x && playerY == y
            } ?: false
            
            hasStone || hasShadow || hasPlayer
        }
        return Pair(completedTargets, totalTargets)
    }
}