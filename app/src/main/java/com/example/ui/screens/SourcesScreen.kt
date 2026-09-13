package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RepoSourceItem
import com.example.ui.components.ManageRepositoriesModal
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

@Composable
fun SourcesScreen(
    viewModel: JulesViewModel,
    onNavigateToNewTaskWithRepo: (String) -> Unit
) {
    val context = LocalContext.current
    val sources by viewModel.sourcesList.collectAsState()
    val isRefreshing by viewModel.isRefreshingSources.collectAsState()
    var showManageModal by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSources = sources.filter {
        searchQuery.isBlank() ||
            it.fullName.contains(searchQuery, ignoreCase = true) ||
            it.defaultBranch.contains(searchQuery, ignoreCase = true) ||
            (it.language.isNotBlank() && it.language.contains(searchQuery, ignoreCase = true))
    }

    val rotationTransition = rememberInfiniteTransition(label = "refresh_spin")
    val spinAngle by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing)
        ),
        label = "spin_angle"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. SOURCES HEADER WITH ACTION BUTTONS
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(JulesSurfaceLow)
                        .padding(16.dp)
                        .testTag("sources_header")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                        .size(34.dp)
                                        .background(JulesPrimary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Connected Sources",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(JulesSurfaceContainerHigh, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${sources.size} repos",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = JulesPrimary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Repositories granted to Google Jules GitHub App",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // BUTTONS: "MANAGE REPOSITORIES" & "REFRESH SOURCES"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // "Manage Repositories" button in Sources header
                            Button(
                                onClick = { showManageModal = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("manage_repositories_header_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = JulesSurfaceContainerHighest,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = JulesPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Manage Repositories",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }

                            // "Refresh Sources" button
                            Button(
                                onClick = {
                                    viewModel.refreshSources()
                                    Toast.makeText(context, "Syncing with GitHub installations...", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("refresh_sources_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = JulesPrimaryContainer,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                enabled = !isRefreshing
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Sources",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .rotate(if (isRefreshing) spinAngle else 0f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isRefreshing) "Syncing..." else "Refresh Sources",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Search Box
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Filter repositories by name or branch...",
                            style = MaterialTheme.typography.bodySmall,
                            color = JulesOutline
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sources_search_input"),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = JulesOutline, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = JulesOutline, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = JulesSurfaceContainerHighest,
                        unfocusedContainerColor = JulesSurfaceContainerHigh,
                        focusedBorderColor = JulesPrimary.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            // 3. REPOSITORY LIST OR EMPTY STATE
            if (filteredSources.isEmpty()) {
                item {
                    // EMPTY STATE WITH "MANAGE REPOSITORIES" BUTTON
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(JulesSurfaceContainer)
                            .border(1.dp, JulesOutlineVariant, RoundedCornerShape(14.dp))
                            .padding(28.dp)
                            .testTag("sources_empty_state"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(JulesSurfaceContainerHigh, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FolderOff,
                                    contentDescription = null,
                                    tint = JulesOutline,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = "No Repositories Available",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Grant repository access to the Google Jules GitHub App, or adjust your active installation settings to sync newly granted codebases.",
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // "Manage Repositories" button in empty state
                            Button(
                                onClick = { showManageModal = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = JulesPrimaryContainer,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("manage_repositories_empty_state_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Manage Repositories")
                            }

                            TextButton(
                                onClick = {
                                    viewModel.refreshSources()
                                    Toast.makeText(context, "Resyncing sources...", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = JulesPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Resync default repos", color = JulesPrimary)
                            }
                        }
                    }
                }
            } else {
                items(filteredSources, key = { it.id }) { repoItem ->
                    RepoSourceCard(
                        repoItem = repoItem,
                        onCreateTask = { onNavigateToNewTaskWithRepo(repoItem.fullName) },
                        onOpenGitHub = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/${repoItem.fullName}"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Open https://github.com/${repoItem.fullName}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // Modal explaining how to add/remove repos
    if (showManageModal) {
        ManageRepositoriesModal(
            onDismiss = { showManageModal = false },
            onTriggerRefresh = {
                viewModel.refreshSources()
                Toast.makeText(context, "Sources refreshed from GitHub!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun RepoSourceCard(
    repoItem: RepoSourceItem,
    onCreateTask: () -> Unit,
    onOpenGitHub: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(JulesSurfaceContainer)
            .border(1.dp, JulesOutlineVariant, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .testTag("repo_card_${repoItem.id}")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Row 1: Icon + Name + Visibility badge
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
                        imageVector = if (repoItem.isPrivate) Icons.Default.Lock else Icons.Default.Public,
                        contentDescription = null,
                        tint = if (repoItem.isPrivate) JulesTertiary else JulesPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = repoItem.fullName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .background(JulesSecondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(5.dp).background(JulesSecondary, CircleShape))
                        Text(
                            text = "Granted",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 10.sp),
                            color = JulesSecondary
                        )
                    }
                }
            }

            // Row 2: Branch, language, synced time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.ForkRight, contentDescription = null, tint = JulesOutline, modifier = Modifier.size(14.dp))
                        Text(repoItem.defaultBranch, style = MaterialTheme.typography.labelSmall, color = JulesOutline)
                    }
                    if (repoItem.language.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(JulesSurfaceContainerHigh, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(repoItem.language, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Text(
                    text = repoItem.lastSynced,
                    style = MaterialTheme.typography.labelSmall,
                    color = JulesOutline
                )
            }

            // Row 3: Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCreateTask,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("create_task_in_${repoItem.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JulesSurfaceContainerHighest,
                        contentColor = JulesPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Task", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold))
                }

                IconButton(
                    onClick = onOpenGitHub,
                    modifier = Modifier
                        .size(34.dp)
                        .background(JulesSurfaceContainerHigh, RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "View on GitHub",
                        tint = JulesOutline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
