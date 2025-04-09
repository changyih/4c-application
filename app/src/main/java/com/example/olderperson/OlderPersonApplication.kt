package com.example.olderperson

import android.app.Application
import android.content.Context
import android.util.Log
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.CoordType
import com.example.olderperson.utils.ExceptionHandler

class OlderPersonApplication : Application() {
    
    companion object {
        private var context: Context? = null

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
        
        Log.d("BaiduMap", "SDK初始化完成")
    }
} 