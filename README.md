# LoreJourney - Game Giải Đố RPG

## 📖 Giới thiệu
LoreJourney là một game giải đố RPG top-down được phát triển trên nền tảng Android. Game kể về cuộc hành trình của Elara, một nhà trừ tà trẻ tuổi, trong việc tìm kiếm Viên Pha Lê Cân Bằng để cứu thế giới khỏi thế lực bóng tối.

## 🎮 Cốt truyện
Nhân vật chính **Elara** là một nhà trừ tà với trí tuệ sắc bén, được triệu hồi để tìm kiếm **Viên Pha Lê Cân Bằng** - một báu vật huyền thoại có khả năng tái lập trật tự cho thế giới đang chìm vào hỗn loạn.

### 🗺️ Ba vùng đất trong hành trình:

1. **Map 1 - Đồng Cỏ Khởi Nguồn (The Verdant Meadows)**
   - Vùng đất xanh tươi bị xáo trộn
   - Chứa mảnh pha lê đầu tiên
   - Khám phá ký ức về nguồn gốc lời nguyền

2. **Map 2 - Vùng Đất Băng Giá (The Frozen Expanse)**
   - Vùng băng tuyết lạnh giá với các thử thách về quán tính
   - Chứa mảnh pha lê thứ hai
   - Tiết lộ mối liên hệ huyết thống của Elara

3. **Map 3 - Thành Pháo Bóng Tối (The Umbral Citadel)**
   - Pháo đài tăm tối với ảo ảnh ánh sáng và bóng tối
   - Chứa mảnh pha lê cuối cùng
   - Đối mặt với bí mật sâu kín nhất

## 🛠️ Công nghệ sử dụng

### Nền tảng
- **Android SDK**: API 24-34
- **Ngôn ngữ**: Kotlin
- **Build system**: Gradle với Kotlin DSL

### Dependencies chính
```kotlin
- androidx.core:core-ktx:1.12.0
- androidx.appcompat:appcompat:1.6.1
- com.google.android.material:material:1.10.0
```

### Cấu hình game
- **Orientation**: Landscape (ngang)
- **Theme**: Fullscreen, no title bar
- **Min SDK**: 24
- **Target SDK**: 34

## 📁 Cấu trúc Project

```
├── app/src/main/
│   ├── AndroidManifest.xml          # Cấu hình ứng dụng Android
│   ├── java/com/example/game/
│   │   ├── MainActivity.kt          # Activity chính
│   │   ├── GameConstants.kt         # Các hằng số game
│   │   ├── GameMap.kt              # Xử lý bản đồ
│   │   ├── SaveManager.kt          # Quản lý save game
│   │   ├── SpritePlayer.kt         # Xử lý sprite nhân vật
│   │   ├── animation/              # Hệ thống animation
│   │   ├── Camera.kt               # Camera game
│   │   ├── core/                   # Core game systems
│   │   ├── engine/                 # Game engine
│   │   ├── entity/                 # Các đối tượng game
│   │   ├── gameMechanic/           # Cơ chế gameplay
│   │   ├── map/                    # Xử lý map
│   │   ├── screens/                # Các màn hình game
│   │   ├── story/                  # Hệ thống câu chuyện
│   │   └── ui/                     # Giao diện người dùng
│   ├── assets/worlds/              # Dữ liệu các world
│   │   ├── world1/                 # World 1 - Đồng Cỏ Khởi Nguồn
│   │   ├── world2/                 # World 2 - Vùng Đất Băng Giá
│   │   └── world3/                 # World 3 - Thành Pháo Bóng Tối
│   └── res/                        # Resources (graphics, strings)
├── build.gradle.kts                # Build configuration
├── map_editor.html                 # Web-based map editor
└── story.txt                       # Chi tiết cốt truyện
```

## 🎮 Gameplay Features

### Core Mechanics
- **Tile Size**: 96px
- **Player Speed**: 64 units
- **Animation Frame Duration**: 150ms
- **Total Levels**: 15 (World1=4, World2=3, World3=8)

### Game States
- Menu chính
- World selection
- Level selection  
- Playing
- Settings
- Paused

### Level System
- 3 worlds với tổng cộng 15 levels
- Hệ thống unlock progressive
- Save/Load progress

## 🔧 Map Editor
Project bao gồm một map editor web-based (`map_editor.html`) cho phép:
- Tạo và chỉnh sửa map
- Preview real-time
- Export dữ liệu map cho game

## 📱 Yêu cầu hệ thống
- **Android**: API 24+ (Android 7.0)
- **RAM**: Tối thiểu 2GB
- **Storage**: ~50MB

## 🚀 Cách chạy project

### Prerequisites
- Android Studio
- Android SDK 24+
- JDK 8+

### Build và chạy
1. Clone repository
2. Mở project trong Android Studio
3. Sync Gradle
4. Build và run trên device/emulator

```bash
./gradlew assembleDebug
```

## 📄 File Formats

### Level Data Format
Các file level (`.txt`) trong `assets/worlds/` có cấu trúc:
```
[width]
[height]  
[player_x]
[player_y]
[tile_data_matrix]
```

## 🎯 Features đã implement
- ✅ Game engine cơ bản
- ✅ Player movement và animation
- ✅ Map loading system
- ✅ Camera system
- ✅ Save/Load system
- ✅ Multi-world support
- ✅ UI screens (menu, level select)
- ✅ Map editor tool

## 🔮 Roadmap
- [ ] Audio system
- [ ] Particle effects
- [ ] More puzzle mechanics
- [ ] Inventory system
- [ ] Dialog system
- [ ] Achievement system

## 👥 Đóng góp
Project được phát triển cho mục đích học tập tại OceanTech School.

## 📄 License
Educational project - OceanTech School

---
**Developed with ❤️ for OceanTech School**