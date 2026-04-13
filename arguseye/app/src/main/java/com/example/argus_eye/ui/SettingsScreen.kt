package com.example.argus_eye.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.argus_eye.data.local.PrefManager

@Composable
fun SettingsScreen(context: Context) {
    val prefManager = remember { PrefManager(context) }
    var urlText by remember { mutableStateOf(prefManager.getBaseUrl()) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("API Base URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                prefManager.setBaseUrl(urlText)
                // Optional: Restart activity or show toast
                Toast.makeText(context, "URL Updated. Restart app or refresh view.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Save & Apply")
        }
    }
}