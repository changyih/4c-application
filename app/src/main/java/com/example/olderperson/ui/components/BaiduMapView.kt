package com.example.olderperson.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.model.LatLng as BaiduLatLng
import com.baidu.mapapi.search.core.SearchResult
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.PoiDetailResult
import com.baidu.mapapi.search.poi.PoiDetailSearchResult
import com.baidu.mapapi.search.poi.PoiIndoorResult
import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiSearch
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener
import com.baidu.mapapi.search.poi.PoiNearbySearchOption
import com.baidu.mapapi.search.poi.PoiSortType
import com.example.olderperson.R
import com.example.olderperson.ui.theme.DarkText
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.CoordType
import com.example.olderperson.OlderPersonApplication

/**
 * 百度地图位置坐标类，用于兼容Google LatLng到百度LatLng的转换
 */
data class MapLocation(
    val latitude: Double,
    val longitude: Double
) {
    // 转换为百度地图坐标
    fun toBaiduLatLng(): BaiduLatLng {
        return BaiduLatLng(latitude, longitude)
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
    // 添加初始化尝试计数
    var initAttempts by remember { mutableStateOf(0) }
    // 添加标志表示是否应该尝试显示地图
    var shouldShowMap by remember { mutableStateOf(true) }
    
    // 初始化地图
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            try {
                if (mapView != null) {
                    when (event) {
                        Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                        Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
                        else -> {}
                    }
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 显示错误或地图视图
            if (mapInitError != null || !shouldShowMap) {
                // 显示错误信息和重试按钮
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = "地图加载失败",
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (mapInitError != null) "地图加载失败" else "地图不可用",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = mapInitError ?: "地图服务暂时不可用，请稍后再试",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                // 重置错误状态
                                mapInitError = null
                                initAttempts++
                                // 如果超过3次尝试，就不再显示地图
                                shouldShowMap = initAttempts <= 3
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "重试",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("重试")
                            }
                        }
                        
                        if (initAttempts > 3) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "多次尝试后地图仍无法加载，请检查网络连接或重启应用",
                                fontSize = 12.sp,
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // 百度地图视图
                AndroidView(
                    factory = { ctx ->
                        try {
                            Log.d("BaiduMap", "开始初始化地图视图")
                            
                            // 尝试初始化百度地图SDK (如果尚未初始化)
                            try {
                                // 初始化百度地图 SDK
                                // 确保使用ApplicationContext而不是普通Context
                                val appContext = ctx.applicationContext
                                
                                // 检查应用程序中SDK是否已经初始化
                                if (!OlderPersonApplication.isBaiduMapAvailable()) {
                                    Log.d("BaiduMap", "百度地图SDK未在应用级别初始化，可能会导致问题")
                                } else {
                                    Log.d("BaiduMap", "百度地图SDK已在应用中初始化")
                                }
                                
                                // 无需再次调用setAgreePrivacy和initialize，这些已在Application中完成
                                // 如果应用初始化失败，这里重试也不会成功
                                
                                // 设置坐标类型 (可以安全地重复设置)
                                SDKInitializer.setCoordType(CoordType.BD09LL)
                                Log.d("BaiduMap", "百度地图SDK配置确认")
                            
                                // 创建百度地图视图
                                val view = MapView(appContext).apply {
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
                                            val icon = getBitmapDescriptor(appContext)
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
                            } catch(e: Exception) {
                                Log.e("BaiduMap", "百度地图SDK初始化错误: ${e.message}", e)
                                throw e  // 重新抛出异常，让下面的catch捕获
                            }
                        } catch (e: Exception) {
                            // 捕获地图初始化异常
                            Log.e("BaiduMap", "地图初始化错误: ${e.message}", e)
                            mapInitError = "地图初始化错误: ${e.message}"
                            e.printStackTrace()
                            
                            // 返回一个包含错误信息的FrameLayout
                            FrameLayout(ctx).apply {
                                layoutParams = FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                                )
                                // 激活界面更新
                                Handler(Looper.getMainLooper()).post {
                                    shouldShowMap = false
                                }
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
                                        val icon = getBitmapDescriptor(context.applicationContext)
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
    private val TAG = "NearbyPoiSearch"
    private var poiSearch: PoiSearch? = null
    private var listener: OnPoiSearchResultListener? = null
    private var currentKeyword: String = ""
    private var currentLocation: BaiduLatLng? = null
    private var searchAttempts = 0
    private val maxAttempts = 3
    private val appContext = context.applicationContext
    
    // 添加伴生对象，实现静态方法
    companion object {
        private var instance: NearbyPoiSearch? = null
        
        /**
         * 静态搜索方法，供外部直接调用
         */
        fun search(
            location: BaiduLatLng,
            keyword: String,
            radius: Int = 2000,
            onSuccess: (List<PoiInfo>) -> Unit,
            onError: (Int, String) -> Unit
        ) {
            try {
                Log.d("NearbyPoiSearch", "静态方法收到搜索请求: $keyword @ $location, 范围 ${radius}m")
                
                // 使用单例模式
                if (instance?.appContext == null) {
                    Log.e("NearbyPoiSearch", "实例不可用，无法执行搜索")
                    onError(-1, "搜索服务未初始化")
                    return
                }
                
                // 设置回调
                instance?.setOnPoiSearchResultListener(object : OnPoiSearchResultListener {
                    override fun onPoiSearchResult(poiList: List<PoiInfo>) {
                        Log.d("NearbyPoiSearch", "静态方法搜索完成，返回 ${poiList.size} 个结果")
                        onSuccess(poiList)
                    }
                })
                
                // 执行搜索
                instance?.searchNearby(location, keyword, radius)
            } catch (e: Exception) {
                Log.e("NearbyPoiSearch", "静态方法搜索异常: ${e.message}", e)
                onError(-2, "搜索异常: ${e.message}")
            }
        }
        
        /**
         * 初始化方法，应在应用启动时调用
         */
        fun init(context: Context) {
            if (instance == null) {
                instance = NearbyPoiSearch(context.applicationContext)
                Log.d("NearbyPoiSearch", "静态实例已初始化")
            }
        }
    }
    
    init {
        try {
            Log.d(TAG, "初始化NearbyPoiSearch")
            poiSearch = PoiSearch.newInstance()
            
            // 设置单例实例
            instance = this
            
            poiSearch?.setOnGetPoiSearchResultListener(object : OnGetPoiSearchResultListener {
                override fun onGetPoiResult(result: PoiResult?) {
                    Log.d(TAG, "原始POI搜索结果回调: ${result?.allPoi?.size ?: 0}个结果")
                    
                    if (result == null) {
                        Log.e(TAG, "POI搜索结果为空")
                        // 重试搜索
                        retrySearchIfNeeded()
                        return
                    }
                    
                    if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                        Log.e(TAG, "POI搜索错误: ${result.error}, 错误码: ${result.error.ordinal}")
                        // 重试搜索
                        retrySearchIfNeeded()
                        return
                    }
                    
                    val poiList = result.allPoi ?: emptyList()
                    Log.d(TAG, "有效POI搜索结果: ${poiList.size}个")
                    
                    if (poiList.isEmpty() && searchAttempts < maxAttempts) {
                        Log.d(TAG, "搜索结果为空，尝试扩大搜索范围")
                        retrySearchWithLargerRadius()
                        return
                    }
                    
                    // 记录搜索结果详情
                    poiList.forEachIndexed { index, poi ->
                        Log.d(TAG, "结果[$index]: 名称=${poi.name}, 地址=${poi.address}, " +
                              "坐标=(${poi.location?.latitude}, ${poi.location?.longitude}), " +
                              "距离=${poi.distance}米, UID=${poi.uid}")
                    }
                    
                    // 重置搜索尝试次数
                    searchAttempts = 0
                    
                    // 确保在主线程回调
                    Handler(Looper.getMainLooper()).post {
                        listener?.onPoiSearchResult(poiList)
                    }
                }
                
                override fun onGetPoiDetailResult(result: PoiDetailResult?) {
                    Log.d(TAG, "收到POI详情结果: ${result?.name}")
                }
                
                override fun onGetPoiDetailResult(result: PoiDetailSearchResult?) {
                    Log.d(TAG, "收到POI详情搜索结果: ${result?.poiDetailInfoList?.size ?: 0}个结果")
                }
                
                override fun onGetPoiIndoorResult(result: PoiIndoorResult?) {
                    // 不处理室内结果
                    Log.d(TAG, "收到POI室内结果")
                }
            })
            Log.d(TAG, "POI搜索监听器设置成功")
        } catch (e: Exception) {
            Log.e(TAG, "初始化错误: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    fun setOnPoiSearchResultListener(listener: OnPoiSearchResultListener) {
        this.listener = listener
        Log.d(TAG, "设置自定义POI搜索结果监听器")
    }
    
    private fun retrySearchIfNeeded() {
        if (searchAttempts < maxAttempts && currentLocation != null) {
            searchAttempts++
            Log.d(TAG, "重试搜索 (尝试 $searchAttempts/$maxAttempts)")
            
            Handler(Looper.getMainLooper()).postDelayed({
                val radius = 3000 + (searchAttempts * 2000) // 根据尝试次数增加搜索半径
                searchNearbyInternal(currentLocation!!, currentKeyword, radius)
            }, 1000)
        } else {
            searchAttempts = 0
            // 确保在主线程回调
            Handler(Looper.getMainLooper()).post {
                listener?.onPoiSearchResult(emptyList())
            }
        }
    }
    
    private fun retrySearchWithLargerRadius() {
        if (currentLocation != null) {
            searchAttempts++
            val radius = 5000 + (searchAttempts * 3000) // 大幅增加搜索半径
            Log.d(TAG, "使用更大半径重试搜索: ${radius}米 (尝试 $searchAttempts/$maxAttempts)")
            
            Handler(Looper.getMainLooper()).postDelayed({
                searchNearbyInternal(currentLocation!!, currentKeyword, radius)
            }, 1000)
        } else {
            searchAttempts = 0
            // 确保在主线程回调
            Handler(Looper.getMainLooper()).post {
                listener?.onPoiSearchResult(emptyList())
            }
        }
    }
    
    fun searchNearby(location: BaiduLatLng, keyword: String, radius: Int = 2000) {
        searchAttempts = 0
        currentKeyword = keyword
        currentLocation = location
        
        // 记录详细的位置信息
        Log.d(TAG, "开始附近搜索, 位置详情: 纬度=${location.latitude}, 经度=${location.longitude}")
        Log.d(TAG, "搜索关键词: $keyword, 搜索半径: ${radius}米")
        
        // 确保搜索开始时通知UI更新状态
        Handler(Looper.getMainLooper()).post {
            // 可以在这里发送开始搜索的通知，如果需要的话
        }
        
        // 检查当前坐标是否有效
        if (location.latitude < 1.0 && location.longitude < 1.0) {
            Log.e(TAG, "搜索位置坐标无效: (${location.latitude}, ${location.longitude})")
            Handler(Looper.getMainLooper()).post {
                listener?.onPoiSearchResult(emptyList())
            }
            return
        }
        
        // 重要：确保使用当前位置，而不是任何硬编码位置
        Log.d(TAG, "确认搜索位置: (${location.latitude}, ${location.longitude})")
        
        // 使用指定位置进行搜索
        searchNearbyInternal(location, keyword, radius)
    }
    
    private fun searchNearbyInternal(location: BaiduLatLng, keyword: String, radius: Int) {
        try {
            // 检查POI搜索实例是否可用
            if (poiSearch == null) {
                Log.e(TAG, "POI搜索实例不可用，尝试重新初始化")
                poiSearch = PoiSearch.newInstance()
                poiSearch?.setOnGetPoiSearchResultListener(object : OnGetPoiSearchResultListener {
                    override fun onGetPoiResult(result: PoiResult?) {
                        Log.d(TAG, "重新初始化后的POI搜索结果回调: ${result?.allPoi?.size ?: 0}个结果")
                        // 处理搜索结果...
                        val poiList = result?.allPoi ?: emptyList()
                        
                        // 确保在主线程回调
                        Handler(Looper.getMainLooper()).post {
                            listener?.onPoiSearchResult(poiList)
                        }
                    }
                    
                    override fun onGetPoiDetailResult(result: PoiDetailResult?) {}
                    override fun onGetPoiDetailResult(result: PoiDetailSearchResult?) {}
                    override fun onGetPoiIndoorResult(result: PoiIndoorResult?) {}
                })
            }
            
            Log.d(TAG, "开始搜索附近: $keyword, 坐标: (${location.latitude}, ${location.longitude}), 半径: ${radius}米")
            
            // 使用百度地图POI查询选项
            val nearbySearchOption = PoiNearbySearchOption()
                .location(location)
                .keyword(keyword)
                .radius(radius)
                .pageNum(0)
                .pageCapacity(50) // 增加返回结果数量到50
                .sortType(PoiSortType.distance_from_near_to_far) // 按距离从近到远排序
            
            // 记录当前搜索选项
            Log.d(TAG, "搜索选项: location=(${location.latitude}, ${location.longitude}), " +
                  "keyword=$keyword, radius=$radius, pageCapacity=50")
            
            val result = poiSearch?.searchNearby(nearbySearchOption)
            if (result == null) {
                Log.e(TAG, "搜索请求发送失败")
                // 确保在主线程回调
                Handler(Looper.getMainLooper()).post {
                    listener?.onPoiSearchResult(emptyList())
                }
            } else {
                Log.d(TAG, "搜索请求已发送, 结果状态: $result")
                
                // 在此处添加延迟以确保搜索有足够时间完成
                Handler(Looper.getMainLooper()).postDelayed({
                    if (poiSearch != null && searchAttempts == 0) {
                        // 如果几秒后仍未收到结果，发送备用搜索请求
                        searchAttempts++
                        Log.d(TAG, "发送备用搜索请求 (尝试 $searchAttempts/$maxAttempts)")
                        poiSearch?.searchNearby(nearbySearchOption)
                    }
                }, 5000)
            }
        } catch (e: Exception) {
            Log.e(TAG, "搜索出错: ${e.message}", e)
            e.printStackTrace()
            // 确保在主线程回调
            Handler(Looper.getMainLooper()).post {
                listener?.onPoiSearchResult(emptyList())
            }
        }
    }
    
    fun destroy() {
        try {
            Log.d(TAG, "销毁POI搜索实例")
            poiSearch?.destroy()
            poiSearch = null
        } catch (e: Exception) {
            Log.e(TAG, "销毁POI搜索实例时出错: ${e.message}", e)
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
    private val TAG = "LocationService"
    private val locationClient: LocationClient
    
    init {
        LocationClient.setAgreePrivacy(true)
        locationClient = LocationClient(context.applicationContext)
        
        try {
            val option = LocationClientOption().apply {
                // 设置坐标类型
                setCoorType("bd09ll")
                
                // 设置定位模式，高精度定位模式，会同时使用GPS和网络定位
                setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy)
                
                // 设置定位间隔，单位毫秒，减少为3秒以提高响应速度
                setScanSpan(3000)
                
                // 设置是否使用GPS定位
                isOpenGps = true
                
                // 设置是否需要地址信息
                setIsNeedAddress(true)
                
                // 设置是否需要设备方向
                setNeedDeviceDirect(true)
                
                // 设置是否需要定位问题诊断信息
                setIsNeedLocationDescribe(true)
                
                // 设置是否需要位置语义化信息，设置为true可以返回地址信息
                setIsNeedLocationPoiList(true)
                
                // 设置定位超时时间
                setLocationNotify(true)
                
                // 使用最新的GPS数据，不使用缓存
                setEnableSimulateGps(false)
                
                // 设置返回位置精度半径
                setIsNeedAltitude(true)
                
                // 设置输出的坐标为百度坐标系，即BD09坐标
                setCoorType("bd09ll")
                
                // 为了获取更准确的POI信息
                setIsNeedLocationPoiList(true)
                
                // 设置GPS优先
                setOpenGps(true)
                
                // 设置为接口频率较高的使用场景
                setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy)
                
                // 每次定位都回调，频繁更新以确保位置准确性
                setOnceLocation(false)
                setScanSpan(2000)  // 2秒一次
            }
            
            locationClient.locOption = option
            Log.d(TAG, "定位服务初始化完成")
        } catch (e: Exception) {
            Log.e(TAG, "定位服务初始化失败: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    fun registerListener(listener: BDAbstractLocationListener) {
        try {
            locationClient.registerLocationListener(listener)
            Log.d(TAG, "注册定位监听器成功")
        } catch (e: Exception) {
            Log.e(TAG, "注册定位监听器失败: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    fun start() {
        try {
            if (!locationClient.isStarted) {
                locationClient.start()
                Log.d(TAG, "定位服务启动成功")
                
                // 立即进行一次定位
                locationClient.requestLocation()
                Log.d(TAG, "请求立即定位")
                
                // 再次请求定位确保获取到最精确的位置
                Handler(Looper.getMainLooper()).postDelayed({
                    locationClient.requestLocation()
                    Log.d(TAG, "再次请求定位以确保准确性")
                }, 1000)
            } else {
                Log.d(TAG, "定位服务已经启动，手动请求一次定位")
                locationClient.requestLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "定位服务启动失败: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    fun stop() {
        try {
            locationClient.stop()
            Log.d(TAG, "定位服务停止")
        } catch (e: Exception) {
            Log.e(TAG, "定位服务停止失败: ${e.message}", e)
            e.printStackTrace()
        }
    }
}

/**
 * 百度地图视图组件
 */
@Composable
fun BaiduMapView(
    poiList: List<PoiInfo>,
    centerLatLng: BaiduLatLng = BaiduLatLng(43.90200, 125.27900), // 更新为吉林大学前卫南区北苑一公寓的精确坐标
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // 创建地图视图
    val mapView = remember { BaiduMapViewContainer(context) }
    
    // 生命周期管理
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    // 显示POI标记
    LaunchedEffect(poiList) {
        mapView.showPois(poiList, centerLatLng)
    }
    
    // 渲染地图
    AndroidView(
        factory = { mapView },
        modifier = modifier.fillMaxSize()
    )
}

/**
 * 百度地图视图容器
 */
class BaiduMapViewContainer(context: Context) : FrameLayout(context) {
    
    private val mapView: MapView
    private val baiduMap: BaiduMap
    private val markers = mutableListOf<Marker>()
    private val appContext = context.applicationContext
    
    init {
        // 加载地图视图
        val view = LayoutInflater.from(appContext).inflate(R.layout.layout_baidu_map, this, true)
        
        // 获取地图对象
        mapView = findViewById(R.id.bmapView)
        baiduMap = mapView.map
        
        // 配置地图
        baiduMap.apply {
            // 设置地图类型为普通地图
            mapType = BaiduMap.MAP_TYPE_NORMAL
            
            // 启用缩放控件
            uiSettings.isZoomGesturesEnabled = true
            
            // 启用指南针
            uiSettings.isCompassEnabled = true
            
            // 设置缩放级别
            setMapStatus(MapStatusUpdateFactory.zoomTo(15f))
            
            // 开启定位图层
            isMyLocationEnabled = true
        }
        
        Log.d("BaiduMapView", "初始化完成")
    }
    
    /**
     * 显示POI标记
     */
    fun showPois(poiList: List<PoiInfo>, centerLatLng: BaiduLatLng) {
        // 清除之前的标记
        clearMarkers()
        
        if (poiList.isEmpty()) {
            // 如果POI列表为空，仅居中显示地图
            Log.d("BaiduMapView", "POI列表为空，仅居中到位置: $centerLatLng")
            moveTo(centerLatLng)
            return
        }
        
        Log.d("BaiduMapView", "显示${poiList.size}个POI标记")
        
        // 添加POI标记
        poiList.forEachIndexed { index, poi ->
            val location = poi.location
            if (location == null) {
                Log.e("BaiduMapView", "POI位置为空: ${poi.name}")
                return@forEachIndexed
            }
            
            Log.d("BaiduMapView", "添加标记: ${poi.name} 在 (${location.latitude}, ${location.longitude}), 距离: ${poi.distance}米")
            
            try {
                // 根据POI类型选择不同的图标
                val iconResId = when {
                    poi.name.contains("医院") || poi.name.contains("诊所") -> R.drawable.ic_hospital
                    poi.name.contains("药") -> R.drawable.ic_pharmacy
                    poi.name.contains("餐") || poi.name.contains("饭店") || poi.name.contains("食") -> R.drawable.ic_restaurant
                    else -> R.drawable.ic_location
                }
                
                // 创建标记选项
                val options = MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.fromResource(iconResId))
                    .zIndex(index)
                    .title(poi.name)
                
                // 添加标记
                val marker = (baiduMap.addOverlay(options) as Marker).also {
                    it.setAnchor(0.5f, 1.0f) // 设置锚点
                }
                
                markers.add(marker)
            } catch (e: Exception) {
                Log.e("BaiduMapView", "添加标记失败: ${e.message}")
            }
        }
        
        // 设置地图视图以显示所有POI
        if (markers.size == 1) {
            // 单个POI - 直接设置位置和缩放级别
            val poi = poiList[0]
            val location = poi.location
            if (location != null) {
                Log.d("BaiduMapView", "居中到单个POI: ${poi.name} 在 $location")
                val update = MapStatusUpdateFactory.newLatLngZoom(location, 16f)
                baiduMap.setMapStatus(update)
            } else {
                moveTo(centerLatLng)
            }
        } else if (markers.size > 1) {
            // 多个POI - 调整以显示所有标记
            try {
                // 计算所有标记的平均位置
                val validMarkers = markers.filter { it.position != null }
                if (validMarkers.isNotEmpty()) {
                    val sumLat = validMarkers.sumOf { it.position.latitude }
                    val sumLng = validMarkers.sumOf { it.position.longitude }
                    val avgLat = sumLat / validMarkers.size
                    val avgLng = sumLng / validMarkers.size
                    val centerPos = BaiduLatLng(avgLat, avgLng)
                    
                    // 根据POI数量和分布确定合适的缩放级别
                    val zoomLevel = when {
                        validMarkers.size <= 2 -> 15f
                        validMarkers.size <= 5 -> 14f
                        else -> 13f
                    }
                    
                    Log.d("BaiduMapView", "设置多POI视图: 中心=$centerPos, 缩放=$zoomLevel")
                    val update = MapStatusUpdateFactory.newLatLngZoom(centerPos, zoomLevel)
                    baiduMap.setMapStatus(update)
                } else {
                    moveTo(centerLatLng)
                }
            } catch (e: Exception) {
                Log.e("BaiduMapView", "计算多POI视图失败: ${e.message}")
                moveTo(centerLatLng)
            }
        }
        
        // 配置标记点击事件
        baiduMap.setOnMarkerClickListener(object : BaiduMap.OnMarkerClickListener {
            override fun onMarkerClick(marker: Marker): Boolean {
                try {
                    // 移动到被点击的标记位置
                    val update = MapStatusUpdateFactory.newLatLng(marker.position)
                    baiduMap.animateMapStatus(update)
                    
                    // 显示标记标题
                    val title = marker.title
                    Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
                    Log.d("BaiduMapView", "点击标记: $title")
                } catch (e: Exception) {
                    Log.e("BaiduMapView", "处理标记点击失败: ${e.message}")
                }
                return true
            }
        })
    }
    
    /**
     * 移动地图到指定位置
     */
    private fun moveTo(latLng: BaiduLatLng) {
        Log.d("BaiduMapView", "移动到位置: $latLng")
        val update = MapStatusUpdateFactory.newLatLng(latLng)
        baiduMap.setMapStatus(update)
    }
    
    /**
     * 清除所有标记
     */
    private fun clearMarkers() {
        Log.d("BaiduMapView", "清除${markers.size}个标记")
        baiduMap.clear()
        markers.clear()
    }
    
    /**
     * 生命周期方法：恢复
     */
    fun onResume() {
        mapView.onResume()
        Log.d("BaiduMapView", "onResume")
    }
    
    /**
     * 生命周期方法：暂停
     */
    fun onPause() {
        mapView.onPause()
        Log.d("BaiduMapView", "onPause")
    }
    
    /**
     * 生命周期方法：销毁
     */
    fun onDestroy() {
        baiduMap.isMyLocationEnabled = false
        mapView.onDestroy()
        Log.d("BaiduMapView", "onDestroy")
    }
} 