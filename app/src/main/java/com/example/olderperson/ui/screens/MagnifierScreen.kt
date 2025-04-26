package com.example.olderperson.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.olderperson.service.TextToSpeechService
import com.example.olderperson.ui.theme.FontSizeConfig
import com.example.olderperson.utils.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 放大镜屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagnifierScreen(
    textToSpeechService: TextToSpeechService? = null,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    // 状态变量
    var hasCameraPermission by remember { mutableStateOf(false) }
    var isFrozen by remember { mutableStateOf(false) }
    var zoomRatio by remember { mutableStateOf(1.0f) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    // 请求相机权限
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            textToSpeechService?.speak("相机权限已获取，放大镜已准备就绪")
        } else {
            textToSpeechService?.speak("需要相机权限才能使用放大镜功能")
        }
    }
    
    // 检查相机权限
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasCameraPermission) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // 冻结当前画面函数
    fun freezeCurrentFrame() {
        if (!isFrozen && camera != null) {
            isFrozen = true
            imageCapture.takePicture(
                executor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bitmap = image.toBitmap()
                        capturedBitmap = bitmap
                        image.close()
                    }
                    
                    override fun onError(exception: ImageCaptureException) {
                        Log.e("MagnifierScreen", "图像捕获失败: ${exception.imageCaptureError}", exception)
                        // 恢复非冻结状态
                        isFrozen = false
                    }
                }
            )
            textToSpeechService?.speak("已冻结画面，方便您查看")
        } else {
            isFrozen = false
            textToSpeechService?.speak("继续取景")
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "简易放大镜",
                        fontSize = FontSizeConfig.scaledSp(20).sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission) {
                if (isFrozen && capturedBitmap != null) {
                    // 显示冻结的图像
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = "冻结的图像",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 显示相机预览
                    CameraPreview(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        imageCapture = imageCapture,
                        onCameraReady = { cameraInstance ->
                            camera = cameraInstance
                            // 设置初始缩放
                            camera?.cameraControl?.setZoomRatio(zoomRatio)
                        }
                    )
                }
                
                // 底部控制栏
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0x88000000))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 缩放控制按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (!isFrozen && camera != null) {
                                    zoomRatio = (zoomRatio * 0.8f).coerceAtLeast(1.0f)
                                    camera?.cameraControl?.setZoomRatio(zoomRatio)
                                    textToSpeechService?.speak("缩小")
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "缩小",
                                tint = Color.Black
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // 冻结/解冻按钮
                        FloatingActionButton(
                            onClick = { freezeCurrentFrame() },
                            modifier = Modifier.size(64.dp),
                            containerColor = if (isFrozen) Color.Red else Color.White
                        ) {
                            Icon(
                                imageVector = if (isFrozen) Icons.Default.PlayArrow else Icons.Default.PauseCircle,
                                contentDescription = if (isFrozen) "解冻" else "冻结",
                                tint = if (isFrozen) Color.White else Color.Black,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        FloatingActionButton(
                            onClick = {
                                if (!isFrozen && camera != null) {
                                    zoomRatio = (zoomRatio * 1.2f).coerceAtMost(5.0f)
                                    camera?.cameraControl?.setZoomRatio(zoomRatio)
                                    textToSpeechService?.speak("放大")
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "放大",
                                tint = Color.Black
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (isFrozen) "画面已冻结，可以仔细查看" else "点击中间按钮冻结画面",
                        color = Color.White,
                        fontSize = FontSizeConfig.scaledSp(16).sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // 请求相机权限的UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "相机",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "需要相机权限才能使用放大镜功能",
                        fontSize = FontSizeConfig.scaledSp(18).sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("授予相机权限")
                    }
                }
            }
        }
    }
}

/**
 * 相机预览组件
 */
@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    imageCapture: ImageCapture,
    onCameraReady: (Camera) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                try {
                    // 解绑之前的用例
                    cameraProvider.unbindAll()
                    
                    // 绑定用例到相机
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                    )
                    
                    // 通知相机已准备就绪
                    onCameraReady(camera)
                    
                } catch (e: Exception) {
                    Log.e("CameraPreview", "相机绑定失败: ${e.message}", e)
                }
            }, ContextCompat.getMainExecutor(context))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
} 