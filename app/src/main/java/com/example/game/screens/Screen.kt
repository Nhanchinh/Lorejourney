package com.example.game.screens

import android.graphics.Canvas
import android.view.MotionEvent

abstract class Screen {
    abstract fun update(deltaTime: Long)
    abstract fun draw(canvas: Canvas)
    abstract fun handleTouch(event: MotionEvent): Boolean
    
    // Thêm method này để subclasses có thể override
    open fun onScreenSizeChanged(width: Int, height: Int) {
        // Default implementation - do nothing
    }
}
