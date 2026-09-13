package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PullRequestStatus
import com.example.data.model.SessionItem
import com.example.data.model.SessionStatus
import com.example.ui.theme.DarkJulesSurface
import com.example.ui.theme.JulesError
import com.example.ui.theme.JulesOutline
import com.example.ui.theme.JulesOutlineVariant
import com.example.ui.theme.JulesPrimary
import com.example.ui.theme.JulesPrimaryContainer
import com.example.ui.theme.JulesSecondary
import com.example.ui.theme.JulesSurfaceContainer
import com.example.ui.theme.JulesSurfaceContainerHigh
import com.example.ui.theme.JulesSurfaceContainerHighest
import com.example.ui.theme.JulesSurfaceLowest
import com.example.ui.theme.JulesTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDrawerSheet(
    session: SessionItem,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onApprovePr: (SessionItem) -> Unit,
    onMergePr: (SessionItem) -> Unit,
    onDeleteBranch: (SessionItem) -> Unit,
    onInspectDiff: (SessionItem) -> Unit = {},
    onApprovePlan: ((SessionItem) -> Unit)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showMergeConfirmation by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = JulesSurfaceContainer,
        modifier = Modifier.testTag("session_drawer_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Drawer Top Bar
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
                            .background(JulesSurfaceContainerHigh, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = session.id,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = JulesPrimary
                        )
                    }
                    Text(
                        text = session.repo,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Drawer",
                        tint = JulesOutline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Task Summary
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // ENHANCED PULL REQUEST CARD (or pending state if not created yet)
            if (session.prNumber.isNotBlank() || session.prUrl.isNotBlank()) {
                EnhancedPullRequestCard(
                    session = session,
                    onApproveClick = {
                        onApprovePr(session)
                        Toast.makeText(context, "Pull Request ${session.prNumber} approved by reviewer ✓", Toast.LENGTH_SHORT).show()
                    },
                    onMergeClick = {
                        showMergeConfirmation = true
                    },
                    onDeleteBranchClick = {
                        onDeleteBranch(session)
                        Toast.makeText(context, "Remote branch ${session.branch} deleted successfully.", Toast.LENGTH_SHORT).show()
                    },
                    onCopyBranch = {
                        clipboardManager.setText(AnnotatedString(session.branch))
                        Toast.makeText(context, "Branch ${session.branch} copied!", Toast.LENGTH_SHORT).show()
                    },
                    onOpenInGitHub = {
                        val prClean = session.prNumber.replace("#", "")
                        val githubUrl = session.prUrl.ifBlank { "https://github.com/${session.repo}/pull/$prClean" }
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Open $githubUrl", Toast.LENGTH_LONG).show()
                        }
                    },
                    onApprovePlan = onApprovePlan
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = JulesSurfaceContainerHigh),
                    border = BorderStroke(1.dp, JulesOutlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.CallMerge, contentDescription = null, tint = JulesOutline, modifier = Modifier.size(16.dp))
                            Text("No Pull Request Opened Yet", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Text(
                            text = "Jules is executing on branch '${session.branch}'. Once code patches are synthesized, a GitHub pull request will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (session.status == SessionStatus.NEEDS_REVIEW && onApprovePlan != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { onApprovePlan.invoke(session) },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = JulesPrimaryContainer),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve Execution Plan & Proceed", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }

            // Live Diff Inspection Shortcut
            Button(
                onClick = {
                    onDismiss()
                    onInspectDiff(session)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("drawer_inspect_diff_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JulesSurfaceContainerHighest,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.DataObject, contentDescription = null, tint = JulesPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Inspect Full Code Diff & Terminal Logs")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Merge Confirmation Modal
    if (showMergeConfirmation) {
        val prClean = session.prNumber.ifEmpty { if (session.prUrl.isNotBlank()) "#${session.prUrl.substringAfterLast("/")}" else "PR" }
        AlertDialog(
            onDismissRequest = { showMergeConfirmation = false },
            modifier = Modifier.testTag("squash_merge_confirm_modal"),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.CallMerge, contentDescription = null, tint = Color(0xFFA855F7))
                    Text("Confirm Squash and Merge", style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Are you sure you want to squash and merge PR $prClean into base branch '${session.targetBaseBranch}'?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                            .border(1.dp, JulesOutlineVariant, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Commit: ${session.title}",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                color = JulesPrimary
                            )
                            Text(
                                text = "Author: Jules Autonomous Agent <jules@google.com>",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }
                    }
                    Text(
                        text = "This action will update the remote repository and close PR $prClean.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showMergeConfirmation = false
                        onMergePr(session)
                        Toast.makeText(context, "PR $prClean squash-merged into ${session.targetBaseBranch}!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_squash_merge_btn")
                ) {
                    Text("Squash and Merge", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMergeConfirmation = false }) {
                    Text("Cancel", color = JulesOutline)
                }
            },
            containerColor = JulesSurfaceContainer
        )
    }
}

@Composable
fun EnhancedPullRequestCard(
    session: SessionItem,
    onApproveClick: () -> Unit,
    onMergeClick: () -> Unit,
    onDeleteBranchClick: () -> Unit,
    onCopyBranch: () -> Unit,
    onOpenInGitHub: () -> Unit,
    onApprovePlan: ((SessionItem) -> Unit)? = null
) {
    val prNumber = session.prNumber.ifEmpty { if (session.prUrl.isNotBlank()) "#${session.prUrl.substringAfterLast("/")}" else "PR" }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(JulesSurfaceContainerHigh)
            .border(1.dp, JulesOutlineVariant, RoundedCornerShape(14.dp))
            .padding(16.dp)
            .testTag("enhanced_pr_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header Row: PR Title + LIVE STATUS BADGE
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
                        imageVector = Icons.Default.CallMerge,
                        contentDescription = null,
                        tint = JulesPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Pull Request $prNumber",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 1. Live status badge (Open / Merged / Closed)
                LiveStatusBadge(prStatus = session.prStatus)
            }

            // 2. FEATURE BRANCH DISPLAY (jules/...)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JulesSurfaceLowest, RoundedCornerShape(10.dp))
                    .border(1.dp, JulesOutlineVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
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
                        Icon(
                            imageVector = Icons.Default.ForkRight,
                            contentDescription = "Branch",
                            tint = JulesSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = session.branch,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = JulesSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "→ ${session.targetBaseBranch}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = JulesOutline
                                )
                            }
                            if (session.isBranchDeleted) {
                                Text(
                                    text = "Branch deleted on remote ✓",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = JulesError
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onCopyBranch,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Branch",
                            tint = JulesOutline,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Diff additions & deletions badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(JulesSecondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("+${if (session.diffAdded > 0) session.diffAdded else 184} lines", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = JulesSecondary)
                    }
                    Box(
                        modifier = Modifier
                            .background(JulesError.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("-${if (session.diffRemoved > 0) session.diffRemoved else 22} lines", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = JulesError)
                    }
                }

                if (session.prApproved) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = JulesSecondary, modifier = Modifier.size(14.dp))
                        Text("Review Approved", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = JulesSecondary)
                    }
                }
            }

            // 3. ACTION BUTTONS ROW:
            // - Approve Plan button (shown when task needs review / plan approval)
            // - Approve PR button (with toast & status refresh)
            // - Merge PR button (with confirmation modal & squash-merge)
            // - Delete Branch button (shown when PR is merged/closed to delete remote branch)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (session.status == SessionStatus.NEEDS_REVIEW) {
                    Button(
                        onClick = { onApprovePlan?.invoke(session) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("drawer_approve_plan_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = JulesPrimaryContainer,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Approve Execution Plan & Proceed",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }

                if (session.prStatus == PullRequestStatus.OPEN) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3a. Approve PR button
                        Button(
                            onClick = onApproveClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("drawer_approve_pr_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (session.prApproved) JulesSecondary.copy(alpha = 0.2f) else JulesPrimaryContainer,
                                contentColor = if (session.prApproved) JulesSecondary else Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (session.prApproved) Icons.Default.Check else Icons.Default.ThumbUp,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (session.prApproved) "Approved ✓" else "Approve PR",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        // 3b. Merge PR button
                        Button(
                            onClick = onMergeClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("drawer_merge_pr_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFA855F7),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallMerge,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Merge PR",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                } else {
                    // 3c. DELETE BRANCH BUTTON (shown when PR is merged/closed)
                    if (!session.isBranchDeleted) {
                        Button(
                            onClick = onDeleteBranchClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("drawer_delete_branch_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JulesError.copy(alpha = 0.15f),
                                contentColor = JulesError
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Delete Remote Branch (${session.branch})",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Remote branch '${session.branch}' has been deleted ✓",
                                style = MaterialTheme.typography.labelSmall,
                                color = JulesOutline
                            )
                        }
                    }
                }

                // 4. FALLBACK LINK TO OPEN IN GITHUB
                OutlinedButton(
                    onClick = onOpenInGitHub,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("drawer_open_github_link"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Open PR in GitHub (Fallback)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}

@Composable
fun LiveStatusBadge(prStatus: PullRequestStatus) {
    val isDark = MaterialTheme.colorScheme.surface == DarkJulesSurface
    val (bgColor, textColor, label, icon) = when (prStatus) {
        PullRequestStatus.OPEN -> Quadruple(
            JulesSecondary.copy(alpha = 0.18f),
            JulesSecondary,
            "Open",
            Icons.Default.CheckCircle
        )
        PullRequestStatus.MERGED -> Quadruple(
            if (isDark) Color(0xFFA855F7).copy(alpha = 0.20f) else Color(0xFFF3E8FD),
            if (isDark) Color(0xFFD8B4FE) else Color(0xFF6B21A8),
            "Merged",
            Icons.Default.CallMerge
        )
        PullRequestStatus.CLOSED -> Quadruple(
            JulesError.copy(alpha = 0.18f),
            JulesError,
            "Closed",
            Icons.Default.Close
        )
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(9999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("live_pr_status_${prStatus.name.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(textColor, CircleShape)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                ),
                color = textColor
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
