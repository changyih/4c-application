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

/**
 * 天气管理工具类
 * 负责获取城市天气信息、空气质量、日期和节气等信息
 */
class WeatherManager {
    companion object {
        private const val TAG = "WeatherManager"
        private const val WEATHER_API_KEY = "your_api_key_here" // 替换为实际的API密钥
        private const val WEATHER_API_URL = "https://restapi.amap.com/v3/weather/weatherInfo"
        
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
                    val url = URL("$WEATHER_API_URL?city=$cityName&key=$WEATHER_API_KEY&extensions=base")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 5000
                    
                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val reader = BufferedReader(InputStreamReader(connection.inputStream))
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()
                        
                        // 解析JSON响应
                        Log.d(TAG, "收到天气API响应: ${response.substring(0, Math.min(100, response.length))}...")
                        val jsonObject = JSONObject(response.toString())
                        val lives = jsonObject.getJSONArray("lives")
                        if (lives.length() > 0) {
                            val live = lives.getJSONObject(0)
                            val city = live.getString("city")
                            val weather = live.getString("weather")
                            val temperature = live.getString("temperature") + "°C"
                            val humidity = live.optInt("humidity", 60)
                            
                            // 这里使用模拟数据，实际应用中应从API获取
                            val airQuality = getAirQuality(humidity)
                            val airIndex = getAirIndex(humidity)
                            
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
                            
                            return@withContext weatherInfo
                        }
                    }
                    
                    // 如果API调用失败，返回默认值
                    Log.e(TAG, "天气API调用失败 (响应码: $responseCode)，使用默认值")
                    val defaultInfo = createDefaultWeatherInfo(cityName)
                    weatherCache[cityName] = Pair(defaultInfo, now)
                    return@withContext defaultInfo
                    
                } catch (e: Exception) {
                    Log.e(TAG, "获取天气信息异常", e)
                    val defaultInfo = createDefaultWeatherInfo(cityName)
                    weatherCache[cityName] = Pair(defaultInfo, now)
                    return@withContext defaultInfo
                }
            }
        }
        
        // 根据湿度生成空气质量描述
        private fun getAirQuality(humidity: Int): String {
            return when {
                humidity < 30 -> "优"
                humidity < 50 -> "良"
                humidity < 70 -> "轻度污染"
                humidity < 85 -> "中度污染"
                else -> "重度污染"
            }
        }
        
        // 根据湿度生成空气指数
        private fun getAirIndex(humidity: Int): String {
            val baseIndex = when {
                humidity < 30 -> (90..100).random()
                humidity < 50 -> (75..89).random()
                humidity < 70 -> (50..74).random()
                humidity < 85 -> (25..49).random() 
                else -> (0..24).random()
            }
            return baseIndex.toString()
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