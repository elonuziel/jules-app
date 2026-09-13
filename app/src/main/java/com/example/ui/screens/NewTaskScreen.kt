package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskCategory
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
fun NewTaskScreen(
    viewModel: JulesViewModel
) {
    val selectedRepo by viewModel.selectedRepo.collectAsState()
    val targetBranch by viewModel.targetBranch.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val promptText by viewModel.promptText.collectAsState()
    val isAutonomous by viewModel.isAutonomous.collectAsState()
    val autoTestSuite by viewModel.autoTestSuite.collectAsState()
    val reasoningBudgetK by viewModel.reasoningBudgetK.collectAsState()
    val isExecutionSettingsExpanded by viewModel.isExecutionSettingsExpanded.collectAsState()
    val isDispatching by viewModel.isDispatching.collectAsState()
    val dispatchSuccessMessage by viewModel.dispatchSuccessMessage.collectAsState()

    var repoDropdownExpanded by remember { mutableStateOf(false) }
    var showAdvancedOptions by remember { mutableStateOf(false) }
    val repoOptions = listOf(
        "google/cloud-android-sdk",
        "google/jules-runtime-engine",
        "google/mobile-agent-ui",
        "corp/cloud-orchestration",
        "android-gemini-client"
    )

    val advancedChevronRotation by animateFloatAsState(
        targetValue = if (showAdvancedOptions) 180f else 0f,
        label = "advanced_chevron_rotate"
    )
    val accordionChevronRotation by animateFloatAsState(
        targetValue = if (isExecutionSettingsExpanded) 180f else 0f,
        label = "chevron_rotate"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Clean Title Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "New Coding Task",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Describe your issue or feature. Jules will inspect your codebase, run tests, and open a pull request.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Repository Source & Branch Specifier
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JulesSurfaceContainer, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REPOSITORY SOURCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .background(JulesSurfaceContainerHigh, RoundedCornerShape(9999.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = null,
                                    tint = JulesSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "GitHub Connected",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesSecondary
                                )
                            }
                        }
                    }

                    // Repository Dropdown Selector
                    Box {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JulesSurfaceContainerHigh, RoundedCornerShape(10.dp))
                                .clickable { repoDropdownExpanded = true }
                                .padding(12.dp)
                                .testTag("repo_selector"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(JulesSurfaceLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Source,
                                        contentDescription = null,
                                        tint = JulesPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = selectedRepo,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "default: main",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = JulesOutline
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                tint = JulesOutlineVariant
                            )
                        }

                        DropdownMenu(
                            expanded = repoDropdownExpanded,
                            onDismissRequest = { repoDropdownExpanded = false },
                            modifier = Modifier.background(JulesSurfaceContainerHighest)
                        ) {
                            repoOptions.forEach { repo ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = repo,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (repo == selectedRepo) JulesPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.selectedRepo.value = repo
                                        repoDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Target Branch field
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "TARGET BRANCH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.8.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = targetBranch,
                            onValueChange = { viewModel.targetBranch.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_branch_input"),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ForkRight,
                                    contentDescription = null,
                                    tint = JulesOutline,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .background(JulesSurfaceLowest, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "NEW",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            textStyle = MaterialTheme.typography.labelLarge.copy(
                                color = JulesPrimary,
                                fontFamily = FontFamily.Monospace
                            ),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = JulesSurfaceContainerHighest,
                                unfocusedContainerColor = JulesSurfaceContainerHigh,
                                focusedBorderColor = JulesPrimary.copy(alpha = 0.4f),
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        // 3. Task Category 4-Segmented Control
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TASK CATEGORY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JulesSurfaceLow, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TaskCategory.values().forEach { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) JulesPrimaryContainer else Color.Transparent)
                                .clickable { viewModel.selectedCategory.value = category }
                                .padding(vertical = 8.dp)
                                .testTag("category_${category.apiValue}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 4. Direct Agent Prompt Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JulesSurfaceContainer, RoundedCornerShape(14.dp))
                    .padding(16.dp)
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
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = JulesTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DIRECT AGENT PROMPT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.8.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${promptText.length} chars",
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutline
                        )
                    }

                    // Quick Seed Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SeedChip(
                            label = "Fix Flaky Test",
                            icon = Icons.Default.Bolt,
                            iconColor = JulesTertiary,
                            onClick = {
                                viewModel.setPrompt("Fix flaky integration test in database runner")
                            }
                        )
                        SeedChip(
                            label = "Add Unit Tests",
                            icon = Icons.Default.AddTask,
                            iconColor = JulesPrimary,
                            onClick = {
                                viewModel.setPrompt("Add comprehensive unit tests for edge cases with mock fixtures")
                            }
                        )
                        SeedChip(
                            label = "Optimize DB",
                            icon = Icons.Default.Speed,
                            iconColor = JulesSecondary,
                            onClick = {
                                viewModel.setPrompt("Optimize Room SQLite query indexing and avoid full-table scans")
                            }
                        )
                        SeedChip(
                            label = "Upgrade Deps",
                            icon = Icons.Default.Upgrade,
                            iconColor = JulesOutline,
                            onClick = {
                                viewModel.setPrompt("Bump Android Gradle Plugin and harmonize transitive dependencies")
                            }
                        )
                    }

                    // Textarea with Purple Accent Left Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(JulesSurfaceLowest)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Purple accent bar
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(110.dp)
                                    .background(JulesTertiary)
                            )

                            OutlinedTextField(
                                value = promptText,
                                onValueChange = { viewModel.setPrompt(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("prompt_textarea"),
                                placeholder = {
                                    Text(
                                        "Provide instructions, issue number, stack traces, or expected behavior...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = JulesOutline
                                    )
                                },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }

                    // Context Note
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JulesSurfaceLow, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = JulesOutline,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Jules indexes symbol graphs dynamically upon session initiation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 5. Execution Settings (Collapsible Accordion)
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
                                viewModel.isExecutionSettingsExpanded.value = !isExecutionSettingsExpanded
                            }
                            .padding(16.dp)
                            .testTag("execution_settings_accordion_toggle"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = JulesPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Execution Settings",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Gemini 1.5 Pro • ${reasoningBudgetK}k Reasoning Budget",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            tint = JulesOutlineVariant,
                            modifier = Modifier.rotate(accordionChevronRotation)
                        )
                    }

                    AnimatedVisibility(visible = isExecutionSettingsExpanded) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Autonomy Level
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "AUTONOMY LEVEL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    AutonomyCard(
                                        title = "Autonomous",
                                        subtitle = "Self-executes unit tests and drafts complete PR.",
                                        isSelected = isAutonomous,
                                        onClick = { viewModel.isAutonomous.value = true },
                                        modifier = Modifier.weight(1f)
                                    )
                                    AutonomyCard(
                                        title = "Interactive",
                                        subtitle = "Pauses for engineer signoff at every patch diff.",
                                        isSelected = !isAutonomous,
                                        onClick = { viewModel.isAutonomous.value = false },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }

                            // Auto Test Suite Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JulesSurfaceContainerHigh, RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(JulesSecondary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Rule,
                                            contentDescription = null,
                                            tint = JulesSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "Auto Test Suite",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "gradlew testDebugUnitTest",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = JulesOutline
                                        )
                                    }
                                }

                                Switch(
                                    checked = autoTestSuite,
                                    onCheckedChange = { viewModel.autoTestSuite.value = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = JulesPrimaryContainer,
                                        uncheckedThumbColor = JulesOutline,
                                        uncheckedTrackColor = JulesSurfaceLowest
                                    )
                                )
                            }

                            // Reasoning Token Budget
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(JulesSurfaceContainerHigh, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "REASONING TOKEN BUDGET",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                letterSpacing = 0.8.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Box(
                                            modifier = Modifier
                                                .background(JulesTertiary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${reasoningBudgetK}k tokens",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = JulesTertiary
                                            )
                                        }
                                    }

                                    Slider(
                                        value = reasoningBudgetK.toFloat(),
                                        onValueChange = { viewModel.reasoningBudgetK.value = it.toInt() },
                                        valueRange = 16f..128f,
                                        steps = 6,
                                        colors = SliderDefaults.colors(
                                            thumbColor = JulesTertiary,
                                            activeTrackColor = JulesTertiary,
                                            inactiveTrackColor = JulesSurfaceLowest
                                        )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("16k (Fast)", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                                        Text("64k (Deep)", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                                        Text("128k (Full Audit)", style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                                    }
                                }
                            }

                            // Base RPC Gateway
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "BASE RPC GATEWAY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        letterSpacing = 0.8.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "https://developers.google.com/jules/api/v1alpha",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = JulesOutline
                                    )
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = JulesSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Sticky Dispatch Action Button
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.dispatchNewTask() },
                    enabled = !isDispatching,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("launch_task_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (dispatchSuccessMessage != null) JulesSecondaryContainer else JulesPrimaryContainer,
                        contentColor = Color.White
                    )
                ) {
                    if (isDispatching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Dispatching Session...",
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        )
                    } else if (dispatchSuccessMessage != null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dispatchSuccessMessage ?: "Session Queued!",
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Launch Jules Agent Task",
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = JulesOutline,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Estimated execution: ~3-5 mins",
                        style = MaterialTheme.typography.labelSmall,
                        color = JulesOutline
                    )
                }
            }
        }
    }
}

@Composable
fun SeedChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(JulesSurfaceContainerHigh, RoundedCornerShape(9999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun AutonomyCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) JulesPrimary.copy(alpha = 0.12f) else JulesSurfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = JulesPrimary)
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
