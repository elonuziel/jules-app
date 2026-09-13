package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.JulesOutline
import com.example.ui.theme.JulesOutlineVariant
import com.example.ui.theme.JulesPrimary
import com.example.ui.theme.JulesSecondary
import com.example.ui.theme.JulesSurfaceContainerHigh
import com.example.ui.theme.JulesSurfaceLowest

const val JULES_LOGO_URL =
    "https://lh3.googleusercontent.com/aida/AEtjO1UCvOeFzEhEhJuEPZUVrJb1mHlprVss2t7KtEF6vTgiNqFjBWFxVvq4oWIZl9FNRCzn6NPul3qaw1nh5biILxH7I1q_SRRwdBuqyF27Crf9Mnd1meCBc6DHS3TtKzJOWfmtZufjsfusIyAw8FzOAsHAFtl2QHb-uUBAdRFzD7RIGfKWHjGl7AtauPAHg38zBeVIdoUJDzOm5mxeJNgmN5hQT-Ak61k7rco_3N86ZyC3-1ki8-tG91EBH_FD"

const val JULES_AVATAR_URL =
    "https://lh3.googleusercontent.com/aida-public/AB6AXuDuf9xNKkwqFmeN92FKsQwh4Vo6QWYFLvr8VNozdHgFOQYXl3xQAaBCzO6g4jPMvgaLQdNDnTE5nTWsVB06seNBQgB3mX0GydwW5UKG3n5MFXrIYc1sk6qa2kxpudzuE6GegoQk7T_ni3NuR9HVc6QVHe32DhyO1Etq88qqjf9o4UDSd5cHo1QRWMWNQd-yYnLAkUxQiyPJmMgOCQxhUtHD71pZ3NxutETwTR9hI87shlGzK0dcDzNu3Q"

@Composable
fun JulesTopAppBar(
    subtitle: String,
    isGitHubConnected: Boolean = true,
    maskedGitHubToken: String = "",
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {},
    onGitHubPillClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(JulesSurfaceLowest.copy(alpha = 0.92f))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Logo + App Name + Screen title
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = JULES_LOGO_URL,
                    contentDescription = "Google Jules Logo",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Jules",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = JulesSurfaceContainerHigh,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "API",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = JulesPrimary
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Right: Theme Toggle & Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Subtle GitHub status indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(
                            if (isGitHubConnected) JulesSecondary.copy(alpha = 0.12f) else JulesSurfaceContainerHigh
                        )
                        .clickable { onGitHubPillClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("github_status_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    if (isGitHubConnected) JulesSecondary else JulesOutline,
                                    shape = CircleShape
                                )
                        )
                        Text(
                            text = if (isGitHubConnected) "GitHub" else "Offline",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isGitHubConnected) JulesSecondary else JulesOutline
                        )
                    }
                }

                // Theme Toggle Quick Action Button (Sun / Moon)
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(JulesSurfaceContainerHigh)
                        .testTag("theme_quick_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Theme" else "Switch to Dark Theme",
                        tint = JulesPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Profile Avatar with circular ring border
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                        .testTag("profile_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = JULES_AVATAR_URL,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(34.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.align(Alignment.BottomCenter),
            thickness = 0.5.dp,
            color = JulesOutlineVariant.copy(alpha = 0.4f)
        )
    }
}
