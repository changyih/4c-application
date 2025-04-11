package com.example.olderperson.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.net.URLEncoder
import kotlin.math.min

/**
 * 天气管理工具类
 * 负责获取城市天气信息、空气质量、日期和节气等信息
 */
class WeatherManager {
    companion object {
        private const val TAG = "WeatherManager"
        // 心知天气API 参数
        private const val SENIVERSE_API_UID = "U4AA41D8D4" // 心知天气用户ID (公钥)
        private const val SENIVERSE_API_KEY = "SUWg4oa4FvgpL_4wp" // 心知天气API密钥 (私钥)
        private const val SENIVERSE_API_BASE_URL = "https://api.seniverse.com" // 心知天气基础URL
        
        // V3版本API路径 - 正确的路径
        private const val WEATHER_NOW_API_PATH = "/v3/weather/now.json" // 实时天气接口路径
        private const val AIR_NOW_API_PATH = "/v3/air/now.json" // 空气质量接口路径
        
        // 缓存上次获取的天气信息
        private val weatherCache = mutableMapOf<String, Pair<WeatherInfo, Long>>()
        // 缓存过期时间（30分钟）
        private const val CACHE_EXPIRY_MS = 30 * 60 * 1000
        
        // 中国传统二十四节气表 (月, 日)
        private val solarTerms = mapOf(
            "立春" to Pair(2, 4),
            "雨水" to Pair(2, 19),
            "惊蛰" to Pair(3, 6),
            "春分" to Pair(3, 21),
            "清明" to Pair(4, 5),
            "谷雨" to Pair(4, 20),
            "立夏" to Pair(5, 6),
            "小满" to Pair(5, 21),
            "芒种" to Pair(6, 6),
            "夏至" to Pair(6, 21),
            "小暑" to Pair(7, 7),
            "大暑" to Pair(7, 23),
            "立秋" to Pair(8, 8),
            "处暑" to Pair(8, 23),
            "白露" to Pair(9, 8),
            "秋分" to Pair(9, 23),
            "寒露" to Pair(10, 8),
            "霜降" to Pair(10, 24),
            "立冬" to Pair(11, 8),
            "小雪" to Pair(11, 22),
            "大雪" to Pair(12, 7),
            "冬至" to Pair(12, 22),
            "小寒" to Pair(1, 6),
            "大寒" to Pair(1, 20)
        )
        
        // 默认城市天气数据
        private val defaultCityData = mapOf(
            "长春" to Triple("晴", "23°C", "良"),
            "北京" to Triple("多云", "25°C", "良"),
            "上海" to Triple("阴", "26°C", "良"),
            "广州" to Triple("小雨", "29°C", "优")
        )
        
        data class WeatherInfo(
            val city: String,
            val weather: String,
            val temperature: String,
            val airQuality: String,
            val airIndex: String,
            val date: String,
            val solarTerm: String
        )
        
        // 获取当前日期
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("yyyy年MM月dd日 E", Locale.CHINA)
            return sdf.format(Date())
        }
        
        // 获取当前节气
        fun getCurrentSolarTerm(): String {
            val calendar = Calendar.getInstance()
            val currentMonth = calendar.get(Calendar.MONTH) + 1
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            
            // 找到最接近的节气
            var nearestTerm = ""
            var minDayDiff = Int.MAX_VALUE
            
            for ((term, date) in solarTerms) {
                val termMonth = date.first
                val termDay = date.second
                
                // 如果是相同月份，直接计算日期差
                if (termMonth == currentMonth) {
                    val diff = Math.abs(currentDay - termDay)
                    if (diff < minDayDiff) {
                        minDayDiff = diff
                        nearestTerm = term
                    }
                } else {
                    // 不同月份时，计算相对于本月的距离
                    // 简化计算，认为每个月30天
                    val diffToCurrentMonth = Math.abs(termMonth - currentMonth) * 30
                    val totalDiff = diffToCurrentMonth + Math.abs(termDay - currentDay)
                    if (totalDiff < minDayDiff) {
                        minDayDiff = totalDiff
                        nearestTerm = term
                    }
                }
            }
            
            return nearestTerm
        }
        
