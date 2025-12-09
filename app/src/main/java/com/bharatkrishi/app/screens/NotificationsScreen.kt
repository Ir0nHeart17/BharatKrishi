// screens/NotificationsScreen.kt
package com.bharatkrishi.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bharatkrishi.app.utils.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val notifications = listOf(
        NotificationItem(
            id = 1,
            title = LocalizationManager.get("Weather Alert"),
            message = LocalizationManager.get("Heavy rain expected in 2 days. Cover your crops!"),
            time = "2 hours ago",
            icon = Icons.Default.Warning,
            iconColor = Color(0xFFFF9800),
            isRead = false
        ),
        NotificationItem(
            id = 2,
            title = LocalizationManager.get("Pest Identified"),
            message = LocalizationManager.get("Aphids detected on tomatoes. Immediate action required."),
            time = "5 hours ago",
            icon = Icons.Default.BugReport,
            iconColor = Color(0xFFf44336),
            isRead = false
        ),
        NotificationItem(
            id = 3,
            title = LocalizationManager.get("Market Price Update"),
            message = LocalizationManager.get("Wheat prices increased by 2.4% today"),
            time = "1 day ago",
            icon = Icons.Default.TrendingUp,
            iconColor = Color(0xFF4CAF50),
            isRead = true
        ),
        NotificationItem(
            id = 4,
            title = LocalizationManager.get("Fertilizer Reminder"),
            message = LocalizationManager.get("Time to apply nitrogen fertilizer to your wheat crop"),
            time = "2 days ago",
            icon = Icons.Default.Grass,
            iconColor = Color(0xFF2196F3),
            isRead = true
        ),
        NotificationItem(
            id = 5,
            title = LocalizationManager.get("Soil Test Results"),
            message = LocalizationManager.get("Your soil test results are ready. pH level: 6.5"),
            time = "3 days ago",
            icon = Icons.Default.Science,
            iconColor = Color(0xFF9C27B0),
            isRead = true
        ),
        NotificationItem(
            id = 6,
            title = LocalizationManager.get("New Tutorial Available"),
            message = LocalizationManager.get("Learn about organic farming techniques"),
            time = "1 week ago",
            icon = Icons.Default.PlayCircle,
            iconColor = Color(0xFF607D8B),
            isRead = true
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        LocalizationManager.get("Notifications"),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    TextButton(onClick = { /* Mark all as read */ }) {
                        Text(
                            LocalizationManager.get("Mark all read"),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Notification stats
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "${notifications.count { !it.isRead }} ${LocalizationManager.get("unread notifications")}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            LocalizationManager.get("Stay updated with important farm alerts"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Notifications List
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification = notification)
                }

                // Add bottom padding
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(notification.iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    notification.icon,
                    contentDescription = notification.title,
                    tint = notification.iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Notification content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    notification.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    notification.time,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val time: String,
    val icon: ImageVector,
    val iconColor: Color,
    val isRead: Boolean
)