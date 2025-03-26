package com.example.olderperson.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.olderperson.data.HealthData
import com.example.olderperson.data.HealthDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HealthMonitoringService(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _healthData = MutableStateFlow<Map<HealthDataType, HealthData>>(emptyMap())
    val healthData: StateFlow<Map<HealthDataType, HealthData>> = _healthData

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                when (it.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> {
                        updateHealthData(HealthDataType.HEART_RATE, it.values[0])
                    }
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        updateHealthData(HealthDataType.BODY_TEMPERATURE, it.values[0])
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun updateHealthData(type: HealthDataType, value: Float) {
        val currentData = _healthData.value.toMutableMap()
        currentData[type] = HealthData(type, value)
        _healthData.value = currentData
    }

    fun startMonitoring() {
        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(sensorEventListener)
    }
} 