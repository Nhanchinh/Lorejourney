
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
    
    // Get selected world from gameStateManager
    private val selectedWorld: Int get() = gameStateManager.selectedWorld
    
    // Chapter data - UPDATED WITH WORLD-BASED STRUCTURE
    private data class Chapter(
        val id: Int,
        val worldId: Int,
        val levelInWorld: Int,
        val name: String,
        val title: String,
        val difficulty: String,
        val description: String,
        val isUnlocked: Boolean = true,
        val primaryColor: Int,
        val secondaryColor: Int
    )
    
    // All chapters data - organized by worlds
    private val allChapters = listOf(
        // World 1 - Đồng Cỏ Khởi Nguồn (Levels 1-4)
        Chapter(1, 1, 1, "1-1", "Vào Đồng Cỏ Khởi Nguồn", "Dễ", 
            "Elara bước vào vùng đất từng trù phú nay bị xáo trộn bởi lời nguyền. Những con đường quen thuộc giờ bị cây cối và đá tảng chắn lối. Hành trình tìm mảnh pha lê đầu tiên bắt đầu.", 
            true, Color.parseColor("#66BB6A"), Color.parseColor("#4CAF50")),
        Chapter(2, 1, 2, "1-2", "Phế Tích Làng Cổ", "Dễ", 
            "Khám phá những phế tích của làng cổ, nơi ẩn chứa ký ức của những cư dân đã biến mất. Elara bắt đầu nhớ ra những mảnh ký ức mơ hồ về vùng đất này.", 
            true, Color.parseColor("#81C784"), Color.parseColor("#66BB6A")),
        Chapter(3, 1, 3, "1-3", "Mê Cung Ngầm Bị Lãng Quên", "Bình thường", 
            "Đi vào những đường hầm ngầm mà lời nguyền đã tạo ra. Những bóng tối dày đặc và cạm bẫy ẩn giấu kiểm tra khả năng của Elara trong hành trình tìm mảnh pha lê.", 
            true, Color.parseColor("#FF9800"), Color.parseColor("#F57C00")),
        Chapter(4, 1, 4, "1-4", "Hầm Mộ Pha Lê Đầu Tiên", "Bình thường", 
            "Tìm thấy mảnh pha lê đầu tiên trong một hầm mộ bị lãng quên. Elara khám phá manh mối về mối liên hệ giữa lời nguyền và tâm trí con người, cùng với ký ức về tổ tiên mình.", 
            true, Color.parseColor("#FFA726"), Color.parseColor("#FF9800")),
            
        // World 2 - Vùng Đất Băng Giá (Levels 5-7)
        Chapter(5, 2, 1, "2-1", "Bước Vào Vùng Băng Giá", "Khó", 
            "Elara tiến vào vùng đất băng tuyết lạnh giá, nơi mọi chuyển động bị chi phối bởi quán tính và gió băng. Hồ băng trơn trượt tạo thành những thử thách mới.", 
            false, Color.parseColor("#42A5F5"), Color.parseColor("#2196F3")),
        Chapter(6, 2, 2, "2-2", "Ngôi Đền Bị Chôn Vùi", "Khó", 
            "Khám phá những ngôi đền cổ bị chôn vùi trong băng tuyết. Các tảng băng di động tạo thành mê cung tự nhiên, nơi Elara tìm thấy manh mối về nguồn gốc lời nguyền.", 
            false, Color.parseColor("#64B5F6"), Color.parseColor("#42A5F5")),
        Chapter(7, 2, 3, "2-3", "Mảnh Pha Lê Thứ Hai", "Chuyên gia", 
            "Tìm thấy mảnh pha lê thứ hai trong ngôi đền cổ. Bóng tối bắt đầu 'nói chuyện' với Elara qua ảo ảnh, gieo rắc nghi ngờ về sứ mệnh của cô.", 
            false, Color.parseColor("#90CAF9"), Color.parseColor("#64B5F6")),
            
        // World 3 - Thành Pháo Bóng Tối (Levels 8-15) - 8 maps
        Chapter(8, 3, 1, "3-1", "Cổng Vào Thành Pháo", "Huyền thoại", 
            "Bước vào pháo đài tăm tối lơ lửng giữa ánh sáng mờ ảo. Nơi bóng tối và ánh sáng đan xen, tạo ra các hành lang ảo ảnh khó phân biệt thực tế.", 
            false, Color.parseColor("#AB47BC"), Color.parseColor("#9C27B0")),
        Chapter(9, 3, 2, "3-2", "Lò Rèn Của Bóng Tối", "Huyền thoại", 
            "Khám phá nơi lời nguyền được tạo ra. Elara đối mặt với những ký ức về tổ tiên mình - những người từng chiến đấu ở đây nhưng thất bại.", 
            false, Color.parseColor("#BA68C8"), Color.parseColor("#AB47BC")),
        Chapter(10, 3, 3, "3-3", "Thư Viện Nguội Lạnh", "Huyền thoại", 
            "Đi qua thư viện chứa đựng những bí mật cổ xưa. Ký ức về quá khứ và sự thật về bản chất của lời nguyền dần được hé lộ.", 
            false, Color.parseColor("#CE93D8"), Color.parseColor("#BA68C8")),
        Chapter(11, 3, 4, "3-4", "Hành Lang Ảo Ảnh", "Thần thoại", 
            "Trong những hành lang đầy ảo ảnh, Elara khám phá rằng lời nguyền không chỉ là thực thể bên ngoài mà còn liên kết với nỗi sợ hãi trong tâm trí cô.", 
            false, Color.parseColor("#E1BEE7"), Color.parseColor("#CE93D8")),
        Chapter(12, 3, 5, "3-5", "Tầng Thẳm Sâu Nhất", "Thần thoại", 
            "Đi sâu vào tầng thẳm của thành pháo, nơi những bí mật sâu kín nhất về tổ tiên Elara và nguồn gốc lời nguyền được tiết lộ hoàn toàn.", 
            false, Color.parseColor("#F3E5F5"), Color.parseColor("#E1BEE7")),
        Chapter(13, 3, 6, "3-6", "Buồng Ký Ức", "Thần thoại", 
            "Trong buồng chứa những ký ức bị lãng quên, Elara phải đối mặt với sự thật về mối liên hệ huyết thống giữa cô và những người bảo vệ pha lê.", 
            false, Color.parseColor("#E040FB"), Color.parseColor("#D500F9")),
        Chapter(14, 3, 7, "3-7", "Tháp Trung Tâm", "Thần thánh", 
            "Tiến đến tháp trung tâm của thành pháo, nơi mảnh pha lê cuối cùng được bảo vệ. Chuẩn bị cho cuộc đối đầu cuối cùng với bản chất của bóng tối.", 
            false, Color.parseColor("#C51162"), Color.parseColor("#E040FB")),
        Chapter(15, 3, 8, "3-8", "Đối Mặt Bóng Tối", "Thần thánh", 
            "Cuộc chiến cuối cùng! Elara tái hợp Viên Pha Lê Cân Bằng và đối mặt với bóng tối. Liệu cô có thể xua tan lời nguyền và mang ánh sáng trở lại thế giới?", 
            false, Color.parseColor("#AD1457"), Color.parseColor("#C51162"))
    )
    
    // Get chapters for currently selected world
    private val chapters: List<Chapter> get() = allChapters.filter { it.worldId == selectedWorld }
    
    // World titles for display
    private val worldTitles = mapOf(
        1 to "Rừng Huyền Bí",
        2 to "Đền Trên Mây", 
        3 to "Vương Quốc Vũ Trụ"
    )
    
    private var selectedChapter = 0 // Always start with first chapter in selected world
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
        textAlign = Paint.Align.CENTER
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
        val titleY = 100f  // Dịch từ 60f xuống 68f (8px ≈ 0.2cm)
        
        // VẼ TITLE WITH WORLD INFO
        val worldTitle = worldTitles[selectedWorld] ?: "Unknown World"
        canvas.drawText("$worldTitle - CHỌN CẤP ĐỘ", centerX, titleY, titlePaint)
        
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
        
        canvas.drawText("← QUAY LẠI", backButton.centerX(), backButton.centerY() + 8f, backTextPaint)
    }
    
    private fun drawChapterButtons(canvas: Canvas) {
        for (i in chapterButtons.indices) {
            if (i >= chapters.size) break // Safety check
            
            val chapter = chapters[i]
            val level = chapter.id // Use actual level ID from chapter
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
            
            // Vẽ text với shadow đẹp hơn - DISPLAY LEVEL NAME (1-1, 1-2, etc.)
            val text = if (isUnlocked) chapter.name else "🔒"
            canvas.drawText(
                text,
                button.centerX(),
                button.centerY() + 8f,
                textPaint
            )
        }
    }
    
    private fun drawDescriptionPanel(canvas: Canvas) {
        if (selectedChapter >= chapters.size) return // Safety check
        
        val selectedChapterData = chapters[selectedChapter]
        val selectedLevel = selectedChapterData.id
        val isUnlocked = selectedLevel <= GameConstants.MAX_UNLOCKED_LEVEL
        
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
        
        // Level title with world-level format
        val levelTitle = if (isUnlocked) {
            "CẤP ĐỘ ${selectedChapterData.name}: ${selectedChapterData.title}"
        } else {
            "CẤP ĐỘ ${selectedChapterData.name}: LOCKED"
        }
        
        canvas.drawText(
            levelTitle,
            descriptionRect.centerX(),
            descriptionRect.top + 60f,
            chapterTitlePaint
        )
        
        // Difficulty - với spacing cho text to hơn
        canvas.drawText(
            "Độ khó: ${selectedChapterData.difficulty}",
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
        if (selectedChapter >= chapters.size) return // Safety check
        
        val selectedChapterData = chapters[selectedChapter]
        val selectedLevel = selectedChapterData.id
        val isPlayable = selectedLevel <= GameConstants.MAX_UNLOCKED_LEVEL

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
        
        val playText = if (isPlayable) "CHƠI" else "KHÓA"
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
                gameStateManager.changeState(GameConstants.STATE_WORLD_SELECT)
                return true
            }
            
            // Check chapter button clicks
            for (i in chapterButtons.indices) {
                if (i >= chapters.size) break // Safety check
                
                val chapter = chapters[i]
                val level = chapter.id
                val button = chapterButtons[i]
                
                if (button.contains(x, y)) {
                    selectedChapter = i
                    println("Selected chapter $i (Level ${chapter.name} - ID: $level) - Unlocked: ${level <= GameConstants.MAX_UNLOCKED_LEVEL}")
                    return true
                }
            }
            
            // Play button
            if (playButton.contains(x, y)) {
                if (selectedChapter < chapters.size) {
                    val selectedChapterData = chapters[selectedChapter]
                    val selectedLevel = selectedChapterData.id
                    if (selectedLevel <= GameConstants.MAX_UNLOCKED_LEVEL) {
                        println("Starting level $selectedLevel")
                        gameStateManager.startLevel(selectedLevel)
                    } else {
                        println("Cannot start locked level $selectedLevel (max unlocked: ${GameConstants.MAX_UNLOCKED_LEVEL})")
                    }
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
        
        backButton.set(15f, backButtonY, 250f, backButtonY + backButtonHeight)
        
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