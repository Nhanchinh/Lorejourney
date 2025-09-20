
package com.example.game.screens

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.example.game.GameConstants
import com.example.game.R
import com.example.game.animation.AnimationManager
import com.example.game.core.GameStateManager
import com.example.game.SaveManager
import android.graphics.LinearGradient
import android.graphics.Shader

class LevelSelectScreen(
    private val gameStateManager: GameStateManager,
    private val context: Context,
    private val animationManager: AnimationManager
) : Screen() {
    
    private var backgroundBitmap: Bitmap? = null
    
    // Chapter data - ĐÚNG THEO DESIGN GỐC
    private data class Chapter(
        val id: Int,
        val name: String,
        val title: String,
        val difficulty: String,
        val description: String,
        val isUnlocked: Boolean = true,
        val primaryColor: Int,
        val secondaryColor: Int
    )
    
    private val chapters = listOf(
        Chapter(1, "Chapter 1", "The Beginning", "Easy", 
            "Your journey starts in a mysterious forest. Learn the basic controls and discover ancient secrets hidden in the shadows.", 
            true, Color.parseColor("#66BB6A"), Color.parseColor("#4CAF50")), // Xanh lá sáng
        Chapter(2, "Chapter 2", "Underground Maze", "Normal", 
            "Venture into the dark underground tunnels. Navigate through complex puzzles and avoid dangerous traps.", 
            true, Color.parseColor("#FF9800"), Color.parseColor("#F57C00")), // Cam sáng
        Chapter(3, "Chapter 3", "Crystal Caverns", "Hard", 
            "Explore the magical crystal caves. Use the power of crystals to solve mind-bending puzzles.", 
            true, Color.parseColor("#EF5350"), Color.parseColor("#E53935")), // Đỏ sáng
        Chapter(4, "Chapter 4", "Sky Temple", "Expert", 
            "Ascend to the floating temple in the clouds. Master advanced puzzle mechanics to reach the summit.", 
            false, Color.parseColor("#BDBDBD"), Color.parseColor("#9E9E9E")), // Xám locked
        Chapter(5, "Chapter 5", "Final Trial", "Expert", 
            "Face the ultimate challenge in the Final Trial, where all your skills will be tested to their limits. This legendary realm combines the mysteries of all previous chapters into one epic conclusion. Only true masters can claim victory in this final confrontation.", 
            false, Color.parseColor("#9575CD"), Color.parseColor("#7E57C2")), // Tím locked
        Chapter(6, "Chapter 6", "Sprite Realm", "Master", 
            "Enter the mystical Sprite Realm, where reality bends and ancient art comes to life. This special chapter uses advanced sprite-based graphics to create a unique visual experience. Master the art of visual puzzles in this stunning new dimension.", 
            true, Color.parseColor("#00BCD4"), Color.parseColor("#0097A7")) // Cyan cho map6
    )
    
    private var selectedChapter = 5 // Default Chapter 6 (Sprite Realm) để showcase map6
    private val chapterButtons = mutableListOf<RectF>()
    private val backButton = RectF()
    private val playButton = RectF()
    private val descriptionRect = RectF()
    
    // Paint objects - SỬA TITLE PAINT
    private val titlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 80f  // Giảm một chút để đảm bảo hiển thị
        isFakeBoldText = true
       
        alpha = 255  // Đảm bảo alpha = 255
    }
    
    private val chapterTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 44f  // TO HƠN NỮA từ 36f lên 44f
        isFakeBoldText = true
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    
    private val backButtonPaint = Paint().apply {
        isAntiAlias = true
    }
    
    private val backTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 34f  // TO HƠN NỮA từ 28f lên 34f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    // THÊM BORDER PAINT CHO TẤT CẢ BUTTONS
    private val buttonBorderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.WHITE
        alpha = 120  // TRẮNG NHẠT
    }
    
    private val descriptionBgPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#0D1B2A")
        alpha = 180
    }
    
    private val descriptionBorderPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.parseColor("#00E5FF")
        setShadowLayer(8f, 0f, 0f, Color.parseColor("#00E5FF"))
    }
    
    private val chapterTitlePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
        textAlign = Paint.Align.LEFT
        textSize = 50f  // TO HƠN NỮA từ 42f lên 50f
        isFakeBoldText = true
        setShadowLayer(3f, 1f, 1f, Color.BLACK)
    }
    
    private val difficultyPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFB300")
        textAlign = Paint.Align.LEFT
        textSize = 32f  // TO HƠN NỮA từ 26f lên 32f
        isFakeBoldText = true
    }
    
    private val descriptionTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        textSize = 28f  // TO HƠN NỮA từ 22f lên 28f
        alpha = 220
    }
    
    private val playButtonPaint = Paint().apply {
        isAntiAlias = true
    }
    
    private val playTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 38f  // TO HƠN NỮA từ 32f lên 38f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    private val lockIconPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFD700")
        textAlign = Paint.Align.CENTER
        textSize = 36f  // TO HƠN NỮA từ 30f lên 36f
    }

    // Thêm các Paint objects cho locked levels
    private val lockedButtonPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#757575")
    }

    private val lockedTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#BDBDBD")
        textAlign = Paint.Align.CENTER
        textSize = 44f
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.BLACK)
    }
    
    init {
        loadBackground()
        updateUIPositions()
    }
    
    private fun loadBackground() {
        try {
            backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.background_img)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun update(deltaTime: Long) {
        // No complex animations needed
    }
    
    override fun draw(canvas: Canvas) {
        // Draw background
        backgroundBitmap?.let { bg ->
            val scaleX = canvas.width.toFloat() / bg.width
            val scaleY = canvas.height.toFloat() / bg.height
            val scale = maxOf(scaleX, scaleY)
            
            canvas.save()
            canvas.scale(scale, scale)
            canvas.drawBitmap(bg, 0f, 0f, null)
            canvas.restore()
        }
        
        // TITLE dịch xuống 0.2cm (khoảng 8px)
        val centerX = GameConstants.SCREEN_WIDTH / 2f
        val titleY = 68f  // Dịch từ 60f xuống 68f (8px ≈ 0.2cm)
        
        // VẼ TITLE
        canvas.drawText("SELECT CHAPTER", centerX, titleY, titlePaint)
        
        // Back button
        drawBackButton(canvas)
        
        // Chapter buttons
        drawChapterButtons(canvas)
        
        // Description panel
        drawDescriptionPanel(canvas)
        
        // Play button
        drawPlayButton(canvas)
    }
    
    private fun drawBackButton(canvas: Canvas) {
        // Gradient cho back button
        val gradient = LinearGradient(
            backButton.left, backButton.top, backButton.left, backButton.bottom,
            intArrayOf(
                Color.parseColor("#546E7A"),
                Color.parseColor("#37474F"),
                Color.parseColor("#263238")
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        backButtonPaint.shader = gradient
        
        // Bo góc ÍT HƠN - vuông vắn hơn
        canvas.drawRoundRect(backButton, 8f, 8f, backButtonPaint)  // Giảm từ 12f xuống 8f
        canvas.drawRoundRect(backButton, 8f, 8f, buttonBorderPaint)
        
        canvas.drawText("← BACK", backButton.centerX(), backButton.centerY() + 8f, backTextPaint)
    }
    
    private fun drawChapterButtons(canvas: Canvas) {
        for (i in chapterButtons.indices) {
            val level = i + 1
            val button = chapterButtons[i]
            val isSelected = i == selectedChapter
            
            // Kiểm tra xem level có unlock không
            val isUnlocked = level <= GameConstants.MAX_UNLOCKED_LEVEL
            
            // Gradient cho button background - SỬ DỤNG #00CAFF LÀM CHỦ ĐẠO
            val buttonGradient = if (isUnlocked) {
                if (isSelected) {
                    // Gradient cyan sáng cho selected button
                    LinearGradient(
                        button.left, button.top, button.left, button.bottom,
                        intArrayOf(
                            Color.parseColor("#00FFDE"), // Cyan sáng nhất
                            Color.parseColor("#00CAFF"), // Cyan chủ đạo
                            Color.parseColor("#00A8E8")  // Cyan đậm hơn
                        ),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                } else {
                    // Gradient cyan chủ đạo cho unlocked button
                    LinearGradient(
                        button.left, button.top, button.left, button.bottom,
                        intArrayOf(
                            Color.parseColor("#00CAFF"), // Cyan chủ đạo
                            Color.parseColor("#0099CC"), // Cyan đậm hơn
                            Color.parseColor("#0065F8")  // Xanh dương đậm
                        ),
                        floatArrayOf(0f, 0.5f, 1f),
                        Shader.TileMode.CLAMP
                    )
                }
            } else {
                // Gradient cho locked button - xám vừa
                LinearGradient(
                    button.left, button.top, button.left, button.bottom,
                    intArrayOf(
                        Color.parseColor("#BDBDBD"),
                        Color.parseColor("#9E9E9E"),
                        Color.parseColor("#757575")
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            
            // Tạo paint cho button
            val buttonPaint = Paint().apply {
                isAntiAlias = true
                shader = buttonGradient
                alpha = if (isSelected && isUnlocked) 200 else 220 // Vừa phải
            }
            
            // Chọn text paint
            val textPaint = if (isUnlocked) {
                chapterTextPaint
            } else {
                lockedTextPaint
            }
            
            // Vẽ button với bo góc mềm mại hơn
            canvas.drawRoundRect(button, 25f, 25f, buttonPaint)
            
            // Vẽ border - VIỀN TRẮNG NHẠT TRONG SUỐT CHO SELECTED
            if (isSelected && isUnlocked) {
                // Border trắng nhạt trong suốt cho selected button
                val selectedBorderPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                    color = Color.parseColor("#FFFFFF") // Trắng
                    alpha = 140 // Nhạt hơn (từ 180 xuống 140)
                    setShadowLayer(6f, 0f, 0f, Color.parseColor("#FFFFFF")) // Shadow cũng nhạt hơn
                }
                canvas.drawRoundRect(button, 25f, 25f, selectedBorderPaint)
            } else {
                // Border bình thường cho các nút khác
                val normalBorderPaint = Paint().apply {
                    isAntiAlias = true
                    style = Paint.Style.STROKE
                    strokeWidth = 2f
                    color = Color.parseColor("#00CAFF") // Cyan chủ đạo
                    alpha = 180
                }
                canvas.drawRoundRect(button, 25f, 25f, normalBorderPaint)
            }
            
            // Vẽ text với shadow đẹp hơn
            val text = if (isUnlocked) level.toString() else "🔒"
            canvas.drawText(
                text,
                button.centerX(),
                button.centerY() + 8f,
                textPaint
            )
        }
    }
    
    private fun drawDescriptionPanel(canvas: Canvas) {
        val selectedLevel = selectedChapter + 1
        val selectedChapterData = chapters[selectedChapter]
        val isUnlocked = selectedLevel <= GameConstants.MAX_UNLOCKED_LEVEL // SỬA DÒNG NÀY
        
        // Background trong suốt với gradient subtle
        val bgGradient = LinearGradient(
            descriptionRect.left, descriptionRect.top,
            descriptionRect.left, descriptionRect.bottom,
            intArrayOf(
                Color.parseColor("#263238"),
                Color.parseColor("#37474F")
            ),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        descriptionBgPaint.shader = bgGradient
        
        canvas.drawRoundRect(descriptionRect, 15f, 15f, descriptionBgPaint)
        canvas.drawRoundRect(descriptionRect, 15f, 15f, buttonBorderPaint)
        
        // Level title
        val levelTitle = if (isUnlocked) {
            "LEVEL ${selectedLevel}: ${selectedChapterData.title}"
        } else {
            "LEVEL ${selectedLevel}: LOCKED"
        }
        
        canvas.drawText(
            levelTitle,
            descriptionRect.centerX(),
            descriptionRect.top + 60f,
            chapterTitlePaint
        )
        
        // Difficulty - với spacing cho text to hơn
        canvas.drawText(
            "Difficulty: ${selectedChapterData.difficulty}",
            descriptionRect.left + 30f,
            descriptionRect.top + 100f,  // Điều chỉnh spacing
            difficultyPaint
        )
        
        // Description
        val description = if (isUnlocked) {
            selectedChapterData.description
        } else {
            "Complete previous levels to unlock this challenge."
        }
        
        drawWrappedText(
            canvas,
            description,
            descriptionRect.left + 30f,
            descriptionRect.top + 140f,
            descriptionRect.width() - 60f,
            descriptionTextPaint
        )
    }
    
    private fun drawPlayButton(canvas: Canvas) {
        val selectedLevel = selectedChapter + 1
        val isPlayable = selectedLevel <= GameConstants.MAX_UNLOCKED_LEVEL // SỬA DÒNG NÀY

        // Gradient cho play button
        val gradient = if (isPlayable) {
            LinearGradient(
                playButton.left, playButton.top, playButton.left, playButton.bottom,
                intArrayOf(
                    Color.parseColor("#66BB6A"),
                    Color.parseColor("#4CAF50"),
                    Color.parseColor("#388E3C")
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
        } else {
            LinearGradient(
                playButton.left, playButton.top, playButton.left, playButton.bottom,
                intArrayOf(
                    Color.parseColor("#616161"),
                    Color.parseColor("#424242")
                ),
                floatArrayOf(0f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        
        playButtonPaint.shader = gradient
        
        // Bo góc ÍT HƠN - vuông vắn hơn
        canvas.drawRoundRect(playButton, 8f, 8f, playButtonPaint)  // Giảm từ 25f xuống 8f
        canvas.drawRoundRect(playButton, 8f, 8f, buttonBorderPaint)
        
        val playText = if (isPlayable) "PLAY" else "LOCKED"
        playTextPaint.alpha = if (isPlayable) 255 else 150
        canvas.drawText(playText, playButton.centerX(), playButton.centerY() + 15f, playTextPaint)
    }
    
    private fun drawWrappedText(canvas: Canvas, text: String, x: Float, y: Float, maxWidth: Float, paint: Paint) {
        val words = text.split(" ")
        var line = ""
        var currentY = y
        
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val testWidth = paint.measureText(testLine)
            
            if (testWidth > maxWidth && line.isNotEmpty()) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += paint.textSize + 15f  // TO HƠN line spacing cho text to hơn
            } else {
                line = testLine
            }
        }
        
        if (line.isNotEmpty()) {
            canvas.drawText(line, x, currentY, paint)
        }
    }
    
    override fun handleTouch(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            
            println("Touch detected - Current max unlocked: ${GameConstants.MAX_UNLOCKED_LEVEL}")
            
            if (backButton.contains(x, y)) {
                gameStateManager.changeState(GameConstants.STATE_MENU)
                return true
            }
            
            // Check chapter button clicks
            for (i in chapterButtons.indices) {
                val level = i + 1
                val button = chapterButtons[i]
                
                if (button.contains(x, y)) {
                    selectedChapter = i
                    println("Selected chapter $i (Level $level) - Unlocked: ${level <= GameConstants.MAX_UNLOCKED_LEVEL}")
                    return true
                }
            }
            
            // Play button
            if (playButton.contains(x, y)) {
                val selectedLevel = selectedChapter + 1
                if (selectedLevel <= GameConstants.MAX_UNLOCKED_LEVEL) {
                    println("Starting level $selectedLevel")
                    gameStateManager.startLevel(selectedLevel)
                } else {
                    println("Cannot start locked level $selectedLevel (max unlocked: ${GameConstants.MAX_UNLOCKED_LEVEL})")
                }
                return true
            }
        }
        
        return false
    }
    
    private fun updateUIPositions() {
        if (GameConstants.SCREEN_WIDTH <= 0 || GameConstants.SCREEN_HEIGHT <= 0) return
        
        val screenW = GameConstants.SCREEN_WIDTH.toFloat()
        val screenH = GameConstants.SCREEN_HEIGHT.toFloat()
        
        chapterButtons.clear()
        
        // Back button - điều chỉnh theo title mới
        val titleCenterY = 68f  // Cập nhật theo titleY mới
        val backButtonHeight = 50f
        val backButtonY = titleCenterY - backButtonHeight / 2f
        
        backButton.set(15f, backButtonY, 170f, backButtonY + backButtonHeight)
        
        // Chapter buttons - DỊCH SANG PHẢI 0.5cm (khoảng 20px)
        val buttonWidth = screenW * 0.28f
        val buttonHeight = 95f
        val startY = 138f  // Dịch xuống một chút theo title
        val margin = 20f
        val leftMargin = 40f  // DỊCH SANG PHẢI từ 20f lên 40f (20px ≈ 0.5cm)
        
        for (i in chapters.indices) {
            val y = startY + i * (buttonHeight + margin)
            chapterButtons.add(RectF(leftMargin, y, leftMargin + buttonWidth, y + buttonHeight))
        }
        
        // Description panel - điều chỉnh vị trí theo chapter buttons mới
        val descX = screenW * 0.37f  // Dịch sang phải một chút vì chapter buttons dịch phải
        val descY = 138f  // Cùng với startY của chapter buttons
        val descWidth = screenW * 0.58f  // Giảm width một chút vì chapter buttons chiếm space
        val descHeight = screenH * 0.55f
        
        descriptionRect.set(descX, descY, descX + descWidth, descY + descHeight)
        
        // Play button - giữ nguyên
        val playButtonWidth = 260f
        val playButtonHeight = 90f
        val playX = (screenW - playButtonWidth) / 2f
        val playY = screenH - playButtonHeight - 40f
        
        playButton.set(playX, playY, playX + playButtonWidth, playY + playButtonHeight)
    }
    
    override fun onScreenSizeChanged(width: Int, height: Int) {
        updateUIPositions()
    }
}