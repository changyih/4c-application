package com.example.olderperson.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.olderperson.R
import com.example.olderperson.ui.theme.DarkText
import com.google.android.gms.maps.model.LatLng as GoogleLatLng

@Composable
fun LocationMapCard(
    modifier: Modifier = Modifier,
    userLocation: GoogleLatLng = GoogleLatLng(31.230416, 121.473701), // 默认上海位置
    onLocationUpdate: (GoogleLatLng) -> Unit = {}
) {
    // 使用MapLocation类包装GoogleLatLng
    val mapLocation = MapLocation.fromGoogleLatLng(userLocation)
    
    // 使用BaiduMapCard替代GoogleMap
    BaiduMapCard(
        modifier = modifier,
        userLocation = mapLocation,
        onLocationUpdate = { location ->
            // 将百度地图的位置转换回GoogleLatLng并回调
            onLocationUpdate(GoogleLatLng(location.latitude, location.longitude))
        }
    )
}

@Composable
fun LocationSection(
    userLocation: GoogleLatLng = GoogleLatLng(31.230416, 121.473701),
    onLocationUpdate: (GoogleLatLng) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LocationMapCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            userLocation = userLocation,
            onLocationUpdate = onLocationUpdate
        )
    }
} 