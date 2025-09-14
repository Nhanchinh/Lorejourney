package com.example.game.map

/**
 * Constants cho các loại tile trong game
 */
object TileConstants {
    // Sprite sheet tiles: 0-271 (reserved for sprite-based rendering)
    // Code-based tiles: 272+ (hand-drawn/procedural tiles)
    
    // Basic tiles - offset by 272
    const val TILE_EMPTY = 0  // Keep 0 as empty
    const val TILE_WALL = 273  // 1 + 272
    const val TILE_FLOOR = 274  // 2 + 272
    const val TILE_GRASS = 275  // 3 + 272
    const val TILE_WATER = 276  // 4 + 272
    const val TILE_STONE = 277  // 5 + 272
    const val TILE_DIRT = 278  // 6 + 272
    
    // Interactive tiles - special IDs remain low for compatibility
    const val TILE_KEY = 10
    const val TILE_BUTTON = 11
    const val TILE_DOOR = 12
    const val TILE_CHEST = 285  // 13 + 272
    const val TILE_END = 14  // Keep for compatibility
    
    // Decorative tiles - offset by 272
    const val TILE_TREE = 292  // 20 + 272
    const val TILE_ROCK = 293  // 21 + 272
    const val TILE_FLOWER = 294  // 22 + 272
    const val TILE_BUSH = 295  // 23 + 272
    
    // Building tiles - offset by 272
    const val TILE_HOUSE_WALL = 302  // 30 + 272
    const val TILE_HOUSE_FLOOR = 303  // 31 + 272
    const val TILE_ROOF = 304  // 32 + 272
    const val TILE_WINDOW = 305  // 33 + 272
    
    // Special tiles - offset by 272
    const val TILE_LAVA = 312  // 40 + 272
    const val TILE_ICE = 313  // 41 + 272
    const val TILE_SAND = 314  // 42 + 272
    
    // Push puzzle tiles - offset by 272
    const val TILE_PUSHABLE_STONE = 322   // 50 + 272 - Đá có thể đẩy
    const val TILE_TARGET = 323           // 51 + 272 - Vị trí đích
    const val TILE_STONE_ON_TARGET = 324  // 52 + 272 - Đá đã vào đích
    
    // Shadow system tiles - offset by 272
    const val TILE_SHADOW_SPAWN = 332     // 60 + 272 - Ô spawn bóng
    const val TILE_SHADOW_TRIGGER = 333   // 61 + 272 - Ô cần bóng để mở cửa
    const val TILE_SHADOW_DOOR = 334      // 62 + 272 - Cửa được mở bởi bóng
    
    // Ice tiles for sliding mechanic (sprite-based IDs)
    const val TILE_ICE_1 = 172  // Ice tile type 1
    const val TILE_ICE_2 = 173  // Ice tile type 2
    const val TILE_ICE_3 = 174  // Ice tile type 3
    const val TILE_ICE_4 = 188  // Ice tile type 4
    const val TILE_ICE_5 = 189  // Ice tile type 5
    const val TILE_ICE_6 = 190  // Ice tile type 6
    const val TILE_ICE_7 = 204  // Ice tile type 7
    const val TILE_ICE_8 = 205  // Ice tile type 8
    const val TILE_ICE_9 = 206  // Ice tile type 9
    
    /**
     * Check if tile is walkable
     */
    fun isWalkable(tileId: Int): Boolean {
        return when (tileId) {
            TILE_EMPTY, TILE_FLOOR, TILE_GRASS, TILE_DIRT, TILE_SAND,
            TILE_KEY, TILE_BUTTON, TILE_DOOR, TILE_HOUSE_FLOOR, TILE_END,
            TILE_TARGET -> true  // Target có thể đi qua
            
            TILE_WALL, TILE_WATER, TILE_STONE, TILE_TREE, TILE_ROCK,
            TILE_BUSH, TILE_HOUSE_WALL, TILE_ROOF, TILE_WINDOW,
            TILE_LAVA, TILE_ICE,
            TILE_PUSHABLE_STONE, TILE_STONE_ON_TARGET -> false  // Đá không thể đi qua
            
            // Sprite-based tiles (1-271) are walkable by default unless specified
            in 1..271 -> true
            
            else -> true // Default to walkable
        }
    }
    
    /**
     * Check if tile có thể đẩy
     */
    fun isPushable(tileId: Int): Boolean {
        return tileId == TILE_PUSHABLE_STONE || tileId == TILE_STONE_ON_TARGET
    }
    
    /**
     * Check if tile là target position
     */
    fun isTarget(tileId: Int): Boolean {
        return tileId == TILE_TARGET || tileId == TILE_STONE_ON_TARGET
    }

    /**
     * Check if tile là shadow spawn
     */
    fun isShadowSpawn(tileId: Int): Boolean {
        return tileId == TILE_SHADOW_SPAWN
    }

    /**
     * Check if tile là shadow trigger
     */
    fun isShadowTrigger(tileId: Int): Boolean {
        return tileId == TILE_SHADOW_TRIGGER
    }

    /**
     * Check if tile is ice (slippery surface)
     */
    fun isIce(tileId: Int): Boolean {
        return tileId in listOf(TILE_ICE_1, TILE_ICE_2, TILE_ICE_3, TILE_ICE_4, 
                               TILE_ICE_5, TILE_ICE_6, TILE_ICE_7, TILE_ICE_8, TILE_ICE_9)
    }
}
