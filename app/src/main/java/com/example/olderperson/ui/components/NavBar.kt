package com.example.olderperson.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.olderperson.R
import com.example.olderperson.ui.theme.OrangeMain
import com.example.olderperson.ui.theme.PurpleMain

enum class NavSection {
    HEALTH_DATA,
    HEALTH_PLAN,
    HEALTH_TIPS,
    FAMILY
}

@Composable
fun GradientNavBar(
    currentSection: NavSection,
    onSectionSelect: (NavSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(PurpleMain, OrangeMain),
        startX = 0f,
        endX = 1000f
    )
    
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // 渐变标题栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(gradient)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        // 导航选项卡
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(
                title = stringResource(R.string.nav_health_data),
                isSelected = currentSection == NavSection.HEALTH_DATA,
                onClick = { onSectionSelect(NavSection.HEALTH_DATA) }
            )
            
            NavItem(
                title = stringResource(R.string.nav_health_plan),
                isSelected = currentSection == NavSection.HEALTH_PLAN,
                onClick = { onSectionSelect(NavSection.HEALTH_PLAN) }
            )
            
            NavItem(
                title = stringResource(R.string.nav_health_tips),
                isSelected = currentSection == NavSection.HEALTH_TIPS,
                onClick = { onSectionSelect(NavSection.HEALTH_TIPS) }
            )
            
            NavItem(
                title = stringResource(R.string.nav_family),
                isSelected = currentSection == NavSection.FAMILY,
                onClick = { onSectionSelect(NavSection.FAMILY) }
            )
        }
        
        Divider()
    }
}

@Composable
fun NavItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isSelected) PurpleMain else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val weight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = weight
            )
        )
        
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 2.dp)
                    .background(PurpleMain)
            )
        }
    }
} 