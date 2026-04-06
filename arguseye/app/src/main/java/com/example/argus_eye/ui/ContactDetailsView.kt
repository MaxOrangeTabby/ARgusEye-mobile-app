package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argus_eye.data.model.ContactModel
import com.example.argus_eye.data.remote.api.controller.ContactsController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    contact: ContactModel,
    contactsController: ContactsController,
    onBack: () -> Unit
) {
    var notes by remember(contact.id) { mutableStateOf(contact.notes ?: "") }
    val isLoading = contactsController.isLoading.value
    val error = contactsController.error.value

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
            
            // Save Button
            if (notes != (contact.notes ?: "")) {
                IconButton(
                    onClick = {
                        contactsController.updateNotes(contact.id, notes) {
                            // Optionally handle success (e.g., show a toast or hide keyboard)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF6750A4)
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = Color(0xFF6750A4))
                    }
                }
            }
        }
        HorizontalDivider(color = Color(0xFFFFD54F), thickness = 2.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

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
                    
                    // Editable Notes Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "NOTES:",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF5A6978),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF5A6978),
                                fontSize = 14.sp
                            ),
                            placeholder = { Text("Add notes here...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6750A4),
                                unfocusedBorderColor = Color(0xFFD1D9E0)
                            )
                        )
                    }
                    
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
