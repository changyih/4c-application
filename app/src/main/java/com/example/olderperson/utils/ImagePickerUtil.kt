package com.example.olderperson.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 图片选择和处理工具类
 */
class ImagePickerUtil(private val context: Context) {
    private val TAG = "ImagePickerUtil"
    
    // 临时图片存储目录
    private val tempDir: File by lazy {
        val dir = File(context.cacheDir, "images")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        dir
    }
    
    /**
     * 从Uri加载Bitmap
     */
    suspend fun loadBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            return@withContext inputStream?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            Log.e(TAG, "从Uri加载Bitmap失败: ${e.message}", e)
            return@withContext null
        }
    }
    
    /**
     * 压缩Bitmap以减小尺寸
     */
    suspend fun compressBitmap(bitmap: Bitmap, maxWidth: Int = 1024, maxHeight: Int = 1024, quality: Int = 85): Bitmap = withContext(Dispatchers.Default) {
        var width = bitmap.width
        var height = bitmap.height
        
        // 计算缩放比例
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
        
        var finalWidth = maxWidth
        var finalHeight = maxHeight
        
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        
        // 如果原图比目标尺寸小，直接返回原图
        if (width <= maxWidth && height <= maxHeight) {
            return@withContext bitmap
        }
        
        // 缩放图片
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
        
        // 如果需要进一步压缩
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val compressedData = outputStream.toByteArray()
        
        return@withContext BitmapFactory.decodeByteArray(compressedData, 0, compressedData.size)
    }
    
    /**
     * 保存Bitmap到临时文件
     */
    suspend fun saveBitmapToTempFile(bitmap: Bitmap): File = withContext(Dispatchers.IO) {
        val file = File(tempDir, "temp_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        return@withContext file
    }
    
    /**
     * 创建图片选择启动器
     */
    fun createImagePickerLauncher(activity: FragmentActivity, onImageSelected: (Uri) -> Unit): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { onImageSelected(it) }
        }
    }
    
    /**
     * 清理临时目录
     */
    suspend fun clearTempFiles() = withContext(Dispatchers.IO) {
        try {
            tempDir.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.e(TAG, "清理临时文件失败: ${e.message}", e)
        }
    }
} 