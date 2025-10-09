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
                "ELARA: Tôi là Elara, một nhà trừ tà trẻ tuổi. Tôi tỉnh dậy trong một phòng đá kỳ lạ với những khối đá cổ xưa.",
                "Thế lực bóng tối đã bắt đầu thao túng thế giới, bóp méo luật vật lý và tạo ra những mê cung logic thử thách tâm trí.",
                "ELARA: Viên Pha Lê Cân Bằng... đó là chìa khóa để tái lập trật tự. Nhưng nó đã bị chia thành ba mảnh và ẩn giấu trong ba thế giới thử thách khác nhau.",
                "Hành trình của bạn bắt đầu tại Phòng Thử Nghiệm Đá - nơi những trận đồ đá cổ xưa chờ đợi người giải mã.",
                "ELARA: Tôi có thể cảm nhận được mảnh pha lê đầu tiên ở đâu đó sau những bức tường đá này. Hãy cẩn thận... mỗi bước đi đều cần tư duy logic."
            )
        ),
        
        // Level 1-2: Phòng phức tạp hơn
        2 to StoryEntry(
            title = "Phòng Mê Trận Đá",
            segments = listOf(
                "ELARA: Phòng thứ hai phức tạp hơn nhiều. Những khối đá này tuân theo quy tắc kỳ lạ.",
                "Mỗi khối đá chỉ di chuyển theo hướng nhất định cho đến khi va phải vật cản. Đây là thử thách logic thuần túy.",
                "ELARA: Tôi bắt đầu nhớ ra... có lẽ tổ tiên tôi đã từng giải những trận đồ tương tự. Những ký ức mơ hồ đang dần trở lại."
            )
        ),
        
        // Level 1-3: Mê cung đá
        3 to StoryEntry(
            title = "Mê Cung Đá Cổ",
            segments = listOf(
                "ELARA: Phòng này... là một mê cung đá thực thụ với nhiều lớp thử thách chồng chéo.",
                "Mỗi bước đi đều phải tính toán kỹ lưỡng. Một sai lầm nhỏ sẽ phải bắt đầu lại từ đầu.",
                "ELARA: Tôi có thể cảm nhận được sức mạnh của mảnh pha lê đang gần hơn. Nó ẩn sau bức tường đá cuối cùng."
            )
        ),
        
        // Level 1-4: Buồng pha lê
        4 to StoryEntry(
            title = "Buồng Pha Lê Đầu Tiên",
            segments = listOf(
                "ELARA: Đây rồi... buồng cuối cùng! Mảnh pha lê đầu tiên nằm trong trận đồ đá phức tạp nhất.",
                "Nó được bảo vệ bởi một cơ chế đẩy đá cổ xưa đòi hỏi tư duy logic hoàn hảo.",
                "ELARA: Một ký ức... tôi thấy những người bảo vệ pha lê giải các trận đồ trong quá khứ. Có phải tôi có liên quan đến họ không?",
                "Mảnh pha lê đầu tiên đã được tìm thấy! Nhưng còn hai mảnh nữa đang chờ đợi ở những thế giới nguy hiểm hơn - nơi băng trượt và bóng tối chi phối."
            )
        ),
        
        // Level 2-1: Bắt đầu thế giới 2 - Phòng Băng
        5 to StoryEntry(
            title = "Vào Phòng Băng Trượt",
            segments = listOf(
                "Mảnh pha lê đầu tiên đã mở cánh cửa dẫn đến thế giới thứ hai - phòng băng trượt.",
                "ELARA: Lạnh quá... và sàn nhà trơn trượt! Mọi thứ đều trượt không ngừng.",
                "Mọi chuyển động ở đây bị chi phối bởi quán tính băng. Tôi và khối đá đều trượt cho đến khi va phải vật cản.",
                "ELARA: Mảnh pha lê thứ hai ở đâu đó trong những phòng băng phức tạp hơn. Tôi phải tính toán mỗi bước trượt thật chính xác."
            )
        ),
        
        // Level 2-2: Phòng băng phức tạp
        6 to StoryEntry(
            title = "Mê Cung Băng Phức Tạp",
            segments = listOf(
                "ELARA: Phòng băng này... thiết kế mê cung phức tạp đến khó tin.",
                "Mỗi bước trượt phải tính toán từ trước. Một sai lầm sẽ trượt vào chỗ bế tắc.",
                "ELARA: Những ký ức lại xuất hiện... Tôi thấy tổ tiên tôi chinh phục băng giá trong quá khứ."
            )
        ),
        
        // Level 2-3: Buồng pha lê băng
        7 to StoryEntry(
            title = "Buồng Pha Lê Băng",
            segments = listOf(
                "ELARA: Mảnh pha lê thứ hai! Nhưng... bóng tối đang tạo ảo giác làm tôi lú lẫn.",
                "Những ảo ảnh xuất hiện, khiến tôi không phân biệt được hướng trượt đúng. Liệu tôi có đang đi đúng hướng?",
                "ELARA: Không... tôi phải tập trung vào logic, không để ảo giác lừa phỉnh. Mảnh pha lê này thuộc về tôi!",
                "Hai mảnh pha lê đã được tập hợp. Chỉ còn lại một mảnh cuối cùng... trong thế giới băng tối tuyệt đối."
            )
        ),
        
        // Level 3-1: Phòng băng tối
        8 to StoryEntry(
            title = "Bước Vào Thế Giới Băng Tối",
            segments = listOf(
                "Hai mảnh pha lê mở cánh cửa cuối cùng - phòng băng trong bóng tối tuyệt đối.",
                "ELARA: Đây rồi... băng trượt kết hợp với bóng tối. Tôi chỉ thấy được vài ô xung quanh.",
                "Tầm nhìn bị hạn chế, chỉ dựa vào trí nhớ và logic thuần túy. Đây là thử thách khó nhất.",
                "ELARA: Những ký ức sâu kín nhất đang trở lại... Tôi phải đối mặt với nỗi sợ hãi bóng tối của mình."
            )
        ),
        
        // Level 3-2: Mê cung băng tối
        9 to StoryEntry(
            title = "Mê Cung Băng Tối",
            segments = listOf(
                "ELARA: Mê cung băng trong tăm tối... Tôi chỉ cảm nhận được vật thể khi va phải.",
                "Những ký ức về tổ tiên tôi trở nên rõ ràng hơn. Họ đã từng thất bại ở đây vì không chinh phục được nỗi sợ.",
                "ELARA: Tôi hiểu rồi... lời nguyền không chỉ là thử thách vật lý. Nó còn là thử thách tinh thần - chinh phục nỗi sợ bóng tối."
            )
        ),
        
        // Level 3-8: Boss cuối
        15 to StoryEntry(
            title = "Buồng Pha Lê Cuối Cùng",
            segments = listOf(
                "ELARA: Mảnh pha lê cuối cùng! Buồng băng tối với trận đồ phức tạp nhất.",
                "Sự thật được hé lộ: Lời nguyền sinh ra từ chính nỗi sợ hãi bóng tối và băng giá của những người bảo vệ pha lê.",
                "ELARA: Tổ tiên tôi... họ đã thất bại vì không vượt qua nỗi sợ. Giờ đây, trách nhiệm thuộc về tôi.",
                "Ba mảnh pha lê hợp nhất trong ánh sáng xanh rực rỡ xé toạc bóng tối! Viên Pha Lê Cân Bằng được tái tạo!",
                "ELARA: Tôi đã chinh phục cả băng lẫn tối, vượt qua nỗi sợ hãi. Ánh sáng đã trở lại!",
                "Ánh sáng tràn ngập thế giới... nhưng một giọng nói mơ hồ vang lên: 'Đây chưa phải là kết thúc...'",
                "ELARA: Hành trình này đã kết thúc, nhưng tôi biết rằng còn nhiều thử thách khác đang chờ đợi..."
            )
        )
    )
    
    /**
     * Story kết thúc game - hiển thị sau khi hoàn thành level cuối
     */
    private val endingStory = StoryEntry(
        title = "Kết Thúc Hành Trình",
        segments = listOf(
            "Ba mảnh pha lê từ từ bay lên, tỏa ra ánh sáng rực rỡ như ba vì sao sáng nhất trong đêm tối. Chúng xoay quanh nhau trong điệu múa cổ xưa.",
            "ELARA: Cuối cùng... Viên Pha Lê Cân Bằng đã được tái tạo! Tôi có thể cảm nhận được sức mạnh của tổ tiên đang chảy qua người mình.",
            "Những ký ức cuối cùng hiện ra rõ ràng... Nữ hoàng Lyralei, tổ tiên của Elara, xuất hiện trong ánh sáng dịu dàng.",
            "LYRALEI: Con gái yêu dấu của ta... con đã chứng minh được rằng dòng máu bảo vệ pha lê vẫn chảy mạnh mẽ trong huyết quản của con.",
            "ELARA: Tổ tiên Lyralei... tất cả những thử thách này... đều là để kiểm tra con phải không?",
            "LYRALEI: Đúng vậy, Elara. Lời nguyền không phải là sự trả thù, mà là bài kiểm tra cuối cùng để tìm ra người kế thừa xứng đáng.",
            "ELARA: Con hiểu rồi... Con sẽ tiếp tục bảo vệ thế giới này, duy trì cân bằng giữa ánh sáng và bóng tối.",
            "Viên Pha Lê Cân Bằng rung động mạnh mẽ, xé toạc màn đêm tối! Những mê cung biến mất, thay vào đó là khu vườn tươi đẹp và thành phố huy hoàng.",
            "LYRALEI: Hành trình này chỉ là khởi đầu, con yêu. Vũ trụ còn vô vàn thế giới khác đang chờ đợi một người bảo vệ như con...",
            "ELARA: Con hứa sẽ không phụ lòng tin của tổ tiên. Từ giờ, con là người bảo vệ mới của những thế giới này!",
            "Ánh sáng cuối cùng tỏa ra, bao trùm khắp không gian. Khi ánh sáng tan đi, Elara đứng trong khu vườn tươi đẹp, tay cầm Viên Pha Lê Cân Bằng.",
            "Viên pha lê rung động nhẹ nhàng trong tay cô, như thể đang hát những khúc ca của vũ trụ... Và câu chuyện của Elara sẽ còn tiếp tục...",
            "CẢM ƠN BẠN ĐÃ CHƠI LORE JOURNEY!",
            "HÃY CHỜ ĐỢI NHỮNG CUỘC PHIÊU LƯU MỚI CÙNG ELARA..."
        )
    )

    /**
     * Lấy story cho level cụ thể
     */
    fun getStoryForLevel(levelId: Int): StoryEntry? {
        return storyData[levelId]
    }
    
    /**
     * Lấy ending story - hiển thị sau khi hoàn thành game
     */
    fun getEndingStory(): StoryEntry {
        return endingStory
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