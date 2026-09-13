package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DiffDataProvider
import com.example.data.model.DiffLine
import com.example.data.model.DiffLineType
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
                                        text = "#JLS-8492",
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
                                        text = "4m 12s elapsed",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Fix SQLite Cursor Leak in SyncWorker",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Step 5/6 Chip
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
                                    text = "Step 5/6",
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
                            text = "google/cloud-android-sdk",
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
                            text = "jules/cursor-leak-fix",
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
                                    text = "Running Patch Verification Suite",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesSecondary
                                )
                            }
                            Text(
                                text = "$progress% complete",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progress / 100f },
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
                ViewModePill(
                    label = "Step Logs",
                    icon = Icons.Default.ReceiptLong,
                    isSelected = selectedViewMode == 0,
                    onClick = { viewModel.diffSelectedViewMode.value = 0 }
                )
                ViewModePill(
                    label = "Code Diff (3)",
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
                    label = "14/14 Passed",
                    icon = Icons.Default.CheckCircle,
                    iconTint = JulesSecondary,
                    isSelected = selectedViewMode == 3,
                    onClick = { viewModel.diffSelectedViewMode.value = 3 }
                )
            }
        }

        // 3. Main Active Diff View (SyncWorker.kt)
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
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = JulesPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = DiffDataProvider.mainFile.fileName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                ),
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
                                Text("+18", style = MaterialTheme.typography.labelSmall, color = JulesSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .background(JulesError.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("-6", style = MaterialTheme.typography.labelSmall, color = JulesError)
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
                            DiffDataProvider.mainFile.lines.forEach { line ->
                                DiffLineRow(line = line)
                            }
                        }
                    }
                }
            }
        }

        // 4. Collapsible Secondary Diff Files
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Secondary File 1 (SyncWorkerTest.kt)
                SecondaryDiffCard(
                    fileName = DiffDataProvider.secondaryFile1.fileName,
                    addedCount = DiffDataProvider.secondaryFile1.addedCount,
                    deletedCount = DiffDataProvider.secondaryFile1.deletedCount,
                    isExpanded = isSecondary1Expanded,
                    onToggle = { viewModel.isSecondaryFile1Expanded.value = !isSecondary1Expanded },
                    description = DiffDataProvider.secondaryFile1.testDescription,
                    statusTag = "Passed (120ms)",
                    icon = Icons.Default.CheckCircle,
                    iconTint = JulesSecondary
                )

                // Secondary File 2 (DatabaseHelper.kt)
                SecondaryDiffCard(
                    fileName = DiffDataProvider.secondaryFile2.fileName,
                    addedCount = DiffDataProvider.secondaryFile2.addedCount,
                    deletedCount = DiffDataProvider.secondaryFile2.deletedCount,
                    isExpanded = isSecondary2Expanded,
                    onToggle = { viewModel.isSecondaryFile2Expanded.value = !isSecondary2Expanded },
                    description = DiffDataProvider.secondaryFile2.testDescription,
                    statusTag = "StrictMode Tagged",
                    icon = Icons.Default.Description,
                    iconTint = JulesOutline
                )
            }
        }

        // 5. Jules Agent Reasoner Box
        item {
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
                                text = "Confidence: 99.4%",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }

                        Text(
                            text = "Identified an unclosed cursor in the database reader loop that bypassed clean-up during intermittent CursorWindowAllocationException. Wrapped query execution within Kotlin's standard .use { } scoping block, ensuring deterministic release of native binder handles even under crash scenarios.",
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
                                    text = "Zero regressions detected",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesSecondary
                                )
                            }
                            Text("•", style = MaterialTheme.typography.labelSmall, color = JulesOutlineVariant)
                            Text(
                                text = "Target SDK 34",
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
                        viewModel.showRepromptDialog.value = false
                        viewModel.triggerQuickDirective("Re-prompt: $customRepromptText")
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
