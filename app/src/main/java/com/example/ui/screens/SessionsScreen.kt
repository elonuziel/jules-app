package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.OpenInNew
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.model.PullRequestStatus
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.ui.components.EnhancedPullRequestCard
import com.example.ui.components.SessionDrawerSheet
import com.example.ui.theme.JulesDiffAdditionBg
import com.example.ui.theme.JulesError
import com.example.ui.theme.JulesOutline
import com.example.ui.theme.JulesOutlineVariant
import com.example.ui.theme.JulesPrimary
import com.example.ui.theme.JulesPrimaryContainer
import com.example.ui.theme.JulesSecondary
import com.example.ui.theme.JulesSurfaceContainer
import com.example.ui.theme.JulesSurfaceContainerHigh
import com.example.ui.theme.JulesSurfaceContainerHighest
import com.example.ui.theme.JulesSurfaceLow
import com.example.ui.theme.JulesSurfaceLowest
import com.example.ui.theme.JulesTertiary
import com.example.ui.viewmodel.JulesViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    viewModel: JulesViewModel,
    onNavigateToNewTask: () -> Unit,
    onNavigateToLiveDiff: () -> Unit
) {
    val context = LocalContext.current
    val filteredList by viewModel.filteredSessions.collectAsState()
    val allList by viewModel.allSessions.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val currentFilter by viewModel.selectedFilter.collectAsState()
    val activeDrawerSession by viewModel.activeDrawerSession.collectAsState()
    val isRefreshing by viewModel.isRefreshingSessions.collectAsState()
    val connectivityError by viewModel.connectivityErrorMessage.collectAsState()
    val settings by viewModel.settingsState.collectAsState()

    var focusedSessionId by remember { mutableStateOf<String?>(null) }
    var isQueueExpanded by remember { mutableStateOf(true) }

    // Identify the current session's active task to highlight
    val activeSession = (focusedSessionId?.let { id -> allList.firstOrNull { it.id == id } })
        ?: allList.firstOrNull { it.status == SessionStatus.RUNNING || it.status == SessionStatus.PATCHING }
        ?: allList.firstOrNull { it.status == SessionStatus.NEEDS_REVIEW }
        ?: allList.firstOrNull()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 0. Connectivity or API Key Error Banner
            if (connectivityError != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(JulesError.copy(alpha = 0.12f))
                            .border(1.dp, JulesError.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = JulesError,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = connectivityError ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(
                                onClick = { viewModel.dismissConnectivityError() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = JulesOutline,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Connect Google Jules API Banner (shown when API key is not configured)
            if (!settings.isJulesConfigured) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("connect_jules_api_banner"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = JulesSurfaceContainer),
                        border = BorderStroke(1.dp, JulesPrimary.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(JulesPrimaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Key,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Connect Google Jules API",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Enter your API key to access your real repositories and live tasks.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            var bannerKeyInput by remember { mutableStateOf("") }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = bannerKeyInput,
                                    onValueChange = { bannerKeyInput = it },
                                    placeholder = { Text("Paste Jules API key...", style = MaterialTheme.typography.bodySmall) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = JulesSurfaceLowest,
                                        unfocusedContainerColor = JulesSurfaceLowest
                                    )
                                )
                                Button(
                                    onClick = {
                                        if (bannerKeyInput.isNotBlank()) {
                                            viewModel.updateByokJulesKey(bannerKeyInput)
                                            Toast.makeText(context, "Connecting to Jules API...", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Please enter an API key", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Connect")
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val openUrl = "https://jules.google.com/settings#api"
                                Text(
                                    text = "Get key from Jules settings ↗",
                                    style = MaterialTheme.typography.labelSmall.copy(color = JulesPrimary),
                                    modifier = Modifier.clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(openUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, openUrl, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                                TextButton(
                                    onClick = {
                                        viewModel.loadDemoData()
                                        Toast.makeText(context, "Loaded demo sample data", Toast.LENGTH_SHORT).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Load Demo Mode", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                                }
                            }
                        }
                    }
                }
            }

            // 1. Clean Focus Workspace Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Focus Workspace",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (activeSession != null) "Current Active Task: #${activeSession.id}" else "Ready for new tasks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.refreshSessions() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Sessions",
                                tint = if (isRefreshing) JulesSecondary else JulesOutline,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Live pulse indicator
                        val runningCount = allList.count { it.status == SessionStatus.RUNNING || it.status == SessionStatus.PATCHING }
                        Box(
                            modifier = Modifier
                                .background(
                                    if (runningCount > 0) JulesSecondary.copy(alpha = 0.12f) else JulesSurfaceContainerHigh,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (runningCount > 0) JulesSecondary else JulesOutline, CircleShape)
                                )
                                Text(
                                    text = if (runningCount > 0) "$runningCount Active" else "Standing By",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = if (runningCount > 0) JulesSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Optional refresh loading bar
            if (isRefreshing) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = JulesPrimary,
                        trackColor = JulesSurfaceContainerHigh
                    )
                }
            }

            // 2. Current Session's Active Task Highlight Card
            if (activeSession != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("active_task_focus_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = JulesSurfaceContainer
                        ),
                        border = BorderStroke(1.5.dp, JulesPrimary.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Header badge row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(JulesPrimaryContainer, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "IN FOCUS",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(JulesSurfaceLowest, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = activeSession.category.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = JulesPrimary
                                        )
                                    }
                                }

                                Text(
                                    text = "#${activeSession.id}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = JulesOutline
                                )
                            }

                            // Active Task Title
                            Text(
                                text = activeSession.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Target Repo & Branch
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Source,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = activeSession.repo,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesOutlineVariant
                                )
                                Text(
                                    text = activeSession.branch,
                                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                    color = JulesPrimary
                                )
                            }

                            // Prompt / Objective
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(JulesSurfaceLowest)
                                    .padding(10.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = JulesTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = activeSession.prompt,
                                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Live Execution Progress
                            when (activeSession.status) {
                                SessionStatus.RUNNING, SessionStatus.PATCHING -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Sync,
                                                    contentDescription = null,
                                                    tint = JulesSecondary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = activeSession.currentStep,
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                    color = JulesSecondary
                                                )
                                            }
                                            Text(
                                                text = "${activeSession.progressPercent}%",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                                color = JulesSecondary
                                            )
                                        }

                                        LinearProgressIndicator(
                                            progress = { activeSession.progressPercent / 100f },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = JulesSecondary,
                                            trackColor = JulesSurfaceLowest
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = activeSession.testSuiteInfo,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = JulesOutline
                                            )
                                            Text(
                                                text = activeSession.etaRemaining,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = JulesOutline
                                            )
                                        }
                                    }
                                }
                                SessionStatus.NEEDS_REVIEW -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(JulesTertiary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Pull Request #${activeSession.prNumber} Generated",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "+${activeSession.diffAdded} additions • -${activeSession.diffRemoved} deletions • ${activeSession.testSuiteInfo}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = JulesOutline
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(JulesTertiary, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = "READY",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                SessionStatus.COMPLETED -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(JulesSecondary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = JulesSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Task completed, tested, and ready for deployment.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                                SessionStatus.FAILED -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(JulesError.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = JulesError,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Task execution failed. Inspect logs for details.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = JulesError
                                        )
                                    }
                                }
                                SessionStatus.PAUSED -> {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(JulesSurfaceContainerHighest, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = JulesOutline,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Task execution paused.",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Prominent Direct Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activeSession?.let { viewModel.loadDiffForSession(it) }
                                        onNavigateToLiveDiff()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("focus_inspect_diff_btn"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = JulesPrimaryContainer,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Terminal,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Inspect Diff",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openSessionDrawer(activeSession) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("focus_session_details_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    contentPadding = PaddingValues(vertical = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MergeType,
                                        contentDescription = null,
                                        tint = JulesPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PR & Details",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    // Empty workspace state
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = JulesSurfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(JulesSurfaceContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "Workspace Idle",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Launch a task to start autonomous test execution and code synthesis.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = onNavigateToNewTask,
                                colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer)
                            ) {
                                Text("+ Launch New Task")
                            }
                        }
                    }
                }
            }

            // 3. Task Switcher & History (Clean, Collapsible / Minimalist)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isQueueExpanded = !isQueueExpanded }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "All Sessions & Switcher (${allList.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isQueueExpanded) "Tap card to switch current focus" else "Tap to show full task queue & filters",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }

                        Icon(
                            imageVector = if (isQueueExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Queue",
                            tint = JulesOutline
                        )
                    }

                    AnimatedVisibility(visible = isQueueExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Search Box
                            OutlinedTextField(
                                value = query,
                                onValueChange = { viewModel.searchQuery.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("session_search_input"),
                                placeholder = {
                                    Text(
                                        "Filter repositories, branches, or tasks...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = JulesOutline
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = JulesPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (query.isNotEmpty()) {
                                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = JulesSurfaceContainer,
                                    unfocusedContainerColor = JulesSurfaceContainer,
                                    focusedBorderColor = JulesPrimary,
                                    unfocusedBorderColor = JulesOutlineVariant.copy(alpha = 0.5f),
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            // Filter Chips Row
                            val runningCount = allList.count { it.status == SessionStatus.RUNNING || it.status == SessionStatus.PATCHING }
                            val reviewCount = allList.count { it.status == SessionStatus.NEEDS_REVIEW }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterPill(
                                    label = "All (${allList.size})",
                                    isSelected = currentFilter == "all",
                                    dotColor = null,
                                    onClick = { viewModel.selectedFilter.value = "all" },
                                    tag = "filter_all"
                                )
                                FilterPill(
                                    label = "Running ($runningCount)",
                                    isSelected = currentFilter == "running",
                                    dotColor = JulesSecondary,
                                    onClick = { viewModel.selectedFilter.value = "running" },
                                    tag = "filter_running"
                                )
                                FilterPill(
                                    label = "Needs Review ($reviewCount)",
                                    isSelected = currentFilter == "needs-review",
                                    dotColor = JulesTertiary,
                                    onClick = { viewModel.selectedFilter.value = "needs-review" },
                                    tag = "filter_needs_review"
                                )
                                val completedCount = allList.count { it.status == SessionStatus.COMPLETED }
                                FilterPill(
                                    label = "Completed ($completedCount)",
                                    isSelected = currentFilter == "completed",
                                    dotColor = null,
                                    onClick = { viewModel.selectedFilter.value = "completed" },
                                    tag = "filter_completed"
                                )
                            }
                        }
                    }
                }
            }

            // 4. Session Cards (Rendered when expanded, or if user searches)
            if (isQueueExpanded || query.isNotEmpty()) {
                if (filteredList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(JulesSurfaceContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (allList.isEmpty()) Icons.Default.SmartToy else Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = if (allList.isEmpty()) "No Sessions Found" else "No matching sessions",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (allList.isEmpty())
                                    "Dispatch your first coding task to Google Jules"
                                else
                                    "Try clearing filters or search queries",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (allList.isEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = onNavigateToNewTask,
                                    colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Create New Task")
                                }
                            }
                        }
                    }
                } else {
                    items(filteredList, key = { it.id }) { session ->
                        SessionCard(
                            session = session,
                            onViewDiff = {
                                viewModel.loadDiffForSession(session)
                                onNavigateToLiveDiff()
                            },
                            onReviewGithub = { viewModel.openSessionDrawer(session) },
                            onLiveWorkspace = {
                                viewModel.loadDiffForSession(session)
                                onNavigateToLiveDiff()
                            },
                            onCardClick = {
                                focusedSessionId = session.id
                                Toast.makeText(context, "Switched focus to #${session.id}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button for New Task
        FloatingActionButton(
            onClick = onNavigateToNewTask,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 76.dp, end = 16.dp)
                .testTag("fab_new_task"),
            containerColor = JulesPrimaryContainer,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AddBox,
                    contentDescription = "New Agent Task",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "New Agent Task",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }

    // Session Drawer: Enhanced Pull Request Card & Drawer
    activeDrawerSession?.let { drawerSession ->
        SessionDrawerSheet(
            session = drawerSession,
            onDismiss = { viewModel.closeSessionDrawer() },
            onApprovePr = { s ->
                viewModel.approvePr(s)
                Toast.makeText(context, "PR #${s.prNumber.ifEmpty { "412" }} Approved ✓", Toast.LENGTH_SHORT).show()
            },
            onMergePr = { s ->
                viewModel.mergePr(s)
                Toast.makeText(context, "PR #${s.prNumber.ifEmpty { "412" }} Merged into ${s.targetBaseBranch}!", Toast.LENGTH_SHORT).show()
            },
            onDeleteBranch = { s ->
                viewModel.deleteBranch(s)
                Toast.makeText(context, "Branch ${s.branch} deleted successfully!", Toast.LENGTH_SHORT).show()
            },
            onInspectDiff = { s ->
                viewModel.closeSessionDrawer()
                viewModel.loadDiffForSession(s)
                onNavigateToLiveDiff()
            },
            onApprovePlan = { s ->
                viewModel.approveSessionPlan(s.id)
                Toast.makeText(context, "Plan Approved for #${s.id}! Jules will now synthesize patch.", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun FleetOverviewCard(
    activeCount: Int,
    prCount: Int,
    testRate: String = "98.4%",
    latency: String = "142ms"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fleet_overview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = JulesSurfaceContainer
        ),
        border = BorderStroke(1.dp, JulesOutlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(JulesSecondary, CircleShape)
                    )
                    Text(
                        text = "AGENT FLEET STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = JulesSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Autonomous Ready",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = JulesSecondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FleetStatItem(
                    label = "Active Agents",
                    value = activeCount.toString(),
                    icon = Icons.Default.SmartToy,
                    color = JulesSecondary
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(JulesOutlineVariant.copy(alpha = 0.35f))
                )
                FleetStatItem(
                    label = "PRs Pending",
                    value = prCount.toString(),
                    icon = Icons.Default.MergeType,
                    color = JulesPrimary
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(JulesOutlineVariant.copy(alpha = 0.35f))
                )
                FleetStatItem(
                    label = "Tests Pass",
                    value = testRate,
                    icon = Icons.Default.Verified,
                    color = JulesSecondary
                )
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(JulesOutlineVariant.copy(alpha = 0.35f))
                )
                FleetStatItem(
                    label = "API Latency",
                    value = latency,
                    icon = Icons.Default.Speed,
                    color = JulesTertiary
                )
            }
        }
    }
}

@Composable
fun FleetStatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(JulesSurfaceContainer, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = subtitleColor
                )
            }
        }
    }
}

@Composable
fun FilterPill(
    label: String,
    isSelected: Boolean,
    dotColor: Color?,
    onClick: () -> Unit,
    tag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(9999.dp))
            .background(
                color = if (isSelected) JulesPrimary.copy(alpha = 0.14f) else JulesSurfaceContainer,
                shape = RoundedCornerShape(9999.dp)
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) JulesPrimary else JulesOutlineVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(9999.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(tag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(dotColor, CircleShape)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = if (isSelected) JulesPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SessionCard(
    session: SessionItem,
    onViewDiff: () -> Unit,
    onReviewGithub: () -> Unit,
    onLiveWorkspace: () -> Unit,
    onCardClick: () -> Unit = onReviewGithub
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "spin_angle"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("session_card_${session.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = JulesSurfaceContainer
        ),
        border = BorderStroke(1.dp, JulesOutlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Repo, branch & status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Source,
                            contentDescription = null,
                            tint = JulesOutline,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = session.repo,
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesPrimary
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutlineVariant
                        )
                        Text(
                            text = session.branch,
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutline
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Status Badge
                when (session.status) {
                    SessionStatus.RUNNING -> {
                        Box(
                            modifier = Modifier
                                .background(JulesSecondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(JulesSecondary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Running", style = MaterialTheme.typography.labelSmall, color = JulesSecondary)
                            }
                        }
                    }
                    SessionStatus.NEEDS_REVIEW -> {
                        Box(
                            modifier = Modifier
                                .background(JulesTertiary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(JulesTertiary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Review Req.", style = MaterialTheme.typography.labelSmall, color = JulesTertiary)
                            }
                        }
                    }
                    SessionStatus.PATCHING -> {
                        Box(
                            modifier = Modifier
                                .background(JulesPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(JulesPrimary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Patching", style = MaterialTheme.typography.labelSmall, color = JulesPrimary)
                            }
                        }
                    }
                    SessionStatus.COMPLETED -> {
                        Box(
                            modifier = Modifier
                                .background(JulesSurfaceContainerHighest, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Completed", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                        }
                    }
                    SessionStatus.FAILED -> {
                        Box(
                            modifier = Modifier
                                .background(JulesError.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Failed", style = MaterialTheme.typography.labelSmall, color = JulesError)
                        }
                    }
                    SessionStatus.PAUSED -> {
                        Box(
                            modifier = Modifier
                                .background(JulesSurfaceContainerHighest, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("Paused", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                        }
                    }
                }
            }

            // Status & Progress summary
            when (session.status) {
                SessionStatus.RUNNING -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .rotate(spinAngle)
                                )
                                Text(
                                    text = session.currentStep,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${session.progressPercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        LinearProgressIndicator(
                            progress = { session.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = JulesPrimary,
                            trackColor = JulesSurfaceContainerHighest
                        )
                    }
                }
                SessionStatus.NEEDS_REVIEW -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallMerge,
                                contentDescription = null,
                                tint = JulesSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Pull Request ${session.prNumber.ifEmpty { "#412" }} ready for review",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "+${session.diffAdded} -${session.diffRemoved}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = JulesSecondary
                        )
                    }
                }
                SessionStatus.PATCHING -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = null,
                            tint = JulesPrimary,
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(spinAngle)
                        )
                        Text(
                            text = session.currentStep,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                SessionStatus.COMPLETED -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = JulesSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Completed • Verified by tests",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                SessionStatus.FAILED -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = JulesError,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Failed • Check error log",
                            style = MaterialTheme.typography.bodySmall,
                            color = JulesError
                        )
                    }
                }
                SessionStatus.PAUSED -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = JulesOutline,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Paused",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (session.status == SessionStatus.RUNNING) {
                    Box(
                        modifier = Modifier
                            .background(JulesSurfaceContainerHigh, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = session.testSuiteInfo.ifEmpty { "pytest suite: 42/48" },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onViewDiff,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JulesSurfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("view_diff_btn_${session.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DataObject,
                                contentDescription = null,
                                tint = JulesPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Diff & Logs", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                    }
                } else if (session.status == SessionStatus.NEEDS_REVIEW) {
                    // Small avatar bubbles
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(JulesPrimaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("J", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .background(JulesSurfaceContainerHigh, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = onReviewGithub,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JulesPrimaryContainer,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("review_github_btn_${session.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Review on GitHub", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        }
                    }
                } else if (session.status == SessionStatus.PATCHING) {
                    Text(
                        text = "AST Graph: ${session.astNodesModified} nodes modified",
                        style = MaterialTheme.typography.labelSmall,
                        color = JulesOutline
                    )

                    Button(
                        onClick = onLiveWorkspace,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JulesSurfaceContainerHighest,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("live_workspace_btn_${session.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = JulesPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Live Workspace", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                        }
                    }
                }
            }
        }
    }
}
