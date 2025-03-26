package com.example.olderperson.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.olderperson.R
import com.example.olderperson.ui.theme.Green500
import com.example.olderperson.ui.theme.OrangeMain
import com.example.olderperson.ui.theme.PurpleMain
import com.example.olderperson.ui.theme.Red500

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<androidx.compose.ui.graphics.Color> = listOf(PurpleMain, OrangeMain)
) {
    val gradient = Brush.horizontalGradient(colors = colors)
    
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(gradient)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun BottomActionBar(
    onEmergencyClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onConsultClick: () -> Unit,
    onServicesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = stringResource(R.string.btn_consult),
                onClick = onConsultClick,
                modifier = Modifier.weight(1f),
                colors = listOf(PurpleMain, PurpleMain.copy(alpha = 0.7f))
            )
            
            ActionButton(
                text = stringResource(R.string.btn_services),
                onClick = onServicesClick,
                modifier = Modifier.weight(1f),
                colors = listOf(OrangeMain, OrangeMain.copy(alpha = 0.7f))
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionButton(
                text = stringResource(R.string.btn_video_call),
                onClick = onVideoCallClick,
                modifier = Modifier.weight(1f),
                colors = listOf(Green500, Green500.copy(alpha = 0.7f))
            )
            
            ActionButton(
                text = stringResource(R.string.btn_emergency),
                onClick = onEmergencyClick,
                modifier = Modifier.weight(1f),
                colors = listOf(Red500, Red500.copy(alpha = 0.7f))
            )
        }
    }
} 