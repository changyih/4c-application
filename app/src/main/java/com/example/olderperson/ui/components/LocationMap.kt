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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun LocationMapCard(
    modifier: Modifier = Modifier,
    userLocation: LatLng = LatLng(31.230416, 121.473701), // 默认上海位置
    onLocationUpdate: (LatLng) -> Unit = {}
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.location_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = DarkText
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        mapToolbarEnabled = false
                    )
                ) {
                    Marker(
                        state = MarkerState(position = userLocation),
                        title = "当前位置"
                    )
                }
            }
        }
    }
}

@Composable
fun LocationSection(
    userLocation: LatLng = LatLng(31.230416, 121.473701),
    onLocationUpdate: (LatLng) -> Unit = {},
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