        // 生成签名 - 心知天气API认证所需
        private fun generateSignature(path: String, params: Map<String, String>): String {
            try {
                // 按照字典序对参数进行排序
                val sortedParams = params.toSortedMap()
                
                // 构建参数字符串
                val paramStr = StringBuilder()
                sortedParams.forEach { (key, value) -> 
                    if (paramStr.isNotEmpty()) {
                        paramStr.append("&")
                    }
                    paramStr.append("$key=$value") 
                }
                
                // 构建签名原始字符串
                val signString = "GET$path?$paramStr"
                
                Log.d(TAG, "签名原始字符串: $signString")
                
                // 使用HMAC-SHA1算法进行签名
                val mac = Mac.getInstance("HmacSHA1")
                val secretKey = SecretKeySpec(SENIVERSE_API_KEY.toByteArray(), "HmacSHA1")
                mac.init(secretKey)
                val signData = mac.doFinal(signString.toByteArray())
                
                // 将签名转换为Base64编码
                val signBase64 = android.util.Base64.encodeToString(signData, android.util.Base64.NO_WRAP)
                
                // URL编码
                val encodedSign = URLEncoder.encode(signBase64, "UTF-8")
                Log.d(TAG, "生成的签名: $encodedSign")
                
                return encodedSign
            } catch (e: Exception) {
                Log.e(TAG, "生成签名异常", e)
                return ""
            }
        }
        
        // 构建心知天气API的URL - 使用V3版本的API
        private fun buildSeniverseApiUrl(path: String, location: String): String {
            // 准备公共参数
            val ttl = 1800 // 签名有效期，单位为秒 (30分钟)
            val ts = System.currentTimeMillis() / 1000 // 当前时间戳，单位为秒
            
            // 构建参数Map
            val params = mutableMapOf(
                "key" to SENIVERSE_API_KEY, // V3 API使用key参数，而不是uid和sig
                "location" to location,
                "language" to "zh-Hans", // 使用简体中文
                "unit" to "c" // 温度单位为摄氏度
            )
            
            // 构建最终URL
            val urlBuilder = StringBuilder("$SENIVERSE_API_BASE_URL$path?")
            var isFirst = true
            params.forEach { (key, value) -> 
                if (isFirst) {
                    isFirst = false
                } else {
                    urlBuilder.append("&")
                }
                urlBuilder.append("$key=$value") 
            }
            
            val finalUrl = urlBuilder.toString()
            Log.d(TAG, "API请求URL: $finalUrl")
            
            return finalUrl
        }
        
