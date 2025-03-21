package com.youssef.weatherforcast.Navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favourite,
        BottomNavItem.Alerts,
        BottomNavItem.Settings
    )

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF1E3A8A), Color(0xFF4F46E5))
    )

    NavigationBar(
        modifier = Modifier
            .height(75.dp)
            .fillMaxWidth()
            .background(gradientBrush),
        containerColor = Color.Transparent,
        tonalElevation = 8.dp
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            var isPressed by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.92f else 1f,
                animationSpec = tween(durationMillis = 120),
                label = "Scale Animation"
            )

            val isSelected = currentRoute == item.route
            val iconColor = when {
                item is BottomNavItem.Favourite && isSelected -> Color.Red
                isSelected -> Color.Cyan
                else -> Color.LightGray
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    isPressed = true
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Icon(
                            item.icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier
                                .scale(scale)
                                .size(28.dp)
                        )
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", Icons.Filled.Home)
    object Favourite : BottomNavItem("favourite", Icons.Filled.Favorite)
    object Alerts : BottomNavItem("alerts", Icons.Filled.Notifications)
    object Settings : BottomNavItem("settings", Icons.Filled.Settings)
}
