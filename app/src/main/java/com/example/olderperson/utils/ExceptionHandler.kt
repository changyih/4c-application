package com.example.olderperson.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.PrintWriter
import java.io.StringWriter

/**
 * 全局异常处理工具类
 */
class ExceptionHandler(private val context: Context) : Thread.UncaughtExceptionHandler {
    private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
    
    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
    
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // 获取详细的异常堆栈信息
            val sw = StringWriter()
            val pw = PrintWriter(sw)
            throwable.printStackTrace(pw)
            val stackTrace = sw.toString()
            
            // 记录异常
            Log.e("APP_ERROR", "应用发生未捕获异常: ${throwable.message}")
            Log.e("APP_ERROR", "异常堆栈: $stackTrace")
            
            // 显示错误提示
            try {
                Toast.makeText(context, "应用发生错误：${throwable.message}", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("APP_ERROR", "无法显示Toast提示: ${e.message}")
            }
            
            // 给系统默认的异常处理器处理
            defaultHandler?.uncaughtException(thread, throwable)
        } catch (e: Exception) {
            // 确保即使在处理异常时出现问题，也不会阻止默认处理器运行
            Log.e("APP_ERROR", "异常处理器异常: ${e.message}")
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
} 