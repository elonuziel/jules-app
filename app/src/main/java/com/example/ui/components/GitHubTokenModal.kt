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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun GitHubTokenModal(
    currentToken: String,
    onSaveToken: (String) -> Unit,
    onDisconnectToken: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var tokenInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(currentToken.isBlank()) }
    var isTokenVisible by remember { mutableStateOf(false) }
    var showDisconnectConfirm by remember { mutableStateOf(false) }

    val isConnected = currentToken.isNotBlank()
    val maskedToken = when {
        currentToken.isBlank() -> "No token configured"
        currentToken.length >= 8 -> "${currentToken.take(4)}••••${currentToken.takeLast(4)}"
        else -> "ghp_••••"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("github_token_modal"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (isConnected) JulesSecondary.copy(alpha = 0.15f) else JulesTertiary.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = if (isConnected) JulesSecondary else JulesTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "GitHub Token Management",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Client-Side BYOK Credentials",
                            style = MaterialTheme.typography.labelSmall,
                            color = JulesOutline
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = JulesOutline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Connection Status Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isConnected) JulesSurfaceContainerHigh else JulesSurfaceContainer)
                        .border(
                            1.dp,
                            if (isConnected) JulesSecondary.copy(alpha = 0.35f) else JulesOutlineVariant,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(12.dp)
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
                                    .size(10.dp)
                                    .background(if (isConnected) JulesSecondary else JulesTertiary, CircleShape)
                            )
                            Column {
                                Text(
                                    text = if (isConnected) "GitHub Status: Connected" else "GitHub Status: Unconfigured",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isConnected) JulesSecondary else JulesTertiary
                                )
                                Text(
                                    text = if (isConnected) "Active for PR creation & branch inspection" else "Add a PAT to enable PR automation & merging",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Current Token Display (Masked)
                if (isConnected) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ACTIVE MASKED TOKEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = JulesOutline
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JulesSurfaceLowest, RoundedCornerShape(8.dp))
                                .border(1.dp, JulesOutlineVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTokenVisible) currentToken else maskedToken,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = JulesPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { isTokenVisible = !isTokenVisible },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle token visibility",
                                        tint = JulesOutline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentToken))
                                        Toast.makeText(context, "GitHub Token copied to clipboard", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy token",
                                        tint = JulesOutline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Disconnect Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { isInputExpanded = !isInputExpanded }
                            ) {
                                Text(
                                    text = if (isInputExpanded) "Hide Update Field" else "Update / Replace PAT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = JulesPrimary
                                )
                            }

                            Button(
                                onClick = { showDisconnectConfirm = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = JulesError.copy(alpha = 0.15f),
                                    contentColor = JulesError
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("disconnect_github_pat_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Disconnect PAT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium))
                            }
                        }
                    }
                }

                // Input Field for new/replacement PAT
                AnimatedVisibility(visible = isInputExpanded || !isConnected) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isConnected) "ENTER NEW GITHUB PAT" else "INPUT GITHUB PERSONAL ACCESS TOKEN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = JulesOutline
                        )

                        OutlinedTextField(
                            value = tokenInput,
                            onValueChange = { tokenInput = it },
                            placeholder = {
                                Text(
                                    text = "ghp_xxxxxxxxxxxxxxxxxxxx",
                                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                    color = JulesOutline
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("github_pat_input_field"),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = JulesSurfaceLowest,
                                unfocusedContainerColor = JulesSurfaceLowest,
                                focusedBorderColor = JulesPrimary,
                                unfocusedBorderColor = JulesOutlineVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Button(
                            onClick = {
                                if (tokenInput.isNotBlank()) {
                                    onSaveToken(tokenInput.trim())
                                    tokenInput = ""
                                    isInputExpanded = false
                                    Toast.makeText(context, "GitHub Token updated & connected!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = tokenInput.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_github_pat_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = JulesPrimaryContainer,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save & Connect GitHub PAT")
                        }
                    }
                }

                // Security & Required Scopes Info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(JulesSurfaceContainerHighest, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = JulesSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Required Scopes & Client-Side Security",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = JulesSecondary
                            )
                        }
                        Text(
                            text = "Token requires 'repo', 'workflow', and 'read:org' permissions. Your key is stored exclusively on this device and is used for direct GitHub API requests.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // External Link to Generate Token
                OutlinedButton(
                    onClick = {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/settings/tokens/new?scopes=repo,workflow,read:org&description=Jules%20Autonomous%20Agent")
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Open https://github.com/settings/tokens/new", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate New PAT on GitHub", style = MaterialTheme.typography.labelMedium)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = JulesPrimary)
            }
        },
        containerColor = JulesSurfaceContainer
    )

    // Disconnect Confirmation Modal
    if (showDisconnectConfirm) {
        AlertDialog(
            onDismissRequest = { showDisconnectConfirm = false },
            title = { Text("Disconnect GitHub PAT?") },
            text = {
                Text("Are you sure you want to disconnect your GitHub Personal Access Token? Jules will no longer be able to open or merge pull requests on your behalf until a token is reconfigured.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDisconnectToken()
                        showDisconnectConfirm = false
                        Toast.makeText(context, "GitHub PAT disconnected.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JulesError)
                ) {
                    Text("Disconnect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisconnectConfirm = false }) {
                    Text("Cancel", color = JulesOutline)
                }
            },
            containerColor = JulesSurfaceContainer
        )
    }
}
