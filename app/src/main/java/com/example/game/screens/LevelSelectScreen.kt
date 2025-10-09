
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
import com.example.game.music.MusicManager

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
        // World 1 - Phòng Thử Nghiệm Đá (Levels 1-4)
        Chapter(1, 1, 1, "1-1", "Phòng Khởi Đầu", "Dễ", 
            "BẮT ĐẦU HÀNH TRÌNH: Elara - cô gái trừ tà trẻ tuổi - tỉnh dậy trong một phòng bí ẩn với những khối đá cổ xưa. Thế lực bóng tối đã tạo ra những thử thách logic bóp méo tâm trí. Viên Pha Lê Cân Bằng bị chia thành 3 mảnh, mỗi mảnh được bảo vệ bởi mê cung giải đố. Phòng đầu tiên chỉ cần đẩy đá đơn giản...", 
            true, Color.parseColor("#66BB6A"), Color.parseColor("#4CAF50")),
        Chapter(2, 1, 2, "1-2", "Phòng Mê Trận Đá", "Dễ", 
            "THỬ THÁCH LOGIC: Phòng thứ hai phức tạp hơn với nhiều khối đá cần sắp xếp theo trật tự. Lời nguyền đã tạo ra những quy tắc kỳ lạ - đá chỉ di chuyển theo hướng nhất định. Elara bắt đầu nhớ lại ký ức mơ hồ về những người giải đố cổ đại...", 
            true, Color.parseColor("#81C784"), Color.parseColor("#66BB6A")),
        Chapter(3, 1, 3, "1-3", "Mê Cung Đá Cổ", "Bình thường", 
            "MÊ CUNG TƯ DUY: Mê cung đá phức tạp với nhiều lớp thử thách chồng chéo. Mỗi bước đi đều phải tính toán kỹ lưỡng - một sai lầm sẽ phải bắt đầu lại. Năng lượng của mảnh pha lê đầu tiên đang rất gần, ẩn sau bức tường đá cuối cùng...", 
            true, Color.parseColor("#FF9800"), Color.parseColor("#F57C00")),
        Chapter(4, 1, 4, "1-4", "Buồng Pha Lê Đầu Tiên", "Bình thường", 
            "MẢNH PHA LÊ ĐẦU TIÊN: Buồng cuối cùng với thử thách đẩy đá phức tạp nhất! Mảnh pha lê đầu tiên được bảo vệ bởi một trận đồ đá cổ xưa. Elara thấy ký ức về những người bảo vệ pha lê giải đố trong quá khứ. Liệu cô có liên quan đến họ? Còn 2 mảnh nữa ở những thế giới nguy hiểm hơn...", 
            true, Color.parseColor("#FFA726"), Color.parseColor("#FF9800")),
            
        // World 2 - Phòng Băng Trượt (Levels 5-7)
        Chapter(5, 2, 1, "2-1", "Phòng Băng Cơ Bản", "Khó", 
            "THẾ GIỚI BĂNG GIÁ: Mảnh pha lê đầu tiên mở cánh cửa dẫn đến phòng băng trượt. Sàn nhà phủ đầy băng, mọi thứ trượt không ngừng! Luật vật lý ở đây hoàn toàn khác - quán tính chi phối mọi chuyển động. Đá và Elara đều trượt cho đến khi va vào tường hoặc vật cản.", 
            false, Color.parseColor("#42A5F5"), Color.parseColor("#2196F3")),
        Chapter(6, 2, 2, "2-2", "Mê Cung Băng Phức Tạp", "Khó", 
            "MÊ CUNG TRƯỢT: Phòng băng với thiết kế mê cung phức tạp. Mỗi bước trượt phải tính toán chính xác - một sai lầm sẽ trượt vào chỗ bế tắc. Khối đá băng nặng hơn, trượt xa hơn. Ký ức về những người bảo vệ pha lê chinh phục băng giá trong quá khứ lại xuất hiện...", 
            false, Color.parseColor("#64B5F6"), Color.parseColor("#42A5F5")),
        Chapter(7, 2, 3, "2-3", "Buồng Pha Lê Băng", "Chuyên gia", 
            "THÁCH THỨC BĂNG CUỐI: Mảnh pha lê thứ hai nằm trong buồng băng khó nhất! Bóng tối bắt đầu 'nói chuyện' với Elara, tạo ảo giác khiến cô lú lẫn về hướng trượt. 'Liệu tôi có đang đi đúng hướng?' Elara phải vượt qua sự lừa phỉnh và hoàn thành trận đồ băng phức tạp. Hai mảnh pha lê tập hợp, chỉ còn một mảnh cuối ở thế giới tối tăm nhất...", 
            false, Color.parseColor("#90CAF9"), Color.parseColor("#64B5F6")),
            
        // World 3 - Phòng Băng Tối (Levels 8-15) - 8 maps
        Chapter(8, 3, 1, "3-1", "Phòng Băng Tối Cổng", "Huyền thoại", 
            "THẾ GIỚI BĂNG TỐI: Hai mảnh pha lê mở cánh cửa cuối cùng - phòng băng trong bóng tối tuyệt đối. Tầm nhìn bị hạn chế, chỉ thấy vài ô xung quanh! Băng trượt kết hợp với bóng tối tạo thử thách khó nhất. Phải dựa vào trí nhớ và logic thuần túy để vượt qua.", 
            false, Color.parseColor("#AB47BC"), Color.parseColor("#9C27B0")),
        Chapter(9, 3, 2, "3-2", "Mê Cung Tối Băng", "Huyền thoại", 
            "NGUỒN GỐC TÀ ÁC: Mê cung băng trong tăm tối tuyệt đối! Elara chỉ cảm nhận được vật thể khi va phải. Ký ức về tổ tiên cô trở nên rõ ràng - họ đã từng thất bại ở đây. Lời nguyền không chỉ là thử thách vật lý mà còn là thử thách tinh thần...", 
            false, Color.parseColor("#BA68C8"), Color.parseColor("#AB47BC")),
        Chapter(10, 3, 3, "3-3", "Phòng Băng Ảo Giác", "Huyền thoại", 
            "ẢO GIÁC BĂNG TỐI: Phòng băng tối với ảo giác lừa phỉnh. Đôi khi thấy ánh sáng nhấp nháy, nhưng đó chỉ là ảo ảnh của bóng tối. Khối đá băng ẩn hiện trong đêm tối. Sự thật về quá khứ dần được hé lộ qua từng bước trượt mù quáng.", 
            false, Color.parseColor("#CE93D8"), Color.parseColor("#BA68C8")),
        Chapter(11, 3, 4, "3-4", "Buồng Băng Tối Tuyệt", "Thần thoại", 
            "TỐI TUYỆT ĐỐI: Trong buồng này, tầm nhìn gần như bằng không. Elara chỉ nghe thấy tiếng băng cọ sát. Cô khám phá sự thật: lời nguyền sinh ra từ nỗi sợ hãi trong bóng tối! Phải chinh phục nỗi sợ để tiếp tục...", 
            false, Color.parseColor("#E1BEE7"), Color.parseColor("#CE93D8")),
        Chapter(12, 3, 5, "3-5", "Phòng Ký Ức Băng", "Thần thoại", 
            "BÍ MẬT CUỐI CÙNG: Phòng băng tối với những mảnh ký ức hiện lên như ánh sáng lờ mờ. Ở đây, Elara khám phá hoàn toàn về nguồn gốc dòng tộc mình và lý do tại sao chỉ có cô mới có thể phá vỡ lời nguyền băng tối này.", 
            false, Color.parseColor("#F3E5F5"), Color.parseColor("#E1BEE7")),
        Chapter(13, 3, 6, "3-6", "Mê Cung Băng Thế Hệ", "Thần thoại", 
            "SỰ THẬT VỀ DÒNG TỘCH: Mê cung băng tối tiết lộ sự thật choáng váng - Elara là hậu duệ trực tiếp của những người bảo vệ pha lê! Những ký ức bị lãng quên của tổ tiên cô trở về qua từng bước trượt trên băng trong đêm tối.", 
            false, Color.parseColor("#E040FB"), Color.parseColor("#D500F9")),
        Chapter(14, 3, 7, "3-7", "Phòng Băng Chung Kết", "Thần thánh", 
            "THÁCH THỨC CUỐI CÙNG: Phòng băng tối khổng lồ - nơi mảnh pha lê cuối cùng chờ đợi! Elara chuẩn bị tinh thần cho thử thách trượt băng trong tối tăm khó nhất. Bóng tối đã chờ đợi khoảnh khắc này từ lâu. Liệu cô có đủ sức mạnh và trí tuệ để hoàn thành?", 
            false, Color.parseColor("#C51162"), Color.parseColor("#E040FB")),
        Chapter(15, 3, 8, "3-8", "Buồng Pha Lê Cuối", "Thần thánh", 
            "HOÀN THÀNH SỨ MỆNH: Buồng băng tối cuối cùng với trận đồ phức tạp nhất! Ba mảnh pha lê hợp nhất trong ánh sáng xanh rực rỡ xé toạc bóng tối. Viên Pha Lê Cân Bằng được tái tạo! Elara chinh phục cả băng lẫn tối, mang ánh sáng trở lại. Nhưng giọng nói vang lên: 'Đây chưa phải là kết thúc...'", 
            false, Color.parseColor("#AD1457"), Color.parseColor("#C51162"))
    )
    
    // Get chapters for currently selected world
    private val chapters: List<Chapter> get() = allChapters.filter { it.worldId == selectedWorld }
    
    // World titles for display
    private val worldTitles = mapOf(
        1 to "PHÒNG THỬ NGHIỆM ĐÁ",
        2 to "PHÒNG BĂNG TRƯỢT", 
        3 to "PHÒNG BĂNG TỐI"
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
        MusicManager.playWaitingHallMusic(context)
        
        // Auto-select next level if coming from completion
        if (gameStateManager.lastCompletedLevel >= 0) {
            val nextLevelId = gameStateManager.lastCompletedLevel + 1
            val nextChapter = allChapters.find { it.id == nextLevelId }
            if (nextChapter != null) {
                // Nếu level tiếp theo ở world khác, chuyển world
                if (nextChapter.worldId != selectedWorld) {
                    gameStateManager.selectedWorld = nextChapter.worldId
                }
                // Tìm index trong chapters của world MỚI (sử dụng nextChapter.worldId)
                val chaptersInNewWorld = allChapters.filter { it.worldId == nextChapter.worldId }
                val nextChapterIndex = chaptersInNewWorld.indexOfFirst { it.id == nextLevelId }
                if (nextChapterIndex >= 0) {
                    selectedChapter = nextChapterIndex
                }
                println("🎯 Auto-selected level $nextLevelId in world ${nextChapter.worldId}, chapter index: $nextChapterIndex")
            }
            gameStateManager.lastCompletedLevel = -1
        }
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
                MusicManager.playSound(context, "torch")
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
                    MusicManager.playSound(context, "torch")
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
                MusicManager.playSound(context, "torch")
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