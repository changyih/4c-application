package com.example.olderperson.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.PoiDetailResult
import com.baidu.mapapi.search.poi.PoiDetailSearchResult
import com.baidu.mapapi.search.poi.PoiIndoorResult
import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiSearch
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener
import com.baidu.mapapi.search.poi.PoiNearbySearchOption
import com.example.olderperson.R
import com.example.olderperson.ui.theme.DarkText
import kotlinx.coroutines.launch

/**
 * 百度地图位置坐标类，用于兼容Google LatLng到百度LatLng的转换
 */
data class MapLocation(
    val latitude: Double,
    val longitude: Double
) {
    // 转换为百度地图坐标
    fun toBaiduLatLng(): LatLng {
        return LatLng(latitude, longitude)
    }
    
    // 从Google地图坐标转换
    companion object {
        fun fromGoogleLatLng(googleLatLng: com.google.android.gms.maps.model.LatLng): MapLocation {
            return MapLocation(googleLatLng.latitude, googleLatLng.longitude)
        }
    }
}

/**
 * 百度地图卡片组件
 */
@Composable
fun BaiduMapCard(
    modifier: Modifier = Modifier,
    userLocation: MapLocation = MapLocation(43.817071, 125.323544), // 默认长春位置
    onLocationUpdate: (MapLocation) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var mapView: MapView? = remember { null }
    var baiduMap: BaiduMap? = remember { null }
    var mapInitError by remember { mutableStateOf<String?>(null) }
    
    // 初始化地图
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            try {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                    Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                    else -> {}
                }
            } catch (e: Exception) {
                mapInitError = "地图生命周期错误: ${e.message}"
                e.printStackTrace()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            try {
                lifecycleOwner.lifecycle.removeObserver(observer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            if (mapInitError != null) {
                // 显示错误信息
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "地图加载失败",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mapInitError ?: "未知错误",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { mapInitError = null }
                    ) {
                        Text("重试")
                    }
                }
            } else {
                // 百度地图视图
                AndroidView(
                    factory = { ctx ->
                        try {
                            Log.d("BaiduMap", "开始初始化地图视图")
                            // 创建百度地图视图
                            val view = MapView(ctx).apply {
                                Log.d("BaiduMap", "MapView创建成功")
                                mapView = this
                                baiduMap = this.map
                                Log.d("BaiduMap", "获取BaiduMap对象: ${baiduMap != null}")
                                
                                // 设置地图初始位置
                                baiduMap?.apply {
                                    // 允许缩放
                                    uiSettings.isZoomGesturesEnabled = true
                                    // 允许旋转
                                    uiSettings.isRotateGesturesEnabled = true
                                    // 允许指南针
                                    uiSettings.isCompassEnabled = true
                                    
                                    // 设置地图中心点
                                    val baiduLatLng = userLocation.toBaiduLatLng()
                                    val update = MapStatusUpdateFactory.newLatLngZoom(baiduLatLng, 15f)
                                    animateMapStatus(update)
                                    
                                    // 添加当前位置标记 - 使用安全的方式添加图标
                                    try {
                                        // 尝试加载自定义图标
                                        val icon = getBitmapDescriptor(ctx)
                                        val markerOptions = MarkerOptions()
                                            .position(baiduLatLng)
                                            .icon(icon)
                                        addOverlay(markerOptions)
                                        Log.d("BaiduMap", "标记添加成功")
                                    } catch (e: Exception) {
                                        Log.e("BaiduMap", "添加标记失败: ${e.message}", e)
                                        // 标记添加失败，但地图仍可正常显示，不抛出异常
                                    }
                                }
                            }
                            Log.d("BaiduMap", "地图初始化完成")
                            view
                        } catch (e: Exception) {
                            // 捕获地图初始化异常
                            Log.e("BaiduMap", "地图初始化错误: ${e.message}", e)
                            mapInitError = "地图初始化错误: ${e.message}"
                            e.printStackTrace()
                            
                            // 返回一个空的FrameLayout作为占位符
                            FrameLayout(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        try {
                            // 更新地图位置
                            if (view is MapView) {
                                val baiduLatLng = userLocation.toBaiduLatLng()
                                baiduMap?.apply {
                                    clear()
                                    val update = MapStatusUpdateFactory.newLatLng(baiduLatLng)
                                    animateMapStatus(update)
                                    
                                    // 添加当前位置标记 - 使用安全的方式更新图标
                                    try {
                                        val icon = getBitmapDescriptor(context)
                                        val markerOptions = MarkerOptions()
                                            .position(baiduLatLng)
                                            .icon(icon)
                                        addOverlay(markerOptions)
                                    } catch (e: Exception) {
                                        Log.e("BaiduMap", "更新标记失败: ${e.message}", e)
                                        // 标记更新失败，但地图仍可正常显示，不抛出异常
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // 捕获地图更新异常
                            Log.e("BaiduMap", "地图更新错误: ${e.message}", e)
                            mapInitError = "地图更新错误: ${e.message}"
                            e.printStackTrace()
                        }
                    }
                )
            }
        }
    }
}

/**
 * 获取地图标记图标
 */
private fun getBitmapDescriptor(context: Context): BitmapDescriptor {
    // 最简单可靠的方法，直接使用颜色作为标记
    return BitmapDescriptorFactory.fromResource(R.drawable.ic_location)
}

/**
 * 将Drawable转换为Bitmap
 */
private fun Drawable.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth.coerceAtLeast(1),
        intrinsicHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}

/**
 * 搜索周边POI
 */
class NearbyPoiSearch(private val context: Context) {
    private var poiSearch: PoiSearch? = null
    
    init {
        try {
            poiSearch = PoiSearch.newInstance()
            poiSearch?.setOnGetPoiSearchResultListener(object : OnGetPoiSearchResultListener {
                override fun onGetPoiResult(result: PoiResult?) {
                    if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                        listener?.onPoiSearchResult(emptyList())
                        return
                    }
                    
                    val poiList = result.allPoi
                    listener?.onPoiSearchResult(poiList ?: emptyList())
                }
                
                override fun onGetPoiDetailResult(result: PoiDetailResult?) {
                    // 不处理详情结果
                }
                
                override fun onGetPoiDetailResult(result: PoiDetailSearchResult?) {
                    // 不处理详情结果
                }
                
                override fun onGetPoiIndoorResult(result: PoiIndoorResult?) {
                    // 不处理室内结果
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private var listener: OnPoiSearchResultListener? = null
    
    fun setOnPoiSearchResultListener(listener: OnPoiSearchResultListener) {
        this.listener = listener
    }
    
    fun searchNearby(location: LatLng, keyword: String, radius: Int = 2000) {
        try {
            val nearbySearchOption = PoiNearbySearchOption()
                .location(location)
                .keyword(keyword)
                .radius(radius)
                .pageNum(0)
                .pageCapacity(10)
            
            poiSearch?.searchNearby(nearbySearchOption)
        } catch (e: Exception) {
            e.printStackTrace()
            listener?.onPoiSearchResult(emptyList())
        }
    }
    
    fun destroy() {
        try {
            poiSearch?.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    interface OnPoiSearchResultListener {
        fun onPoiSearchResult(poiList: List<PoiInfo>)
    }
}

/**
 * 百度定位服务
 */
class LocationService(context: Context) {
    private val locationClient: LocationClient = LocationClient(context)
    
    init {
        try {
            val option = LocationClientOption()
            option.isOpenGps = true // 打开GPS
            option.setCoorType("bd09ll") // 设置坐标类型
            option.setScanSpan(1000) // 1秒更新一次
            locationClient.locOption = option
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun registerListener(listener: BDAbstractLocationListener) {
        locationClient.registerLocationListener(listener)
    }
    
    fun start() {
        locationClient.start()
    }
    
    fun stop() {
        locationClient.stop()
    }
} 