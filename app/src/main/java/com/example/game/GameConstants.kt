package com.example.game

object GameConstants {
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
    var MAX_UNLOCKED_LEVEL = 6 // Mở tất cả level để test
    const val TOTAL_LEVELS = 6 // Tổng số level trong game
}
