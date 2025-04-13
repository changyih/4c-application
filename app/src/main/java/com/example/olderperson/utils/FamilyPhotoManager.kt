package com.example.olderperson.utils

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * 家庭相册照片管理类
 * 用于管理照片的存储、读取和删除
 */
class FamilyPhotoManager(private val context: Context) {

    // 照片数据类
    data class PhotoItem(
        val id: String = UUID.randomUUID().toString(),
        val title: String,
        val filePath: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    // SharedPreferences 键名
    private val PREFS_NAME = "family_photo_prefs"
    private val PHOTOS_KEY = "family_photos"

    // 照片存储目录
    private val photoDir: File by lazy {
        val dir = File(context.filesDir, "family_photos")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }

    // 获取 SharedPreferences
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Gson 实例用于序列化和反序列化
    private val gson = Gson()

    /**
     * 获取所有相册照片
     * @return 照片列表
     */
    fun getAllPhotos(): List<PhotoItem> {
        val json = prefs.getString(PHOTOS_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<PhotoItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "获取照片失败: ${e.message}")
            emptyList()
        }
    }

    /**
     * 保存所有照片信息
     */
    private fun saveAllPhotos(photos: List<PhotoItem>) {
        val json = gson.toJson(photos)
        prefs.edit().putString(PHOTOS_KEY, json).apply()
    }

    /**
     * 添加照片
     * @param uri 照片URI
     * @param title 照片标题
     * @return 是否添加成功
     */
    suspend fun addPhoto(uri: Uri, title: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // 从URI加载位图
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // 生成唯一文件名
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val file = File(photoDir, fileName)

            // 保存位图到文件
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            // 创建并添加照片项
            val photoItem = PhotoItem(
                title = title,
                filePath = file.absolutePath
            )

            // 获取现有照片并添加新照片
            val photos = getAllPhotos().toMutableList()
            photos.add(0, photoItem) // 将新照片添加到列表开头
            saveAllPhotos(photos)

            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "添加照片失败: ${e.message}")
            return@withContext false
        }
    }

    /**
     * 删除照片
     * @param id 照片ID
     * @return 是否删除成功
     */
    fun deletePhoto(id: String): Boolean {
        try {
            // 获取所有照片
            val photos = getAllPhotos().toMutableList()
            
            // 查找要删除的照片
            val photoToDelete = photos.find { it.id == id } ?: return false
            
            // 删除物理文件
            val file = File(photoToDelete.filePath)
            if (file.exists()) {
                file.delete()
            }
            
            // 从列表中移除照片信息
            photos.removeIf { it.id == id }
            saveAllPhotos(photos)
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "删除照片失败: ${e.message}")
            return false
        }
    }

    /**
     * 获取照片位图
     * @param photoItem 照片项
     * @return 照片位图，如果加载失败则返回null
     */
    suspend fun getPhotoBitmap(photoItem: PhotoItem): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(photoItem.filePath)
            if (file.exists()) {
                return@withContext BitmapFactory.decodeFile(file.absolutePath)
            }
            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "获取照片位图失败: ${e.message}")
            return@withContext null
        }
    }

    /**
     * 清空所有照片
     * @return 是否清空成功
     */
    fun clearAllPhotos(): Boolean {
        try {
            // 删除所有照片文件
            photoDir.listFiles()?.forEach { it.delete() }
            
            // 清空照片信息
            prefs.edit().remove(PHOTOS_KEY).apply()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "清空照片失败: ${e.message}")
            return false
        }
    }

    /**
     * 更新照片标题
     * @param id 照片ID
     * @param newTitle 新标题
     * @return 是否更新成功
     */
    fun updatePhotoTitle(id: String, newTitle: String): Boolean {
        try {
            // 获取所有照片
            val photos = getAllPhotos().toMutableList()
            
            // 查找要更新的照片
            val index = photos.indexOfFirst { it.id == id }
            if (index == -1) return false
            
            // 更新照片标题
            val updatedPhoto = photos[index].copy(title = newTitle)
            photos[index] = updatedPhoto
            
            // 保存更新
            saveAllPhotos(photos)
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "更新照片标题失败: ${e.message}")
            return false
        }
    }

    /**
     * 根据ID获取照片项
     * @param id 照片ID
     * @return 照片项，如果未找到则返回null
     */
    fun getPhotoById(id: String): PhotoItem? {
        return getAllPhotos().find { it.id == id }
    }

    companion object {
        private const val TAG = "FamilyPhotoManager"
        
        // 单例实例
        @Volatile
        private var INSTANCE: FamilyPhotoManager? = null

        fun getInstance(context: Context): FamilyPhotoManager {
            return INSTANCE ?: synchronized(this) {
                val instance = FamilyPhotoManager(context)
                INSTANCE = instance
                instance
            }
        }
    }
} 