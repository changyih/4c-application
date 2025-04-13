package com.example.olderperson.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * 日程安排管理类，用于管理老年人的日程安排
 */
class ScheduleManager(private val context: Context) {

    // 日程安排项数据类
    data class ScheduleItem(
        val id: String = UUID.randomUUID().toString(),
        var time: String,
        var title: String,
        var description: String
    )

    // SharedPreferences 键名
    private val PREFS_NAME = "schedule_prefs"
    private val SCHEDULE_KEY = "schedule_items"

    // 获取 SharedPreferences
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Gson 实例用于序列化和反序列化
    private val gson = Gson()

    /**
     * 获取所有日程安排项
     */
    fun getAllScheduleItems(): List<ScheduleItem> {
        val json = prefs.getString(SCHEDULE_KEY, null) ?: return getDefaultScheduleItems()
        val type = object : TypeToken<List<ScheduleItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            getDefaultScheduleItems()
        }
    }

    /**
     * 保存所有日程安排项
     */
    private fun saveAllScheduleItems(items: List<ScheduleItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString(SCHEDULE_KEY, json).apply()
    }

    /**
     * 添加一个新的日程安排项
     */
    fun addScheduleItem(item: ScheduleItem) {
        val items = getAllScheduleItems().toMutableList()
        items.add(item)
        // 按时间排序
        items.sortBy { it.time }
        saveAllScheduleItems(items)
    }

    /**
     * 更新现有的日程安排项
     */
    fun updateScheduleItem(item: ScheduleItem) {
        val items = getAllScheduleItems().toMutableList()
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item
            // 按时间排序
            items.sortBy { it.time }
            saveAllScheduleItems(items)
        }
    }

    /**
     * 删除日程安排项
     */
    fun deleteScheduleItem(id: String) {
        val items = getAllScheduleItems().toMutableList()
        items.removeIf { it.id == id }
        saveAllScheduleItems(items)
    }

    /**
     * 清空所有日程安排项
     */
    fun clearAllScheduleItems() {
        prefs.edit().remove(SCHEDULE_KEY).apply()
    }

    /**
     * 获取默认的日程安排项（如果没有保存的数据）
     */
    private fun getDefaultScheduleItems(): List<ScheduleItem> {
        return listOf(
            ScheduleItem(
                id = "1",
                time = "08:00",
                title = "晨间服药",
                description = "降压药 1片，维生素 1片"
            ),
            ScheduleItem(
                id = "2",
                time = "10:30",
                title = "心脏科复诊",
                description = "市第一人民医院"
            )
        )
    }

    companion object {
        // 单例实例
        @Volatile
        private var INSTANCE: ScheduleManager? = null

        fun getInstance(context: Context): ScheduleManager {
            return INSTANCE ?: synchronized(this) {
                val instance = ScheduleManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
} 