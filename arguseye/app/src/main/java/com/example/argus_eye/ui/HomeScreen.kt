package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.argus_eye.data.model.ContactModel
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    unlabeledPeople: List<ContactModel>,
    isLoading: Boolean,
    error: String?,
    user: FirebaseUser?,
    onRetry: () -> Unit,
    onLabel: (Int, String) -> Unit,
    onDismiss: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (error != null && unlabeledPeople.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: $error", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        val displayName = user?.displayName ?: user?.email?.split("@")?.get(0) ?: "User"
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Welcome $displayName!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5A6978)
                            )
                        )
                    }

                    items(unlabeledPeople, key = { it.id }) { person ->
                        HomeCard(
                            person = person,
                            onLabel = { name -> onLabel(person.id, name) },
                            onDismiss = { onDismiss(person.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    person: ContactModel,
    onLabel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    val isEnterEnabled = nameInput.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD1D9E0), RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            AsyncImage(
                model = person.profileImageBytes,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFFFD54F), CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Who is this?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5A6978)
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                placeholder = { Text("Enter name (e.g. Peter)", color = Color(0xFFD1D9E0)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFD1D9E0),
                    focusedBorderColor = Color(0xFF6750A4)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isEnterEnabled) {
                            onLabel(nameInput)
                        }
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLabel(nameInput) },
                enabled = isEnterEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF6750A4).copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "SAVE NAME",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
