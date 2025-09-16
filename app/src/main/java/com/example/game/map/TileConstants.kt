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
    const val TILE_END = 95  // Keep for compatibility
    
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
    
    // Push puzzle tiles - sprite-based IDs (theo yêu cầu mới)
    const val TILE_PUSHABLE_CRATE = 42    // Thùng có thể đẩy
    const val TILE_PUSHABLE_BLOCK = 152   // Khối đá có thể đẩy
    const val TILE_TARGET = 96            // Vị trí đích (theo yêu cầu mới)
    
    // Legacy push puzzle tiles - offset by 272 (giữ lại cho compatibility)
    const val TILE_PUSHABLE_STONE = 322   // 50 + 272 - Đá có thể đẩy
    const val TILE_TARGET_OLD = 323       // 51 + 272 - Vị trí đích cũ
    const val TILE_STONE_ON_TARGET = 240  // phiến đá màu xanh 
    
    // Additional pushable tiles (legacy)
    const val TILE_PUSHABLE_BARREL = 55   // Thùng gỗ có thể đẩy(đá nền băng)
    
    // Shadow system tiles - offset by 272
    const val TILE_SHADOW_SPAWN = 80     // 60 + 272 - Ô spawn bóng
    const val TILE_SHADOW_TRIGGER = 176   // 61 + 272 - Ô cần bóng để mở cửa
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
    const val TILE_ICE_10 = 221  // Ice tile type 10
    const val TILE_ICE_11 = 222  // Ice tile type 11
    const val TILE_ICE_12 = 237  // Ice tile type 12
    const val TILE_ICE_13 = 238  // Ice tile type 13
    
    // Danh sách các tile không thể đi qua (sprite-based IDs)
    private val NON_WALKABLE_SPRITE_TILES = setOf(
        6, 7, 8, 10, 11, 12, 13, 14, 15, 16, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32,
        39, 42, 43, 47, 48, 49, 50, 51, 52, 55, 56, 65, 66, 67, 68, 69, 70, 71, 72, 73,
        85, 86, 87, 88, 89, 92, 93, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108,
        111, 113, 114, 115, 116, 117, 118, 119, 121, 122, 124, 127, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140,
        145, 146, 147, 148, 149, 150, 151, 152, 153, 161, 162, 163, 165, 166, 167, 168, 169, 170, 171,
        177, 179, 182, 183, 184, 185, 186, 187, 192, 193, 194, 195, 198, 199, 200, 201, 202, 203,
        208, 209, 210, 211, 214, 215, 216, 217, 218, 219, 224, 225, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236,
        241, 242, 243, 244, 245, 248, 250, 253, 254, 264, 265, 266, 267, 268, 269, 270
    )

    /**
     * Check if tile is walkable
     */
    fun isWalkable(tileId: Int): Boolean {
        return when (tileId) {
            TILE_EMPTY, TILE_FLOOR, TILE_GRASS, TILE_DIRT, TILE_SAND,
            TILE_KEY, TILE_BUTTON, TILE_DOOR, TILE_HOUSE_FLOOR, TILE_END,
            TILE_TARGET, TILE_TARGET_OLD -> true  // Target có thể đi qua
            
            TILE_WALL, TILE_WATER, TILE_STONE, TILE_TREE, TILE_ROCK,
            TILE_BUSH, TILE_HOUSE_WALL, TILE_ROOF, TILE_WINDOW,
            TILE_LAVA, TILE_ICE,
            TILE_PUSHABLE_STONE, TILE_STONE_ON_TARGET,
            TILE_PUSHABLE_CRATE, TILE_PUSHABLE_BARREL, TILE_PUSHABLE_BLOCK -> false  // Các khối có thể đẩy không thể đi qua
            
            // Sprite-based tiles (1-271) - check against non-walkable list
            in 1..271 -> !NON_WALKABLE_SPRITE_TILES.contains(tileId)
            
            else -> true // Default to walkable
        }
    }
    
    /**
     * Check if tile có thể đẩy
     */
    fun isPushable(tileId: Int): Boolean {
        return tileId == TILE_PUSHABLE_CRATE ||   // ID 42 (theo yêu cầu mới)
               tileId == TILE_PUSHABLE_BLOCK ||   // ID 152 (theo yêu cầu mới)
               tileId == TILE_PUSHABLE_STONE ||   // Legacy
               tileId == TILE_STONE_ON_TARGET ||  // Legacy
               tileId == TILE_PUSHABLE_BARREL     // ID 55 (legacy)
    }
    
    /**
     * Check if tile là target position
     */
    fun isTarget(tileId: Int): Boolean {
        return tileId == TILE_TARGET ||           // ID 96 (theo yêu cầu mới)
               tileId == TILE_TARGET_OLD ||       // ID 323 (legacy)
               tileId == TILE_STONE_ON_TARGET     // ID 324 (đá đã vào đích)
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
