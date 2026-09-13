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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiffDataProvider
import com.example.data.model.DiffFile
import com.example.data.model.DiffLine
import com.example.data.model.DiffLineType
import com.example.data.model.SessionStatus
import com.example.ui.theme.JulesDiffAdditionBg
import com.example.ui.theme.JulesDiffDeletionBg
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
fun LiveDiffScreen(
    viewModel: JulesViewModel
) {
    val selectedViewMode by viewModel.diffSelectedViewMode.collectAsState()
    val isSecondary1Expanded by viewModel.isSecondaryFile1Expanded.collectAsState()
    val isSecondary2Expanded by viewModel.isSecondaryFile2Expanded.collectAsState()
    val isMainExpanded by viewModel.isMainFileExpanded.collectAsState()
    val progress by viewModel.liveDiffProgress.collectAsState()
    val approvalMessage by viewModel.prApprovedMessage.collectAsState()
    val showReprompt by viewModel.showRepromptDialog.collectAsState()
    val selectedSession by viewModel.selectedSessionForDiff.collectAsState()
    val diffFiles by viewModel.activeDiffFiles.collectAsState()
    val selectedDiffFileIndex by viewModel.selectedDiffFileIndex.collectAsState()
    val isLoadingDiff by viewModel.isLoadingDiff.collectAsState()
    val diffError by viewModel.diffErrorMessage.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    val sessionActivities by viewModel.sessionActivities.collectAsState()
    val isSendingMessage by viewModel.isSendingMessage.collectAsState()
    val isApprovingPlan by viewModel.isApprovingPlan.collectAsState()
    val chatInputText by viewModel.chatInputText.collectAsState()

    val activeSession = selectedSession
        ?: allSessions.firstOrNull { it.status == SessionStatus.RUNNING || it.status == SessionStatus.PATCHING }
        ?: allSessions.firstOrNull()

    LaunchedEffect(activeSession?.id) {
        activeSession?.id?.let { sid ->
            viewModel.loadSessionActivities(sid)
        }
    }

    val displayId = activeSession?.let { "#${it.id}" } ?: "No Active Task"
    val displayTitle = activeSession?.title ?: "Select a task from Focus Workspace to inspect diff and logs"
    val displayRepo = activeSession?.repo ?: "—"
    val displayBranch = activeSession?.branch ?: "—"
    val displayStep = activeSession?.currentStep ?: "Idle"
    val displayProgress = activeSession?.progressPercent ?: 0

    val activeDiffFile = diffFiles.getOrNull(selectedDiffFileIndex)
        ?: diffFiles.firstOrNull()

    var customRepromptText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_and_spin")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing)
        ),
        label = "spin_angle"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Session Control Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JulesSurfaceContainer)
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(JulesSurfaceContainerHighest, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = displayId,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = JulesTertiary
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = JulesOutline,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Active Session",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = displayTitle,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Step / Status Chip
                        Box(
                            modifier = Modifier
                                .background(JulesSurfaceContainerHigh, RoundedCornerShape(9999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .scale(pulseScale)
                                        .background(JulesSecondary, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activeSession?.status?.label ?: "Running",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = JulesSecondary
                                )
                            }
                        }
                    }

                    // Repo and Branch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Source,
                            contentDescription = null,
                            tint = JulesOutline,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = displayRepo,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutlineVariant
                        )
                        Icon(
                            imageVector = Icons.Default.ForkRight,
                            contentDescription = null,
                            tint = JulesTertiary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = displayBranch,
                            style = MaterialTheme.typography.labelMedium,
                            color = JulesTertiary
                        )
                    }

                    // Step Progress Micro-Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = JulesSecondary,
                                    modifier = Modifier
                                        .size(13.dp)
                                        .rotate(spinAngle)
                                )
                                Text(
                                    text = displayStep,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesSecondary
                                )
                            }
                            Text(
                                text = "$displayProgress% complete",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }

                        LinearProgressIndicator(
                            progress = { displayProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = JulesSecondary,
                            trackColor = JulesSurfaceContainerHighest
                        )
                    }
                }
            }
        }

        // 2. Segmented View Mode Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JulesSurfaceLowest, RoundedCornerShape(12.dp))
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val verificationLabel = when (activeSession?.status) {
                    SessionStatus.COMPLETED -> "Verified"
                    SessionStatus.FAILED -> "Failed"
                    SessionStatus.RUNNING, SessionStatus.PATCHING -> "Testing"
                    SessionStatus.NEEDS_REVIEW -> "Review Plan"
                    else -> "Verification"
                }
                val verificationIcon = when (activeSession?.status) {
                    SessionStatus.COMPLETED -> Icons.Default.CheckCircle
                    SessionStatus.FAILED -> Icons.Default.ErrorOutline
                    SessionStatus.RUNNING, SessionStatus.PATCHING -> Icons.Default.Sync
                    else -> Icons.Default.Verified
                }
                val verificationTint = when (activeSession?.status) {
                    SessionStatus.COMPLETED -> JulesSecondary
                    SessionStatus.FAILED -> JulesError
                    SessionStatus.RUNNING, SessionStatus.PATCHING -> JulesTertiary
                    else -> JulesOutline
                }

                ViewModePill(
                    label = if (sessionActivities.isNotEmpty()) "Step Logs (${sessionActivities.size})" else "Step Logs",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    isSelected = selectedViewMode == 0,
                    onClick = { viewModel.diffSelectedViewMode.value = 0 }
                )
                ViewModePill(
                    label = "Code Diff (${diffFiles.size})",
                    icon = Icons.Default.Difference,
                    isSelected = selectedViewMode == 1,
                    onClick = { viewModel.diffSelectedViewMode.value = 1 }
                )
                ViewModePill(
                    label = "Console",
                    icon = Icons.Default.Terminal,
                    isSelected = selectedViewMode == 2,
                    onClick = { viewModel.diffSelectedViewMode.value = 2 }
                )
                ViewModePill(
                    label = verificationLabel,
                    icon = verificationIcon,
                    iconTint = verificationTint,
                    isSelected = selectedViewMode == 3,
                    onClick = { viewModel.diffSelectedViewMode.value = 3 }
                )
            }
        }

        // Optional Loading or Error Banner
        if (isLoadingDiff) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JulesSurfaceContainerHigh, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = JulesPrimary
                        )
                        Text(
                            text = "Loading pull request files from GitHub...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // VIEW MODE 0: Step Logs / Interactive Plan Approval & Chat Timeline
        if (selectedViewMode == 0) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVITY & CHAT TIMELINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = JulesOutline
                        )
                        Text(
                            text = "${sessionActivities.size} events",
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutlineVariant
                        )
                    }

                    if (sessionActivities.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JulesSurfaceContainer, RoundedCornerShape(12.dp))
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = JulesOutline,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "No activities recorded yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    sessionActivities.forEach { activity ->
                        // 1. Plan Generated Card
                        activity.planGenerated?.plan?.let { plan ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(JulesSurfaceContainer)
                                    .border(1.dp, JulesPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                                imageVector = Icons.AutoMirrored.Filled.Assignment,
                                                contentDescription = null,
                                                tint = JulesPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Agent Execution Plan",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(JulesPrimaryContainer.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${plan.steps?.size ?: 0} Steps",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = JulesPrimary
                                            )
                                        }
                                    }

                                    // Step items list
                                    plan.steps?.forEach { step ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .background(JulesPrimaryContainer, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${step.index ?: 1}",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = Color.White
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = step.title ?: "Step",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (!step.description.isNullOrBlank()) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = step.description,
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // 1-Tap Approve Plan Action Button
                                    if (activeSession?.status == SessionStatus.NEEDS_REVIEW || activeSession?.status == SessionStatus.RUNNING) {
                                        Button(
                                            onClick = { activeSession?.id?.let { viewModel.approveSessionPlan(it) } },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = JulesPrimaryContainer,
                                                contentColor = Color.White
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            enabled = !isApprovingPlan
                                        ) {
                                            if (isApprovingPlan) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    strokeWidth = 2.dp,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Approving Plan...")
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Approve Plan & Proceed",
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Agent Messaged Bubble
                        activity.agentMessaged?.message?.let { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(JulesSurfaceContainerHighest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = "Jules",
                                        tint = JulesTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 14.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
                                        .background(JulesSurfaceContainer)
                                        .padding(12.dp)
                                        .fillMaxWidth(0.85f)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Jules",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = JulesTertiary
                                            )
                                            Text(
                                                text = activity.createTime?.substringAfter("T")?.take(5) ?: "now",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = JulesOutline
                                            )
                                        }
                                        Text(
                                            text = msg,
                                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // 3. User Messaged Bubble
                        activity.userMessaged?.message?.let { msg ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 2.dp, bottomStart = 14.dp, bottomEnd = 14.dp))
                                        .background(JulesPrimaryContainer)
                                        .padding(12.dp)
                                        .fillMaxWidth(0.85f)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "You",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White
                                            )
                                            Text(
                                                text = activity.createTime?.substringAfter("T")?.take(5) ?: "now",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                        }
                                        Text(
                                            text = msg,
                                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(JulesPrimaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // 4. Progress Updated Chip
                        activity.progressUpdated?.let { prog ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        tint = JulesSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = prog.message ?: "Progress updated",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.weight(1f)
                                    )
                                    prog.progressPercent?.let { p ->
                                        Text(
                                            text = "$p%",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = JulesSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // 5. Session Failed Banner
                        activity.sessionFailed?.let { fail ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JulesError.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = JulesError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = fail.reason ?: "Session failed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = JulesError
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Chat Input Bar for Step Logs view
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(JulesSurfaceContainer)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = chatInputText,
                            onValueChange = { viewModel.chatInputText.value = it },
                            placeholder = {
                                Text(
                                    text = "Send directive to Jules...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = JulesOutline
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = MaterialTheme.typography.bodySmall,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = JulesSurfaceLowest,
                                unfocusedContainerColor = JulesSurfaceLowest,
                                focusedBorderColor = JulesPrimary,
                                unfocusedBorderColor = JulesOutlineVariant.copy(alpha = 0.5f)
                            )
                        )

                        IconButton(
                            onClick = {
                                activeSession?.id?.let { sid ->
                                    viewModel.sendChatMessage(sid, chatInputText)
                                }
                            },
                            enabled = chatInputText.isNotBlank() && !isSendingMessage,
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    if (chatInputText.isNotBlank() && !isSendingMessage) JulesPrimaryContainer else JulesSurfaceContainerHighest,
                                    CircleShape
                                )
                        ) {
                            if (isSendingMessage) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (chatInputText.isNotBlank()) Color.White else JulesOutline,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // VIEW MODE 1: Code Diff with Multi-File Selector Carousel
        if (selectedViewMode == 1) {
            if (diffFiles.isEmpty() || activeDiffFile == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = JulesSurfaceContainer),
                        border = BorderStroke(1.dp, JulesOutlineVariant)
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
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = JulesOutline,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Text(
                                text = "No Code Diffs Available",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = diffError ?: "Once Jules generates code changes and creates a GitHub pull request, modified files and unified git diffs will appear here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Horizontal Multi-File Carousel
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHANGED FILES (${diffFiles.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = JulesOutline
                        )
                        Text(
                            text = "Tap to switch file",
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutlineVariant
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        diffFiles.forEachIndexed { index, file ->
                            val isSelected = index == selectedDiffFileIndex
                            val baseName = file.fileName.substringAfterLast("/")
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) JulesPrimaryContainer else JulesSurfaceContainerHigh)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) JulesPrimary else JulesOutlineVariant.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectedDiffFileIndex.value = index }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else JulesOutline,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = baseName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isSelected) Color.White.copy(alpha = 0.2f) else JulesSecondary.copy(alpha = 0.12f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "+${file.addedCount}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = if (isSelected) Color.White else JulesSecondary
                                        )
                                    }
                                    if (file.deletedCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (isSelected) Color.White.copy(alpha = 0.2f) else JulesError.copy(alpha = 0.12f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "-${file.deletedCount}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = if (isSelected) Color.White else JulesError
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Main Active Diff View for selectedDiffFile
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(JulesSurfaceLowest)
                ) {
                    Column {
                        // File Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JulesSurfaceContainerHigh)
                                .clickable { viewModel.isMainFileExpanded.value = !isMainExpanded }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f, fill = false)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = activeDiffFile.fileName,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(JulesSecondary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("+${activeDiffFile.addedCount}", style = MaterialTheme.typography.labelSmall, color = JulesSecondary)
                                }
                                if (activeDiffFile.deletedCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .background(JulesError.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("-${activeDiffFile.deletedCount}", style = MaterialTheme.typography.labelSmall, color = JulesError)
                                    }
                                }
                                Icon(
                                    imageVector = if (isMainExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                                    contentDescription = "Toggle",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Code Canvas
                        AnimatedVisibility(visible = isMainExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                activeDiffFile.lines.forEach { line ->
                                    DiffLineRow(line = line)
                                }
                            }
                        }
                    }
                }
            }

            // Other secondary modified files
            val secondaryFiles = diffFiles.filterIndexed { index, _ -> index != selectedDiffFileIndex }
            if (secondaryFiles.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "OTHER MODIFIED FILES (${secondaryFiles.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = JulesOutline
                        )
                        secondaryFiles.forEach { file ->
                            val origIndex = diffFiles.indexOf(file)
                            SecondaryDiffCard(
                                fileName = file.fileName,
                                addedCount = file.addedCount,
                                deletedCount = file.deletedCount,
                                isExpanded = false,
                                onToggle = { viewModel.selectedDiffFileIndex.value = origIndex },
                                description = file.testDescription.ifBlank { "Click to switch active diff viewer" },
                                statusTag = if (file.testPassed) "Patch Verified" else "Modified",
                                icon = if (file.testPassed) Icons.Default.CheckCircle else Icons.Default.Description,
                                iconTint = if (file.testPassed) JulesSecondary else JulesOutline
                            )
                        }
                    }
                }
            }
            }
        }

        // VIEW MODE 2: Console
        if (selectedViewMode == 2) {
            val consoleLogs = remember(activeSession, sessionActivities, diffFiles) {
                buildString {
                    appendLine("$ git fetch origin && git checkout ${activeSession?.branch ?: "main"}")
                    appendLine("Switched to branch '${activeSession?.branch ?: "main"}' on repository '${activeSession?.repo ?: "workspace"}'")
                    appendLine("$ jules agent --session ${activeSession?.id ?: "current"}")
                    appendLine("[jules] Status: ${activeSession?.status?.label ?: "IDLE"}")
                    appendLine("[jules] Current step: ${activeSession?.currentStep ?: "Standby"}")
                    if (sessionActivities.isNotEmpty()) {
                        appendLine("\n--- SESSION ACTIVITY STREAM ---")
                        sessionActivities.forEach { act ->
                            val time = act.createTime?.substringAfter("T")?.take(8) ?: "00:00:00"
                            val msg = act.agentMessaged?.message ?: act.userMessaged?.message ?: act.progressUpdated?.message ?: act.description ?: "Event"
                            appendLine("[$time] [${act.originator ?: "AGENT"}] $msg")
                        }
                    }
                    if (diffFiles.isNotEmpty()) {
                        appendLine("\n$ git status --short")
                        diffFiles.forEach { file ->
                            appendLine("M  ${file.fileName} (+${file.addedCount} / -${file.deletedCount})")
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F0E17))
                        .padding(14.dp)
                ) {
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
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFFFF5F56), CircleShape))
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFFFFBD2E), CircleShape))
                                Box(modifier = Modifier.size(10.dp).background(Color(0xFF27C93F), CircleShape))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "agent@jules-runner: ~/${activeSession?.repo?.substringAfterLast("/") ?: "workspace"}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    ),
                                    color = JulesOutline
                                )
                            }
                            Text(
                                text = "bash",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = JulesOutlineVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = consoleLogs,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            color = Color(0xFF50FA7B)
                        )
                    }
                }
            }
        }

        // VIEW MODE 3: Verification & Test Suite
        if (selectedViewMode == 3) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(JulesSurfaceContainer)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (activeSession?.status == SessionStatus.FAILED) Icons.Default.ErrorOutline else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (activeSession?.status == SessionStatus.FAILED) JulesError else JulesSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Session Verification: ${activeSession?.status?.label ?: "Pending"}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (activeSession != null) {
                                "Verification checks for task #${activeSession.id} on repository '${activeSession.repo}' (branch: ${activeSession.branch}). Current status is ${activeSession.status.label} with progress at ${activeSession.progressPercent}%. Current step: ${activeSession.currentStep}.${if (activeSession.prNumber.isNotBlank()) " Pull request: ${activeSession.prNumber}." else ""}"
                            } else {
                                "Select an active task from Focus Workspace to inspect real-time verification suite and test logs."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val filesWithTests = diffFiles.filter { it.testDescription.isNotBlank() }
                        if (filesWithTests.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                filesWithTests.forEach { file ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (file.testPassed) Icons.Default.CheckCircle else Icons.Default.Description,
                                            contentDescription = null,
                                            tint = if (file.testPassed) JulesSecondary else JulesOutline,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Column {
                                            Text(
                                                text = file.fileName.substringAfterLast("/"),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = file.testDescription,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Jules Agent Reasoner Box
        item {
            val agentReasoning = sessionActivities.lastOrNull { it.agentMessaged != null }?.agentMessaged?.message
                ?: sessionActivities.firstOrNull { it.planGenerated != null }?.description
                ?: activeSession?.let { "Task #${it.id}: ${it.title}\nCurrent step: ${it.currentStep} (${it.progressPercent}% complete on branch ${it.branch})." }
                ?: "Select an active task to view real-time reasoning and agent plan execution details."

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(JulesSurfaceLow)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Left Purple Accent bar
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(130.dp)
                            .background(JulesTertiary)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = JulesTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Jules Agent Reasoner",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = JulesTertiary
                                )
                            }
                            Text(
                                text = "Status: ${activeSession?.status?.label ?: "Idle"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }

                        Text(
                            text = agentReasoning,
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = JulesSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = activeSession?.repo ?: "General",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesSecondary
                                )
                            }
                            Text("•", style = MaterialTheme.typography.labelSmall, color = JulesOutlineVariant)
                            Text(
                                text = activeSession?.branch ?: "main",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 6. Interactive Command Center
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Toast notification if approved
                AnimatedVisibility(visible = approvalMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JulesSecondaryContainer, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = approvalMessage ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.White
                        )
                    }
                }

                // Two primary buttons: Reject / Re-prompt and Approve & PR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.showRepromptDialog.value = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JulesSurfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("reprompt_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = JulesOutline,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reject / Re-prompt",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.approvePr() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JulesPrimaryContainer,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("approve_pr_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Merge,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Approve & PR",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }

                // Quick Directive Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Directive:",
                        style = MaterialTheme.typography.labelSmall,
                        color = JulesOutline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DirectivePill("/add-unit-test") { viewModel.triggerQuickDirective("/add-unit-test") }
                    Spacer(modifier = Modifier.width(4.dp))
                    DirectivePill("/explain") { viewModel.triggerQuickDirective("/explain") }
                    Spacer(modifier = Modifier.width(4.dp))
                    DirectivePill("/rollback") { viewModel.triggerQuickDirective("/rollback") }
                }
            }
        }
    }

    // Re-prompt Dialog
    if (showReprompt) {
        AlertDialog(
            onDismissRequest = { viewModel.showRepromptDialog.value = false },
            title = {
                Text(
                    text = "Refine Agent Instructions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Provide additional directives for Jules to regenerate this patch diff:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = customRepromptText,
                        onValueChange = { customRepromptText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Also add an explicit timeout and catch SQLiteException") },
                        textStyle = MaterialTheme.typography.bodySmall,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val promptText = customRepromptText.trim()
                        viewModel.showRepromptDialog.value = false
                        if (promptText.isNotBlank()) {
                            activeSession?.id?.let { sid ->
                                viewModel.sendChatMessage(sid, promptText)
                            }
                        }
                        customRepromptText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer)
                ) {
                    Text("Re-dispatch Patch")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showRepromptDialog.value = false }) {
                    Text("Cancel", color = JulesOutline)
                }
            },
            containerColor = JulesSurfaceContainer
        )
    }
}

