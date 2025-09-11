package com.example.game.map

/**
 * Constants cho các loại tile trong game
 */
object TileConstants {
    // Basic tiles
    const val TILE_EMPTY = 0
    const val TILE_WALL = 1
    const val TILE_FLOOR = 2
    const val TILE_GRASS = 3
    const val TILE_WATER = 4
    const val TILE_STONE = 5
    const val TILE_DIRT = 6
    
    // Interactive tiles
    const val TILE_KEY = 10
    const val TILE_BUTTON = 11
    const val TILE_DOOR = 12
    const val TILE_CHEST = 13
    const val TILE_END = 14  // NEW: Tile kết thúc level
    
    // Decorative tiles
    const val TILE_TREE = 20
    const val TILE_ROCK = 21
    const val TILE_FLOWER = 22
    const val TILE_BUSH = 23
    
    // Building tiles
    const val TILE_HOUSE_WALL = 30
    const val TILE_HOUSE_FLOOR = 31
    const val TILE_ROOF = 32
    const val TILE_WINDOW = 33
    
    // Special tiles
    const val TILE_LAVA = 40
    const val TILE_ICE = 41
    const val TILE_SAND = 42
    
    /**
     * Check if tile is walkable
     */
    fun isWalkable(tileId: Int): Boolean {
        return when (tileId) {
            TILE_EMPTY, TILE_FLOOR, TILE_GRASS, TILE_DIRT, TILE_SAND,
            TILE_KEY, TILE_BUTTON, TILE_DOOR, TILE_HOUSE_FLOOR, TILE_END -> true
            
            TILE_WALL, TILE_WATER, TILE_STONE, TILE_TREE, TILE_ROCK,
            TILE_BUSH, TILE_HOUSE_WALL, TILE_ROOF, TILE_WINDOW,
            TILE_LAVA, TILE_ICE -> false
            
            else -> true // Default to walkable
        }
    }
}