        // 异步获取天气信息
        suspend fun getWeatherInfo(context: Context, cityName: String = "长春"): WeatherInfo {
            // 检查缓存
            val now = System.currentTimeMillis()
            weatherCache[cityName]?.let { (cachedInfo, timestamp) ->
                if (now - timestamp < CACHE_EXPIRY_MS) {
                    Log.d(TAG, "使用缓存的天气数据: $cityName")
                    return cachedInfo
                }
            }
            
            return withContext(Dispatchers.IO) {
                try {
                    Log.d(TAG, "开始获取天气数据: $cityName")
                    
                    // 使用V3 API分别获取天气和空气质量数据
                    val weatherUrl = buildSeniverseApiUrl(WEATHER_NOW_API_PATH, cityName)
                    val weatherData = fetchApiData(weatherUrl)
                    
                    val airUrl = buildSeniverseApiUrl(AIR_NOW_API_PATH, cityName)
                    val airData = fetchApiData(airUrl)
                    
                    // 解析天气数据
                    var city = cityName
                    var weather = "晴"
                    var temperature = "25°C"
                    var airQuality = "良"
                    var airIndex = "80"
                    
                    if (weatherData.isNotEmpty()) {
                        try {
                            val weatherJson = JSONObject(weatherData)
                            val resultsArray = weatherJson.getJSONArray("results")
                            
                            if (resultsArray.length() > 0) {
                                val result = resultsArray.getJSONObject(0)
                                
                                // 获取位置信息
                                val location = result.getJSONObject("location")
                                city = location.getString("name")
                                
                                // 获取天气信息
                                val weatherNow = result.getJSONObject("now")
                                weather = weatherNow.getString("text")
                                temperature = weatherNow.getString("temperature") + "°C"
                                
                                Log.d(TAG, "成功解析天气数据: $city, $weather, $temperature")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "解析天气数据异常", e)
                        }
                    }
                    
                    // 解析空气质量数据
                    if (airData.isNotEmpty()) {
                        try {
                            val airJson = JSONObject(airData)
                            val resultsArray = airJson.getJSONArray("results")
                            
                            if (resultsArray.length() > 0) {
                                val result = resultsArray.getJSONObject(0)
                                
                                if (result.has("air")) {
                                    val air = result.getJSONObject("air")
                                    val cityAir = air.getJSONObject("city")
                                    airQuality = cityAir.getString("quality")
                                    airIndex = cityAir.getString("aqi")
                                    
                                    Log.d(TAG, "成功解析空气质量数据: $airQuality, $airIndex")
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "解析空气质量数据异常", e)
                        }
                    }
                    
                    // 创建天气信息对象
                    val weatherInfo = WeatherInfo(
                        city = city,
                        weather = weather,
                        temperature = temperature,
                        airQuality = airQuality,
                        airIndex = airIndex,
                        date = getCurrentDate(),
                        solarTerm = getCurrentSolarTerm()
                    )
                    
                    // 更新缓存
                    weatherCache[cityName] = Pair(weatherInfo, now)
                    
                    Log.d(TAG, "成功获取天气数据: $city, $weather, $temperature")
                    
                    return@withContext weatherInfo
                    
                } catch (e: Exception) {
                    Log.e(TAG, "获取天气信息异常", e)
                    val defaultInfo = createDefaultWeatherInfo(cityName)
                    weatherCache[cityName] = Pair(defaultInfo, now)
                    return@withContext defaultInfo
                }
            }
        }
        
        // 从API获取数据
        private fun fetchApiData(apiUrl: String): String {
            try {
                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                
                // 添加User-Agent请求头，模拟浏览器请求
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36")
                
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()
                    
                    val responseStr = response.toString()
                    val displayLength = min(100, responseStr.length)
                    Log.d(TAG, "收到API响应: ${responseStr.substring(0, displayLength)}...")
                    return responseStr
                } else {
                    // 输出错误信息
                    val errorStream = connection.errorStream
                    if (errorStream != null) {
                        val reader = BufferedReader(InputStreamReader(errorStream))
                        val errorResponse = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            errorResponse.append(line)
                        }
                        reader.close()
                        Log.e(TAG, "API错误响应: $errorResponse")
                    }
                    Log.e(TAG, "API请求失败，响应码: $responseCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "API请求异常", e)
            }
            
            return ""
        }
        
        // 创建默认天气信息（当API调用失败时使用）
        private fun createDefaultWeatherInfo(cityName: String): WeatherInfo {
            val (weather, temperature, quality) = defaultCityData[cityName] ?: defaultCityData["长春"]!!
            val airIndex = when(quality) {
                "优" -> (90..100).random().toString()
                "良" -> (75..89).random().toString()
                "轻度污染" -> (50..74).random().toString()
                else -> "80"
            }
            
            return WeatherInfo(
                city = cityName,
                weather = weather,
                temperature = temperature,
                airQuality = quality,
                airIndex = airIndex,
                date = getCurrentDate(),
                solarTerm = getCurrentSolarTerm()
            )
        }
    }
} 