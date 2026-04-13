package com.example.argus_eye.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.argus_eye.controller.ConversationHistController
import com.example.argus_eye.data.model.ContactModel
import com.example.argus_eye.data.model.InteractionResponse
import com.example.argus_eye.data.remote.api.controller.ContactsController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    contact: ContactModel,
    contactsController: ContactsController,
    conversationController: ConversationHistController,
    onBack: () -> Unit,
    onInteractionClick: (InteractionResponse) -> Unit
) {
    var notes by remember(contact.id) { mutableStateOf(contact.notes ?: "") }
    val isLoading = contactsController.isLoading.value
    val error = contactsController.error.value
    
    val interactions = conversationController.interactions.value.filter { it.personId == contact.id }

    LaunchedEffect(contact.id) {
        conversationController.fetchInteractions()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom Top Bar
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
                            // Optionally handle success
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Profile Image
            AsyncImage(
                model = contact.profileImageBytes,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFFFD54F), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))
            
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
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Conversations Section
            Text(
                text = "CONVERSATION HISTORY:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5A6978),
                    fontSize = 16.sp
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            if (interactions.isEmpty()) {
                Text(
                    text = "No conversations found.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                interactions.forEach { interaction ->
                    InteractionItem(interaction) { onInteractionClick(interaction) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InteractionItem(interaction: InteractionResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD1D9E0), RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ID: ${interaction.interactionId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF5C6BC0)
                )
                Text(
                    text = interaction.timestamp.split("T").getOrNull(0) ?: interaction.timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = interaction.context,
                fontSize = 13.sp,
                color = Color.DarkGray,
                maxLines = 2
            )
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