@Composable
fun ViewModePill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconTint: Color? = null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) JulesSurfaceContainerHigh else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint ?: if (isSelected) JulesPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                ),
                color = if (isSelected) JulesPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DiffLineRow(line: DiffLine) {
    val bgColor = when (line.type) {
        DiffLineType.CONTEXT -> Color.Transparent
        DiffLineType.ADDITION -> JulesDiffAdditionBg
        DiffLineType.DELETION -> JulesDiffDeletionBg
    }
    val textColor = when (line.type) {
        DiffLineType.CONTEXT -> MaterialTheme.colorScheme.onSurfaceVariant
        DiffLineType.ADDITION -> JulesSecondary
        DiffLineType.DELETION -> JulesError
    }
    val sign = when (line.type) {
        DiffLineType.CONTEXT -> " "
        DiffLineType.ADDITION -> "+"
        DiffLineType.DELETION -> "-"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Old line number column
        Text(
            text = line.oldLineNumber?.toString() ?: "",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (line.type == DiffLineType.DELETION) JulesError else JulesOutline.copy(alpha = 0.6f),
            modifier = Modifier.width(32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
        Spacer(modifier = Modifier.width(6.dp))

        // New line number column
        Text(
            text = line.newLineNumber?.toString() ?: "",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (line.type == DiffLineType.ADDITION) JulesSecondary else JulesOutline.copy(alpha = 0.6f),
            modifier = Modifier.width(32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
        Spacer(modifier = Modifier.width(8.dp))

        // Sign (+ / -)
        Text(
            text = sign,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.width(14.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.width(6.dp))

        // Code line
        Text(
            text = line.text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun SecondaryDiffCard(
    fileName: String,
    addedCount: Int,
    deletedCount: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    description: String,
    statusTag: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(JulesSurfaceLow)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JulesSurfaceContainer)
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(JulesSecondary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("+$addedCount", style = MaterialTheme.typography.labelSmall, color = JulesSecondary)
                    }
                    if (deletedCount > 0) {
                        Box(
                            modifier = Modifier
                                .background(JulesError.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("-$deletedCount", style = MaterialTheme.typography.labelSmall, color = JulesError)
                        }
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle",
                        tint = JulesOutline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JulesSurfaceLowest)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = statusTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DirectivePill(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(JulesSurfaceContainer, RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = JulesPrimary
        )
    }
}
