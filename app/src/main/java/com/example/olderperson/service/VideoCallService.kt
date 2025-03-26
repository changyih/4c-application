package com.example.olderperson.service

import android.content.Context
import android.view.SurfaceView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 视频通话服务，提供视频通话相关功能的框架
 */
class VideoCallService(private val context: Context) {
    // 通话状态流
    private val _isInCall = MutableStateFlow(false)
    val isInCall: StateFlow<Boolean> = _isInCall

    /**
     * 开始通话
     */
    fun startCall(channelName: String) {
        // 模拟通话开始
        _isInCall.value = true
    }

    /**
     * 结束通话
     */
    fun endCall() {
        // 模拟通话结束
        _isInCall.value = false
    }

    /**
     * 设置本地视频视图
     */
    fun setupLocalVideo(surfaceView: SurfaceView) {
        // 实际实现将在后续添加
    }

    /**
     * 设置远程视频视图
     */
    fun setupRemoteVideo(surfaceView: SurfaceView, uid: Int) {
        // 实际实现将在后续添加
    }

    /**
     * 释放资源
     */
    fun release() {
        // 实际实现将在后续添加
    }
} 