package com.example.game.screens

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.widget.VideoView
import android.widget.FrameLayout
import com.example.game.GameConstants

class VideoPopup(
    private val context: Context
) {
    
    var isActive = false
        private set
    
    private var videoView: VideoView? = null
    private var parentContainer: FrameLayout? = null
    private var currentVideoName = "mechanic1"
    
    // UI elements
    private val popupBox = RectF()
    private val closeButton = RectF()
    
    // Tab buttons
    private val tabButtons = mutableListOf<RectF>()
    private val tabNames = listOf("mechanic1", "mechanic2", "mechanic3", "mechanic4", "mechanic5", "mechanic6")
    private val tabTitles = listOf("Điều khiển cơ bản", "Cơ chế đẩy", "Điều kiện thắng", "Cơ chế băng", "Cơ chế bóng ma", "Băng và bóng ma")
    
    // Paints
    private val overlayPaint = Paint().apply {
        color = Color.argb(200, 0, 0, 0)
        style = Paint.Style.FILL
    }
    
    private val popupBoxPaint = Paint().apply {
        color = Color.argb(240, 50, 50, 50)
        style = Paint.Style.FILL
    }
    
    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    
    private val titlePaint = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val buttonPaint = Paint().apply {
        color = Color.argb(200, 100, 100, 100)
        style = Paint.Style.FILL
    }
    
    private val buttonTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    private val selectedTabPaint = Paint().apply {
        color = Color.argb(200, 70, 130, 180) // Blue color for selected tab
        style = Paint.Style.FILL
    }
    
    private val tabTextPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    
    fun show(videoName: String = "mechanic1", parentContainer: FrameLayout) {
        this.parentContainer = parentContainer
        isActive = true
        currentVideoName = videoName
        setupPopup()
        startVideo()
    }
    
    fun hide() {
        isActive = false
        stopVideo()
    }
    
    private fun setupPopup() {
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val centerY = GameConstants.SCREEN_HEIGHT / 2f
        val popupWidth = GameConstants.SCREEN_WIDTH * 0.75f // Giảm từ 0.9f xuống 0.75f
        val popupHeight = GameConstants.SCREEN_HEIGHT * 0.65f // Giảm từ 0.8f xuống 0.65f
        
        popupBox.set(
            centerX - popupWidth / 2,
            centerY - popupHeight / 2,
            centerX + popupWidth / 2,
            centerY + popupHeight / 2
        )
        
        // Setup close button
        val buttonWidth = 200f
        val buttonHeight = 60f
        val buttonY = popupBox.bottom - 80f
        
        closeButton.set(
            centerX - buttonWidth / 2,
            buttonY,
            centerX + buttonWidth / 2,
            buttonY + buttonHeight
        )
        
        // Setup tab buttons
        setupTabButtons()
    }
    
    private fun setupTabButtons() {
        tabButtons.clear()
        
        val tabAreaWidth = popupBox.width() * 0.3f // Tăng từ 25% lên 30% vì popup nhỏ hơn
        val tabAreaHeight = popupBox.height() - 200f // Leave space for title and close button
        val tabAreaX = popupBox.left + 20f
        val tabAreaY = popupBox.top + 100f
        
        val tabWidth = tabAreaWidth - 20f
        val tabHeight = (tabAreaHeight - (tabNames.size - 1) * 10f) / tabNames.size // 10f spacing between tabs
        
        for (i in tabNames.indices) {
            val tabX = tabAreaX
            val tabY = tabAreaY + i * (tabHeight + 10f)
            
            val tabButton = RectF(tabX, tabY, tabX + tabWidth, tabY + tabHeight)
            tabButtons.add(tabButton)
        }
    }
    
    private fun startVideo() {
        try {
            // Stop current video if playing
            stopVideo()
            
            // Create VideoView
            videoView = VideoView(context)
            
            // Set video size and position (right side of popup) - DỊCH PHẢI THÊM 2CM
            val videoAreaWidth = popupBox.width() * 0.65f
            val videoAreaHeight = popupBox.height() - 200f
            val videoAreaX = popupBox.right - videoAreaWidth - 30f
            val videoAreaY = popupBox.top + 100f
            
            // Video sẽ chiếm 96% diện tích của video area
            val videoWidth = videoAreaWidth.toInt()
            val videoHeight = videoAreaHeight.toInt()
            
            // Dịch video qua bên phải thêm 2cm (80px)
            val videoX = videoAreaX + videoAreaWidth - videoWidth + 2f // Thêm 80px (2cm)
            val videoY = videoAreaY + (videoAreaHeight - videoHeight) / 2f + 10f // Căn giữa theo chiều dọc
            
            videoView?.layoutParams = FrameLayout.LayoutParams(videoWidth, videoHeight)
            videoView?.x = videoX
            videoView?.y = videoY
            
            // Set video source from raw resources
            val videoPath = "android.resource://${context.packageName}/raw/$currentVideoName"
            videoView?.setVideoPath(videoPath)
            
            // THÊM: Set video loop để phát lặp lại
            videoView?.setOnCompletionListener { mediaPlayer ->
                mediaPlayer.start() // Phát lại từ đầu
            }
            
            // Add to parent container
            parentContainer?.addView(videoView)
            
            // Start playing
            videoView?.start()
            
        } catch (e: Exception) {
            println("Error starting video $currentVideoName: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun stopVideo() {
        videoView?.let { view ->
            view.stopPlayback()
            parentContainer?.removeView(view)
        }
        videoView = null
    }
    
    fun draw(canvas: Canvas) {
        if (!isActive) return
        
        // Draw overlay background
        canvas.drawRect(0f, 0f, GameConstants.SCREEN_WIDTH.toFloat(), GameConstants.SCREEN_HEIGHT.toFloat(), overlayPaint)
        
        // Draw popup box
        canvas.drawRoundRect(popupBox, 20f, 20f, popupBoxPaint)
        canvas.drawRoundRect(popupBox, 20f, 20f, borderPaint)
        
        // Draw title
        canvas.drawText("HƯỚNG DẪN", popupBox.centerX(), popupBox.top + 70f, titlePaint)
        
        // Draw tab buttons
        for (i in tabButtons.indices) {
            val tabButton = tabButtons[i]
            val isSelected = tabNames[i] == currentVideoName
            
            // Draw tab background
            canvas.drawRoundRect(tabButton, 10f, 10f, if (isSelected) selectedTabPaint else buttonPaint)
            canvas.drawRoundRect(tabButton, 10f, 10f, borderPaint)
            
            // Draw tab text
            val tabText = tabTitles[i]
            canvas.drawText(tabText, tabButton.centerX(), tabButton.centerY() + 15f, tabTextPaint)
        }
        
        // Draw video area background (right side) - để thấy được video area
        val videoAreaWidth = popupBox.width() * 0.65f + 13f
        val videoAreaHeight = popupBox.height() - 200f
        val videoAreaX = popupBox.right - videoAreaWidth - 21f
        val videoAreaY = popupBox.top + 100f
        
        val videoArea = RectF(videoAreaX, videoAreaY, videoAreaX + videoAreaWidth, videoAreaY + videoAreaHeight)
        canvas.drawRect(videoArea, Paint().apply { color = Color.BLACK })
        canvas.drawRect(videoArea, borderPaint)
        
        // Draw close button
        canvas.drawRoundRect(closeButton, 10f, 10f, buttonPaint)
        canvas.drawText("ĐÓNG", closeButton.centerX(), closeButton.centerY() + 15f, buttonTextPaint)
    }
    
    fun handleTouch(event: MotionEvent): Boolean {
        if (!isActive) return false
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                
                // Check close button
                if (closeButton.contains(x, y)) {
                    hide()
                    return true
                }
                
                // Check tab buttons
                for (i in tabButtons.indices) {
                    if (tabButtons[i].contains(x, y)) {
                        val newVideoName = tabNames[i]
                        if (newVideoName != currentVideoName) {
                            currentVideoName = newVideoName
                            startVideo() // Restart video with new selection
                        }
                        return true
                    }
                }
            }
        }
        return true
    }
}
