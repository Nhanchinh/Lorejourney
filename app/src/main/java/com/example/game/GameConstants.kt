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
    const val STATE_WORLD_SELECT = 1
    const val STATE_LEVEL_SELECT = 2
    const val STATE_PLAYING = 3
    const val STATE_SETTINGS = 4
    const val STATE_PAUSED = 5
    
    // Level progress
    var MAX_UNLOCKED_LEVEL = 15 // Mở tất cả level để test
    const val TOTAL_LEVELS = 15 // Tổng số level trong game (15 maps: World1=4, World2=3, World3=8)
}
