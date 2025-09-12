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
                val tile = gameMap.getTile(x, y)
                if (TileConstants.isTarget(tile)) {
                    originalTargets.add(Pair(x, y))
                    println("🎯 Target found at ($x, $y)")
                }
            }
        }
    }

    /**
     * Thực hiện đẩy đá nếu có thể
     * @return true nếu push thành công, false nếu không thể push
     */
    fun tryPush(playerTileX: Int, playerTileY: Int, dx: Int, dy: Int): Boolean {
        val stoneTileX = playerTileX + dx
        val stoneTileY = playerTileY + dy
        val pushToX = stoneTileX + dx
        val pushToY = stoneTileY + dy

        // Check xem có đá để đẩy không
        val stoneTile = gameMap.getTile(stoneTileX, stoneTileY)
        if (!TileConstants.isPushable(stoneTile)) {
            return false // Không có đá để đẩy
        }

        // Check xem vị trí đẩy đến có hợp lệ không
        val destinationTile = gameMap.getTile(pushToX, pushToY)
        if (!canPushTo(destinationTile)) {
            println("❌ Cannot push stone to ($pushToX, $pushToY) - tile ID: $destinationTile")
            return false // Không thể đẩy đến vị trí này
        }

        // Thực hiện đẩy
        performPush(stoneTileX, stoneTileY, pushToX, pushToY)
        println("🔄 Pushed stone from ($stoneTileX, $stoneTileY) to ($pushToX, $pushToY)")
        return true
    }

    private fun canPushTo(tileId: Int): Boolean {
        return when (tileId) {
            TileConstants.TILE_FLOOR,
            TileConstants.TILE_TARGET -> true
            else -> false
        }
    }

    private fun performPush(fromX: Int, fromY: Int, toX: Int, toY: Int) {
        val destinationTile = gameMap.getTile(toX, toY)

        // Update destination tile
        val newDestinationTile = when {
            TileConstants.isTarget(destinationTile) -> {
                println("✅ Stone pushed onto target at ($toX, $toY)")
                TileConstants.TILE_STONE_ON_TARGET
            }
            else -> TileConstants.TILE_PUSHABLE_STONE
        }
        gameMap.setTile(toX, toY, newDestinationTile)

        // Update source tile (restore original if it was target)
        val newSourceTile = when {
            originalTargets.contains(Pair(fromX, fromY)) -> {
                println("🎯 Restored target at ($fromX, $fromY)")
                TileConstants.TILE_TARGET
            }
            else -> TileConstants.TILE_FLOOR
        }
        gameMap.setTile(fromX, fromY, newSourceTile)
    }

    /**
     * Check xem đã hoàn thành puzzle chưa
     */
    fun isPuzzleComplete(): Boolean {
        // Kiểm tra tất cả targets đều có đá
        val completed = originalTargets.all { (x, y) ->
            gameMap.getTile(x, y) == TileConstants.TILE_STONE_ON_TARGET
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
            gameMap.getTile(x, y) == TileConstants.TILE_STONE_ON_TARGET
        }
        return Pair(completedTargets, totalTargets)
    }
}