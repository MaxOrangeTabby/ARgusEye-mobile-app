package com.example.argus_eye.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.argus_eye.data.local.PrefManager
import com.example.argus_eye.data.remote.api.RetrofitClient
import com.google.firebase.auth.FirebaseUser

@Composable
fun ProfileScreen(
    user: FirebaseUser?,
    onLogoutClick: () -> Unit
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val prefManager = remember { PrefManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        if (user?.photoUrl != null) {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder if no photo URL
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color(0xFFE1E5E9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture Placeholder",
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFF9AA5B1)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Profile Menu Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProfileMenuItem("Profile", isSelected = true)
            ProfileMenuItem("Setting", onClick = { showSettingsDialog = true })
            ProfileMenuItem("About")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6750A4),
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .height(48.dp)
        ) {
            Text("Log out", fontWeight = FontWeight.Bold)
        }
    }

    if (showSettingsDialog) {
        var urlInput by remember { mutableStateOf(prefManager.getBaseUrl()) }
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Text("API Base URL", style = MaterialTheme.typography.labelMedium)
                    TextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    prefManager.setBaseUrl(urlInput)
                    RetrofitClient.getApiService(context) // Refresh the Retrofit instance
                    showSettingsDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileMenuItem(text: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(30.dp)
                    .background(Color(0xFF6750A4))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6750A4),
                    fontSize = 18.sp
                )
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A6978),
                    fontSize = 18.sp
                )
            )
        }
    }
}