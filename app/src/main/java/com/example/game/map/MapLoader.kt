package com.example.game.map

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Loader để đọc map từ file txt format:
 * width
 * height
 * playerX
 * playerY
 * tile,tile,tile,...
 * tile,tile,tile,...
 * ...
 */
object MapLoader {
    
    data class MapData(
        val width: Int,
        val height: Int,
        val playerSpawnX: Int,
        val playerSpawnY: Int,
        val bottomLayer: Array<IntArray>,
        val mainLayer: Array<IntArray>,
        val activeLayer: Array<IntArray>,
        val topLayer: Array<IntArray>
    ) {
        // Compatibility property for old code that uses single tiles array
        @Deprecated("Use specific layers instead")
        val tiles: Array<IntArray> get() = mainLayer
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as MapData
            return width == other.width &&
                   height == other.height &&
                   playerSpawnX == other.playerSpawnX &&
                   playerSpawnY == other.playerSpawnY &&
                   bottomLayer.contentDeepEquals(other.bottomLayer) &&
                   mainLayer.contentDeepEquals(other.mainLayer) &&
                   activeLayer.contentDeepEquals(other.activeLayer) &&
                   topLayer.contentDeepEquals(other.topLayer)
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + playerSpawnX
            result = 31 * result + playerSpawnY
            result = 31 * result + bottomLayer.contentDeepHashCode()
            result = 31 * result + mainLayer.contentDeepHashCode()
            result = 31 * result + activeLayer.contentDeepHashCode()
            result = 31 * result + topLayer.contentDeepHashCode()
            return result
        }
    }
    
    /**
     * Load map từ assets folder
     */
    fun loadFromAssets(context: Context, fileName: String): MapData? {
        return try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()
            reader.close()
            parseMapData(lines)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Load map từ string (cho testing)
     */
    fun loadFromString(mapString: String): MapData? {
        return try {
            val lines = mapString.trim().split("\n").map { it.trim() }
            parseMapData(lines)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun parseMapData(lines: List<String>): MapData? {
        if (lines.size < 4) return null
        
        try {
            val width = lines[0].toInt()
            val height = lines[1].toInt()
            val playerSpawnX = lines[2].toInt()
            val playerSpawnY = lines[3].toInt()
            
            // Check if we have enough lines for 4-layer format
            val expectedLines = 4 + (height * 4) // 4 header lines + 4 layers * height
            
            if (lines.size >= expectedLines) {
                // New 4-layer format
                return parseFourLayerFormat(lines, width, height, playerSpawnX, playerSpawnY)
            } else if (lines.size >= 4 + (height * 3)) {
                // 3-layer format
                return parseThreeLayerFormat(lines, width, height, playerSpawnX, playerSpawnY)
            } else if (lines.size >= 4 + height) {
                // Old single-layer format - convert to 3 layers
                return parseSingleLayerFormat(lines, width, height, playerSpawnX, playerSpawnY)
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
    
    private fun parseFourLayerFormat(lines: List<String>, width: Int, height: Int, playerSpawnX: Int, playerSpawnY: Int): MapData {
        val bottomLayer = Array(height) { IntArray(width) }
        val mainLayer = Array(height) { IntArray(width) }
        val activeLayer = Array(height) { IntArray(width) }
        val topLayer = Array(height) { IntArray(width) }
        
        // Parse bottom layer
        for (y in 0 until height) {
            val lineIndex = 4 + y
            parseLayerLine(lines[lineIndex], bottomLayer[y], width)
        }
        
        // Parse main layer
        for (y in 0 until height) {
            val lineIndex = 4 + height + y
            parseLayerLine(lines[lineIndex], mainLayer[y], width)
        }
        
        // Parse active layer
        for (y in 0 until height) {
            val lineIndex = 4 + (height * 2) + y
            parseLayerLine(lines[lineIndex], activeLayer[y], width)
        }
        
        // Parse top layer
        for (y in 0 until height) {
            val lineIndex = 4 + (height * 3) + y
            parseLayerLine(lines[lineIndex], topLayer[y], width)
        }
        
        return MapData(width, height, playerSpawnX, playerSpawnY, bottomLayer, mainLayer, activeLayer, topLayer)
    }
    
    private fun parseThreeLayerFormat(lines: List<String>, width: Int, height: Int, playerSpawnX: Int, playerSpawnY: Int): MapData {
        val bottomLayer = Array(height) { IntArray(width) }
        val mainLayer = Array(height) { IntArray(width) }
        val activeLayer = Array(height) { IntArray(width) }
        val topLayer = Array(height) { IntArray(width) } // Empty top layer
        
        // Parse bottom layer
        for (y in 0 until height) {
            val lineIndex = 4 + y
            parseLayerLine(lines[lineIndex], bottomLayer[y], width)
        }
        
        // Parse main layer
        for (y in 0 until height) {
            val lineIndex = 4 + height + y
            parseLayerLine(lines[lineIndex], mainLayer[y], width)
        }
        
        // Parse active layer
        for (y in 0 until height) {
            val lineIndex = 4 + (height * 2) + y
            parseLayerLine(lines[lineIndex], activeLayer[y], width)
        }
        
        // Fill top layer with empty tiles
        for (y in 0 until height) {
            for (x in 0 until width) {
                topLayer[y][x] = TileConstants.TILE_EMPTY
            }
        }
        
        return MapData(width, height, playerSpawnX, playerSpawnY, bottomLayer, mainLayer, activeLayer, topLayer)
    }
    
    private fun parseSingleLayerFormat(lines: List<String>, width: Int, height: Int, playerSpawnX: Int, playerSpawnY: Int): MapData {
        val mainLayer = Array(height) { IntArray(width) }
        val bottomLayer = Array(height) { IntArray(width) } // Empty layer
        val activeLayer = Array(height) { IntArray(width) } // Empty layer
        val topLayer = Array(height) { IntArray(width) } // Empty layer
        
        // Parse single layer into main layer
        for (y in 0 until height) {
            val lineIndex = 4 + y
            if (lineIndex < lines.size) {
                parseLayerLine(lines[lineIndex], mainLayer[y], width)
            }
        }
        
        // Fill bottom, active, and top layers with empty tiles
        for (y in 0 until height) {
            for (x in 0 until width) {
                bottomLayer[y][x] = TileConstants.TILE_EMPTY
                activeLayer[y][x] = TileConstants.TILE_EMPTY
                topLayer[y][x] = TileConstants.TILE_EMPTY
            }
        }
        
        return MapData(width, height, playerSpawnX, playerSpawnY, bottomLayer, mainLayer, activeLayer, topLayer)
    }
    
    private fun parseLayerLine(line: String, targetArray: IntArray, width: Int) {
        val tileIds = line.split(",").map { it.trim().toInt() }
        for (x in 0 until width) {
            targetArray[x] = if (x < tileIds.size) tileIds[x] else TileConstants.TILE_EMPTY
        }
    }
}
