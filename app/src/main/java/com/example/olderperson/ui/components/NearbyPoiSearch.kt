package com.example.olderperson.ui.components

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng

/**
 * 地点信息类（POI - Point of Interest）
 * 这个数据类可能仍被 BaiduMapView.kt 中的 NearbyPoiSearch 使用，予以保留。
 */
data class PoiInfo(
    val name: String = "",
    val address: String? = null,
    val distance: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

/* // 删除旧的、使用模拟数据的 NearbyPoiSearch 类及其相关内容

/**
 * 周边搜索类
 * 用于替代原百度地图的POI搜索
 */
class NearbyPoiSearch(private val context: Context) {
    
    private var listener: OnPoiSearchResultListener? = null
    
    // 模拟POI搜索
    fun searchNearby(location: LatLng, keyword: String, radius: Int) {
        Log.d("NearbyPoiSearch", "搜索关键词: $keyword, 半径: ${radius}米")
        
        // 返回预设的数据
        val results = generateMockResults(location, keyword)
        listener?.onPoiSearchResult(results)
    }
    
    // 生成模拟数据
    private fun generateMockResults(location: LatLng, keyword: String): List<PoiInfo> {
        // 根据不同的关键词返回不同的模拟数据
        return when (keyword) {
            "医院" -> generateHospitals(location)
            "药店" -> generatePharmacies(location)
            "餐厅" -> generateRestaurants(location)
            else -> emptyList()
        }
    }
    
    // 生成模拟医院数据
    private fun generateHospitals(location: LatLng): List<PoiInfo> {
        return listOf(
            PoiInfo("长春市人民医院", "长春市南关区亚泰大街1518号", 1200, location.latitude + 0.01, location.longitude + 0.01),
            PoiInfo("吉林大学第一医院", "长春市朝阳区新民大街71号", 1800, location.latitude - 0.01, location.longitude + 0.02),
            PoiInfo("吉林省人民医院", "长春市朝阳区工农大路1183号", 2200, location.latitude + 0.02, location.longitude - 0.01),
            PoiInfo("长春市中心医院", "长春市南关区人民大街1810号", 1500, location.latitude - 0.01, location.longitude - 0.01)
        )
    }
    
    // 生成模拟药店数据
    private fun generatePharmacies(location: LatLng): List<PoiInfo> {
        return listOf(
            PoiInfo("大参林药店", "长春市南关区南环城路126号", 800, location.latitude + 0.005, location.longitude + 0.005),
            PoiInfo("一心堂药店", "长春市朝阳区前进大街1688号", 1100, location.latitude - 0.005, location.longitude + 0.01),
            PoiInfo("老百姓大药房", "长春市绿园区西安大路1077号", 1600, location.latitude + 0.01, location.longitude - 0.005),
            PoiInfo("益丰大药房", "长春市二道区东盛大街1358号", 950, location.latitude - 0.004, location.longitude - 0.006)
        )
    }
    
    // 生成模拟餐厅数据
    private fun generateRestaurants(location: LatLng): List<PoiInfo> {
        return listOf(
            PoiInfo("东北农家菜", "长春市南关区南环城路128号", 600, location.latitude + 0.003, location.longitude + 0.004),
            PoiInfo("鑫源饺子馆", "长春市朝阳区前进大街1670号", 900, location.latitude - 0.003, location.longitude + 0.006),
            PoiInfo("老街烧烤", "长春市绿园区西安大路1055号", 1300, location.latitude + 0.006, location.longitude - 0.003),
            PoiInfo("长春小炒", "长春市二道区东盛大街1340号", 850, location.latitude - 0.002, location.longitude - 0.004)
        )
    }
    
    // 设置搜索结果监听器
    fun setOnPoiSearchResultListener(listener: OnPoiSearchResultListener) {
        this.listener = listener
    }
    
    // 销毁搜索实例
    fun destroy() {
        this.listener = null
    }
    
    // 搜索结果监听器接口
    interface OnPoiSearchResultListener {
        fun onPoiSearchResult(poiList: List<PoiInfo>)
    }
}
*/ 