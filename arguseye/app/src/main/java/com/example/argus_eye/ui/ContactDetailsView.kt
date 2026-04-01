package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argus_eye.data.model.ContactModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(contact: ContactModel, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Custom Top Bar similar to the image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF5A6978))
            }
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5A6978)
                )
            )
        }
        HorizontalDivider(color = Color(0xFFFFD54F), thickness = 2.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Container for fields
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFD1D9E0), RoundedCornerShape(4.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailField(
                        label = "FIRST MET:",
                        value = contact.createdAt?.split("T")?.get(0) ?: "Unknown"
                    )
                    DetailField(
                        label = "LAST SEEN:",
                        value = contact.lastSeen?.split("T")?.get(0) ?: "Unknown"
                    )
                    DetailField(
                        label = "NOTES:",
                        value = contact.notes ?: "No notes"
                    )
                    
                    DetailField(label = "ORGANIZATION:")
                    DetailField(label = "RELATION:")
                    DetailField(label = "AGE")
                    DetailField(label = "ETC")
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DetailField(label: String, value: String = "") {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .border(1.dp, Color(0xFFD1D9E0), RoundedCornerShape(4.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF5A6978),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
                if (value.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF5A6978),
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}
