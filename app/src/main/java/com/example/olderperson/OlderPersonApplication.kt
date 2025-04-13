package com.example.olderperson

import android.app.Application
import android.content.Context
import android.util.Log
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.CoordType
import com.example.olderperson.utils.ExceptionHandler
import com.example.olderperson.data.UserManager

class OlderPersonApplication : Application() {
    
    companion object {
        private var context: Context? = null
        private const val TAG = "OlderPersonApp"
        // 添加一个标志位表示百度地图初始化状态
        private var isBaiduMapInitialized = false

        fun getContext(): Context {
            return context ?: throw IllegalStateException("Application context is null")
        }
        
        // 添加方法检查百度地图是否已初始化
        fun isBaiduMapAvailable(): Boolean {
            return isBaiduMapInitialized
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "应用正在初始化...")
        
        try {
            // 保存应用上下文
            context = applicationContext
            Log.d(TAG, "应用上下文已保存")
            
            // 初始化全局异常处理器
            ExceptionHandler(this)
            Log.d(TAG, "全局异常处理器已初始化")
            
            // 设置全局未捕获异常处理器
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                // 日志记录崩溃信息
                Log.e(TAG, "应用发生未处理异常: ${throwable.message}", throwable)
                
                // 这里可以添加崩溃报告逻辑，例如发送到服务器
                
                // 注意：Android 默认的异常处理器会结束应用进程，
                // 在这里你只能记录信息，但不能阻止应用崩溃
            }
            Log.d(TAG, "全局未捕获异常处理器已设置")
            
            // 初始化用户数据
            initializeUserData()
            
            // 安全初始化百度地图SDK
            initializeBaiduMapSafely()
            
            Log.i(TAG, "应用初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "应用初始化过程中发生严重错误: ${e.message}", e)
        }
    }
    
    /**
     * 安全地初始化百度地图SDK
     */
    private fun initializeBaiduMapSafely() {
        try {
            Log.d(TAG, "开始初始化百度地图SDK")
            
            // 确保先设置隐私政策同意
            try {
                Log.d(TAG, "设置百度地图SDK隐私政策同意")
                // 重要：从地图SDK v7.5.0版本起，必须先调用隐私合规接口
                SDKInitializer.setAgreePrivacy(this, true)
                Log.d(TAG, "百度地图隐私政策设置成功")
            } catch (e: Exception) {
                Log.e(TAG, "设置百度地图隐私政策同意失败: ${e.message}", e)
                // 隐私政策设置失败，SDK将无法正常工作
                isBaiduMapInitialized = false
                return
            }
            
            // 在使用SDK各组件之前初始化百度地图SDK
            try {
                Log.d(TAG, "执行SDKInitializer.initialize")
                SDKInitializer.initialize(this)
                Log.d(TAG, "SDKInitializer.initialize完成")
            } catch (e: Exception) {
                Log.e(TAG, "百度地图SDK初始化失败: ${e.message}", e)
                isBaiduMapInitialized = false
                return
            }
            
            // 设置坐标类型
            try {
                Log.d(TAG, "设置百度地图坐标类型")
                // 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
                // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
                SDKInitializer.setCoordType(CoordType.BD09LL)
                Log.d(TAG, "百度地图坐标类型设置完成")
            } catch (e: Exception) {
                Log.e(TAG, "设置百度地图坐标类型失败: ${e.message}", e)
                // 坐标类型设置失败不影响基本功能
            }
            
            // 标记百度地图初始化成功
            isBaiduMapInitialized = true
            Log.d(TAG, "百度地图SDK初始化成功")
        } catch (e: Exception) {
            // 捕获并记录任何初始化异常
            isBaiduMapInitialized = false
            Log.e(TAG, "百度地图SDK初始化过程中发生意外异常: ${e.message}", e)
        }
    }
    
    private fun initializeUserData() {
        try {
            // 确保用户数据已加载，在UserManager中用户数据已预设，所以不需要额外操作
            val users = UserManager.getUsers()
            Log.i(TAG, "已初始化${users.size}个用户账号")
            
            users.forEach { user -> 
                Log.d(TAG, "用户: ${user.name}, 角色: ${user.role}, 电话: ${user.phoneNumber}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "初始化用户数据失败: ${e.message}", e)
        }
    }
} 