package com.example.project_test.utility

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.example.project_test.ui.theme.PLantBuddiesTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermission() {
    val writePermission = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val readPermission = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var showDialog by remember { mutableStateOf(!writePermission.status.isGranted || !readPermission.status.isGranted) }


    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Permissions Required",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Storage permission required for saving favorite plants.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            if (!writePermission.status.isGranted) {
                                writePermission.launchPermissionRequest()
                            }
                            if (!readPermission.status.isGranted) {
                                readPermission.launchPermissionRequest()
                            }
                            showDialog = false // Close dialogue
                        }) {
                            Text("Grant Permissions")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 300)
@Composable
fun RequestPermissionPreview() {
    PLantBuddiesTheme {
        RequestPermission()
    }
}
