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
        val tiles: Array<IntArray>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as MapData
            return width == other.width &&
                   height == other.height &&
                   playerSpawnX == other.playerSpawnX &&
                   playerSpawnY == other.playerSpawnY &&
                   tiles.contentDeepEquals(other.tiles)
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + playerSpawnX
            result = 31 * result + playerSpawnY
            result = 31 * result + tiles.contentDeepHashCode()
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
            
            if (lines.size < 4 + height) return null
            
            val tiles = Array(height) { IntArray(width) }
            
            for (y in 0 until height) {
                val lineIndex = 4 + y
                if (lineIndex >= lines.size) break
                
                val tileLine = lines[lineIndex]
                val tileIds = tileLine.split(",").map { it.trim().toInt() }
                
                for (x in 0 until width) {
                    tiles[y][x] = if (x < tileIds.size) tileIds[x] else TileConstants.TILE_EMPTY
                }
            }
            
            return MapData(width, height, playerSpawnX, playerSpawnY, tiles)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
