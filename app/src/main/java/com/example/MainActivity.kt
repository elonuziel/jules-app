package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GitHubTokenModal
import com.example.ui.components.JULES_AVATAR_URL
import com.example.ui.components.JulesBottomNavBar
import com.example.ui.components.JulesTopAppBar
import com.example.ui.screens.ApiKeysScreen
import com.example.ui.screens.LiveDiffScreen
import com.example.ui.screens.NewTaskScreen
import com.example.ui.screens.SessionsScreen
import com.example.ui.screens.SourcesScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.JulesOutline
import com.example.ui.theme.JulesOutlineVariant
import com.example.ui.theme.JulesPrimary
import com.example.ui.theme.JulesPrimaryContainer
import com.example.ui.theme.JulesSecondary
import com.example.ui.theme.JulesSurfaceContainer
import com.example.ui.theme.JulesSurfaceContainerHigh
import com.example.ui.theme.JulesSurfaceLowest
import com.example.ui.theme.JulesTertiary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.JulesViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: JulesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by viewModel.settingsState.collectAsState()
            val currentTabIndex by viewModel.currentTabIndex.collectAsState()
            val showGitHubModal by viewModel.showGitHubTokenModal.collectAsState()
            var showProfileDialog by remember { mutableStateOf(false) }

            val subtitle = when (currentTabIndex) {
                0 -> "Focus Workspace"
                1 -> "Sources & Repos"
                2 -> "New Task"
                3 -> "Live Diff"
                4 -> "Settings"
                else -> "Workspace"
            }

            MyApplicationTheme(darkTheme = settings.isDarkTheme) {
                Crossfade(
                    targetState = settings.hasCompletedWelcome,
                    animationSpec = tween(300),
                    label = "welcome_flow"
                ) { hasCompleted ->
                    if (!hasCompleted) {
                        WelcomeScreen(
                            viewModel = viewModel,
                            onEnterWorkspace = {
                                // Handled by ViewModel completeWelcome
                            }
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                            topBar = {
                                JulesTopAppBar(
                                    subtitle = subtitle,
                                    isGitHubConnected = settings.isGitHubConnected,
                                    maskedGitHubToken = settings.maskedGitHubToken,
                                    isDarkTheme = settings.isDarkTheme,
                                    onToggleTheme = {
                                        viewModel.setTheme(!settings.isDarkTheme)
                                    },
                                    onGitHubPillClick = {
                                        viewModel.showGitHubTokenModal.value = true
                                    },
                                    onProfileClick = { showProfileDialog = true }
                                )
                            },
                            bottomBar = {
                                JulesBottomNavBar(
                                    selectedTabIndex = currentTabIndex,
                                    onTabSelected = { viewModel.selectTab(it) }
                                )
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                Crossfade(
                                    targetState = currentTabIndex,
                                    animationSpec = tween(250),
                                    label = "screen_transition"
                                ) { tab ->
                                    when (tab) {
                                        0 -> SessionsScreen(
                                            viewModel = viewModel,
                                            onNavigateToNewTask = { viewModel.selectTab(2) },
                                            onNavigateToLiveDiff = { viewModel.selectTab(3) }
                                        )
                                        1 -> SourcesScreen(
                                            viewModel = viewModel,
                                            onNavigateToNewTaskWithRepo = { repo ->
                                                viewModel.selectedRepo.value = repo
                                                viewModel.selectTab(2)
                                            }
                                        )
                                        2 -> NewTaskScreen(viewModel = viewModel)
                                        3 -> LiveDiffScreen(viewModel = viewModel)
                                        4 -> ApiKeysScreen(viewModel = viewModel)
                                    }
                                }
                            }

                            if (showProfileDialog) {
                                ProfileDialog(
                                    email = settings.accountEmail,
                                    gcpProject = settings.gcpProjectId,
                                    isDarkTheme = settings.isDarkTheme,
                                    onToggleTheme = { viewModel.setTheme(!settings.isDarkTheme) },
                                    onReopenWelcome = {
                                        showProfileDialog = false
                                        viewModel.reopenWelcomeScreen()
                                    },
                                    onResetCleanSlate = {
                                        showProfileDialog = false
                                        viewModel.resetToCleanSlate()
                                    },
                                    onDismiss = { showProfileDialog = false }
                                )
                            }

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
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDialog(
    email: String,
    gcpProject: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onReopenWelcome: () -> Unit,
    onResetCleanSlate: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AsyncImage(
                    model = JULES_AVATAR_URL,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, JulesOutlineVariant, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = "Engineering Lead",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JulesSurfaceContainerHigh, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("GCP Cloud Project:", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                            Text(gcpProject, style = MaterialTheme.typography.labelSmall, color = JulesPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Agent Engine:", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                            Text("Gemini 1.5 Pro", style = MaterialTheme.typography.labelSmall, color = JulesSecondary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Quota Usage:", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                            Text("18 / 50 daily runs", style = MaterialTheme.typography.labelSmall, color = JulesTertiary)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JulesSurfaceContainerHigh, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                            contentDescription = null,
                            tint = JulesPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isDarkTheme) "Dark Interface Mode" else "Light Interface Mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                            contentDescription = "Switch Theme",
                            tint = JulesPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onReopenWelcome,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = JulesPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Revisit Welcome Guide",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OutlinedButton(
                    onClick = onResetCleanSlate,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = JulesPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reset to Clean Slate (Clear Keys)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer)
            ) {
                Text("Done")
            }
        },
        containerColor = JulesSurfaceContainer
    )
}

