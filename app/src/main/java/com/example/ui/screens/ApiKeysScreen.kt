package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GitHubTokenModal
import com.example.ui.components.JULES_AVATAR_URL
import com.example.ui.components.ManageRepositoriesModal
import com.example.ui.theme.JulesError
import com.example.ui.theme.JulesOutline
import com.example.ui.theme.JulesOutlineVariant
import com.example.ui.theme.JulesPrimary
import com.example.ui.theme.JulesPrimaryContainer
import com.example.ui.theme.JulesSecondary
import com.example.ui.theme.JulesSecondaryContainer
import com.example.ui.theme.JulesSurfaceContainer
import com.example.ui.theme.JulesSurfaceContainerHigh
import com.example.ui.theme.JulesSurfaceContainerHighest
import com.example.ui.theme.JulesSurfaceLow
import com.example.ui.theme.JulesSurfaceLowest
import com.example.ui.theme.JulesTertiary
import com.example.ui.viewmodel.JulesViewModel

@Composable
fun ApiKeysScreen(
    viewModel: JulesViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.settingsState.collectAsState()
    val copyToast by viewModel.copyToast.collectAsState()
    val testSuccess by viewModel.testConnectionSuccess.collectAsState()
    val isReposExpanded by viewModel.isReposAccordionExpanded.collectAsState()
    val showGitHubModal by viewModel.showGitHubTokenModal.collectAsState()
    val sources by viewModel.sourcesList.collectAsState()
    val isRefreshingSources by viewModel.isRefreshingSources.collectAsState()

    var isKeyRevealed by remember { mutableStateOf(false) }
    var showEditKeyDialog by remember { mutableStateOf(false) }
    var editKeyInput by remember { mutableStateOf("") }
    var showManageReposModal by remember { mutableStateOf(false) }

    val reposRotation by animateFloatAsState(
        targetValue = if (isReposExpanded) 180f else 0f,
        label = "repos_chevron"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceLow)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = JulesPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "BYOK & GitHub Settings",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "100% Client-Side Key Management. Keys are persisted on-device in secure storage and never transmitted to external proxy servers.",
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. BYOK API Key Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JULES CLIENT-SIDE API KEY (BYOK)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (settings.isJulesConfigured) JulesSecondary.copy(alpha = 0.15f) else JulesOutlineVariant.copy(alpha = 0.35f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (settings.isJulesConfigured) "Client Stored" else "Clean Slate",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (settings.isJulesConfigured) JulesSecondary else JulesOutline
                            )
                        }
                    }

                    // Key Display Bar
                    val displayedKey = when {
                        settings.byokApiKey.isBlank() -> "No Key Set (Sandbox Exploration Mode)"
                        isKeyRevealed -> settings.byokApiKey
                        else -> settings.maskedJulesKey
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayedKey,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            color = if (settings.isJulesConfigured) JulesPrimary else JulesOutline,
                            modifier = Modifier.weight(1f)
                        )

                        if (settings.isJulesConfigured) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { isKeyRevealed = !isKeyRevealed },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isKeyRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Key",
                                        tint = JulesOutline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.copyToken() },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (copyToast) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy Key",
                                        tint = if (copyToast) JulesSecondary else JulesOutline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                editKeyInput = settings.byokApiKey
                                showEditKeyDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("edit_byok_key_button")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (settings.isJulesConfigured) "Edit Key" else "Set Key",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        if (settings.isJulesConfigured) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.updateByokJulesKey("")
                                    Toast.makeText(context, "Key cleared (Clean slate)", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = JulesOutline,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.regenerateToken() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("regenerate_key_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = JulesOutline,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (settings.isJulesConfigured) "Regenerate" else "Generate Demo Key",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }
                    }
                }
            }
        }

        // 3. GITHUB TOKEN MANAGEMENT CARD
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
                    .testTag("github_token_management_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GITHUB TOKEN MANAGEMENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (settings.isGitHubConnected) JulesSecondary.copy(alpha = 0.15f) else JulesTertiary.copy(alpha = 0.15f),
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (settings.isGitHubConnected) "Connected" else "Unconfigured",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (settings.isGitHubConnected) JulesSecondary else JulesTertiary
                            )
                        }
                    }

                    // Display Masked Token (ghp_••••xxxx)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = settings.maskedGitHubToken,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = if (settings.isGitHubConnected) JulesPrimary else JulesOutline
                        )

                        Button(
                            onClick = { viewModel.showGitHubTokenModal.value = true },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JulesPrimaryContainer,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("manage_github_token_btn")
                        ) {
                            Text(
                                text = if (settings.isGitHubConnected) "Manage Token" else "Configure PAT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    Text(
                        text = "Used for PR creation, automated checks, and branch operations. Masked format: ${settings.maskedGitHubToken}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = JulesOutline
                    )
                }
            }
        }

        // 4. Jules Gateway & Diagnostics
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JULES GATEWAY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(JulesSecondary, CircleShape)
                            )
                            Text(
                                text = "${settings.apiLatencyMs}ms Ping",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesSecondary
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "https://developers.google.com/jules/api/v1alpha",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = JulesOutline
                        )
                        if (settings.isPinging) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = JulesPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(
                                onClick = { viewModel.pingGateway() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = "Ping",
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Connected Repositories & Manage Repositories
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.isReposAccordionExpanded.value = !isReposExpanded
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = JulesPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Connected Repositories (${sources.size})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = JulesOutlineVariant,
                            modifier = Modifier.rotate(reposRotation)
                        )
                    }

                    AnimatedVisibility(visible = isReposExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sources.forEach { sourceItem ->
                                RepoRowItem(sourceItem.fullName, sourceItem.defaultBranch, "Active")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showManageReposModal = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("api_keys_manage_repos_btn"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = JulesSurfaceContainerHighest,
                                        contentColor = JulesPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Manage Repositories", style = MaterialTheme.typography.labelSmall)
                                }

                                Button(
                                    onClick = {
                                        viewModel.refreshSources()
                                        Toast.makeText(context, "Repositories refreshed!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("api_keys_refresh_sources_btn"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = JulesPrimaryContainer,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    enabled = !isRefreshingSources
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isRefreshingSources) "Syncing..." else "Refresh Sources", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Webhook Automation & Alerts
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "AUTOMATION & NOTIFICATIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    SettingToggleRow(
                        title = "Push notification on PR Ready",
                        subtitle = "Notify instantly when Jules creates a verified Pull Request",
                        isChecked = settings.pushNotificationOnPr,
                        onCheckedChange = { viewModel.togglePushNotifications() }
                    )

                    SettingToggleRow(
                        title = "Notify on failed unit test suite",
                        subtitle = "Ping mobile alert if regression is detected during verification",
                        isChecked = settings.notifyOnFailedTests,
                        onCheckedChange = { viewModel.toggleNotifyOnFailedTests() }
                    )

                    SettingToggleRow(
                        title = "Require manual approval before push",
                        subtitle = "Review diff locally before remote branch creation",
                        isChecked = settings.requireManualApproval,
                        onCheckedChange = { viewModel.toggleRequireManualApproval() }
                    )
                }
            }
        }

        // Appearance & Theme Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "APPEARANCE & THEME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(JulesSurfaceContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (settings.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (settings.isDarkTheme) "Dark Interface Mode" else "Light Interface Mode",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (settings.isDarkTheme) "Developer terminal dark palette" else "Clean, high-contrast daytime light palette",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = JulesOutline
                                )
                            }
                        }

                        Switch(
                            checked = !settings.isDarkTheme,
                            onCheckedChange = { isLight -> viewModel.setTheme(!isLight) },
                            thumbContent = {
                                Icon(
                                    imageVector = if (!settings.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = JulesPrimaryContainer,
                                uncheckedThumbColor = JulesOutline,
                                uncheckedTrackColor = JulesSurfaceLowest
                            )
                        )
                    }
                }
            }
        }

        // 7. Linked Google Account
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "LINKED GOOGLE ACCOUNT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model = if (settings.gitHubUserAvatarUrl.isNotBlank()) settings.gitHubUserAvatarUrl else JULES_AVATAR_URL,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (settings.gitHubUserName.isNotBlank()) settings.gitHubUserName else if (settings.gitHubUserLogin.isNotBlank()) "@${settings.gitHubUserLogin}" else "Jules Developer",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (settings.gitHubUserEmail.isNotBlank()) settings.gitHubUserEmail else if (settings.gitHubUserLogin.isNotBlank()) "@${settings.gitHubUserLogin} (GitHub Connected)" else "GitHub: Not Configured",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                            Text(
                                text = "Jules API: ${if (settings.isJulesConfigured) "Configured (v1alpha)" else "No Key Set"}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 8. Clean Slate & Onboarding Settings
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
                    .testTag("clean_slate_onboarding_card")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ONBOARDING & CLEAN SLATE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "You can relaunch the welcome onboarding tour anytime or wipe all locally cached keys and settings to return to an unconfigured state.",
                        style = MaterialTheme.typography.bodySmall,
                        color = JulesOutline
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.reopenWelcomeScreen() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = JulesPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Welcome Guide",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.resetToCleanSlate()
                                Toast.makeText(context, "Clean slate active: all keys cleared", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = JulesTertiary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reset Clean Slate",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesTertiary
                            )
                        }
                    }
                }
            }
        }

        // 9. Save & Diagnostic Test Button
        item {
            Button(
                onClick = { viewModel.saveAndTestConnection() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_test_connection_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JulesPrimaryContainer,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (testSuccess == null) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Verify All Connections",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    } else {
                        Text(
                            text = testSuccess ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = JulesSecondary
                        )
                    }
                }
            }
        }
    }

    // Modal to input, view masked token (ghp_••••xxxx), and disconnect GitHub PAT
    if (showGitHubModal) {
        GitHubTokenModal(
            currentToken = settings.githubPatToken,
            onSaveToken = { token ->
                viewModel.updateGitHubPat(token)
            },
            onDisconnectToken = {
                viewModel.disconnectGitHubPat()
            },
            onDismiss = {
                viewModel.showGitHubTokenModal.value = false
            }
        )
    }

    // Modal explaining how to add/remove repos
    if (showManageReposModal) {
        ManageRepositoriesModal(
            onDismiss = { showManageReposModal = false },
            onTriggerRefresh = {
                viewModel.refreshSources()
                Toast.makeText(context, "Repositories refreshed!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Client-Side BYOK Jules API Key Dialog
    if (showEditKeyDialog) {
        AlertDialog(
            onDismissRequest = { showEditKeyDialog = false },
            title = { Text("Edit Client-Side Jules API Key") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Input your personal Jules API key (stored securely on this device only):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = editKeyInput,
                        onValueChange = { editKeyInput = it },
                        modifier = Modifier.fillMaxWidth().testTag("byok_key_input_field"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = JulesSurfaceLowest,
                            unfocusedContainerColor = JulesSurfaceLowest,
                            focusedBorderColor = JulesPrimary,
                            unfocusedBorderColor = JulesOutlineVariant
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editKeyInput.isNotBlank()) {
                            viewModel.updateByokJulesKey(editKeyInput.trim())
                            showEditKeyDialog = false
                            Toast.makeText(context, "Jules API Key saved on device!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer)
                ) {
                    Text("Save Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditKeyDialog = false }) {
                    Text("Cancel", color = JulesOutline)
                }
            },
            containerColor = JulesSurfaceContainer
        )
    }
}

@Composable
fun RepoRowItem(
    repo: String,
    branch: String,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = repo,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Branch: $branch",
                style = MaterialTheme.typography.labelSmall,
                color = JulesOutline
            )
        }
        Box(
            modifier = Modifier
                .background(JulesSecondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(status, style = MaterialTheme.typography.labelSmall, color = JulesSecondary)
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = JulesPrimaryContainer,
                uncheckedThumbColor = JulesOutline,
                uncheckedTrackColor = JulesSurfaceLowest
            )
        )
    }
}
