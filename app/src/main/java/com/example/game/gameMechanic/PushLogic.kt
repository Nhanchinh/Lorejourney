package com.example.game.gameMechanic

import com.example.game.GameMap
import com.example.game.map.TileConstants

/**
 * Quản lý logic đẩy đá trong game puzzle
 */
class PushLogic(private val gameMap: GameMap) {

    // Track vị trí các target gốc (để restore khi đá rời khỏi target)
    private val originalTargets = mutableSetOf<Pair<Int, Int>>()

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
        print("🔄 Trying to push stone from ($playerTileX, $playerTileY) to (${playerTileX + dx}, ${playerTileY + dy})")
        val stoneTileX = playerTileX + dx
        val stoneTileY = playerTileY + dy
        val pushToX = stoneTileX + dx
        val pushToY = stoneTileY + dy

        // Check xem có đá để đẩy không (trên active layer)
        val stoneTile = gameMap.getTile(stoneTileX, stoneTileY, 2)
        if (!TileConstants.isPushable(stoneTile)) {
            return false // Không có đá để đẩy
        }

        // Check xem vị trí đẩy đến có hợp lệ không (check cả main và active layer)
        val mainDestinationTile = gameMap.getTile(pushToX, pushToY, 1) // Main layer
        val activeDestinationTile = gameMap.getTile(pushToX, pushToY, 2) // Active layer
        if (!canPushTo(mainDestinationTile, activeDestinationTile)) {
            println("❌ Cannot push stone to ($pushToX, $pushToY) - main: $mainDestinationTile, active: $activeDestinationTile")
            return false // Không thể đẩy đến vị trí này
        }

        // Thực hiện đẩy
        performPush(stoneTileX, stoneTileY, pushToX, pushToY)
        println("🔄 Pushed stone from ($stoneTileX, $stoneTileY) to ($pushToX, $pushToY) on active layer")
        return true
    }

    private fun canPushTo(mainTileId: Int, activeTileId: Int): Boolean {
        // Main layer phải walkable (sử dụng TileConstants.isWalkable)
        val mainWalkable = TileConstants.isWalkable(mainTileId)
        
        // Active layer phải empty hoặc target
        val activeValid = when (activeTileId) {
            TileConstants.TILE_EMPTY,
            TileConstants.TILE_TARGET -> true
            else -> false
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
                    // Target chỉ có trên active layer, restore lại
                    println("🎯 Restored target at ($fromX, $fromY) on active layer")
                    TileConstants.TILE_TARGET
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