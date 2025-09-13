package com.example.game.entity

import android.graphics.Canvas

/**
 * Quản lý tất cả entities trong game
 */
class EntityManager {
    private val entities = mutableListOf<Entity>()
    private val entitiesToAdd = mutableListOf<Entity>()
    private val entitiesToRemove = mutableListOf<Entity>()
    
    /**
     * Thêm entity vào manager
     */
    fun addEntity(entity: Entity) {
        entitiesToAdd.add(entity)
        println("📦 Entity added to queue: ${entity::class.simpleName}")
    }
    
    /**
     * Xóa entity khỏi manager
     */
    fun removeEntity(entity: Entity) {
        entitiesToRemove.add(entity)
        println("🗑️ Entity removed from queue: ${entity::class.simpleName}")
    }
    
    /**
     * Lấy tất cả entities theo type
     */
    fun <T : Entity> getEntitiesOfType(type: Class<T>): List<T> {
        @Suppress("UNCHECKED_CAST")
        return entities.filter { type.isInstance(it) } as List<T>
    }
    
    /**
     * Tìm entity đầu tiên theo type
     */
    fun <T : Entity> findEntityOfType(type: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return entities.find { type.isInstance(it) } as T?
    }
    
    /**
     * Update tất cả entities
     */
    fun updateAll(deltaTime: Long) {
        // Process additions
        if (entitiesToAdd.isNotEmpty()) {
            entities.addAll(entitiesToAdd)
            println("➕ Added ${entitiesToAdd.size} entities. Total: ${entities.size}")
            entitiesToAdd.clear()
        }
        
        // Process removals
        if (entitiesToRemove.isNotEmpty()) {
            entities.removeAll(entitiesToRemove)
            entitiesToRemove.forEach { it.dispose() }
            println("➖ Removed ${entitiesToRemove.size} entities. Total: ${entities.size}")
            entitiesToRemove.clear()
        }
        
        // Update active entities
        entities.filter { it.isActive }.forEach { entity ->
            try {
                entity.update(deltaTime)
            } catch (e: Exception) {
                println("❌ Error updating entity ${entity::class.simpleName}: ${e.message}")
            }
        }
        
        // Remove inactive entities
        val inactiveEntities = entities.filter { !it.isActive }
        if (inactiveEntities.isNotEmpty()) {
            entities.removeAll(inactiveEntities)
            inactiveEntities.forEach { it.dispose() }
            println("🚫 Removed ${inactiveEntities.size} inactive entities")
        }
    }
    
    /**
     * Draw tất cả entities
     */
    fun drawAll(canvas: Canvas) {
        entities.filter { it.isVisible }.forEach { entity ->
            try {
                entity.draw(canvas)
            } catch (e: Exception) {
                println("❌ Error drawing entity ${entity::class.simpleName}: ${e.message}")
            }
        }
    }
    
    /**
     * Draw entities với camera culling
     */
    fun drawAll(canvas: Canvas, cameraX: Float, cameraY: Float, screenWidth: Int, screenHeight: Int) {
        entities.filter { it.isVisible && it.isOnScreen(cameraX, cameraY, screenWidth, screenHeight) }
                .forEach { entity ->
                    try {
                        entity.draw(canvas)
                    } catch (e: Exception) {
                        println("❌ Error drawing entity ${entity::class.simpleName}: ${e.message}")
                    }
                }
    }
    
    /**
     * Clear tất cả entities
     */
    fun clear() {
        entities.forEach { it.dispose() }
        entities.clear()
        entitiesToAdd.clear()
        entitiesToRemove.clear()
        println("🧹 All entities cleared")
    }
    
    /**
     * Get thống kê
     */
    fun getEntityCount(): Int = entities.size
    fun getActiveEntityCount(): Int = entities.count { it.isActive }
    fun getVisibleEntityCount(): Int = entities.count { it.isVisible }
    
    /**
     * Debug info
     */
    fun printDebugInfo() {
        println("🔍 EntityManager Debug:")
        println("   Total entities: ${getEntityCount()}")
        println("   Active entities: ${getActiveEntityCount()}")
        println("   Visible entities: ${getVisibleEntityCount()}")
        
        val entityTypes = entities.groupBy { it::class.simpleName }
        entityTypes.forEach { (type, list) ->
            println("   $type: ${list.size}")
        }
    }
}
