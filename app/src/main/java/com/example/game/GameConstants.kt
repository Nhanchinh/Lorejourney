package com.example.game

object GameConstants {
    // Map tile IDs
    const val TILE_EMPTY = 0
    const val TILE_WALL = 1
    const val TILE_FLOOR = 2
    const val TILE_KEY = 4
    const val TILE_BUTTON = 5
    
    // Game settings
    const val TILE_SIZE = 96
    const val PLAYER_SPEED = 64
    const val ANIMATION_FRAME_DURATION = 150L
    
    // Screen dimensions
    var SCREEN_WIDTH = 0
    var SCREEN_HEIGHT = 0
    
    // Game states
    const val STATE_MENU = 0
    const val STATE_LEVEL_SELECT = 1
    const val STATE_PLAYING = 2
    const val STATE_SETTINGS = 3
    const val STATE_PAUSED = 4
    
    // Level progress
    var MAX_UNLOCKED_LEVEL = 6 // Mở tất cả level để test, bao gồm cả map6
    const val TOTAL_LEVELS = 6 // Tổng số level trong game bao gồm map6
}
