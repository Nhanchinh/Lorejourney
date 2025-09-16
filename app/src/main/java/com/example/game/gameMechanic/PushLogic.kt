package com.example.game.gameMechanic

import com.example.game.GameMap
import com.example.game.GameConstants
import com.example.game.map.TileConstants

/**
 * Quản lý logic đẩy đá trong game puzzle
 */
class PushLogic(private val gameMap: GameMap) {

    // Track vị trí các target gốc (để restore khi đá rời khỏi target)
    private val originalTargets = mutableSetOf<Pair<Int, Int>>()
    
    // Track ID gốc của stones khi đẩy vào target (để restore sau)
    private val stoneOnTargetOriginalIds = mutableMapOf<Pair<Int, Int>, Int>()
    
    // Animator cho push animations
    val animator = PushableObjectAnimator()

    // Map để lưu trữ các push actions pending completion
    private val pendingPushActions = mutableMapOf<String, PendingPushAction>()
    
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
        
        // Nếu đây là TILE_STONE_ON_TARGET, lấy ID gốc từ tracking map
        val actualStoneTile = if (stoneTile == TileConstants.TILE_STONE_ON_TARGET) {
            stoneOnTargetOriginalIds[Pair(stoneTileX, stoneTileY)] ?: TileConstants.TILE_PUSHABLE_CRATE
        } else {
            stoneTile
        }
        
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
        
        // Bắt đầu animation và lưu pending action với actual stone ID
        val actionKey = "${stoneTileX}_${stoneTileY}_${finalDestination.first}_${finalDestination.second}"
        pendingPushActions[actionKey] = PendingPushAction(stoneTileX, stoneTileY, finalDestination.first, finalDestination.second, actualStoneTile)
        
