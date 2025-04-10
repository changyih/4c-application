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

        fun getContext(): Context {
            return context!!
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        
        // 在使用SDK各组件之前初始化百度地图SDK
        SDKInitializer.setAgreePrivacy(this, true)
        SDKInitializer.initialize(this)
        
        // 自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        // 包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL)
        
        // 初始化全局异常处理器
        ExceptionHandler(this)
        
        // 设置全局未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            // 日志记录崩溃信息
            Log.e(TAG, "应用发生未处理异常", throwable)
            
            // 这里可以添加崩溃报告逻辑，例如发送到服务器
            
            // 注意：Android 默认的异常处理器会结束应用进程，
            // 在这里你只能记录信息，但不能阻止应用崩溃
        }
        
        // 初始化用户数据
        initializeUserData()
        
        Log.d("BaiduMap", "SDK初始化完成")
        Log.i(TAG, "应用初始化完成")
    }
    
    private fun initializeUserData() {
        // 确保用户数据已加载，在UserManager中用户数据已预设，所以不需要额外操作
        val users = UserManager.getUsers()
        Log.i(TAG, "已初始化${users.size}个用户账号")
        
        users.forEach { user -> 
            Log.d(TAG, "用户: ${user.name}, 角色: ${user.role}, 电话: ${user.phoneNumber}")
        }
    }
} 