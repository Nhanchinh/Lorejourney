package com.example.game.story

object StoryContent {
    
    /**
     * Story data cho từng level
     */
    private val storyData = mapOf(
        // Level 1-1: Đoạn giới thiệu tổng quan về Elara và nhiệm vụ
        1 to StoryEntry(
            title = "Sự Khởi Đầu",
            segments = listOf(
                "Trong những giấc mơ, tiếng thì thầm cổ xưa vang vọng... Một sức mạnh bí ẩn đang thức tỉnh.",
                "ELARA: Tôi là Elara, một nhà trừ tà trẻ tuổi. Những lời thì thầm này... chúng dẫn tôi đến đây vì một lý do nào đó.",
                "Thế lực bóng tối đã bắt đầu thao túng thế giới, bóp méo luật vật lý và tạo ra những mê cung tư duy.",
                "ELARA: Viên Pha Lê Cân Bằng... đó là chìa khóa để tái lập trật tự. Nhưng nó đã bị chia thành ba mảnh và ẩn giấu ở ba vùng đất khác nhau.",
                "Hành trình của bạn bắt đầu tại Đồng Cỏ Khởi Nguồn - vùng đất từng trù phú nay bị xáo trộn bởi lời nguyền.",
                "ELARA: Tôi có thể cảm nhận được mảnh pha lê đầu tiên ở đâu đó trong khu vực này. Hãy cẩn thận... những thử thách phía trước sẽ không đơn giản."
            )
        ),
        
        // Level 1-2: Đi sâu vào rừng
        2 to StoryEntry(
            title = "Sâu Trong Đồng Cỏ",
            segments = listOf(
                "ELARA: Những con đường quen thuộc giờ đây trở nên lạ lẫm. Lời nguyền đã thay đổi mọi thứ.",
                "Cây cối mọc lên chắn lối, những tảng đá xuất hiện ở những nơi không nên có. Đây không còn là thế giới tự nhiên nữa.",
                "ELARA: Tôi bắt đầu nhớ ra... có lẽ tôi đã từng đến đây trước đây. Những ký ức mơ hồ đang dần trở lại."
            )
        ),
        
        // Level 1-3: Mê cung ngầm
        3 to StoryEntry(
            title = "Mê Cung Ngầm",
            segments = listOf(
                "ELARA: Những đường hầm này... chúng không có trong ký ức của tôi. Lời nguyền đã tạo ra chúng.",
                "Bóng tối dày đặc hơn ở đây. Mọi bước đi đều phải cẩn thận để tránh những cạm bẫy ẩn giấu.",
                "ELARA: Tôi có thể cảm nhận được sức mạnh của mảnh pha lê đang gần hơn. Nó đang ở đâu đó bên dưới."
            )
        ),
        
        // Level 1-4: Hang động pha lê
        4 to StoryEntry(
            title = "Hang Động Pha Lê Đầu Tiên",
            segments = listOf(
                "ELARA: Đây rồi... mảnh pha lê đầu tiên! Tôi có thể cảm nhận được sức mạnh của nó.",
                "Nhưng nó được bảo vệ bởi những thử thách phức tạp. Lời nguyền không muốn tôi tiếp cận nó.",
                "ELARA: Một ký ức... tôi thấy những người bảo vệ pha lê trong quá khứ. Có phải tôi có liên quan đến họ không?",
                "Mảnh pha lê đầu tiên đã được tìm thấy, nhưng còn hai mảnh nữa đang chờ đợi ở những vùng đất nguy hiểm hơn."
            )
        ),
        
        // Level 2-1: Bắt đầu thế giới 2 - Vùng Đất Băng Giá
        5 to StoryEntry(
            title = "Vào Vùng Đất Băng Giá",
            segments = listOf(
                "Mảnh pha lê đầu tiên đã chỉ đường đến vùng đất thứ hai - một thế giới băng tuyết lạnh giá.",
                "ELARA: Lạnh quá... nhưng đây không chỉ là cái lạnh thông thường. Đây là sự băng giá của lời nguyền.",
                "Mọi chuyển động ở đây bị chi phối bởi quán tính và gió băng. Thậm chí luật vật lý cũng khác biệt.",
                "ELARA: Mảnh pha lê thứ hai ở đâu đó trong những ngôi đền cổ bị chôn vùi. Tôi phải tìm thấy nó."
            )
        ),
        
        // Level 2-2: Cung điện mây
        6 to StoryEntry(
            title = "Cung Điện Trên Băng",
            segments = listOf(
                "ELARA: Những ngôi đền này... chúng cổ xưa hơn tôi tưởng. Ai đã xây dựng chúng?",
                "Các tảng băng di động tạo thành những mê cung tự nhiên. Lời nguyền đang kiểm tra khả năng của tôi.",
                "ELARA: Những ký ức lại xuất hiện... Tôi thấy những người bảo vệ pha lê chiến đấu với bóng tối trong quá khứ."
            )
        ),
        
        // Level 2-3: Tận cùng bão tố
        7 to StoryEntry(
            title = "Ngôi Đền Cuối Cùng",
            segments = listOf(
                "ELARA: Mảnh pha lê thứ hai! Nhưng... bóng tối đang nói chuyện với tôi.",
                "Những ảo ảnh xuất hiện, gieo rắc nghi ngờ về sứ mệnh của tôi. Liệu tôi có đang làm đúng?",
                "ELARA: Không... tôi không thể để bóng tối lừa phỉnh tôi. Mảnh pha lê này thuộc về tôi!",
                "Hai mảnh pha lê đã được tập hợp. Chỉ còn lại một mảnh cuối cùng... ở nơi nguy hiểm nhất."
            )
        ),
        
        // Level 3-1: Thành pháo bóng tối
        8 to StoryEntry(
            title = "Bước Vào Thành Pháo Bóng Tối",
            segments = listOf(
                "Hai mảnh pha lê dẫn đường đến pháo đài cuối cùng - một thành trì tăm tối lơ lửng giữa ánh sáng mờ ảo.",
                "ELARA: Đây rồi... nơi mà mọi thứ bắt đầu. Tôi có thể cảm nhận được bóng tối mạnh mẽ nhất ở đây.",
                "Hành lang ảo ảnh và thư viện nguội lạnh bao quanh. Thực tại và ảo ảnh đan xen khó phân biệt.",
                "ELARA: Những ký ức sâu kín nhất đang trở lại... Tôi phải đối mặt với sự thật về quá khứ của mình."
            )
        ),
        
        // Level 3-2 đến 3-7: Các thử thách trong thành pháo
        9 to StoryEntry(
            title = "Lò Rèn Của Bóng Tối",
            segments = listOf(
                "ELARA: Nơi này... đây là nơi lời nguyền được tạo ra. Tôi có thể cảm nhận được năng lượng tà ác.",
                "Những ký ức về tổ tiên tôi trở nên rõ ràng hơn. Họ đã từng chiến đấu ở đây... và thất bại.",
                "ELARA: Tôi hiểu rồi... lời nguyền không chỉ là một thực thể bên ngoài. Nó còn liên kết với nỗi sợ hãi trong tâm trí tôi."
            )
        ),
        
        // Level 3-8: Boss cuối
        15 to StoryEntry(
            title = "Đối Mặt Với Bóng Tối",
            segments = listOf(
                "ELARA: Mảnh pha lê cuối cùng! Nhưng bóng tối đang chờ đợi tôi.",
                "Sự thật được hé lộ: Lời nguyền sinh ra từ chính những ký ức bị lãng quên và nỗi sợ hãi của những người bảo vệ pha lê.",
                "ELARA: Tổ tiên tôi... họ đã cố gắng phong ấn nó nhưng thất bại. Giờ đây, trách nhiệm thuộc về tôi.",
                "Ba mảnh pha lê hợp nhất lại! Viên Pha Lê Cân Bằng đã được tái tạo!",
                "ELARA: Tôi sẽ sử dụng sức mạnh này để tái lập trật tự và xua tan lời nguyền một lần và mãi mãi!",
                "Ánh sáng tràn ngập thế giới... nhưng một giọng nói mơ hồ vang lên: 'Đây chưa phải là kết thúc...'",
                "ELARA: Hành trình này đã kết thúc, nhưng tôi biết rằng còn nhiều thử thách khác đang chờ đợi..."
            )
        )
    )
    
    /**
     * Lấy story cho level cụ thể
     */
    fun getStoryForLevel(levelId: Int): StoryEntry? {
        return storyData[levelId]
    }
    
    /**
     * Kiểm tra xem level có story không
     */
    fun hasStoryForLevel(levelId: Int): Boolean {
        return storyData.containsKey(levelId)
    }
    
    /**
     * Lấy danh sách tất cả level có story
     */
    fun getAllStoryLevels(): List<Int> {
        return storyData.keys.sorted()
    }
    
    /**
     * Data class cho story entry
     */
    data class StoryEntry(
        val title: String,
        val segments: List<String>
    )
}