        if (finalDestination.first != pushToX || finalDestination.second != pushToY) {
            // Có trượt trên băng - animation dài hơn
            println("🧊 Stone will slide on ice from ($pushToX, $pushToY) to (${finalDestination.first}, ${finalDestination.second})")
            animator.startIceSlideAnimation(actualStoneTile, stoneTileX, stoneTileY, finalDestination.first, finalDestination.second)
        } else {
            // Push bình thường
            animator.startPushAnimation(actualStoneTile, stoneTileX, stoneTileY, pushToX, pushToY)
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

    private fun canPushTo(mainTileId: Int, activeTileId: Int): Boolean {
        // Main layer phải walkable (sử dụng TileConstants.isWalkable)
        val mainWalkable = TileConstants.isWalkable(mainTileId)
        
        // Active layer phải empty hoặc target
        val activeValid = when (activeTileId) {
            TileConstants.TILE_EMPTY -> true
            else -> TileConstants.isTarget(activeTileId) // Sử dụng function để check tất cả target types
        }
        
        println("🔍 canPushTo check - main: $mainTileId (walkable: $mainWalkable), active: $activeTileId (valid: $activeValid)")
        return mainWalkable && activeValid
    }

    private fun performPush(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val destinationActiveTile = gameMap.getTile(toX, toY, 2) // Active layer
        val destinationMainTile = gameMap.getTile(toX, toY, 1) // Main layer
        val originalStoneTile = gameMap.getTile(fromX, fromY, 2) // Lấy loại đá gốc

        // Update destination tile on active layer
        val newDestinationTile = when {
            TileConstants.isTarget(destinationActiveTile) || TileConstants.isTarget(destinationMainTile) -> {
                println("✅ Stone pushed onto target at ($toX, $toY)")
                TileConstants.TILE_STONE_ON_TARGET
            }
            else -> originalStoneTile // Giữ nguyên loại đá gốc (42, 55, 152, etc.)
        }
        gameMap.setTile(toX, toY, newDestinationTile, 2) // Set on active layer

        // Update source tile on active layer - check what should be restored
        val sourceMainTile = gameMap.getTile(fromX, fromY, 1) // Check main layer at source
        val newSourceTile = when {
            originalTargets.contains(Pair(fromX, fromY)) -> {
                // Có original target ở vị trí này
                if (TileConstants.isTarget(sourceMainTile)) {
                    // Nếu main layer có target, thì active layer để empty
                    println("🎯 Target on main layer at ($fromX, $fromY), setting active layer to empty")
                    TileConstants.TILE_EMPTY
                } else {
                    // Target chỉ có trên active layer, restore lại với ID mới
                    println("🎯 Restored target at ($fromX, $fromY) on active layer")
                    TileConstants.TILE_TARGET // ID 96 (theo yêu cầu mới)
                }
            }
            else -> {
                // Không có original target, set empty
                TileConstants.TILE_EMPTY
            }
        }
        gameMap.setTile(fromX, fromY, newSourceTile, 2) // Set on active layer
    }

    /**
     * Xóa tile tại vị trí nguồn (chỉ clear active layer)
     */
    private fun clearSourceTile(x: Int, y: Int) {
        val sourceMainTile = gameMap.getTile(x, y, 1) // Check main layer at source
        val newSourceTile = when {
            originalTargets.contains(Pair(x, y)) -> {
                // Có original target ở vị trí này
                if (TileConstants.isTarget(sourceMainTile)) {
                    // Nếu main layer có target, thì active layer để empty
                    println("🎯 Target on main layer at ($x, $y), setting active layer to empty")
                    TileConstants.TILE_EMPTY
                } else {
                    // Target chỉ có trên active layer, restore lại
                    println("🎯 Restored target at ($x, $y) on active layer")
                    TileConstants.TILE_TARGET
                }
            }
            else -> {
                // Không có original target, set empty
                TileConstants.TILE_EMPTY
            }
        }
        gameMap.setTile(x, y, newSourceTile, 2) // Set on active layer
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
     * Hoàn thành push action sau khi animation xong
     */
    private fun completePushAction(action: PendingPushAction) {
        val destinationActiveTile = gameMap.getTile(action.toX, action.toY, 2) // Active layer
        val destinationMainTile = gameMap.getTile(action.toX, action.toY, 1) // Main layer
        
        // Xử lý khi đẩy stone ra khỏi target
        val sourcePos = Pair(action.fromX, action.fromY)
        if (stoneOnTargetOriginalIds.containsKey(sourcePos)) {
            // Stone được đẩy ra khỏi target, restore target và stone ID gốc
            val originalStoneId = stoneOnTargetOriginalIds.remove(sourcePos)!!
            
            // Restore target tại vị trí cũ
            if (originalTargets.contains(sourcePos)) {
                gameMap.setTile(action.fromX, action.fromY, TileConstants.TILE_TARGET, 2)
                println("🎯 Restored target at (${action.fromX}, ${action.fromY})")
            }
            
            // Set stone với ID gốc tại vị trí mới
            gameMap.setTile(action.toX, action.toY, originalStoneId, 2)
            println("🔄 Restored stone ID $originalStoneId at (${action.toX}, ${action.toY})")
            return
        }

        // Logic bình thường: đẩy stone vào target
        val newDestinationTile = when {
            TileConstants.isTarget(destinationActiveTile) || TileConstants.isTarget(destinationMainTile) -> {
                println("✅ Stone completed push onto target at (${action.toX}, ${action.toY})")
                // Lưu ID gốc của stone
                stoneOnTargetOriginalIds[Pair(action.toX, action.toY)] = action.stoneTile
                TileConstants.TILE_STONE_ON_TARGET
            }
            else -> action.stoneTile // Giữ nguyên loại đá gốc (42, 55, 152, etc.)
        }
        gameMap.setTile(action.toX, action.toY, newDestinationTile, 2) // Set on active layer
    }

    /**
     * Check xem đã hoàn thành puzzle chưa
     */
    fun isPuzzleComplete(): Boolean {
        // Kiểm tra tất cả original targets đều có đá
        val completed = originalTargets.all { (x, y) ->
            val activeTile = gameMap.getTile(x, y, 2)
            activeTile == TileConstants.TILE_STONE_ON_TARGET
        }

        if (completed) {
            println("🎉 PUZZLE COMPLETED! All stones on targets!")
        }

        return completed
    }

    /**
     * Get số lượng targets và stones completed
     */
    fun getProgress(): Pair<Int, Int> {
        val totalTargets = originalTargets.size
        val completedTargets = originalTargets.count { (x, y) ->
            val activeTile = gameMap.getTile(x, y, 2)
            activeTile == TileConstants.TILE_STONE_ON_TARGET
        }
        return Pair(completedTargets, totalTargets)
    }
}